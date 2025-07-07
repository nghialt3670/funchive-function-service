package com.funchive.functionservice.function.model.dto.implementation.python;

import com.funchive.functionservice.function.model.dao.implementation.python.PythonImport;
import com.funchive.functionservice.function.model.dao.implementation.python.PythonPackage;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationUpdate;
import jakarta.annotation.Nonnull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PythonImplementationUpdate extends ImplementationUpdate {
    @Nonnull
    private String version;
    private List<PythonImport> imports = new ArrayList<>();
    private List<PythonPackage> packages = new ArrayList<>();
    @Nonnull
    private String functionBody;
} 