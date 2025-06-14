package com.funchive.functionservice.function.exception;

public class ExecutionTimeoutException extends RuntimeException {
    public ExecutionTimeoutException(String functionId, int duration) {
        super(String.format("Execution for function with id %s timed out after %d seconds", functionId, duration));
    }
}
