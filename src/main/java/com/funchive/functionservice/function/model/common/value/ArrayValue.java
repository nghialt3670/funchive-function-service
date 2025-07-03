package com.funchive.functionservice.function.model.common.value;

import com.funchive.functionservice.function.model.common.type.EType;
import lombok.Getter;

import java.util.List;

@Getter
public final class ArrayValue extends Value<List<Value<?>>> {
    public ArrayValue(List<Value<?>> data) {
        super(EType.ARRAY.name(), data);
    }
} 