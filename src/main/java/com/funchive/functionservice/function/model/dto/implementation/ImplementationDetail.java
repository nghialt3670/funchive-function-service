package com.funchive.functionservice.function.model.dto.implementation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.funchive.functionservice.function.model.dto.implementation.http.HttpImplementationDetail;
import com.funchive.functionservice.function.model.dto.implementation.java.JavaImplementationDetail;
import com.funchive.functionservice.function.model.dto.implementation.python.PythonImplementationDetail;
import lombok.Data;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PythonImplementationDetail.class, name = "PYTHON"),
        @JsonSubTypes.Type(value = JavaImplementationDetail.class, name = "JAVA"),
        @JsonSubTypes.Type(value = HttpImplementationDetail.class, name = "HTTP")
})
@Data
public abstract class ImplementationDetail {
    protected String id;
    protected String type;
    protected String name;
    protected String createdBy;
    protected Instant createdAt;
    protected String updatedBy;
    protected Instant updatedAt;
} 