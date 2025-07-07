package com.funchive.functionservice.function.service;

import com.funchive.functionservice.function.FunctionService;
import com.funchive.functionservice.function.exception.*;
import com.funchive.functionservice.function.model.dao.function.Function;
import com.funchive.functionservice.function.model.dao.implementation.*;
import com.funchive.functionservice.function.model.dto.function.*;
import com.funchive.functionservice.function.model.dto.implementation.*;
import com.funchive.functionservice.function.model.dto.message.CompilationRequestMessage;
import com.funchive.functionservice.function.model.dto.message.ExecutionRequestMessage;
import com.funchive.functionservice.function.model.mapper.FunctionMapper;
import com.funchive.functionservice.function.model.mapper.ImplementationMapper;
import com.funchive.functionservice.function.repository.FunctionRepository;
import com.funchive.functionservice.function.repository.ImplementationRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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

    private final FunctionRepository funcRepository;
    private final ImplementationRepository implRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FunctionMapper funcMapper;
    private final ImplementationMapper implMapper;

    @Override
    @Transactional
    public FunctionDetail createFunction(@NotNull FunctionCreate functionCreate) {
        Function func = funcMapper.toFunction(functionCreate);
        Function savedFunc = funcRepository.save(func);
        return toFunctionDetail(savedFunc);
    }

    @Override
    @Transactional(readOnly = true)
    public FunctionDetail getFunctionDetail(String functionId) {
        Function func = findFunctionById(functionId);
        return toFunctionDetail(func);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FunctionDetail> getFunctionPage(FunctionFilter functionFilter, Pageable pageable) {
        // TODO: Implement filter criteria using QueryDSL or Criteria API
        Page<Function> funcPage = funcRepository.findAll(pageable);
        return funcPage.map(this::toFunctionDetail);
    }

    @Override
    @Transactional
    public FunctionDetail updateFunction(String functionId, @NotNull FunctionUpdate functionUpdate) {
        Function func = findFunctionById(functionId);
        funcMapper.updateFunction(functionUpdate, func);
        Function savedFunc = funcRepository.save(func);
        return toFunctionDetail(savedFunc);
    }

    @Override
    @Transactional
    public void deleteFunction(String functionId) {
        funcRepository.delete(findFunctionById(functionId));
    }

    @Override
    @Transactional
    public ImplementationDetail createImplementation(String functionId, ImplementationCreate implementationCreate) {
        Implementation impl = implMapper.toImplementation(implementationCreate);
        Implementation savedImpl = implRepository.save(impl);
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
        Page<Implementation> implPage = implRepository.findAll(pageable);
        return implPage.map(this::toImplementationDetail);
    }

    @Override
    @Transactional
    public ImplementationDetail updateImplementation(String functionId, String implementationId, ImplementationUpdate implementationUpdate) {
        Implementation impl = findImplementationByIdAndFunctionId(implementationId, functionId);

        if (!implementationUpdate.getType().equals(impl.getType().name())) {
            throw new ImplementationNotMatchException(impl.getType().name(), implementationUpdate.getType());
        }

        implMapper.updateImplementation(implementationUpdate, impl);
        Implementation savedImpl = implRepository.save(impl);
        return toImplementationDetail(savedImpl);
    }

    @Override
    @Transactional
    public void deleteImplementation(String functionId, String implementationId) {
        Implementation impl = findImplementationByIdAndFunctionId(implementationId, functionId);
        implRepository.delete(impl);
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
            Implementation savedImpl = implRepository.save(compilableImpl);

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
        return funcRepository.findById(functionId)
                .orElseThrow(() -> new FunctionNotFoundException(functionId));
    }

    private Implementation findImplementationByIdAndFunctionId(String implementationId, String functionId) {
        Implementation implementation = implRepository.findById(implementationId)
                .orElseThrow(() -> new ImplementationNotFoundException(implementationId));

        if (!implementation.getFunctionId().equals(functionId)) {
            throw new ImplementationNotFoundException(implementationId, functionId);
        }

        return implementation;
    }

    private FunctionDetail toFunctionDetail(Function function) {
        List<Implementation> impls = implRepository.findAllByFunctionId(function.getId());
        return funcMapper.toFunctionDetail(function, impls);
    }

    private ImplementationDetail toImplementationDetail(Implementation implementation) {
        return implMapper.toImplementationDetail(implementation);
    }
}
