package com.funchive.functionservice.function.repository;

import com.funchive.functionservice.function.model.dao.implementation.Implementation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImplementationRepository extends MongoRepository<Implementation, String> {
}
