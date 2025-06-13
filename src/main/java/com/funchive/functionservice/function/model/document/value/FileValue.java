package com.funchive.functionservice.function.model.document.value;

import com.funchive.functionservice.function.model.document.FileMetadata;
import com.funchive.functionservice.function.model.document.Value;
import lombok.Getter;

@Getter
public final class FileValue extends Value<FileMetadata> {
    public FileValue(FileMetadata data) {
        super("FILE", data);
    }
} 