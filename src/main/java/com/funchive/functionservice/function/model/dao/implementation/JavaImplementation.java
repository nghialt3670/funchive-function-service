package com.funchive.functionservice.function.model.dao.implementation;

import jakarta.annotation.Nonnull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class JavaImplementation extends CompilableImplementation {
    @Nonnull
    private String version;
    private List<String> imports = new ArrayList<>();
    private List<Dependency> dependencies = new ArrayList<>();
    @Nonnull
    private String functionBody;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dependency {
        private String groupId;
        private String artifactId;
        private String version;
    }
}