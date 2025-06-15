package com.funchive.functionservice.function.service;

import com.funchive.functionservice.function.DockerService;
import com.funchive.functionservice.function.config.properties.DockerConfigProperties;
import com.funchive.functionservice.function.model.dto.FileCreateDto;
import com.funchive.functionservice.function.model.dto.FileDto;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.WaitResponse;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class DockerServiceImpl implements DockerService {
    private final DockerClient dockerClient;
    private final DockerConfigProperties dockerConfigProperties;

    @Override
    public String buildImage(String dockerfileContent) {
        File tempDirectory = null;
        try {
            tempDirectory = Files.createTempDirectory("docker-build-").toFile();
            File dockerfile = new File(tempDirectory, "Dockerfile");
            Files.writeString(dockerfile.toPath(), dockerfileContent);

            var imageBuildTimeoutSeconds = dockerConfigProperties.getBuildTimeoutSeconds();
            return dockerClient.buildImageCmd()
                    .withBaseDirectory(tempDirectory)
                    .withDockerfile(dockerfile)
                    .exec(new BuildImageResultCallback())
                    .awaitImageId(imageBuildTimeoutSeconds, TimeUnit.SECONDS);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (tempDirectory != null && !FileUtils.deleteQuietly(tempDirectory)) {
                log.warn("Can not delete temporary directory");
            }
        }
    }

    @Override
    public String buildImage(String dockerfileContent, Consumer<String> logConsumer) {
        File tempDirectory = null;
        try {
            tempDirectory = Files.createTempDirectory("docker-build-").toFile();
            File dockerfile = new File(tempDirectory, "Dockerfile");
            Files.writeString(dockerfile.toPath(), dockerfileContent);

            var imageBuildTimeoutSeconds = dockerConfigProperties.getBuildTimeoutSeconds();
            return dockerClient.buildImageCmd()
                    .withBaseDirectory(tempDirectory)
                    .withDockerfile(dockerfile)
                    .exec(new BuildImageResultCallback() {
                        @Override
                        public void onNext(BuildResponseItem item) {
                            // Call parent implementation first for proper image ID extraction
                            super.onNext(item);
                            
                            // Then do our custom logging
                            if (item.getStream() != null) {
                                logConsumer.accept(item.getStream());
                            }
                            if (item.getErrorDetail() != null) {
                                logConsumer.accept("ERROR: " + item.getErrorDetail().getMessage());
                            }
                        }
                    })
                    .awaitImageId(imageBuildTimeoutSeconds, TimeUnit.SECONDS);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (tempDirectory != null && !FileUtils.deleteQuietly(tempDirectory)) {
                log.warn("Can not delete temporary directory");
            }
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
        dockerClient.startContainerCmd(containerId).exec();
    }

    @Override
    public void startContainer(String containerId, Consumer<String> logConsumer) {
        dockerClient.startContainerCmd(containerId).exec();
        dockerClient.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .withTailAll()
                .exec(new ResultCallback.Adapter<>() {
                    @Override
                    public void onNext(Frame frame) {
                        super.onNext(frame);

                        if (frame.getPayload() != null) {
                            logConsumer.accept(new String(frame.getPayload()));
                        }
                    }
                });
    }

    @Override
    public boolean waitContainer(String containerId) {
        try {
            return dockerClient.waitContainerCmd(containerId)
                    .exec(new WaitContainerResultCallback())
                    .awaitCompletion(dockerConfigProperties.getRunTimeoutSeconds(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
        File tempTarFile = null;

        try {
            tempTarFile = createTarArchive(fileCreateDto);

            try (InputStream tarInputStream = Files.newInputStream(tempTarFile.toPath())) {
                // Extract directory path from the file path
                String filePath = fileCreateDto.getFilePath();
                String destinationPath;
                
                // If the file path contains a directory, use that as the destination
                // Otherwise, use the root directory
                if (filePath.contains("/")) {
                    destinationPath = filePath.substring(0, filePath.lastIndexOf("/"));
                    if (destinationPath.isEmpty()) {
                        destinationPath = "/";
                    }
                } else {
                    destinationPath = "/";
                }

                dockerClient.copyArchiveToContainerCmd(containerId)
                        .withTarInputStream(tarInputStream)
                        .withRemotePath(destinationPath)
                        .exec();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (tempTarFile != null && !tempTarFile.delete()) {
                log.warn("Can not delete temporary tar file");
            }
        }
    }

    @Override
    public void createFilesInContainer(String containerId, List<FileCreateDto> fileCreateDtos) {
        fileCreateDtos.forEach(dto -> createFileInContainer(containerId, dto));
    }

    private File createTarArchive(FileCreateDto fileCreateDto) throws IOException {
        File tempTarFile = File.createTempFile("docker-copy-", ".tar");

        try (FileOutputStream fos = new FileOutputStream(tempTarFile);
             TarArchiveOutputStream tarOut = new TarArchiveOutputStream(fos)) {

            byte[] fileContent;
            try (InputStream inputStream = fileCreateDto.getFileDto().getFileStream()) {
                fileContent = inputStream.readAllBytes();
            }

            // Use the file path from the DTO to create the correct tar entry path
            String filePath = fileCreateDto.getFilePath();
            String entryName;
            
            // If the file path contains a directory, extract just the filename for the tar entry
            // The directory structure will be handled by the withRemotePath
            if (filePath.contains("/")) {
                entryName = filePath.substring(filePath.lastIndexOf("/") + 1);
            } else {
                entryName = filePath;
            }

            TarArchiveEntry tarEntry = new TarArchiveEntry(entryName);
            tarEntry.setSize(fileContent.length);
            tarEntry.setMode(0644);

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
    public List<String> getContainerLogs(String containerId) {
        var containerLogs = new ArrayList<String>();

        try {
            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withTailAll()
                    .exec(new ResultCallback.Adapter<>() {
                        @Override
                        public void onNext(Frame frame) {
                            containerLogs.add(new String(frame.getPayload()));
                        }
                    })
                    .awaitCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return containerLogs;
    }

    @Override
    public long getContainerRunTimeSeconds(String containerId) {
        var containerInfo = dockerClient.inspectContainerCmd(containerId).exec();
        var state = containerInfo.getState();

        var startedAt = DatatypeConverter.parseDateTime(state.getStartedAt()).getTime().toInstant();
        var finishedAt = state.getFinishedAt() != null && !state.getFinishedAt().isEmpty()
                ? DatatypeConverter.parseDateTime(state.getFinishedAt()).getTime().toInstant()
                : Instant.now();

        return Duration.between(startedAt, finishedAt).getSeconds();

    }

}
