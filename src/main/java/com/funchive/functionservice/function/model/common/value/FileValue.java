package com.funchive.functionservice.function.model.common.value;

import com.funchive.functionservice.function.model.common.type.EType;
import lombok.Getter;

@Getter
public final class FileValue extends Value<FileMetadata> {
    public FileValue(FileMetadata data) {
        super(EType.FILE.name(), data);
    }
} 