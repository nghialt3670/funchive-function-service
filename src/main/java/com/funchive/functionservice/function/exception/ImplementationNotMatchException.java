package com.funchive.functionservice.function.exception;

public class ImplementationNotMatchException extends RuntimeException {
    public ImplementationNotMatchException(String expectedType, String providedType) {
        super(String.format("Expected implementation type: %s, provided implementation type: %s", expectedType, providedType));
    }
}
