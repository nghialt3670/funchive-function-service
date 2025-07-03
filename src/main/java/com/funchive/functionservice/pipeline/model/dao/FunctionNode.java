package com.funchive.functionservice.pipeline.model.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FunctionNode extends Node {
    private String functionId;
    private String implementationId;
} 