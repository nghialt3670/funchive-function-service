package com.funchive.functionservice.function.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CompilationResultDto {
    private boolean success;
    private int duration;
}
