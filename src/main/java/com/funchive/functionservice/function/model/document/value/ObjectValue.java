package com.funchive.functionservice.function.model.document.value;

import com.funchive.functionservice.function.model.document.Value;
import lombok.Getter;

import java.util.Map;

@Getter
public final class ObjectValue extends Value<Map<String, Value<?>>> {
    public ObjectValue(Map<String, Value<?>> data) {
        super("OBJECT", data);
    }
} 