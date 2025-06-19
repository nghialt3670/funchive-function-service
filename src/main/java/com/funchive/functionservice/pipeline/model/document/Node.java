package com.funchive.functionservice.pipeline.model.document;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonSubTypes({
        @JsonSubTypes.Type(value = ValueNode.class, name = "VALUE"),
        @JsonSubTypes.Type(value = FunctionNode.class, name = "FUNCTION")
})
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "nodeType",
        visible = true
)
@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class Node {
    private String id;
    private String nodeType;
    private String name;
    private Position position;
} 