package com.funchive.functionservice.function.model.common.value;

import com.funchive.functionservice.function.model.common.type.EType;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public final class ObjectValue extends LoadableValue<Map<String, Value<?>>> {
    public ObjectValue(Map<String, Value<?>> data) {
        super(EType.OBJECT.name(), data);
    }

    public ObjectValue(Map<String, Value<?>> data, String id) {
        super(EType.OBJECT.name(), data, id);
    }
} 