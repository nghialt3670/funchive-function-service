package com.funchive.functionservice.function.exception;

public class FunctionNotFoundException extends RuntimeException {
    public FunctionNotFoundException(String functionalId) {
        super(String.format("Function with ID %s not found", functionalId));
    }
}
