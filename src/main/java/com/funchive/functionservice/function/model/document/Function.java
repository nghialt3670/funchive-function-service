package com.funchive.functionservice.function.model.document;

import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document("users")
public class Function {
    @Id
    private String id;

    private Definition definition;
    private Implementation implementation;
    private CompilationStatus compilationStatus;

    @CreatedBy
    private String createdBy;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedBy
    private String updatedBy;
    @LastModifiedDate
    private Instant updatedAt;
}
