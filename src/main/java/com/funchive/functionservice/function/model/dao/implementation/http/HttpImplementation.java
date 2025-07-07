package com.funchive.functionservice.function.model.dao.implementation.http;

import com.funchive.functionservice.function.model.dao.implementation.Implementation;
import jakarta.annotation.Nonnull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class HttpImplementation extends Implementation {
    @Nonnull
    private String endpoint;
    @Nonnull
    private HttpMethod method;
    private Map<String, String> headers;
    private String accessPath;
}