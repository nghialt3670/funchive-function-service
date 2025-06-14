package com.funchive.functionservice.function.service;

import com.funchive.functionservice.function.DockerService;
import com.funchive.functionservice.function.model.dto.FileDto;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.Frame;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RequiredArgsConstructor
public class DockerInDockerService implements DockerService {
    private final DockerClient dockerClient;

    @Override
    public String buildImage(String dockerfileContent) {
        File dockerFile;

        try {
            dockerFile = File.createTempFile("dockerfile", "");
            Files.writeString(dockerFile.toPath(), dockerfileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return dockerClient.buildImageCmd()
                .withDockerfile(dockerFile)
                .exec(new BuildImageResultCallback())
                .awaitImageId();
    }

    @Override
    public void removeImage(String imageId) {
        dockerClient.removeImageCmd(imageId).getImageId();
    }

    @Override
    public String createContainer(String imageId) {
        return dockerClient.createContainerCmd(imageId).exec().getId();
    }

    @Override
    public void startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).getContainerId();
    }

    @Override
    public void stopContainer(String containerId) {
        dockerClient.stopContainerCmd(containerId).getContainerId();
    }

    @Override
    public void removeContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId).getContainerId();
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
    public void createFileInContainer(String containerId, String filePath, FileDto fileDto) {
        dockerClient.copyArchiveToContainerCmd(containerId)
                .withTarInputStream(fileDto.getFileStream())
                .withRemotePath(filePath)
                .exec();
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
}
