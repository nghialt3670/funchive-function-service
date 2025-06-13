package com.funchive.functionservice.function.model.document.value;

import com.funchive.functionservice.function.model.document.Value;
import com.funchive.functionservice.function.model.document.type.StringType;
import lombok.Getter;

@Getter
public final class StringValue extends Value<String> {
    public StringValue(String type, String data) {
        super("STRING", data);
    }
} 