package com.funchive.functionservice.function.model.dto.function;

import com.funchive.functionservice.function.model.common.type.Type;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationCreate;
import lombok.Data;

import java.util.List;

@Data
public class FunctionCreate {
    private String name;
    private String description;
    private Type inputType;
    private Type outputType;
    private List<ImplementationCreate> implementations;
}
