package com.funchive.functionservice.function.repository;

import com.funchive.functionservice.function.model.dao.implementation.Implementation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ImplementationRepository extends MongoRepository<Implementation, String> {
    List<Implementation> findAllByFunctionId(String functionId);
}
