package com.funchive.functionservice.function.model.dto;

import com.funchive.functionservice.function.model.document.Definition;
import com.funchive.functionservice.function.model.document.Implementation;
import lombok.Data;

@Data
public class FunctionUpdateDto {
    private Definition definition;
    private Implementation implementation;
}
