package com.funchive.functionservice.function;

import com.funchive.functionservice.function.model.dto.CompilationResultDto;
import com.funchive.functionservice.function.model.dto.ExecutionResultDto;
import com.funchive.functionservice.function.model.dto.FunctionDetailDto;

public interface SandboxService {
    CompilationResultDto compileFunction(FunctionDetailDto functionDetailDto);
    ExecutionResultDto executeFunction(FunctionDetailDto functionDetailDto);
}
