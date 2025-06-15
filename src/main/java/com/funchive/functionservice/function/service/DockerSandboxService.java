package com.funchive.functionservice.function.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funchive.functionservice.function.DockerService;
import com.funchive.functionservice.function.ExecutableStorage;
import com.funchive.functionservice.function.FunctionService;
import com.funchive.functionservice.function.SandboxService;
import com.funchive.functionservice.function.config.properties.DockerConfigProperties;
import com.funchive.functionservice.function.exception.ContainerRunFailedException;
import com.funchive.functionservice.function.exception.FunctionAlreadyCompiledException;
import com.funchive.functionservice.function.exception.FunctionNotCompiledException;
import com.funchive.functionservice.function.exception.ImplementationNotSupported;
import com.funchive.functionservice.function.model.document.CompilationStatus;
import com.funchive.functionservice.function.model.document.Implementation;
import com.funchive.functionservice.function.model.document.Value;
import com.funchive.functionservice.function.model.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DockerSandboxService implements SandboxService {
    private final DockerService dockerService;
    private final ExecutableStorage executableStorage;
    private final FunctionService functionService;
    private final List<DockerSandboxStrategy> strategies;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Async
    @Override
    public void compileFunction(FunctionDetailDto functionDetailDto) {
        if (!functionDetailDto.getCompilationStatus().equals(CompilationStatus.NOT_STARTED)) {
            throw new FunctionAlreadyCompiledException(functionDetailDto.getId());
        }

        var implementation = functionDetailDto.getImplementation();
        var strategy = getDockerSandboxStrategy(implementation);
        var startTime = Instant.now();

        CompilationResultDto compilationResultDto = null;

        notificationService.notifyCompilationStarted(functionDetailDto.getId());

        String containerId = null;
        String imageId = null;

        try {
            var dockerfileContent = strategy.getCompilationDockerfileContent(implementation);
            log.info("Building Docker image with Dockerfile content:\n{}", dockerfileContent);
            imageId = dockerService.buildImage(dockerfileContent, log::error);
            containerId = dockerService.createContainer(imageId);

            var sourceFileCreateDtos = strategy.createSourceFiles(functionDetailDto);
            dockerService.createFilesInContainer(containerId, sourceFileCreateDtos);
            dockerService.startContainer(containerId, log::info);

            if (!dockerService.waitContainer(containerId)) {
                var containerLogs = dockerService.getContainerLogs(containerId);
                var runTimeSeconds = dockerService.getContainerRunTimeSeconds(containerId);
                throw new ContainerRunFailedException(dockerfileContent, containerId, containerLogs, runTimeSeconds);
            }

            var executablePath = strategy.getExecutablePath();
            var executable = dockerService.getFileInContainer(containerId, executablePath);

            executableStorage.storeExecutable(functionDetailDto.getId(), executable);
            functionService.updateCompilationStatus(functionDetailDto.getId(), CompilationStatus.SUCCESS);

            var duration = (int) Duration.between(startTime, Instant.now()).toMillis();

            log.info("Compilation succeeded for function {}: {}", functionDetailDto.getId(), compilationResultDto);
            compilationResultDto = CompilationResultDto.builder()
                    .success(true)
                    .duration(duration)
                    .build();

        } catch (Exception e) {
            log.error("Compilation failed for function {}: {}", functionDetailDto.getId(), e.getMessage(), e);

            compilationResultDto = CompilationResultDto.builder()
                    .success(false)
                    .duration((int) Duration.between(startTime, Instant.now()).toMillis())
                    .build();

        } finally {
            notificationService.notifyCompilationComplete(functionDetailDto.getId(), compilationResultDto);

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
    public void executeFunction(FunctionDetailDto functionDetailDto, ExecutionTriggerDto executionTriggerDto) {
        if (!functionDetailDto.getCompilationStatus().equals(CompilationStatus.SUCCESS)) {
            throw new FunctionNotCompiledException(functionDetailDto.getId(), functionDetailDto.getCompilationStatus());
        }

        var implementation = functionDetailDto.getImplementation();
        var strategy = getDockerSandboxStrategy(implementation);
        var startTime = Instant.now();

        int duration = 0;
        ExecutionResultDto executionResultDto = null;

        String containerId = null;
        String imageId = null;

        try {
            var dockerfileContent = strategy.getExecutionDockerfileContent(implementation);
            imageId = dockerService.buildImage(dockerfileContent, log::error);

            Map<String, String> inputEnvironmentVariables = new HashMap<>();
            var inputValue = executionTriggerDto.getInputValue();
            var inputValueJson = objectMapper.writeValueAsString(inputValue);
            inputEnvironmentVariables.put("INPUT_VALUE_JSON", inputValueJson);
            var outputType = functionDetailDto.getDefinition().getOutputType();
            String outputTypeJson = objectMapper.writeValueAsString(outputType);
            inputEnvironmentVariables.put("OUTPUT_TYPE_JSON", outputTypeJson);
            containerId = dockerService.createContainer(imageId, inputEnvironmentVariables);

            var executableDto = executableStorage.loadExecutable(functionDetailDto.getId());
            var executableCreateDto = new FileCreateDto();
            executableCreateDto.setFileDto(executableDto);
            executableCreateDto.setFilePath(strategy.getExecutablePath());
            dockerService.createFileInContainer(containerId, executableCreateDto);

            notificationService.notifyExecutionStarted(functionDetailDto.getId());

            dockerService.startContainer(containerId);

            if (!dockerService.waitContainer(containerId)) {
                var containerLogs = dockerService.getContainerLogs(containerId);
                var runTimeSeconds = dockerService.getContainerRunTimeSeconds(containerId);
                throw new ContainerRunFailedException(dockerfileContent, containerId, containerLogs, runTimeSeconds);
            }

            duration = (int) Duration.between(startTime, Instant.now()).toMillis();

            var outputEnvironmentVariables = dockerService.getEnvironmentVariables(containerId);
            var outputValueJson = outputEnvironmentVariables.get("OUTPUT_VALUE_JSON");
            var outputValue = objectMapper.readValue(outputValueJson, Value.class);
            log.info("Execution succeeded for function {}: {}", functionDetailDto.getId(), outputValue);

            executionResultDto = ExecutionResultDto.builder()
                    .success(true)
                    .duration(duration)
                    .outputValue(outputValue)
                    .build();

        } catch (Exception e) {
            log.error("Execution failed for function {}: {}", functionDetailDto.getId(), e.getMessage(), e);
            executionResultDto = ExecutionResultDto.builder()
                    .success(false)
                    .duration(duration)
                    .outputValue(null)
                    .build();

        } finally {
            notificationService.notifyExecutionComplete(functionDetailDto.getId(), executionResultDto);

            if (containerId != null) {
                dockerService.stopContainer(containerId);
                dockerService.removeContainer(containerId);
            }
            if (imageId != null) {
                dockerService.removeImage(imageId);
            }
        }
    }

    @Override
    public void deleteFunctionExecutable(FunctionDetailDto functionDetailDto) {
        executableStorage.deleteExecutable(functionDetailDto.getId());
    }

    private DockerSandboxStrategy getDockerSandboxStrategy(Implementation implementation) {
        return strategies.stream()
                .filter(strategy -> strategy.isImplementationSupported(implementation))
                .findFirst()
                .orElseThrow(() -> new ImplementationNotSupported(implementation.getLanguage()));
    }
}
