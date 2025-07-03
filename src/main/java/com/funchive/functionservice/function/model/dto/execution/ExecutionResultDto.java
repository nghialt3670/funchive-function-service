package com.funchive.functionservice.function.model.dto.execution;

import com.funchive.functionservice.function.model.common.value.Value;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionResultDto {
    private String status;
    private String message;
    private Value<?> result;
    private String errorMessage;
    private Integer duration;
}
