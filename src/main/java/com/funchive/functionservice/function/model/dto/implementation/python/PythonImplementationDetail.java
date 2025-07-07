package com.funchive.functionservice.function.model.dto.implementation.python;

import com.funchive.functionservice.function.model.dao.implementation.CompilationStatus;
import com.funchive.functionservice.function.model.dao.implementation.python.PythonImport;
import com.funchive.functionservice.function.model.dao.implementation.python.PythonPackage;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PythonImplementationDetail extends ImplementationDetail {
    private String version;
    private List<PythonImport> imports = new ArrayList<>();
    private List<PythonPackage> packages = new ArrayList<>();
    private String functionBody;
    private CompilationStatus compilationStatus;
} 