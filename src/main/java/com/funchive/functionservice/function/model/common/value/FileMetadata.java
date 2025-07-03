package com.funchive.functionservice.function.model.common.value;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FileMetadata {
    private String fileId;
    private String filename;
    private String mimeType;
}
