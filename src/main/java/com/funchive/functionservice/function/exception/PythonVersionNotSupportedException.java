package com.funchive.functionservice.function.exception;

import java.util.List;

public class PythonVersionNotSupportedException extends RuntimeException {
    public PythonVersionNotSupportedException(String requestedVersion, List<String> supportedVersions) {
        super(String.format("Python version %s is not supported. Supported versions: %s", requestedVersion, String.join(", ", supportedVersions)));
    }
}
