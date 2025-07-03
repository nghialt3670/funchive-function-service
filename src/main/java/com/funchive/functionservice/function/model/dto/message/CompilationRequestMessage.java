package com.funchive.functionservice.function.model.dto.message;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CompilationRequestMessage {
    private String functionId;
    private String implementationId;
    private Instant timestamp;
} 