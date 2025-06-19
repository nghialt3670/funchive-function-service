package com.funchive.functionservice.pipeline.model.document;

import com.funchive.functionservice.function.model.document.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ValueNode extends Node {
    private Value<?> value;
    
    public ValueNode(String id, String name, Position position, Value<?> value) {
        super(id, "VALUE", name, position);
        this.value = value;
    }
} 