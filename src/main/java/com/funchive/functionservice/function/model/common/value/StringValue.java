package com.funchive.functionservice.function.model.common.value;

import com.funchive.functionservice.function.model.common.type.EType;
import lombok.Getter;

@Getter
public final class StringValue extends Value<String> {
    public StringValue(String data) {
        super(EType.STRING.name(), data);
    }
} 