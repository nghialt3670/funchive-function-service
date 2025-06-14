package com.funchive.functionservice.function.service;

import com.funchive.functionservice.function.DockerService;
import com.funchive.functionservice.function.ExecutableStorage;
import com.funchive.functionservice.function.FunctionService;
import com.funchive.functionservice.function.SandboxService;
import com.funchive.functionservice.function.config.properties.DockerConfigProperties;
import com.funchive.functionservice.function.exception.FunctionAlreadyCompiledException;
import com.funchive.functionservice.function.exception.FunctionNotCompiledException;
import com.funchive.functionservice.function.exception.ImplementationNotSupported;
import com.funchive.functionservice.function.model.document.CompilationStatus;
import com.funchive.functionservice.function.model.document.Implementation;
import com.funchive.functionservice.function.model.document.Value;
import com.funchive.functionservice.function.model.dto.CompilationResultDto;
import com.funchive.functionservice.function.model.dto.ExecutionResultDto;
import com.funchive.functionservice.function.model.dto.FunctionDetailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DockerSandboxService implements SandboxService {
    private final DockerService dockerService;
    private final ExecutableStorage executableStorage;
    private final FunctionService functionService;
    private final List<DockerSandboxStrategy> strategies;
    private final NotificationService notificationService;
    private final DockerConfigProperties dockerConfigProperties;

    @Async
    @Override
    public void compileFunction(FunctionDetailDto functionDetailDto) {
        if (!functionDetailDto.getCompilationStatus().equals(CompilationStatus.NOT_STARTED)) {
            throw new FunctionAlreadyCompiledException(functionDetailDto.getId());
        }

        var implementation = functionDetailDto.getImplementation();
        var strategy = getDockerSandboxStrategy(implementation);
        var startTime = Instant.now();

        notificationService.notifyCompilationStarted(functionDetailDto.getId());

        String containerId = null;
        String imageId = null;

        try {
            var dockerfileContent = strategy.getCompilationDockerfileContent(implementation);
            imageId = dockerService.buildImage(dockerfileContent);
            containerId = dockerService.createContainer(imageId);

            dockerService.startContainer(containerId);

            var executablePath = strategy.getExecutablePath();
            var executable = dockerService.getFileInContainer(containerId, executablePath);

            executableStorage.storeExecutable(functionDetailDto.getId(), executable);
            functionService.updateCompilationStatus(functionDetailDto.getId(), CompilationStatus.SUCCESS);

            int duration = (int) Duration.between(startTime, Instant.now()).toMillis();
            CompilationResultDto result = CompilationResultDto.builder()
                    .success(true)
                    .duration(duration)
                    .build();

            notificationService.notifyCompilationComplete(functionDetailDto.getId(), result);
        } catch (Exception e) {
            log.error("Compilation failed for function {}: {}", functionDetailDto.getId(), e.getMessage(), e);

            CompilationResultDto result = CompilationResultDto.builder()
                    .success(false)
                    .duration((int) Duration.between(startTime, Instant.now()).toMillis())
                    .build();

            notificationService.notifyCompilationComplete(functionDetailDto.getId(), result);
        } finally {
            if (containerId != null) {
                dockerService.stopContainer(containerId);
                dockerService.removeContainer(containerId);
            }
            if (imageId != null) {
                dockerService.removeImage(imageId);
            }
        }
    }

    @Async
    @Override
    public void executeFunction(FunctionDetailDto functionDetailDto) {
        if (!functionDetailDto.getCompilationStatus().equals(CompilationStatus.SUCCESS)) {
            throw new FunctionNotCompiledException(functionDetailDto.getId(), functionDetailDto.getCompilationStatus());
        }

        var implementation = functionDetailDto.getImplementation();
        var strategy = getDockerSandboxStrategy(implementation);

        String containerId = null;
        String imageId = null;

        try {
            var executableDto = executableStorage.loadExecutable(functionDetailDto.getId());
            imageId = dockerService.buildImage(strategy.getExecutionDockerfileContent(implementation));
            containerId = dockerService.createContainer(imageId);
            dockerService.createFileInContainer(containerId, strategy.getExecutablePath(), executableDto);

            dockerService.startContainer(containerId);

            notificationService.notifyExecutionStarted(functionDetailDto.getId());

            var output = dockerService.getContainerLog(containerId);
            Value<?> result = strategy.createOutputFromLog(output);

            ExecutionResultDto executionResult = ExecutionResultDto.builder()
                    .success(true)
                    .output(result)
                    .build();

            notificationService.notifyExecutionComplete(functionDetailDto.getId(), executionResult);

        } catch (Exception e) {
            log.error("Execution failed for function {}: {}", functionDetailDto.getId(), e.getMessage(), e);
            ExecutionResultDto result = ExecutionResultDto.builder()
                    .success(false)
                    .build();

            notificationService.notifyExecutionComplete(functionDetailDto.getId(), result);
        } finally {
            if (containerId != null) {
                dockerService.stopContainer(containerId);
                dockerService.removeContainer(containerId);
            }
            if (imageId != null) {
                dockerService.removeImage(imageId);
            }
        }

    }

    private DockerSandboxStrategy getDockerSandboxStrategy(Implementation implementation) {
        return strategies.stream()
                .filter(strategy -> strategy.isImplementationSupported(implementation))
                .findFirst()
                .orElseThrow(() -> new ImplementationNotSupported(implementation.getLanguage()));
    }
}
