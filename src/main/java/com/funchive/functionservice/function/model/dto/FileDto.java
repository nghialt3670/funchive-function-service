package com.funchive.functionservice.function.model.dto;

import lombok.Data;

import java.io.InputStream;

@Data
public class FileDto {
    private InputStream fileStream;
    private String filename;
    private String mimeType;
}
