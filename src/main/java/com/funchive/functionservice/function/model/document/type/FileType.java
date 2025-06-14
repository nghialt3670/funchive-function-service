package com.funchive.functionservice.function.model.document.type;

import com.funchive.functionservice.function.model.document.Type;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class FileType extends Type {
    private String extension;

    public FileType(String extension) {
        super("FILE");
        this.extension = extension;
    }
}
