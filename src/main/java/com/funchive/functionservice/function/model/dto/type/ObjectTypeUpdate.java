package com.funchive.functionservice.function.model.dto.type;

import lombok.Data;
import java.util.Map;

@Data
public class ObjectTypeUpdate extends TypeUpdate {
    private Map<String, TypeUpdate> schema;
} 