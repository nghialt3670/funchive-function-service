package com.funchive.functionservice.function.model.mapper;

import com.funchive.functionservice.function.model.dao.implementation.http.HttpImplementation;
import com.funchive.functionservice.function.model.dao.implementation.Implementation;
import com.funchive.functionservice.function.model.dao.implementation.java.JavaImplementation;
import com.funchive.functionservice.function.model.dao.implementation.python.PythonImplementation;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationBasic;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationCreate;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationDetail;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationUpdate;
import com.funchive.functionservice.function.model.dto.implementation.http.HttpImplementationCreate;
import com.funchive.functionservice.function.model.dto.implementation.http.HttpImplementationDetail;
import com.funchive.functionservice.function.model.dto.implementation.java.JavaImplementationCreate;
import com.funchive.functionservice.function.model.dto.implementation.java.JavaImplementationDetail;
import com.funchive.functionservice.function.model.dto.implementation.python.PythonImplementationCreate;
import com.funchive.functionservice.function.model.dto.implementation.python.PythonImplementationDetail;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ImplementationMapper {
    @SubclassMappings({
            @SubclassMapping(source = HttpImplementationCreate.class, target = HttpImplementation.class),
            @SubclassMapping(source = PythonImplementationCreate.class, target = PythonImplementation.class),
            @SubclassMapping(source = JavaImplementationCreate.class, target = JavaImplementation.class),
    })
    Implementation toImplementation(ImplementationCreate implementationCreate);

    ImplementationBasic toImplementationBasic(Implementation implementation);

    @SubclassMappings({
            @SubclassMapping(source = HttpImplementation.class, target = HttpImplementationDetail.class),
            @SubclassMapping(source = PythonImplementation.class, target = PythonImplementationDetail.class),
            @SubclassMapping(source = JavaImplementation.class, target = JavaImplementationDetail.class),
    })
    ImplementationDetail toImplementationDetail(Implementation implementation);

    void updateImplementation(ImplementationUpdate implementationUpdate, @MappingTarget Implementation implementation);
}
