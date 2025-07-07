package com.funchive.functionservice.function.model.dto.type;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class ObjectTypeUpdate extends TypeUpdate {
    private Map<String, TypeUpdate> schema;
} 