package com.funchive.functionservice.function.model.mapper;

import com.funchive.functionservice.function.model.dao.function.Function;
import com.funchive.functionservice.function.model.dao.implementation.Implementation;
import com.funchive.functionservice.function.model.dto.function.FunctionCreate;
import com.funchive.functionservice.function.model.dto.function.FunctionDetail;
import com.funchive.functionservice.function.model.dto.function.FunctionUpdate;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationBasic;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class FunctionMapper {
    @Autowired
    private TypeMapper typeMapper;

    @Autowired
    private ImplementationMapper implMapper;

    public abstract Function toFunction(FunctionCreate functionCreate);

    @Mapping(target = "inputType", ignore = true)
    @Mapping(target = "outputType", ignore = true)
    public abstract void updateFunction(FunctionUpdate functionUpdate, @MappingTarget Function function);

    @BeforeMapping
    public void validateInputOutputType(
            FunctionCreate functionCreate,
            @MappingTarget Function function
    ) {
        typeMapper.validateType(functionCreate.getInputType());
        typeMapper.validateType(functionCreate.getOutputType());
    }


    @Mapping(target = "implementations", ignore = true)
    public abstract FunctionDetail toFunctionDetail(Function function, @Context List<Implementation> implementations);

    @AfterMapping
    public void populateImplementations(
            Function function,
            @MappingTarget FunctionDetail functionDetail,
            @Context List<Implementation> implementations
    ) {
        List<ImplementationBasic> implBasics = implementations.stream()
                .map(implMapper::toImplementationBasic)
                .collect(Collectors.toList());

        functionDetail.setImplementations(implBasics);
    }

    @AfterMapping
    public void updateInputOutputType(
            FunctionUpdate functionUpdate,
            @MappingTarget Function function
    ) {
        if (functionUpdate.getInputType() != null) {
            typeMapper.updateType(functionUpdate.getInputType(), function.getInputType());
        }

        if (functionUpdate.getOutputType() != null) {
            typeMapper.updateType(functionUpdate.getOutputType(), function.getOutputType());
        }
    }
}
