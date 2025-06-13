package com.funchive.functionservice.function.exception;

public class FunctionNotFoundException extends RuntimeException {
    public FunctionNotFoundException(String id) {
        super(String.format("Function with ID %s not found", id));
    }
}
