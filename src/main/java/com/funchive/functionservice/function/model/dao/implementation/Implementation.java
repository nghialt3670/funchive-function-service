package com.funchive.functionservice.function.model.dao.implementation;

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
public abstract class Implementation {
    protected ImplementationType type;
    protected String name;
    @Id
    private String id;
    private String functionId;
    @CreatedBy
    private String createdBy;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedBy
    private String updatedBy;
    @LastModifiedDate
    private Instant updatedAt;
}
