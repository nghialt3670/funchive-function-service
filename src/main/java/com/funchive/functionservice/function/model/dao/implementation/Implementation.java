package com.funchive.functionservice.function.model.dao.implementation;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("implementations")
public class Implementation {
    @Id
    private String id;
    @Nonnull
    private String functionId;
    @Nonnull
    protected ImplementationType type;
    @Nonnull
    protected String name;
    @Nonnull
    protected String description;
    protected ExecutionConfig executionConfig;
    @CreatedBy
    private String createdBy;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedBy
    private String updatedBy;
    @LastModifiedDate
    private Instant updatedAt;
}
