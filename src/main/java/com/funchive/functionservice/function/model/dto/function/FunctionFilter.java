package com.funchive.functionservice.function.model.dto.function;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FunctionFilter {
    private String keyword;
    private String language;
}
