package com.funchive.functionservice.function.model.document;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.funchive.functionservice.function.model.document.implementation.PythonImplementation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "dockersandboxstrategy",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PythonImplementation.class, name = "python")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Implementation {
    protected String language;
    protected String code;
}
