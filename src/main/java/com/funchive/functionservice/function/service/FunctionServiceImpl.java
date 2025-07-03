package com.funchive.functionservice.function.service;

import com.funchive.functionservice.function.FunctionService;
import com.funchive.functionservice.function.exception.*;
import com.funchive.functionservice.function.model.dao.function.Function;
import com.funchive.functionservice.function.model.dao.implementation.*;
import com.funchive.functionservice.function.model.dto.function.*;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationCreate;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationDetail;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationFilter;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationUpdate;
import com.funchive.functionservice.function.model.dto.message.CompilationRequestMessage;
import com.funchive.functionservice.function.model.dto.message.ExecutionRequestMessage;
import com.funchive.functionservice.function.repository.FunctionRepository;
import com.funchive.functionservice.function.repository.ImplementationRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FunctionServiceImpl implements FunctionService {

    private final FunctionRepository functionRepository;
    private final ImplementationRepository implementationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public FunctionDetail createFunction(@NotNull FunctionCreate functionCreate) {
        Function function = modelMapper.map(functionCreate, Function.class);
        Function savedFunction = functionRepository.save(function);
        return modelMapper.map(savedFunction, FunctionDetail.class);
    }

    @Override
    @Transactional(readOnly = true)
    public FunctionDetail getFunctionDetail(String functionId) {
        Function function = findFunctionById(functionId);
        return modelMapper.map(function, FunctionDetail.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FunctionDetail> getFunctionPage(FunctionFilter functionFilter, Pageable pageable) {
        // TODO: Implement filter criteria using QueryDSL or Criteria API
        Page<Function> functionPage = functionRepository.findAll(pageable);
        return functionPage.map(function -> modelMapper.map(function, FunctionDetail.class));
    }

    @Override
    @Transactional
    public FunctionDetail updateFunction(String functionId, @NotNull FunctionUpdate functionUpdate) {
        Function function = findFunctionById(functionId);
        modelMapper.map(functionUpdate, function);
        return modelMapper.map(function, FunctionDetail.class);
    }

    @Override
    @Transactional
    public void deleteFunction(String functionId) {
        functionRepository.delete(findFunctionById(functionId));
    }

    @Override
    @Transactional
    public ImplementationDetail createImplementation(String functionId, ImplementationCreate implementationCreate) {
        Implementation impl = modelMapper.map(implementationCreate, Implementation.class);
        Implementation savedImpl = implementationRepository.save(impl);
        return modelMapper.map(savedImpl, ImplementationDetail.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ImplementationDetail getImplementationDetail(String functionId, String implementationId) {
        Implementation impl = findImplementationByIdAndFunctionId(implementationId, functionId);
        return modelMapper.map(impl, ImplementationDetail.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ImplementationDetail> getImplementationPage(
            String functionId,
            ImplementationFilter implementationFilter,
            Pageable pageable
    ) {
        // TODO: Implement filter criteria using QueryDSL or Criteria API
        Page<Implementation> implPage = implementationRepository.findAll(pageable);
        return implPage.map(impl -> modelMapper.map(impl, ImplementationDetail.class));
    }

    @Override
    @Transactional
    public ImplementationDetail updateImplementation(String functionId, String implementationId, ImplementationUpdate implementationUpdate) {
        Implementation impl = findImplementationByIdAndFunctionId(implementationId, functionId);

        if (!implementationUpdate.getType().equals(impl.getType().name())) {
            throw new ImplementationNotMatchException(impl.getType().name(), implementationUpdate.getType());
        }

        modelMapper.map(implementationUpdate, impl);
        Implementation savedImpl = implementationRepository.save(impl);

        return modelMapper.map(savedImpl, ImplementationDetail.class);
    }

    @Override
    @Transactional
    public void deleteImplementation(String functionId, String implementationId) {
        Implementation impl = findImplementationByIdAndFunctionId(implementationId, functionId);
        implementationRepository.delete(impl);
    }

    @Override
    @Transactional
    public void compileFunction(String functionId, String implementationId) {
        Implementation implementation = findImplementationByIdAndFunctionId(implementationId, functionId);

        if (implementation instanceof CompilableImplementation compilableImplementation) {
            if (compilableImplementation.getCompilationStatus().equals(CompilationStatus.COMPILED)) {
                throw new ImplementationNotCompilableException(implementationId);
            }
        } else {
            throw new ImplementationNotCompilableException(implementationId);
        }

        CompilationRequestMessage message = CompilationRequestMessage.builder()
                .functionId(functionId)
                .implementationId(implementationId)
                .timestamp(Instant.now())
                .build();

        kafkaTemplate.send("function.compile.request", message);
    }

    @Override
    @Transactional
    public void executeFunction(String functionId, String implementationId, String inputValueId) {
        Implementation implementation = findImplementationByIdAndFunctionId(implementationId, functionId);

        if (implementation instanceof CompilableImplementation compilableImplementation) {
            if (!compilableImplementation.getCompilationStatus().equals(CompilationStatus.COMPILED)) {
                throw new ImplementationNotCompilableException(implementationId);
            }
        }

        ExecutionRequestMessage message = ExecutionRequestMessage.builder()
                .functionId(functionId)
                .implementationId(implementationId)
                .inputValueId(inputValueId)
                .timestamp(Instant.now())
                .build();

        kafkaTemplate.send("function.execute.request", message);
    }

    private Function findFunctionById(String id) {
        return functionRepository.findById(id)
                .orElseThrow(() -> new FunctionNotFoundException(id));
    }

    private Implementation findImplementationByIdAndFunctionId(String implementationId, String functionId) {
        Implementation implementation = implementationRepository.findById(implementationId)
                .orElseThrow(() -> new ImplementationNotFoundException(implementationId));

        if (!implementation.getFunctionId().equals(functionId)) {
            throw new ImplementationNotFoundException(implementationId, functionId);
        }

        return implementation;
    }
}
