package com.funchive.functionservice.function.model.document;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.funchive.functionservice.function.model.document.type.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonSubTypes({
        @JsonSubTypes.Type(value = ArrayType.class, name = "ARRAY"),
        @JsonSubTypes.Type(value = BooleanType.class, name = "BOOLEAN"),
        @JsonSubTypes.Type(value = FileType.class, name = "FILE"),
        @JsonSubTypes.Type(value = NumberType.class, name = "NUMBER"),
        @JsonSubTypes.Type(value = ObjectType.class, name = "OBJECT"),
        @JsonSubTypes.Type(value = StringType.class, name = "STRING"),
})
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "name",
        visible = true
)
@Data
public abstract class Type {
    private final String name;
    private final String description = "";
    private final Value<?> defaultValue = null;

    public Type(String name) {
        this.name = name;
    }
}
