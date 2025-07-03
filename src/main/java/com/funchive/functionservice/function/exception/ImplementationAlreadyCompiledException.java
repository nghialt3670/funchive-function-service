package com.funchive.functionservice.function.exception;

public class ImplementationAlreadyCompiledException extends RuntimeException {
    public ImplementationAlreadyCompiledException(String functionId) {
        super(String.format("Implementation with ID %s is already compiled", functionId));
    }
}
