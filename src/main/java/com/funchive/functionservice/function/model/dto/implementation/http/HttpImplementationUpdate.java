package com.funchive.functionservice.function.model.dto.implementation.http;

import com.funchive.functionservice.function.model.dto.implementation.ImplementationUpdate;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class HttpImplementationUpdate extends ImplementationUpdate {
    private String endpoint;
    private String method;
    private Map<String, String> headers;
    private String accessPath;
}