package com.funchive.functionservice.function.model.dto;

import com.funchive.functionservice.function.model.document.Value;
import lombok.Data;

@Data
public class ExecutionTriggerDto {
    private Value<?> inputValue;
}
