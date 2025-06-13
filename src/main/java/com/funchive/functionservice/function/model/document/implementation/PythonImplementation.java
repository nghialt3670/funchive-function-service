package com.funchive.functionservice.function.model.document.implementation;

import com.funchive.functionservice.function.model.document.Implementation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PythonImplementation extends Implementation {
    private String version;
    private List<Import> imports = new ArrayList<>();
    private List<Package> packages = new ArrayList<>();

    public PythonImplementation(String code) {
        super("python", code);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Package {
        private String name;
        private String version;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Import {
        private String source;
        private String target;
    }
} 