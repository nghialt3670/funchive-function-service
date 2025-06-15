package com.funchive.functionservice.function;

import com.funchive.functionservice.function.model.dto.ExecutionTriggerDto;
import com.funchive.functionservice.function.model.dto.FunctionDetailDto;

public interface SandboxService {
    void compileFunction(FunctionDetailDto functionDetailDto);

    void executeFunction(FunctionDetailDto functionDetailDto, ExecutionTriggerDto executionTriggerDto);

    void deleteFunctionExecutable(FunctionDetailDto functionDetailDto);
}
