package com.funchive.functionservice.pipeline.service;

import com.funchive.functionservice.pipeline.repository.PipelineRepository;
import com.funchive.functionservice.pipeline.PipelineService;
import com.funchive.functionservice.pipeline.exception.PipelineNotFoundException;
import com.funchive.functionservice.pipeline.model.dao.Pipeline;
import com.funchive.functionservice.pipeline.model.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineServiceImpl implements PipelineService {

    private final PipelineRepository pipelineRepository;
    private final PipelineExecutionService pipelineExecutionService;
    private final ModelMapper modelMapper;

    @Override
    public PipelineDetail createPipeline(PipelineCreate pipelineCreate) {
        Pipeline createdPipeline = modelMapper.map(pipelineCreate, Pipeline.class);
        Pipeline savedPipeline = pipelineRepository.save(createdPipeline);
        return modelMapper.map(savedPipeline, PipelineDetail.class);
    }

    @Override
    public PipelineDetail getPipelineDetail(String pipelineId) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new RuntimeException("Pipeline not found: " + pipelineId));

        PipelineDetail pipelineDetail = new PipelineDetail();
        BeanUtils.copyProperties(pipeline, pipelineDetail);

        return pipelineDetail;
    }

    @Override
    public Page<PipelineDetail> getPipelinePage(PipelineFilter pipelineFilter, Pageable pageable) {
        // For now, returning all pipelines. In a real implementation, you'd filter based on the filter criteria
        Page<Pipeline> pipelinePage = pipelineRepository.findAll(pageable);

        return pipelinePage.map(pipeline -> {
            PipelineDetail dto = new PipelineDetail();
            BeanUtils.copyProperties(pipeline, dto);
            return dto;
        });
    }

    @Override
    public PipelineDetail updatePipeline(String pipelineId, PipelineUpdate pipelineUpdate) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new RuntimeException("Pipeline not found: " + pipelineId));

        BeanUtils.copyProperties(pipelineUpdate, pipeline);
        Pipeline updatedPipeline = pipelineRepository.save(pipeline);

        PipelineDetail pipelineDetail = new PipelineDetail();
        BeanUtils.copyProperties(updatedPipeline, pipelineDetail);

        return pipelineDetail;
    }

    @Override
    public PipelineDetail deletePipeline(String pipelineId) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new RuntimeException("Pipeline not found: " + pipelineId));

        pipelineRepository.delete(pipeline);

        PipelineDetail pipelineDetail = new PipelineDetail();
        BeanUtils.copyProperties(pipeline, pipelineDetail);

        return pipelineDetail;
    }

    @Override
    public void executePipeline(String pipelineId, PipelineExecutionTriggerDto executionTriggerDto) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new RuntimeException("Pipeline not found: " + pipelineId));

        log.info("Starting pipeline execution for pipeline: {}", pipelineId);

        // Execute the pipeline using the dedicated execution service
        pipelineExecutionService.executePipeline(pipeline, executionTriggerDto.getInputs());

        log.info("Pipeline execution completed for pipeline: {}", pipelineId);
    }

    private Pipeline findPipelineById(String pipelineId) {
        return pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new PipelineNotFoundException(pipelineId));
    }
} 