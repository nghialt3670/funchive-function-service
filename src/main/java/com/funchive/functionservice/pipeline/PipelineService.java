package com.funchive.functionservice.pipeline;

import com.funchive.functionservice.pipeline.model.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PipelineService {
    PipelineDetail createPipeline(PipelineCreate pipelineCreate);

    PipelineDetail getPipelineDetail(String pipelineId);

    Page<PipelineDetail> getPipelinePage(PipelineFilter pipelineFilter, Pageable pageable);

    PipelineDetail updatePipeline(String pipelineId, PipelineUpdate pipelineUpdate);

    PipelineDetail deletePipeline(String pipelineId);

    void executePipeline(String pipelineId, PipelineExecutionTriggerDto executionTriggerDto);
} 