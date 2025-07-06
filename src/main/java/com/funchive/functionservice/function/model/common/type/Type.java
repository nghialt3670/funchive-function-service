package com.funchive.functionservice.function.model.common.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.funchive.functionservice.function.model.common.value.Value;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "name",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ArrayType.class, name = "ARRAY"),
        @JsonSubTypes.Type(value = BooleanType.class, name = "BOOLEAN"),
        @JsonSubTypes.Type(value = FileType.class, name = "FILE"),
        @JsonSubTypes.Type(value = NumberType.class, name = "NUMBER"),
        @JsonSubTypes.Type(value = ObjectType.class, name = "OBJECT"),
        @JsonSubTypes.Type(value = StringType.class, name = "STRING"),
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Type {
    @JsonIgnore
    private String name;
    private String description;
    private Value<?> defaultValue;
    private boolean useDefaultValue;

    public Type(String name) {
        this.name = name;
    }
}
