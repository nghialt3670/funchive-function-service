package com.funchive.functionservice.function.model.dto.type;

import lombok.Data;

@Data
public class ArrayTypeUpdate extends TypeUpdate {
    private TypeUpdate elementType;
} 