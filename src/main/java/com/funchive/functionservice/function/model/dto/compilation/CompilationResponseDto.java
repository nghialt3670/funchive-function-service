package com.funchive.functionservice.function.model.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationResponseDto {
    private String websocket;
    private Instant timestamp;
}
