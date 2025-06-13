package com.funchive.functionservice.function.exception;

import com.funchive.functionservice.function.model.document.CompilationStatus;

public class FunctionNotCompiledException extends RuntimeException {
    public FunctionNotCompiledException(String functionId, CompilationStatus compilationStatus) {
        super(String.format("Function with id %s is not compiled. Compilation status: %s", functionId, compilationStatus));
    }
}
