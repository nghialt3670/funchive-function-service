package com.funchive.functionservice.function.model.dao.implementation;

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
    private List<Import> imports = new ArrayList<>();
    private List<Package> packages = new ArrayList<>();
    @Nonnull
    private String functionBody;

    @Data
    public static class Package {
        private String name;
        private String version;
    }

    @Data
    public static class Import {
        private String source;
        private String target;
    }
} 