package com.funchive.functionservice.function.model.dto.execution;

import com.funchive.functionservice.function.model.common.value.Value;
import lombok.Data;

@Data
public class ExecutionConfigDto {
    private Value<?> inputValue;
}
