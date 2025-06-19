package com.funchive.functionservice.pipeline.model.dto;

import com.funchive.functionservice.pipeline.model.document.Connection;
import com.funchive.functionservice.pipeline.model.document.Node;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class PipelineDetailDto {
    private String id;
    private String name;
    private String description;
    private List<Node> nodes;
    private List<Connection> connections;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
} 