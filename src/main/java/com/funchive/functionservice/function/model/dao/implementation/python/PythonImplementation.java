package com.funchive.functionservice.function.model.dao.implementation.python;

import com.funchive.functionservice.function.model.dao.implementation.CompilableImplementation;
import jakarta.annotation.Nonnull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PythonImplementation extends CompilableImplementation {
    @Nonnull
    private String version;
    @Nonnull
    private List<PythonImport> imports;
    @Nonnull
    private List<PythonPackage> packages;
    @Nonnull
    private String functionBody;
} 