package com.funchive.functionservice.pipeline.model.dto;

import com.funchive.functionservice.function.model.common.value.Value;
import lombok.Data;

import java.util.Map;

@Data
public class PipelineExecutionTriggerDto {
    private Map<String, Value<?>> inputs; // Map of node IDs to their input values
} 