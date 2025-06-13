package com.funchive.functionservice.function.model.document.type;

import com.funchive.functionservice.function.model.document.Type;
import lombok.Getter;

import java.util.Map;

@Getter
public final class ObjectType extends Type {
    private final Map<String, Type> schema;

    public ObjectType(Map<String, Type> schema) {
        super("OBJECT");
        this.schema = schema;
    }
}
