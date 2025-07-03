package com.funchive.functionservice.function.model.common.value;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadableValue<T> extends Value<T>{
    protected String id;

    public LoadableValue(String type, T data) {
        super(type, data);
    }

    public LoadableValue(String type, T data, String id) {
        super(type, data);
        this.id = id;
    }
}
