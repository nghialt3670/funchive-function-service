package com.funchive.functionservice.function.repository;

import com.funchive.functionservice.function.model.dao.function.Function;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FunctionRepository extends MongoRepository<Function, String> {
}
