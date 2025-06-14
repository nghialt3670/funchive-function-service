package com.funchive.functionservice.function.service.dockersandboxstrategy;

import com.funchive.functionservice.function.exception.PythonVersionNotSupportedException;
import com.funchive.functionservice.function.model.document.Implementation;
import com.funchive.functionservice.function.model.document.Value;
import com.funchive.functionservice.function.model.document.implementation.PythonImplementation;
import com.funchive.functionservice.function.model.dto.FileDto;
import com.funchive.functionservice.function.service.DockerSandboxStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PythonDockerSandboxStrategy implements DockerSandboxStrategy {
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
    public Map<String, FileDto> getSourceFiles(Implementation implementation) {
        // TODO: Implement files preparation
        return Map.of();
    }

    @Override
    public String getCompilationDockerfileContent(Implementation implementation) {
        if (!(implementation instanceof PythonImplementation pythonImplementation)) {
            throw new IllegalArgumentException("Implementation must be a PythonImplementation");
        }

        if (!VERSION_TAGS.containsKey(pythonImplementation.getVersion())) {
            throw new PythonVersionNotSupportedException(pythonImplementation.getVersion(), VERSION_TAGS.keySet().stream().toList());
        }

        String baseImage = "python:" + VERSION_TAGS.get(pythonImplementation.getVersion());

        return "FROM " + baseImage + "\n\n" +

                // Set working directory
                "WORKDIR /app\n\n" +

                // Copy requirements file
                "COPY requirements.txt .\n\n" +

                // Install dependencies
                "RUN pip install -r requirements.txt\n\n" +

                // Copy source code
                "COPY main.py .\n\n" +

                // Create standalone executable using PyInstaller
                "RUN echo 'Starting Python compilation...' && \\\n" +
                "    echo 'Current working directory:' && \\\n" +
                "    pwd && \\\n" +
                "    echo 'Checking files in /app:' && \\\n" +
                "    ls -la /app/ && \\\n" +
                "    echo 'Contents of requirements.txt:' && \\\n" +
                "    cat requirements.txt && \\\n" +
                "    echo 'Dependencies installed, starting PyInstaller...' && \\\n" +
                "    pyinstaller --onefile --clean --name main_executable main.py && \\\n" +
                "    echo 'PyInstaller completed, checking dist directory:' && \\\n" +
                "    ls -la dist/ && \\\n" +
                "    echo 'Copying executable to working directory...' && \\\n" +
                "    cp dist/main_executable . && \\\n" +
                "    chmod +x main_executable && \\\n" +
                "    echo 'Final check - executable in working directory:' && \\\n" +
                "    ls -la main_executable && \\\n" +
                "    echo 'Compilation completed successfully!'\n\n";
    }

    @Override
    public String getExecutionDockerfileContent(Implementation implementation) {
        return "FROM alpine:latest\n\n" +

                // Set working directory
                "WORKDIR /app\n\n" +

                // Copy the compiled executable from compilation stage
                "COPY main_executable .\n\n" +

                // Ensure executable permissions
                "RUN chmod +x main_executable\n\n" +

                // Set the default command to run the executable
                "CMD [\"./main_executable\"]\n";
    }

    @Override
    public String getExecutablePath() {
        return "main_executable";
    }

    @Override
    public Value<?> createOutputFromLog(String log) {
        // TODO: Implement result processing based on your Value type
        return null;
    }

    @Override
    public boolean isImplementationSupported(Implementation implementation) {
        if (implementation instanceof PythonImplementation pythonImplementation) {
            return VERSION_TAGS.containsKey(pythonImplementation.getVersion());
        }

        return false;
    }
} 