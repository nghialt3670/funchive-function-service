package com.funchive.functionservice.function.model.dto.implementation.java;

import com.funchive.functionservice.function.model.dto.implementation.ImplementationUpdate;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class JavaImplementationUpdate extends ImplementationUpdate {
    private String version;
    private List<String> imports = new ArrayList<>();
    private List<Dependency> dependencies = new ArrayList<>();
    private String functionBody;

    @Data
    public static class Dependency {
        private String groupId;
        private String artifactId;
        private String version;
    }
} 