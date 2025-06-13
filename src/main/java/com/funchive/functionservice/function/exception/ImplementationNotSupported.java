package com.funchive.functionservice.function.exception;

public class ImplementationNotSupported extends RuntimeException {
    public ImplementationNotSupported(String language) {
        super(String.format("Implementation not supported for dockersandboxstrategy: %s", language));
    }
}
