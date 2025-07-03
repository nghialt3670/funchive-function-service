package com.funchive.functionservice.function.model.common.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class FileType extends Type {
    private String extension;

    public FileType(String extension) {
        super(EType.FILE.name());
        this.extension = extension;
    }
}
