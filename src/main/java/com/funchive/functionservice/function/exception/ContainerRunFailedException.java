package com.funchive.functionservice.function.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ContainerRunFailedException extends RuntimeException {
    private final String dockerfileContent;
    private final String containerId;
    private final List<String> containerLogs;
    private final long runTimeSeconds;

    public ContainerRunFailedException(String dockerfileContent, String containerId, List<String> containerLogs, long runTimeSeconds) {
        super("Container failed to run");
        this.dockerfileContent = dockerfileContent;
        this.containerId = containerId;
        this.containerLogs = containerLogs;
        this.runTimeSeconds = runTimeSeconds;
    }
}
