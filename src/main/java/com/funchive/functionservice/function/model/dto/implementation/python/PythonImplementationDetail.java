package com.funchive.functionservice.function.model.dto.implementation.python;

import com.funchive.functionservice.function.model.dao.implementation.CompilationStatus;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PythonImplementationDetail extends ImplementationDetail {
    private String version;
    private List<Import> imports = new ArrayList<>();
    private List<Package> packages = new ArrayList<>();
    private String functionBody;
    private CompilationStatus compilationStatus;

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