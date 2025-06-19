package com.funchive.functionservice.pipeline;

import com.funchive.functionservice.pipeline.model.document.Pipeline;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PipelineRepository extends MongoRepository<Pipeline, String> {
} 