package com.funchive.functionservice.function.service;

import com.funchive.functionservice.function.model.document.Implementation;
import com.funchive.functionservice.function.model.dto.FileCreateDto;
import com.funchive.functionservice.function.model.dto.FunctionDetailDto;

import java.util.List;

public interface DockerSandboxStrategy {
    List<FileCreateDto> createSourceFiles(FunctionDetailDto functionDetailDto);

    String getCompilationDockerfileContent(Implementation implementation);

    String getExecutionDockerfileContent(Implementation implementation);

    String getExecutablePath();

    boolean isImplementationSupported(Implementation implementation);
}