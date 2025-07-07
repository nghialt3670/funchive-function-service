package com.funchive.functionservice.function.model.mapper;

import com.funchive.functionservice.function.exception.DefaultValueTypeNotMatchExecption;
import com.funchive.functionservice.function.exception.TypeNotMatchException;
import com.funchive.functionservice.function.model.common.type.*;
import com.funchive.functionservice.function.model.dto.type.*;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;

import java.util.Map.Entry;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
@Slf4j
public abstract class TypeMapper {
    public void updateType(TypeUpdate typeUpdate, @MappingTarget Type type) {
        if ( typeUpdate == null ) {
            return;
        }

        if (!typeUpdate.getName().equals(type.getName())) {
            throw new TypeNotMatchException(type.getName(), typeUpdate.getName());
        }

        if (typeUpdate.getDescription() != null) {
            type.setDescription(typeUpdate.getDescription());
        }

        if (typeUpdate.getDefaultValue() != null) {
            if (!typeUpdate.getDefaultValue().getTypeName().equals(type.getDefaultValue().getTypeName())) {
                throw new DefaultValueTypeNotMatchExecption(type.getDefaultValue().getTypeName(), typeUpdate.getDefaultValue().getTypeName());
            }

            type.setDefaultValue(typeUpdate.getDefaultValue());
        }

        type.setUseDefaultValue(typeUpdate.isUseDefaultValue());

        if (typeUpdate.getName().equals(EType.OBJECT.name())) {
            ObjectType objectType = (ObjectType) type;
            ObjectTypeUpdate objectTypeUpdate = (ObjectTypeUpdate) typeUpdate;
            for (Entry<String, Type> entry : objectType.getSchema().entrySet()) {
                if (objectTypeUpdate.getSchema().containsKey(entry.getKey())) {
                    updateType(objectTypeUpdate.getSchema().get(entry.getKey()), entry.getValue());
                }
            }
        } else if (typeUpdate.getName().equals(EType.ARRAY.name())) {
            ArrayType arrayType = (ArrayType) type;
            ArrayTypeUpdate arrayTypeUpdate = (ArrayTypeUpdate) typeUpdate;
            updateType(arrayTypeUpdate.getElementType(), arrayType.getElementType());
        }
    }

    public void validateType(Type type) {
        if (type == null) {
            return;
        }

        if (type.getDefaultValue() != null) {
            if (!type.getDefaultValue().getTypeName().equals(type.getName())) {
                throw new DefaultValueTypeNotMatchExecption(type.getName(), type.getDefaultValue().getTypeName());
            }
        }

        if (type.getName().equals(EType.OBJECT.name())) {
            ObjectType objectType = (ObjectType) type;
            for (Entry<String, Type> entry : objectType.getSchema().entrySet()) {
                validateType(entry.getValue());
            }
        } else if (type.getName().equals(EType.ARRAY.name())) {
            ArrayType arrayType = (ArrayType) type;
            validateType(arrayType.getElementType());
        }
    }
}
