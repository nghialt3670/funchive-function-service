package com.funchive.functionservice.function.model.common.value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "typeName",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ArrayValue.class, name = "ARRAY"),
        @JsonSubTypes.Type(value = BooleanValue.class, name = "BOOLEAN"),
        @JsonSubTypes.Type(value = FileValue.class, name = "FILE"),
        @JsonSubTypes.Type(value = NumberValue.class, name = "NUMBER"),
        @JsonSubTypes.Type(value = ObjectValue.class, name = "OBJECT"),
        @JsonSubTypes.Type(value = StringValue.class, name = "STRING"),
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Value<T> {
    @JsonIgnore
    private String typeName;
    private T data;

    public Value(String typeName) {
        this.typeName = typeName;
    }
}
