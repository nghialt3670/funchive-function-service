package com.funchive.functionservice.pipeline.model.dto;

import com.funchive.functionservice.pipeline.model.dao.Connection;
import com.funchive.functionservice.pipeline.model.dao.Node;
import lombok.Data;

import java.util.List;

@Data
public class PipelineUpdate {
    private String name;
    private String description;
    private List<Node> nodes;
    private List<Connection> connections;
} 