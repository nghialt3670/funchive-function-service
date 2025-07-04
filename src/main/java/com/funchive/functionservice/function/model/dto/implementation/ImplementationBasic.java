package com.funchive.functionservice.function.model.dto.implementation;

import com.funchive.functionservice.function.model.dao.implementation.CompilationStatus;
import lombok.Data;

@Data
public class ImplementationBasic {
    protected String id;
    protected String type;
    protected String name;
    protected String description;
    protected CompilationStatus compilationStatus;
}
