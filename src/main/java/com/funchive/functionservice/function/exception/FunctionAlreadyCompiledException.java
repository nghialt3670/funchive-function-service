package com.funchive.functionservice.function.exception;

public class FunctionAlreadyCompiledException extends RuntimeException {
    public FunctionAlreadyCompiledException(String functionId) {
        super(String.format("Function with id %s is already compiled", functionId));
    }
}
