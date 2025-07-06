package com.funchive.functionservice.function.model.dto.function;

import com.funchive.functionservice.function.model.dto.type.TypeUpdate;
import lombok.Data;

@Data
public class FunctionUpdate {
    private String name;
    private String description;
    private TypeUpdate inputType;
    private TypeUpdate outputType;
}
