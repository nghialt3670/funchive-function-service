package com.funchive.functionservice.function.model.common.value;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileMetadata {
    private String filename;
    private String mimeType;
}
