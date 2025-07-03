package com.funchive.functionservice.function.model.dao.implementation;

import lombok.Data;

@Data
public class CompilationConfig {
    private long timeoutSeconds;
    private boolean streamLogs;
}
