package com.funchive.functionservice.function.model.common.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public final class ObjectType extends Type {
    private Map<String, Type> schema;

    public ObjectType(Map<String, Type> schema) {
        super(EType.OBJECT.name());
        this.schema = schema;
    }
}
