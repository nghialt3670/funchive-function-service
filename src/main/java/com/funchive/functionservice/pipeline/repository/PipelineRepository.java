package com.funchive.functionservice.pipeline.repository;

import com.funchive.functionservice.pipeline.model.dao.Pipeline;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PipelineRepository extends MongoRepository<Pipeline, String> {
} 