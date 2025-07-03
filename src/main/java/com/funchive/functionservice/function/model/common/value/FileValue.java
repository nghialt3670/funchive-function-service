package com.funchive.functionservice.function.model.common.value;

import com.funchive.functionservice.function.model.common.type.EType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class FileValue extends LoadableValue<FileMetadata> {
    public FileValue(FileMetadata data, String id) {
        super(EType.FILE.name(), data, id);
    }
} 