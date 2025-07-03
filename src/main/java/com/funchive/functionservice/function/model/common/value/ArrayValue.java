package com.funchive.functionservice.function.model.common.value;

import com.funchive.functionservice.function.model.common.type.EType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public final class ArrayValue extends LoadableValue<List<Value<?>>> {
    public ArrayValue(List<Value<?>> data) {
        super(EType.ARRAY.name(), data);
    }

    public ArrayValue(List<Value<?>> data, String id) {
        super(EType.ARRAY.name(), data, id);
    }
} 