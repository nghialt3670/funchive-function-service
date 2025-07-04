package com.funchive.functionservice.function.service;

import com.funchive.functionservice.function.FunctionService;
import com.funchive.functionservice.function.exception.*;
import com.funchive.functionservice.function.model.dao.function.Function;
import com.funchive.functionservice.function.model.dao.implementation.*;
import com.funchive.functionservice.function.model.dto.function.*;
import com.funchive.functionservice.function.model.dto.implementation.*;
import com.funchive.functionservice.function.model.dto.message.CompilationRequestMessage;
import com.funchive.functionservice.function.model.dto.message.ExecutionRequestMessage;
import com.funchive.functionservice.function.repository.FunctionRepository;
import com.funchive.functionservice.function.repository.ImplementationRepository;
import com.funchive.functionservice.pipeline.model.dao.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

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
        return toFunctionDetail(savedFunction);
    }

    @Override
    @Transactional(readOnly = true)
    public FunctionDetail getFunctionDetail(String functionId) {
        Function function = findFunctionById(functionId);
        return toFunctionDetail(function);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FunctionDetail> getFunctionPage(FunctionFilter functionFilter, Pageable pageable) {
        // TODO: Implement filter criteria using QueryDSL or Criteria API
        Page<Function> functionPage = functionRepository.findAll(pageable);
        return functionPage.map(this::toFunctionDetail);
    }

    @Override
    @Transactional
    public FunctionDetail updateFunction(String functionId, @NotNull FunctionUpdate functionUpdate) {
        Function function = findFunctionById(functionId);
        return toFunctionDetail(function);
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
        return toImplementationDetail(savedImpl);
    }

    @Override
    @Transactional(readOnly = true)
    public ImplementationDetail getImplementationDetail(String functionId, String implementationId) {
        Implementation impl = findImplementationByIdAndFunctionId(implementationId, functionId);
        return toImplementationDetail(impl);
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
        return implPage.map(this::toImplementationDetail);
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

        return toImplementationDetail(savedImpl);
    }

    @Override
    @Transactional
    public void deleteImplementation(String functionId, String implementationId) {
        Implementation impl = findImplementationByIdAndFunctionId(implementationId, functionId);
        implementationRepository.delete(impl);
    }

    @Override
    @Transactional
    public ImplementationDetail compileFunction(String functionId, String implementationId) {
        Implementation impl = findImplementationByIdAndFunctionId(implementationId, functionId);

        if (impl instanceof CompilableImplementation compilableImpl) {
            if (compilableImpl.getCompilationStatus().equals(CompilationStatus.COMPILED)) {
                throw new ImplementationNotCompilableException(implementationId);
            }

            CompilationRequestMessage message = CompilationRequestMessage.builder()
                    .functionId(functionId)
                    .implementationId(implementationId)
                    .timestamp(Instant.now())
                    .build();

            kafkaTemplate.send("function.compile.request", message);

            compilableImpl.setCompilationStatus(CompilationStatus.PENDING);
            Implementation savedImpl = implementationRepository.save(compilableImpl);

            return toImplementationDetail(savedImpl);
        } else {
            throw new ImplementationNotCompilableException(implementationId);
        }
    }

    @Override
    @Transactional
    public ImplementationDetail executeFunction(String functionId, String implementationId, String inputValueId) {
        Implementation impl = findImplementationByIdAndFunctionId(implementationId, functionId);

        if (impl instanceof CompilableImplementation compilableImpl) {
            if (!compilableImpl.getCompilationStatus().equals(CompilationStatus.COMPILED)) {
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

        return toImplementationDetail(impl);
    }

    private Function findFunctionById(String functionId) {
        return functionRepository.findById(functionId)
                .orElseThrow(() -> new FunctionNotFoundException(functionId));
    }

    private Implementation findImplementationByIdAndFunctionId(String implementationId, String functionId) {
        Implementation implementation = implementationRepository.findById(implementationId)
                .orElseThrow(() -> new ImplementationNotFoundException(implementationId));

        if (!implementation.getFunctionId().equals(functionId)) {
            throw new ImplementationNotFoundException(implementationId, functionId);
        }

        return implementation;
    }

    private FunctionDetail toFunctionDetail(Function function) {
        FunctionDetail functionDetail = modelMapper.map(function, FunctionDetail.class);
        List<Implementation> implementations = implementationRepository.findAllByFunctionId(function.getId());
        functionDetail.setImplementations(implementations.stream()
                .map(implementation -> modelMapper.map(implementation, ImplementationBasic.class))
                .toList());

        return functionDetail;
    }

    private ImplementationDetail toImplementationDetail(Implementation implementation) {
        return modelMapper.map(implementation, ImplementationDetail.class);
    }
}
