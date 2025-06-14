package com.funchive.functionservice.function.model.document.value;

import com.funchive.functionservice.function.model.document.Value;
import lombok.Getter;

@Getter
public final class StringValue extends Value<String> {
    public StringValue(String data) {
        super("STRING", data);
    }
} 