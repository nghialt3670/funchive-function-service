package com.funchive.functionservice.function.model.dto.implementation.http;

import com.funchive.functionservice.function.model.dto.implementation.ImplementationCreate;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class HttpImplementationCreate extends ImplementationCreate {
    private String endpoint;
    private String method;
    private Map<String, String> headers;
    private String accessPath;
}