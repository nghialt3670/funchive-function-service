package com.funchive.functionservice.function.service;

import com.funchive.functionservice.function.ExecutableStorage;
import com.funchive.functionservice.function.FunctionService;
import com.funchive.functionservice.function.SandboxService;
import com.funchive.functionservice.function.exception.*;
import com.funchive.functionservice.function.model.document.CompilationStatus;
import com.funchive.functionservice.function.model.document.Implementation;
import com.funchive.functionservice.function.model.document.Value;
import com.funchive.functionservice.function.model.dto.CompilationResultDto;
import com.funchive.functionservice.function.model.dto.ExecutionResultDto;
import com.funchive.functionservice.function.model.dto.FileDto;
import com.funchive.functionservice.function.model.dto.FunctionDetailDto;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FilenameUtils;
import com.funchive.functionservice.function.utils.DirectoryUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DockerSandboxService implements SandboxService {
    private final DockerClient dockerClient;
    private final ExecutableStorage executableStorage;
    private final FunctionService functionService;
    private final List<DockerSandboxStrategy> strategies;

    private static final String WORK_DIR = "/app";
    private static final int CONTAINER_TIMEOUT_SECONDS = 30;

    @Override
    public CompilationResultDto compileFunction(FunctionDetailDto functionDetailDto) {
        if (!functionDetailDto.getCompilationStatus().equals(CompilationStatus.NOT_STARTED)) {
            throw new FunctionAlreadyCompiledException(functionDetailDto.getId());
        }

        var implementation = functionDetailDto.getImplementation();
        var strategy = getDockerSandboxStrategy(implementation);

        var tempDirectory = DirectoryUtils.createTempDirectory("function");
        var startTime = Instant.now();

        try {
            strategy.prepareFiles(tempDirectory, functionDetailDto);
            
            runDockerContainer(tempDirectory, strategy.getCompilationImage(implementation),
                strategy.getCompilationCommand());

            Path executablePath = tempDirectory.resolve(strategy.getExecutablePath());
            
            FileDto fileDto = new FileDto();
            fileDto.setFileStream(Files.newInputStream(executablePath));
            fileDto.setFilename(functionDetailDto.getId() + FilenameUtils.getExtension(executablePath.getFileName().toString()));
            fileDto.setMimeType("application/octet-stream");

            executableStorage.storeExecutable(functionDetailDto.getId(), fileDto);
            functionService.updateCompilationStatus(functionDetailDto.getId(), CompilationStatus.SUCCESS);

            int duration = (int) Duration.between(startTime, Instant.now()).toMillis();

            return CompilationResultDto.builder()
                    .success(true)
                    .duration(duration)
                    .file(fileDto)
                    .build();
        } catch (Exception e) {
            return CompilationResultDto.builder()
                    .success(false)
                    .duration((int) Duration.between(startTime, Instant.now()).toMillis())
                    .build();
        } finally {
            DirectoryUtils.removeDirectory(tempDirectory);
        }
    }

    @Override
    public ExecutionResultDto executeFunction(FunctionDetailDto functionDetailDto) {
        if (!functionDetailDto.getCompilationStatus().equals(CompilationStatus.SUCCESS)) {
            throw new FunctionNotCompiledException(functionDetailDto.getId(), functionDetailDto.getCompilationStatus());
        }

        var implementation = functionDetailDto.getImplementation();
        var strategy = getDockerSandboxStrategy(implementation);

        var tempDirectory = DirectoryUtils.createTempDirectory("function");
        String containerId = null;
        try {
            var executableDto = executableStorage.loadExecutable(functionDetailDto.getId());
            var executablPath = tempDirectory.resolve(strategy.getExecutablePath());
            Files.copy(executableDto.getFileStream(), executablPath, StandardCopyOption.REPLACE_EXISTING);

            var hostConfig = HostConfig.newHostConfig().withBinds(Bind.parse(tempDirectory.toString() + ":" + WORK_DIR));
            var container = dockerClient.createContainerCmd(strategy.getExecutionImage(functionDetailDto.getImplementation()))
                    .withHostConfig(hostConfig)
                    .withWorkingDir(WORK_DIR)
                    .withCmd(strategy.getExecutionCommand())
                    .exec();
            containerId = container.getId();
            dockerClient.startContainerCmd(containerId).exec();

            boolean completed = dockerClient.waitContainerCmd(containerId)
                    .exec(new WaitContainerResultCallback())
                    .awaitCompletion(CONTAINER_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!completed) {
                throw new DockerContainerTimeoutException(CONTAINER_TIMEOUT_SECONDS);
            }

            StringBuilder logBuilder = new StringBuilder();
            dockerClient.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .withTailAll()
                .exec(new com.github.dockerjava.api.async.ResultCallback.Adapter<>() {
                    @Override
                    public void onNext(com.github.dockerjava.api.model.Frame frame) {
                        logBuilder.append(new String(frame.getPayload()));
                    }
                }).awaitCompletion();
            String log = logBuilder.toString();

            Value<?> output = strategy.getOutputFromLog(log);

            return ExecutionResultDto.builder().success(true).output(output).build();
        } catch (Exception e) {
            return ExecutionResultDto.builder().success(false).build();
        } finally {
            DirectoryUtils.removeDirectory(tempDirectory);
        }
    }

    private void runDockerContainer(Path tempDirectory, String image, String[] command) throws InterruptedException {
        var hostConfig = HostConfig.newHostConfig().withBinds(Bind.parse(tempDirectory.toString() + ":" + WORK_DIR));
        var container = dockerClient.createContainerCmd(image)
                .withHostConfig(hostConfig)
                .withWorkingDir(WORK_DIR)
                .withCmd(command)
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();
        
        boolean completed = dockerClient.waitContainerCmd(container.getId())
                .exec(new WaitContainerResultCallback())
                .awaitCompletion(CONTAINER_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        if (!completed) {
            throw new DockerContainerTimeoutException(CONTAINER_TIMEOUT_SECONDS);
        }
    }

    private DockerSandboxStrategy getDockerSandboxStrategy(Implementation implementation) {
        return strategies.stream()
                .filter(helper -> helper.supportsImplementation(implementation))
                .findFirst()
                .orElseThrow(() -> new ImplementationNotSupported(implementation.getLanguage()));
    }
}
