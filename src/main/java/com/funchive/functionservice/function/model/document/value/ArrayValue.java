package com.funchive.functionservice.function.model.document.value;

import com.funchive.functionservice.function.model.document.Value;
import lombok.Getter;

import java.util.List;

@Getter
public final class ArrayValue extends Value<List<Value<?>>> {
    public ArrayValue(List<Value<?>> data) {
        super("ARRAY", data);
    }
} 