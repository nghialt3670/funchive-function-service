package com.funchive.functionservice.function.model.dto;

import com.funchive.functionservice.function.model.document.Definition;
import com.funchive.functionservice.function.model.document.Implementation;
import com.funchive.functionservice.function.model.document.Type;

import lombok.Data;

@Data
public class FunctionCreateDto {
    private Definition definition;
    private Implementation implementation;
}
