package com.funchive.functionservice.function.service;

import com.funchive.functionservice.function.model.document.Implementation;
import com.funchive.functionservice.function.model.document.Value;
import com.funchive.functionservice.function.model.dto.FunctionDetailDto;

import java.nio.file.Path;

public interface DockerSandboxStrategy {
    void prepareFiles(Path directory, FunctionDetailDto functionDetailDto) throws Exception;

    String getCompilationImage(Implementation implementation);

    String getExecutionImage(Implementation implementation);

    String[] getCompilationCommand();

    String[] getExecutionCommand();

    String getExecutablePath();

    Value<?> getOutputFromLog(String output);

    boolean supportsImplementation(Implementation implementation);
}