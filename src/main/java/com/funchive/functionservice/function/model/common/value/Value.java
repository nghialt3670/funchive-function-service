package com.funchive.functionservice.function.model.common.value;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonSubTypes({
        @JsonSubTypes.Type(value = ArrayValue.class, name = "ARRAY"),
        @JsonSubTypes.Type(value = BooleanValue.class, name = "BOOLEAN"),
        @JsonSubTypes.Type(value = FileValue.class, name = "FILE"),
        @JsonSubTypes.Type(value = NumberValue.class, name = "NUMBER"),
        @JsonSubTypes.Type(value = ObjectValue.class, name = "OBJECT"),
        @JsonSubTypes.Type(value = StringValue.class, name = "STRING"),
})
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true
)
@AllArgsConstructor
@Data
public abstract class Value<T> {
    private final String type;
    private final T data;
}
