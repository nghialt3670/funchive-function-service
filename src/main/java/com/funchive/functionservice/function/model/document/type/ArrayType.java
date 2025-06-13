package com.funchive.functionservice.function.model.document.type;

import com.funchive.functionservice.function.model.document.Type;
import lombok.Getter;

@Getter
public final class ArrayType extends Type {
    private final Type elementType;

    public ArrayType(Type elementType) {
        super("ARRAY");
        this.elementType = elementType;
    }
}
