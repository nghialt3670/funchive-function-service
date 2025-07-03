package com.funchive.functionservice.function.model.common.type;

import lombok.Getter;

@Getter
public final class StringType extends Type {
    public StringType() {
        super(EType.STRING.name());
    }
}
