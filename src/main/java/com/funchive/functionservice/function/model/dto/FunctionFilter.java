package com.funchive.functionservice.function.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FunctionFilter {
    private String keyword;
    private String language;
}
