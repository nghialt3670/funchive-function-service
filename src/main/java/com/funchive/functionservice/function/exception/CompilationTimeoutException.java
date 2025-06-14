package com.funchive.functionservice.function.exception;

public class CompilationTimeoutException extends RuntimeException {
    public CompilationTimeoutException(String functionId, int duration) {
        super(String.format("Compilation for function with id %s timed out after %d seconds", functionId, duration));
    }
}
