package com.funchive.functionservice.function;

import com.funchive.functionservice.function.model.dto.FileCreateDto;
import com.funchive.functionservice.function.model.dto.FileDto;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface DockerService {
    String buildImage(String dockerfileContent);

    String buildImage(String dockerfileContent, Consumer<String> logConsumer);

    void removeImage(String imageId);

    String createContainer(String imageId);

    String createContainer(String imageId, Map<String, String> environmentVariables);

    void startContainer(String containerId);

    void startContainer(String containerId, Consumer<String> logConsumer);

    boolean waitContainer(String containerId);

    void stopContainer(String containerId);

    void removeContainer(String containerId);

    FileDto getFileInContainer(String containerId, String filePath);

    void createFileInContainer(String containerId, FileCreateDto fileCreateDto);

    void createFilesInContainer(String containerId, List<FileCreateDto> fileCreateDtos);

    Map<String, String> getEnvironmentVariables(String containerId);

    List<String> getContainerLogs(String containerId);

    long getContainerRunTimeSeconds(String containerId);
}
