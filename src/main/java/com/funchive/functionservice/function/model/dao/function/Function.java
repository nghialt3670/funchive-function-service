package com.funchive.functionservice.function.model.dao.function;

import com.funchive.functionservice.function.model.common.type.Type;
import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document("functions")
public class Function {
    @Id
    private String id;

    private String name;
    private String description;
    private Type inputType;
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
