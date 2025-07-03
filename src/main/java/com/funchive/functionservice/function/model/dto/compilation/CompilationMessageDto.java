package com.funchive.functionservice.function.model.dto.compilation;

import com.funchive.functionservice.function.model.dao.implementation.CompilationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompilationMessageDto {
    private String functionId;
    private String implementorId;
    private CompilationStatus compilationStatus;
    private String compilationLog;
}
