package com.funchive.functionservice.function.service;

import com.funchive.functionservice.function.model.document.Implementation;
import com.funchive.functionservice.function.model.document.Value;
import com.funchive.functionservice.function.model.dto.FileDto;

import java.util.Map;

public interface DockerSandboxStrategy {
    Map<String, FileDto> getSourceFiles(Implementation implementation);

    String getCompilationDockerfileContent(Implementation implementation);

    String getExecutionDockerfileContent(Implementation implementation);

    String getExecutablePath();

    Value<?> createOutputFromLog(String log);

    boolean isImplementationSupported(Implementation implementation);
}