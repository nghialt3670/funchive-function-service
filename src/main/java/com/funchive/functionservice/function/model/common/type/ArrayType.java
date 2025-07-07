package com.funchive.functionservice.function.model.common.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public final class ArrayType extends Type {
    private Type elementType;

    public ArrayType(Type elementType) {
        super(EType.ARRAY.name());
        this.elementType = elementType;
    }
}
