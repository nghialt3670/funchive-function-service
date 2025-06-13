package com.funchive.functionservice.function;

import com.funchive.functionservice.function.model.dto.FileDto;

public interface ExecutableStorage
{
    void storeExecutable(String fileId, FileDto fileDto);
    FileDto loadExecutable(String fileId);
    void deleteExecutable(String fileId);
}
