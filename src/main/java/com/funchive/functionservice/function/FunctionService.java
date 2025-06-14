package com.funchive.functionservice.function;

import com.funchive.functionservice.function.model.document.CompilationStatus;
import com.funchive.functionservice.function.model.dto.FunctionCreateDto;
import com.funchive.functionservice.function.model.dto.FunctionDetailDto;
import com.funchive.functionservice.function.model.dto.FunctionFilter;
import com.funchive.functionservice.function.model.dto.FunctionUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FunctionService {
    FunctionDetailDto createFunction(FunctionCreateDto functionCreateDto);

    FunctionDetailDto getFunctionDetail(String functionId);

    Page<FunctionDetailDto> getFunctionPage(FunctionFilter functionFilter, Pageable pageable);

    FunctionDetailDto updateFunction(String functionId, FunctionUpdateDto functionUpdateDto);

    FunctionDetailDto deleteFunction(String functionId);

    FunctionDetailDto updateCompilationStatus(String functionId, CompilationStatus compilationStatus);
}
