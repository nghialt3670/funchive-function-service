package com.funchive.functionservice.function.service.dockersandboxstrategy;

import com.funchive.functionservice.function.exception.PythonVersionNotSupportedException;
import com.funchive.functionservice.function.model.document.Implementation;
import com.funchive.functionservice.function.model.document.implementation.PythonImplementation;
import com.funchive.functionservice.function.model.dto.FileCreateDto;
import com.funchive.functionservice.function.model.dto.FileDto;
import com.funchive.functionservice.function.model.dto.FunctionDetailDto;
import com.funchive.functionservice.function.service.DockerSandboxStrategy;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PythonDockerSandboxStrategy implements DockerSandboxStrategy {
    private static final Map<String, String> PYTHON_VERSION_TO_BASE_IMAGE = Map.of(
            "3.6", "python:3.6-slim",
            "3.7", "python:3.7-slim",
            "3.8", "python:3.8-slim",
            "3.9", "python:3.9-slim",
            "3.10", "python:3.10-slim",
            "3.11", "python:3.11-slim",
            "3.12", "python:3.12-slim"
    );

    @Override
    public List<FileCreateDto> createSourceFiles(FunctionDetailDto functionDetailDto) {
        var implementation = functionDetailDto.getImplementation();

        if (!(implementation instanceof PythonImplementation pythonImplementation)) {
            throw new IllegalArgumentException("Implementation must be a PythonImplementation");
        }

        List<FileCreateDto> files = new ArrayList<>();

        // Create main.py file with the template and user code
        String mainPyContent = generateMainPyContent(pythonImplementation, functionDetailDto);
        FileDto mainPyFileDto = new FileDto();
        mainPyFileDto.setFilename("main.py");
        mainPyFileDto.setMimeType("text/x-python");
        mainPyFileDto.setFileStream(new ByteArrayInputStream(mainPyContent.getBytes(StandardCharsets.UTF_8)));

        FileCreateDto mainPyFile = new FileCreateDto();
        mainPyFile.setFileDto(mainPyFileDto);
        mainPyFile.setFilePath("main.py");
        files.add(mainPyFile);

        // Create requirements.txt file from packages
        String requirementsContent = pythonImplementation.getPackages().stream()
                .map(pkg -> pkg.getVersion() != null && !pkg.getVersion().isEmpty()
                        ? pkg.getName() + "==" + pkg.getVersion()
                        : pkg.getName())
                .collect(Collectors.joining("\n"));

        // Add PyInstaller to requirements for compilation
        if (!requirementsContent.isEmpty()) {
            requirementsContent += "\n";
        }
        requirementsContent += "pyinstaller\n";

        FileDto requirementsFileDto = new FileDto();
        requirementsFileDto.setFilename("requirements.txt");
        requirementsFileDto.setMimeType("text/plain");
        requirementsFileDto.setFileStream(new ByteArrayInputStream(requirementsContent.getBytes(StandardCharsets.UTF_8)));

        FileCreateDto requirementsFile = new FileCreateDto();
        requirementsFile.setFileDto(requirementsFileDto);
        requirementsFile.setFilePath("requirements.txt");
        files.add(requirementsFile);

        return files;
    }

    private String generateMainPyContent(PythonImplementation pythonImplementation, FunctionDetailDto functionDetailDto) {
        try {
            // Load the function template  
            String template = loadFunctionTemplate();

            // Generate the imports
            String importsCode = generateImportsCode(pythonImplementation);

            // Get the user function code
            String userCode = pythonImplementation.getCode();

            // Replace the import placeholder
            template = template.replace("### BEGIN IMPORTS\n### END IMPORTS",
                    "### BEGIN IMPORTS\n" + importsCode + "\n### END IMPORTS");

            // Replace the function body placeholder
            template = template.replace("### BEGIN FUNCTION BODY\n### END FUNCTION BODY\n    pass",
                    "### BEGIN FUNCTION BODY\n" + userCode + "\n### END FUNCTION BODY");

            return template;

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate main.py content", e);
        }
    }

    private String loadFunctionTemplate() throws IOException {
        try (InputStream templateStream = getClass().getResourceAsStream("/templates/python.py")) {
            if (templateStream == null) {
                throw new IOException("Function template not found");
            }
            return new String(templateStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String generateImportsCode(PythonImplementation pythonImplementation) {
        return pythonImplementation.getImports().stream()
                .map(imp -> {
                    if (imp.getTarget() != null && !imp.getTarget().isEmpty()) {
                        return "from " + imp.getSource() + " import " + imp.getTarget();
                    } else {
                        return "import " + imp.getSource();
                    }
                })
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String getCompilationDockerfileContent(Implementation implementation) {
        if (!(implementation instanceof PythonImplementation pythonImplementation)) {
            throw new IllegalArgumentException("Implementation must be a PythonImplementation");
        }

        if (!PYTHON_VERSION_TO_BASE_IMAGE.containsKey(pythonImplementation.getVersion())) {
            throw new PythonVersionNotSupportedException(pythonImplementation.getVersion(), PYTHON_VERSION_TO_BASE_IMAGE.keySet().stream().toList());
        }

        String baseImage = getBaseImage(pythonImplementation);

        return "FROM " + baseImage + "\n\n" +

                // Set working directory
                "WORKDIR /app\n\n" +

                // Install system dependencies required by PyInstaller
                "RUN apt-get update && \\\n" +
                "    apt-get install -y binutils build-essential && \\\n" +
                "    apt-get clean && \\\n" +
                "    rm -rf /var/lib/apt/lists/*\n\n" +

                // Create a compilation script that will run when container starts
                // This script expects requirements.txt and main.py to be copied to the container
                "RUN echo '#!/bin/bash' > /app/compile.sh && \\\n" +
                "    echo 'set -e' >> /app/compile.sh && \\\n" +
                "    echo 'echo \"Starting Python compilation...\"' >> /app/compile.sh && \\\n" +
                "    echo 'echo \"Current working directory:\"' >> /app/compile.sh && \\\n" +
                "    echo 'pwd' >> /app/compile.sh && \\\n" +
                "    echo 'echo \"Checking files in /app:\"' >> /app/compile.sh && \\\n" +
                "    echo 'ls -la /app/' >> /app/compile.sh && \\\n" +
                "    echo 'echo \"Contents of requirements.txt:\"' >> /app/compile.sh && \\\n" +
                "    echo 'cat requirements.txt' >> /app/compile.sh && \\\n" +
                "    echo 'echo \"Installing dependencies...\"' >> /app/compile.sh && \\\n" +
                "    echo 'pip install -r requirements.txt' >> /app/compile.sh && \\\n" +
                "    echo 'echo \"Dependencies installed, starting PyInstaller...\"' >> /app/compile.sh && \\\n" +
                "    echo 'pyinstaller --onefile --clean --name main_executable main.py' >> /app/compile.sh && \\\n" +
                "    echo 'echo \"PyInstaller completed, checking dist directory:\"' >> /app/compile.sh && \\\n" +
                "    echo 'ls -la dist/' >> /app/compile.sh && \\\n" +
                "    echo 'echo \"Copying executable to working directory...\"' >> /app/compile.sh && \\\n" +
                "    echo 'cp dist/main_executable . ' >> /app/compile.sh && \\\n" +
                "    echo 'chmod +x main_executable' >> /app/compile.sh && \\\n" +
                "    echo 'echo \"Final check - executable in working directory:\"' >> /app/compile.sh && \\\n" +
                "    echo 'ls -la main_executable' >> /app/compile.sh && \\\n" +
                "    echo 'echo \"Compilation completed successfully!\"' >> /app/compile.sh && \\\n" +
                "    chmod +x /app/compile.sh\n\n" +

                // Set the default command to run the compilation script
                "CMD [\"/app/compile.sh\"]\n";
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

                // Set the default command to run the executable (reads from INPUT_JSON env var)
                "CMD [\"./main_executable\"]\n";
    }

    @Override
    public String getExecutablePath() {
        return "/app/main_executable";
    }

    @Override
    public boolean isImplementationSupported(Implementation implementation) {
        if (implementation instanceof PythonImplementation pythonImplementation) {
            return PYTHON_VERSION_TO_BASE_IMAGE.containsKey(pythonImplementation.getVersion());
        }

        return false;
    }

    private String getBaseImage(PythonImplementation implementation) {
        return PYTHON_VERSION_TO_BASE_IMAGE.get(implementation.getVersion());
    }
} 