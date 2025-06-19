package com.funchive.functionservice.pipeline.model.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Connection {
    private String id;
    private String sourceNodeId;
    private String targetNodeId;
    private String sourcePort; // For functions, this could be the output parameter name
    private String targetPort; // For functions, this could be the input parameter name or object field path
} 