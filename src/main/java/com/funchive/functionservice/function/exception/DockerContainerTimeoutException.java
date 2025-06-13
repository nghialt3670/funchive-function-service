package com.funchive.functionservice.function.exception;

public class DockerContainerTimeoutException extends RuntimeException {
    public DockerContainerTimeoutException(int duration) {
        super(String.format("Docker container timed out after %d seconds", duration));
    }
}
