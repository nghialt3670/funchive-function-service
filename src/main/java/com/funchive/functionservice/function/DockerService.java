package com.funchive.functionservice.function;

import com.funchive.functionservice.function.model.dto.FileCreateDto;
import com.funchive.functionservice.function.model.dto.FileDto;

import java.util.List;
import java.util.Map;

public interface DockerService {
    public String buildImage(String dockerfileContent);

    public void removeImage(String imageId);

    public String createContainer(String imageId);

    public String createContainer(String imageId, Map<String, String> environmentVariables);

    public void startContainer(String containerId);

    public void stopContainer(String containerId);

    public void removeContainer(String containerId);

    public FileDto getFileInContainer(String containerId, String filePath);

    public void createFileInContainer(String containerId, FileCreateDto fileCreateDto);

    public void createFilesInContainer(String containerId, List<FileCreateDto> fileCreateDtos);

    public Map<String, String> getEnvironmentVariables(String containerId);

    public String getContainerLog(String containerId);
}
