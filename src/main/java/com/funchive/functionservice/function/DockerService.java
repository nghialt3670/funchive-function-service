package com.funchive.functionservice.function;

import com.funchive.functionservice.function.model.dto.FileDto;

public interface DockerService {
    public String buildImage(String dockerfileContent);

    public void removeImage(String imageId);

    public String createContainer(String imageId);

    public void startContainer(String containerId);

    public void stopContainer(String containerId);

    public void removeContainer(String containerId);

    public FileDto getFileInContainer(String containerId, String filePath);

    public void createFileInContainer(String containerId, String filePath, FileDto fileDto);

    public String getContainerLog(String containerId);
}
