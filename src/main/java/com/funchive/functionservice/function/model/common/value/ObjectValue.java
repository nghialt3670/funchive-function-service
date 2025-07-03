package com.funchive.functionservice.function.model.common.value;

import com.funchive.functionservice.function.model.common.type.EType;
import lombok.Getter;

import java.util.Map;

@Getter
public final class ObjectValue extends Value<Map<String, Value<?>>> {
    public ObjectValue(Map<String, Value<?>> data) {
        super(EType.OBJECT.name(), data);
    }
} 