package com.funchive.functionservice.function.model.document.value;

import com.funchive.functionservice.function.model.document.Value;
import lombok.Getter;

@Getter
public final class NumberValue extends Value<Double> {
    public NumberValue(Double data) {
        super("NUMBER", data);
    }
} 