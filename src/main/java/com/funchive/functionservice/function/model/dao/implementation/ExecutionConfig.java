package com.funchive.functionservice.function.model.dao.implementation;

import lombok.Data;

@Data
public class ExecutionConfig {
    private long timeoutSeconds;
    private boolean streamLogs;
}
