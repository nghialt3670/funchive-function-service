package com.funchive.functionservice.function.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class DirectoryUtils {
    public static Path createTempDirectory(String directoryName) {
        try {
            return Files.createTempDirectory(directoryName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temporary directory", e);
        }
    }

    public static void removeDirectory(Path directoryPath) {
        try (var paths = Files.walk(directoryPath)) {
            paths.map(Path::toFile)
                 .sorted((a, b) -> -a.compareTo(b))
                 .forEach(file -> {
                     if (!file.delete()) {
                         log.warn("Failed to delete file or directory: {}", file.getAbsolutePath());
                     }
                 });
        } catch (IOException e) {
            log.error("Failed to cleanup temporary directory: {}", directoryPath, e);
        }
    }
} 