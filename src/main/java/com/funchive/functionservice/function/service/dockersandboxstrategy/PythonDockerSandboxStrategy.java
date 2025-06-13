package com.funchive.functionservice.function.service.dockersandboxstrategy;

import com.funchive.functionservice.function.exception.PythonVersionNotSupportedException;
import com.funchive.functionservice.function.model.document.Implementation;
import com.funchive.functionservice.function.model.document.Value;
import com.funchive.functionservice.function.model.document.implementation.PythonImplementation;
import com.funchive.functionservice.function.model.dto.FunctionDetailDto;
import com.funchive.functionservice.function.service.DockerSandboxStrategy;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Component
public class PythonDockerSandboxStrategy implements DockerSandboxStrategy
{
    private static final String WORK_DIR = "/app";
    
    private static final Map<String, String> VERSION_TAGS = Map.of(
        "3.6", "3.6-slim",
        "3.7", "3.7-slim",
        "3.8", "3.8-slim",
        "3.9", "3.9-slim",
        "3.10", "3.10-slim",
        "3.11", "3.11-slim",
        "3.12", "3.12-slim"
    );

    @Override
    public void prepareFiles(Path directory, FunctionDetailDto functionDetailDto) throws Exception {
        if (!(functionDetailDto.getImplementation() instanceof PythonImplementation pythonImplementation)) {
            throw new IllegalArgumentException();
        }

        // Create requirements.txt
        StringBuilder requirements = new StringBuilder();
        for (var p : pythonImplementation.getPackages()) {
            requirements.append(p.getName())
                    .append("==")
                    .append(p.getVersion())
                    .append("\n");
        }
        Files.writeString(directory.resolve("requirements.txt"), requirements.toString());

        // Create main.py
        Files.writeString(directory.resolve("main.py"), pythonImplementation.getCode());
    }

    @Override
    public String getCompilationImage(Implementation implementation) {
        if (implementation instanceof PythonImplementation pythonImplementation) {
            if (VERSION_TAGS.containsKey(pythonImplementation.getVersion())) {
                return "python:".concat(VERSION_TAGS.get(pythonImplementation.getVersion()));
            }

            throw new PythonVersionNotSupportedException(pythonImplementation.getVersion(), VERSION_TAGS.keySet().stream().toList());
        }

        throw new IllegalArgumentException();
    }

    @Override
    public String getExecutionImage(Implementation implementation) {
        if (implementation instanceof PythonImplementation pythonImplementation) {
            if (VERSION_TAGS.containsKey(pythonImplementation.getVersion())) {
                // For Python, we use the same image for both compilation and execution
                // This could be changed in the future if we want to use a slimmer image for execution
                return "python:".concat(VERSION_TAGS.get(pythonImplementation.getVersion()));
            }

            throw new PythonVersionNotSupportedException(pythonImplementation.getVersion(), VERSION_TAGS.keySet().stream().toList());
        }

        throw new IllegalArgumentException();
    }

    @Override
    public String[] getCompilationCommand() {
        return new String[]{"sh", "-c", "pip install -r requirements.txt && python -m py_compile main.py"};
    }

    @Override
    public String[] getExecutionCommand() {
        return new String[]{"python", "main.pyc"};
    }

    @Override
    public String getExecutablePath() {
        return "__pycache__/main.cpython-38.pyc";
    }

    @Override
    public Value<?> getOutputFromLog(String output) {
        // TODO: Implement result processing based on your Value type
        return null;
    }

    @Override
    public boolean supportsImplementation(Implementation implementation) {
        if (implementation instanceof PythonImplementation pythonImplementation) {
            return VERSION_TAGS.containsKey(pythonImplementation.getVersion());
        }

        return false;
    }
} 