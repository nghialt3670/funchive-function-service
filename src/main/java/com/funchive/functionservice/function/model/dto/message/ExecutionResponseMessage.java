package com.funchive.functionservice.function.model.dto.message;

import com.funchive.functionservice.function.model.common.value.Value;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionResponseMessage {
    private String messageId;
    private String functionId;
    private boolean success;
    private Value<?> outputValue;
    private long duration;
    private Instant timestamp;
} 