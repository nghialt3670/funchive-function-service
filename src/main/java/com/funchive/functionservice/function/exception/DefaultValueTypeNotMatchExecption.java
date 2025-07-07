package com.funchive.functionservice.function.exception;

public class DefaultValueTypeNotMatchExecption extends RuntimeException {
    public DefaultValueTypeNotMatchExecption(String expectedType, String providedType) {
        super(String.format("Default value type not match. Expected type: %s, provided type: %s", expectedType, providedType));
    }
}
