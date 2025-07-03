package com.funchive.functionservice.function.model.dto.message;

import lombok.Data;

import java.time.Instant;

@Data
public class CompilationResponseMessage {
    private String functionId;
    private String implementationId;
    private boolean success;
    private long duration;
    private Instant timestamp;
} 