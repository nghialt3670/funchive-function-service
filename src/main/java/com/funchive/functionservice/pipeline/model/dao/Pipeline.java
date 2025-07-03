package com.funchive.functionservice.pipeline.model.dao;

import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document("pipelines")
public class Pipeline {
    @Id
    private String id;

    private String name;
    private String description;
    private List<Node> nodes;
    private List<Connection> connections;

    @CreatedBy
    private String createdBy;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedBy
    private String updatedBy;
    @LastModifiedDate
    private Instant updatedAt;
} 