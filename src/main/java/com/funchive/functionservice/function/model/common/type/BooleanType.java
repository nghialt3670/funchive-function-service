package com.funchive.functionservice.function.model.common.type;

import lombok.Getter;

@Getter
public final class BooleanType extends Type {
    public BooleanType() {
        super(EType.BOOLEAN.name());
    }
}
