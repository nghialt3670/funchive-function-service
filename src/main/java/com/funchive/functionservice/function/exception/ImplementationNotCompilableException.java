package com.funchive.functionservice.function.exception;

public class ImplementationNotCompilableException extends RuntimeException {
    public ImplementationNotCompilableException(String implementationId) {
        super(String.format("Implementation with ID %s is not compilable", implementationId));
    }
}
