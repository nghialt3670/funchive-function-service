package com.funchive.functionservice.function.model.dto;

import com.funchive.functionservice.function.model.document.CompilationStatus;
import com.funchive.functionservice.function.model.document.Definition;
import com.funchive.functionservice.function.model.document.Implementation;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class FunctionDetailDto {
    private String id;
    private Definition definition;
    private Implementation implementation;
    private CompilationStatus compilationStatus;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
