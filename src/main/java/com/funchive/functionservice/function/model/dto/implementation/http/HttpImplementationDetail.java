package com.funchive.functionservice.function.model.dto.implementation.http;

import com.funchive.functionservice.function.model.dto.implementation.ImplementationDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class HttpImplementationDetail extends ImplementationDetail {
    private String endpoint;
    private String method;
    private Map<String, String> headers;
    private String accessPath;
} 