package com.funchive.functionservice.function.model.dto.implementation.python;

import com.funchive.functionservice.function.model.dto.implementation.ImplementationCreate;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PythonImplementationCreate extends ImplementationCreate {
    private String version;
    private List<Import> imports = new ArrayList<>();
    private List<Package> packages = new ArrayList<>();
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