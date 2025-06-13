package com.funchive.functionservice.function;

import com.funchive.functionservice.function.model.document.Function;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FunctionRepository extends MongoRepository<Function, String> {
}
