package com.funchive.functionservice.function;

import com.funchive.functionservice.function.model.common.value.Value;
import com.funchive.functionservice.function.model.dto.function.*;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationCreate;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationDetail;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationFilter;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FunctionService {
    FunctionDetail createFunction(FunctionCreate functionCreate);

    FunctionDetail getFunctionDetail(String functionId);

    Page<FunctionDetail> getFunctionPage(FunctionFilter functionFilter, Pageable pageable);

    FunctionDetail updateFunction(String functionId, FunctionUpdate functionUpdate);

    void deleteFunction(String functionId);

    ImplementationDetail createImplementation(String functionId, ImplementationCreate implementationCreate);

    ImplementationDetail getImplementationDetail(String functionId, String implementationId);

    Page<ImplementationDetail> getImplementationPage(String functionId, ImplementationFilter implementationFilter, Pageable pageable);

    ImplementationDetail updateImplementation(String functionId, String implementationId, ImplementationUpdate implementationUpdate);

    void deleteImplementation(String functionId, String implementationId);

    void compileFunction(String functionId, String implementationId);

    void executeFunction(String functionId, String implementationId, String inputValueId);
}
