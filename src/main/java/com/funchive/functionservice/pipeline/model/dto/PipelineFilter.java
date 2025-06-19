package com.funchive.functionservice.pipeline.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PipelineFilter {
    private String keyword;
    private String createdBy;
} 