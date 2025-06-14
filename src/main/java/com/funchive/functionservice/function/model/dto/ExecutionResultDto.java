package com.funchive.functionservice.function.model.dto;

import com.funchive.functionservice.function.model.document.Value;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ExecutionResultDto {
    private boolean success;
    private int duration;
    private Value<?> output;
}
