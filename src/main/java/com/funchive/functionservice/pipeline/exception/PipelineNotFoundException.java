package com.funchive.functionservice.pipeline.exception;

public class PipelineNotFoundException extends RuntimeException {
    public PipelineNotFoundException(String pipelineId) {
        super(String.format("Pipeline with ID %s not found", pipelineId));
    }
}
