package com.funchive.functionservice.function.exception;

public class ImplementationNotFoundException extends RuntimeException {
    public ImplementationNotFoundException(String implementationId) {
        super(String.format("Implementation with ID %s not found", implementationId));
    }

    public ImplementationNotFoundException(String implementationId, String functionId) {
        super(String.format("Implementation with ID %s not found for function with ID %s", implementationId, functionId));
    }
}
