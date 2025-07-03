package com.funchive.functionservice.function.model.common.value;

import com.funchive.functionservice.function.model.common.type.EType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class BooleanValue extends Value<Boolean> {
    public BooleanValue(Boolean data) {
        super(EType.BOOLEAN.name(), data);
    }
} 