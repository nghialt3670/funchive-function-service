package com.funchive.functionservice.function.model.dao.implementation.java;

import com.funchive.functionservice.function.model.dao.implementation.CompilableImplementation;
import jakarta.annotation.Nonnull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class JavaImplementation extends CompilableImplementation {
    @Nonnull
    private String version;
    @Nonnull
    private List<String> imports = new ArrayList<>();
    @Nonnull
    private List<JavaDependency> dependencies = new ArrayList<>();
    @Nonnull
    private String functionBody;
}