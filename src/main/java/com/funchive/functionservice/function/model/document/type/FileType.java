package com.funchive.functionservice.function.model.document.type;

import com.funchive.functionservice.function.model.document.Type;
import lombok.Getter;

@Getter
public final class FileType extends Type {
    private final String extension;

    public FileType(String extension) {
        super("FILE");
        this.extension = extension;
    }
}
