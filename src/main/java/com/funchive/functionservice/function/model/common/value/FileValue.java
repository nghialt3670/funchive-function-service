package com.funchive.functionservice.function.model.common.value;

import com.funchive.functionservice.function.model.common.type.EType;
import lombok.*;

@Getter
@Setter
public final class FileValue extends LoadableValue<FileMetadata> {
    public FileValue() {
        super(EType.FILE.name());
    }

    public FileValue(FileMetadata data) {
        super(EType.FILE.name(), data);
    }
    public FileValue(FileMetadata data, String id) {
        super(EType.FILE.name(), data, id);
    }
} 