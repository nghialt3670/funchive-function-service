package com.funchive.functionservice.function.model.dao.function;

import com.funchive.functionservice.function.model.common.type.Type;
import jakarta.annotation.Nonnull;
import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document("functions")
public class Function {
    @Id
    private String id;
    @Nonnull
    private String name;
    @Nonnull
    private String description;
    @Nonnull
    private Type inputType;
    @Nonnull
    private Type outputType;
    @CreatedBy
    private String createdBy;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedBy
    private String updatedBy;
    @LastModifiedDate
    private Instant updatedAt;
}
