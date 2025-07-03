package com.funchive.functionservice.function.model.dto.implementation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.funchive.functionservice.function.model.dto.implementation.http.HttpImplementationCreate;
import com.funchive.functionservice.function.model.dto.implementation.java.JavaImplementationCreate;
import com.funchive.functionservice.function.model.dto.implementation.python.PythonImplementationCreate;
import lombok.Data;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PythonImplementationCreate.class, name = "PYTHON"),
        @JsonSubTypes.Type(value = JavaImplementationCreate.class, name = "JAVA"),
        @JsonSubTypes.Type(value = HttpImplementationCreate.class, name = "HTTP")
})
@Data
public abstract class ImplementationCreate {
    protected String type;
    protected String name;
    protected String description;
}