package com.funchive.functionservice.function.model.dto.implementation.java;

import com.funchive.functionservice.function.model.dao.implementation.CompilationStatus;
import com.funchive.functionservice.function.model.dao.implementation.java.JavaDependency;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class JavaImplementationDetail extends ImplementationDetail {
    private String version;
    private List<String> imports = new ArrayList<>();
    private List<JavaDependency> dependencies = new ArrayList<>();
    private String functionBody;
    private CompilationStatus compilationStatus;
} 