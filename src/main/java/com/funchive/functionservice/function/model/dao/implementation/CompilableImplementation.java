package com.funchive.functionservice.function.model.dao.implementation;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CompilableImplementation extends Implementation {
    private CompilationStatus compilationStatus = CompilationStatus.IDLE;
    private CompilationConfig compilationConfig;
}
