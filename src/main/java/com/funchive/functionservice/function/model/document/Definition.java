package com.funchive.functionservice.function.model.document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Definition {
    private String name;
    private String description;
    private Type inputType;
    private Type outputType;
}
