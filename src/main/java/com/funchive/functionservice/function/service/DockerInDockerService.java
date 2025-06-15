package com.funchive.functionservice.function.service;

import com.funchive.functionservice.function.DockerService;
import com.funchive.functionservice.function.config.properties.DockerConfigProperties;
import com.funchive.functionservice.function.model.dto.FileCreateDto;
import com.funchive.functionservice.function.model.dto.FileDto;
import com.funchive.functionservice.function.utils.DirectoryUtils;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.Frame;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DockerInDockerService implements DockerService {
    private final DockerClient dockerClient;
    private final DockerConfigProperties dockerConfigProperties;

    @Override
    public String buildImage(String dockerfileContent) {
        var tempDirectory = DirectoryUtils.createTempDirectory("docker-build-");
        try {
            File dockerfile = new File(tempDirectory.toFile(), "Dockerfile");
            Files.writeString(dockerfile.toPath(), dockerfileContent);

            return dockerClient.buildImageCmd()
                    .withBaseDirectory(tempDirectory.toFile())
                    .withDockerfile(dockerfile)
                    .exec(new CustomBuildImageCallback())
                    .awaitImageId(dockerConfigProperties.getImageBuildTimeOutSeconds(), TimeUnit.SECONDS);

        } catch (IOException e) {
            throw new RuntimeException("Failed to create temporary build context", e);
        } finally {
            DirectoryUtils.removeDirectory(tempDirectory);
        }
    }

    @Override
    public void removeImage(String imageId) {
        dockerClient.removeImageCmd(imageId).exec();
    }

    @Override
    public String createContainer(String imageId) {
        return dockerClient.createContainerCmd(imageId).exec().getId();
    }

    @Override
    public String createContainer(String imageId, Map<String, String> environmentVariables) {
        String[] envArray = environmentVariables.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .toArray(String[]::new);

        return dockerClient.createContainerCmd(imageId)
                .withEnv(envArray)
                .exec()
                .getId();
    }

    @Override
    public void startContainer(String containerId) {
        try {
            dockerClient.startContainerCmd(containerId).exec();

            // Wait for container to finish execution with robust polling
            waitForContainerCompletion(containerId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for container " + containerId, e);
        }
    }

    private void waitForContainerCompletion(String containerId) throws InterruptedException {
        long timeoutMillis = dockerConfigProperties.getContainerTimeoutSeconds() * 1000; // Convert seconds to milliseconds
        long pollIntervalMillis = 5_000; // Check every 5 seconds
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            try {
                var containerInfo = dockerClient.inspectContainerCmd(containerId).exec();
                var state = containerInfo.getState();

                if (Boolean.FALSE.equals(state.getRunning())) {
                    // Container has finished
                    if (state.getExitCodeLong() != 0) {
                        // Get container logs to understand what went wrong
                        String containerLogs = getContainerLog(containerId);
                        throw new RuntimeException("Container " + containerId + " failed with exit code: " + state.getExitCodeLong() +
                                ". Logs: " + containerLogs);
                    }
                    return; // Success
                }

                // Container is still running, wait and check again
                Thread.sleep(pollIntervalMillis);

            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw e;
                }
                throw new RuntimeException("Error checking container status: " + e.getMessage(), e);
            }
        }

        // Timeout reached
        throw new RuntimeException("Container " + containerId + " did not complete within timeout period");
    }

    @Override
    public void stopContainer(String containerId) {
        var containerInfo = dockerClient.inspectContainerCmd(containerId).exec();
        if (Boolean.TRUE.equals(containerInfo.getState().getRunning())) {
            dockerClient.stopContainerCmd(containerId).exec();
        }
    }

    @Override
    public void removeContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId).exec();
    }

    @Override
    public FileDto getFileInContainer(String containerId, String filePath) {
        var fileDto = new FileDto();
        fileDto.setFileStream(dockerClient.copyArchiveFromContainerCmd(containerId, filePath).exec());
        fileDto.setFilename(FilenameUtils.getName(filePath));
        fileDto.setMimeType("application/octet-stream");
        return fileDto;
    }

    @Override
    public void createFileInContainer(String containerId, FileCreateDto fileCreateDto) {
        try {
            // Create a temporary tar file containing the file to copy
            File tempTarFile = createTarArchive(fileCreateDto);

            try (InputStream tarInputStream = Files.newInputStream(tempTarFile.toPath())) {
                String targetPath = fileCreateDto.getFilePath();

                // Extract directory path for the remote path
                String remotePath = "/app";  // Always copy to /app directory

                dockerClient.copyArchiveToContainerCmd(containerId)
                        .withTarInputStream(tarInputStream)
                        .withRemotePath(remotePath)
                        .exec();
            }

            // Clean up temporary file
            tempTarFile.delete();

        } catch (IOException e) {
            throw new RuntimeException("Failed to copy file to container " + containerId, e);
        }
    }

    @Override
    public void createFilesInContainer(String containerId, List<FileCreateDto> fileCreateDtos) {

    }

    private File createTarArchive(FileCreateDto fileCreateDto) throws IOException {
        File tempTarFile = File.createTempFile("docker-copy-", ".tar");

        try (FileOutputStream fos = new FileOutputStream(tempTarFile);
             TarArchiveOutputStream tarOut = new TarArchiveOutputStream(fos)) {

            // Read the file content
            byte[] fileContent;
            try (InputStream inputStream = fileCreateDto.getFileDto().getFileStream()) {
                fileContent = inputStream.readAllBytes();
            }

            // Create tar entry
            String fileName = fileCreateDto.getFileDto().getFilename();
            TarArchiveEntry tarEntry = new TarArchiveEntry(fileName);
            tarEntry.setSize(fileContent.length);
            tarEntry.setMode(0644); // Standard file permissions

            tarOut.putArchiveEntry(tarEntry);
            tarOut.write(fileContent);
            tarOut.closeArchiveEntry();
        }

        return tempTarFile;
    }

    @Override
    public Map<String, String> getEnvironmentVariables(String containerId) {
        var containerInfo = dockerClient.inspectContainerCmd(containerId).exec();
        var envArray = containerInfo.getConfig().getEnv();

        Map<String, String> environmentVariables = new HashMap<>();

        if (envArray != null) {
            for (String envEntry : envArray) {
                // Environment variables are in format "KEY=VALUE"
                int separatorIndex = envEntry.indexOf('=');
                if (separatorIndex > 0) {
                    String key = envEntry.substring(0, separatorIndex);
                    String value = envEntry.substring(separatorIndex + 1);
                    environmentVariables.put(key, value);
                }
            }
        }

        return environmentVariables;
    }

    @Override
    public String getContainerLog(String containerId) {
        StringBuilder logBuilder = new StringBuilder();

        try {
            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withTailAll()
                    .exec(new ResultCallback.Adapter<>() {
                        @Override
                        public void onNext(Frame frame) {
                            logBuilder.append(new String(frame.getPayload()));
                        }
                    }).awaitCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return logBuilder.toString();
    }

    private static class CustomBuildImageCallback extends BuildImageResultCallback {
        @Override
        public void onNext(BuildResponseItem item) {
            super.onNext(item);
            // Log build progress for debugging
            if (item.getStream() != null) {
                System.out.print(item.getStream());
            }
        }

        @Override
        public void onError(Throwable throwable) {
            System.err.println("Build error: " + throwable.getMessage());
            super.onError(throwable);
        }
    }
}
