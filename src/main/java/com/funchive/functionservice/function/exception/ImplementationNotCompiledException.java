package com.funchive.functionservice.function.exception;

import com.funchive.functionservice.function.model.dao.implementation.CompilationStatus;

public class ImplementationNotCompiledException extends RuntimeException {
    public ImplementationNotCompiledException(String implementationId, CompilationStatus compilationStatus) {
        super(String.format("Implementation with ID %s is not compiled. Compilation status: %s", implementationId, compilationStatus));
    }
}
