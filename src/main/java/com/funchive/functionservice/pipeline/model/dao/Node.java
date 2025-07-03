package com.funchive.functionservice.pipeline.model.dao;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonSubTypes({
        @JsonSubTypes.Type(value = ValueNode.class, name = "VALUE"),
        @JsonSubTypes.Type(value = FunctionNode.class, name = "FUNCTION")
})
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true
)
@Data
public abstract class Node {
    private String id;
    private String type;
    private String name;
    private Position position;
} 