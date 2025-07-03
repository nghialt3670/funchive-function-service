package com.funchive.functionservice.function.model.dao.implementation;

public enum CompilationStatus {
    IDLE,
    PENDING,
    COMPILING,
    CANCELING,
    CANCELED,
    FAILED,
    TIMEOUT,
    COMPILED,
}
