package com.funchive.functionservice.pipeline.model.dao;

import com.funchive.functionservice.function.model.common.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValueNode extends Node {
    private Value<?> value;
} 