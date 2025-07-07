package com.funchive.functionservice.function.exception;

public class TypeNotMatchException extends RuntimeException {
    public TypeNotMatchException(String expectedType, String providedType) {
        super(String.format("Type not match. Expected type: %s, provided type: %s", expectedType, providedType));
    }
}
