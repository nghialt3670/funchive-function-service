package com.funchive.functionservice.function.model.document.type;

import com.funchive.functionservice.function.model.document.Type;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public final class ObjectType extends Type {
    private Map<String, Type> schema;

    public ObjectType(Map<String, Type> schema) {
        super("OBJECT");
        this.schema = schema;
    }
}
