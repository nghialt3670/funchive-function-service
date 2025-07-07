package com.funchive.functionservice.function.model.dto.function;

import com.funchive.functionservice.function.model.common.type.Type;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationBasic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionDetail {
    private String id;
    private String name;
    private String description;
    private Type inputType;
    private Type outputType;
    private List<ImplementationBasic> implementations;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
