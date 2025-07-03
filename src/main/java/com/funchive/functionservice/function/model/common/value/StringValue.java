package com.funchive.functionservice.function.model.common.value;

import com.funchive.functionservice.function.model.common.type.EType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class StringValue extends LoadableValue<String> {
    public StringValue(String data) {
        super(EType.STRING.name(), data);
    }

    public StringValue(String data, String id) {
        super(EType.STRING.name(), data, id);
    }
} 