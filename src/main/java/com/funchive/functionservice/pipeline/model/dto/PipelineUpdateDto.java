package com.funchive.functionservice.pipeline.model.dto;

import com.funchive.functionservice.pipeline.model.document.Connection;
import com.funchive.functionservice.pipeline.model.document.Node;
import lombok.Data;

import java.util.List;

@Data
public class PipelineUpdateDto {
    private String name;
    private String description;
    private List<Node> nodes;
    private List<Connection> connections;
} 