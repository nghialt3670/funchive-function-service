package com.funchive.functionservice.pipeline.model.document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FunctionNode extends Node {
    private String functionId;
    
    public FunctionNode(String id, String name, Position position, String functionId) {
        super(id, "FUNCTION", name, position);
        this.functionId = functionId;
    }
} 