package com.funchive.functionservice.function.model.dto;

import com.funchive.functionservice.function.model.document.Value;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ExecutionResultDto {
    private boolean success;
    private Value<?> output;
}
