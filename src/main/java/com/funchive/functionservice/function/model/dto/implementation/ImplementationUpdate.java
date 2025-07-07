package com.funchive.functionservice.function.model.dto.implementation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.funchive.functionservice.function.model.dto.implementation.http.HttpImplementationUpdate;
import com.funchive.functionservice.function.model.dto.implementation.java.JavaImplementationUpdate;
import com.funchive.functionservice.function.model.dto.implementation.python.PythonImplementationUpdate;
import jakarta.annotation.Nonnull;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PythonImplementationUpdate.class, name = "PYTHON"),
        @JsonSubTypes.Type(value = JavaImplementationUpdate.class, name = "JAVA"),
        @JsonSubTypes.Type(value = HttpImplementationUpdate.class, name = "HTTP")
})
@Data
@NoArgsConstructor
public class ImplementationUpdate {
    @Nonnull
    protected String type;
    @Nonnull
    protected String name;
    @Nonnull
    protected String description;
} 