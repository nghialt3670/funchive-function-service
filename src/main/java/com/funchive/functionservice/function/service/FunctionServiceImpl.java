package com.funchive.functionservice.function.service;

import com.funchive.functionservice.function.FunctionRepository;
import com.funchive.functionservice.function.FunctionService;
import com.funchive.functionservice.function.exception.FunctionNotFoundException;
import com.funchive.functionservice.function.model.document.CompilationStatus;
import com.funchive.functionservice.function.model.document.Function;
import com.funchive.functionservice.function.model.dto.FunctionCreateDto;
import com.funchive.functionservice.function.model.dto.FunctionDetailDto;
import com.funchive.functionservice.function.model.dto.FunctionFilter;
import com.funchive.functionservice.function.model.dto.FunctionUpdateDto;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FunctionServiceImpl implements FunctionService {
    @Autowired
    private final FunctionRepository functionRepository;

    @Override
    @Transactional
    public FunctionDetailDto createFunction(FunctionCreateDto functionCreateDto) {
        var function = new Function();

        function.setDefinition(functionCreateDto.getDefinition());
        function.setImplementation(functionCreateDto.getImplementation());
        function.setCompilationStatus(CompilationStatus.NOT_STARTED);

        var createdFunction = functionRepository.save(function);

        return toFunctionDetailDto(createdFunction);
    }

    @Override
    @Transactional(readOnly = true)
    public FunctionDetailDto getFunctionDetail(String functionId) {
        var function = findFunctionById(functionId);

        return toFunctionDetailDto(function);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FunctionDetailDto> getFunctionPage(FunctionFilter functionFilter, Pageable pageable) {
        // TODO: Implement filter criteria using QueryDSL or Criteria API
        Page<Function> functionPage = functionRepository.findAll(pageable);
        return functionPage.map(this::toFunctionDetailDto);
    }

    @Override
    @Transactional
    public FunctionDetailDto updateFunction(String functionId, @NotNull FunctionUpdateDto functionUpdateDto) {
        var function = findFunctionById(functionId);

        var definitionUpdateDto = functionUpdateDto.getDefinition();
        var definition = function.getDefinition();

        if (definitionUpdateDto.getName() != null) {
            definition.setName(definitionUpdateDto.getName());
        }
        if (definitionUpdateDto.getDescription() != null) {
            definition.setDescription(definitionUpdateDto.getDescription());
        }
        if (definitionUpdateDto.getInputType() != null) {
            definition.setInputType(definitionUpdateDto.getInputType());
        }
        if (definitionUpdateDto.getOutputType() != null) {
            definition.setOutputType(definitionUpdateDto.getOutputType());
        }

        var implementationUpdateDto = functionUpdateDto.getImplementation();
        var implementation = function.getImplementation();

        if (implementationUpdateDto.getLanguage() != null) {
            implementation.setLanguage(implementationUpdateDto.getLanguage());
        }
        if (implementationUpdateDto.getCode() != null) {
            implementation.setCode(implementationUpdateDto.getCode());
        }

        function.setDefinition(definition);
        function.setImplementation(implementation);
        function.setCompilationStatus(CompilationStatus.OUTDATED);

        var updatedFunction = functionRepository.save(function);

        return toFunctionDetailDto(updatedFunction);
    }

    @Override
    @Transactional
    public FunctionDetailDto deleteFunction(String functionId) {
        var function = findFunctionById(functionId);
        functionRepository.delete(function);

        return toFunctionDetailDto(function);
    }

    @Override
    @Transactional
    public FunctionDetailDto updateCompilationStatus(String functionId, CompilationStatus compilationStatus) {
        var function = findFunctionById(functionId);
        function.setCompilationStatus(compilationStatus);
        functionRepository.save(function);

        return toFunctionDetailDto(function);
    }

    private Function findFunctionById(String id) {
        return functionRepository.findById(id)
                .orElseThrow(() -> new FunctionNotFoundException(id));
    }

    private FunctionDetailDto toFunctionDetailDto(Function function) {
        var functionDetailDto = new FunctionDetailDto();

        functionDetailDto.setId(function.getId());
        functionDetailDto.setDefinition(function.getDefinition());
        functionDetailDto.setImplementation(function.getImplementation());
        functionDetailDto.setCompilationStatus(function.getCompilationStatus());
        functionDetailDto.setCreatedBy(function.getCreatedBy());
        functionDetailDto.setCreatedAt(function.getCreatedAt());
        functionDetailDto.setUpdatedBy(function.getUpdatedBy());
        functionDetailDto.setUpdatedAt(function.getUpdatedAt());

        return functionDetailDto;
    }
}
