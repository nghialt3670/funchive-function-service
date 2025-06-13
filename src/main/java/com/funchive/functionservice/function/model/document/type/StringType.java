package com.funchive.functionservice.function.model.document.type;

import com.funchive.functionservice.function.model.document.Type;
import lombok.Getter;

@Getter
public final class StringType extends Type {
    public StringType() {
        super("STRING");
    }
}
