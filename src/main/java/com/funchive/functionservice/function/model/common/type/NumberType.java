package com.funchive.functionservice.function.model.common.type;

import lombok.Getter;

@Getter
public final class NumberType extends Type {
    public NumberType() {
        super(EType.NUMBER.name());
    }
}

