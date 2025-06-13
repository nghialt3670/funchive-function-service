package com.funchive.functionservice.function.model.document.value;

import com.funchive.functionservice.function.model.document.Value;
import com.funchive.functionservice.function.model.document.type.BooleanType;
import lombok.Getter;

@Getter
public final class BooleanValue extends Value<Boolean> {
    public BooleanValue(Boolean data) {
        super("BOOLEAN", data);
    }
} 