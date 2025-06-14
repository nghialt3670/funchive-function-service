package com.funchive.functionservice.function.model.document.type;

import com.funchive.functionservice.function.model.document.Type;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class ArrayType extends Type {
    private Type elementType;

    public ArrayType(Type elementType) {
        super("ARRAY");
        this.elementType = elementType;
    }
}
