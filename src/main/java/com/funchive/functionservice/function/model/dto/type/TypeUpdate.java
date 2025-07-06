package com.funchive.functionservice.function.model.dto.type;

import com.funchive.functionservice.function.model.common.value.Value;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "name",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ArrayTypeUpdate.class, name = "ARRAY"),
        @JsonSubTypes.Type(value = ObjectTypeUpdate.class, name = "OBJECT"),
        @JsonSubTypes.Type(value = TypeUpdate.class, names = {"BOOLEAN", "NUMBER", "STRING", "FILE"})
})
@Data
public class TypeUpdate {
    @JsonIgnore(false)
    private String name;
    private String description;
    private Value<?> defaultValue;
    private boolean useDefaultValue;
}
