package com.funchive.functionservice.function.model.document.type;

import com.funchive.functionservice.function.model.document.Type;
import lombok.Getter;

@Getter
public final class NumberType extends Type {
    public NumberType() {
        super("NUMBER");
    }
}

