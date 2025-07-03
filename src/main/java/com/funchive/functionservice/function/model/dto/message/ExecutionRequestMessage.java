package com.funchive.functionservice.function.model.dto.message;

import com.funchive.functionservice.function.model.common.value.Value;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ExecutionRequestMessage {
    private String functionId;
    private String implementationId;
    private String inputValueId;
    private Instant timestamp;
} 