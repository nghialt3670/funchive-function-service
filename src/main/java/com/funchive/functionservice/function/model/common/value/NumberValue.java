package com.funchive.functionservice.function.model.common.value;

import com.funchive.functionservice.function.model.common.type.EType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class NumberValue extends Value<Double> {
    public NumberValue(Double data) {
        super(EType.NUMBER.name(), data);
    }
} 