package com.funchive.functionservice.function.model.dto.function;

import com.funchive.functionservice.function.model.common.type.Type;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationCreate;
import jakarta.annotation.Nonnull;
import lombok.Data;

import java.util.List;

@Data
public class FunctionCreate {
    @Nonnull
    private String name;
    @Nonnull
    private String description;
    @Nonnull
    private Type inputType;
    @Nonnull
    private Type outputType;
//    @Nonnull
    private List<ImplementationCreate> implementations;
}
