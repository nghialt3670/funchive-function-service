package com.funchive.functionservice.function.model.document.type;

import com.funchive.functionservice.function.model.document.Type;
import lombok.Getter;

@Getter
public final class BooleanType extends Type {
    public BooleanType() {
        super("BOOLEAN");
    }
}
