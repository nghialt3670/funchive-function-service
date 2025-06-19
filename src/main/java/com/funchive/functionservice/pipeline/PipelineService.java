package com.funchive.functionservice.pipeline;

import com.funchive.functionservice.pipeline.model.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PipelineService {
    PipelineDetailDto createPipeline(PipelineCreateDto pipelineCreateDto);
    
    PipelineDetailDto getPipelineDetail(String pipelineId);
    
    Page<PipelineDetailDto> getPipelinePage(PipelineFilter pipelineFilter, Pageable pageable);
    
    PipelineDetailDto updatePipeline(String pipelineId, PipelineUpdateDto pipelineUpdateDto);
    
    PipelineDetailDto deletePipeline(String pipelineId);
    
    void executePipeline(String pipelineId, PipelineExecutionTriggerDto executionTriggerDto);
} 