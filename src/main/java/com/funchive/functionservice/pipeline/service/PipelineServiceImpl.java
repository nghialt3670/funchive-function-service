package com.funchive.functionservice.pipeline.service;

import com.funchive.functionservice.pipeline.PipelineRepository;
import com.funchive.functionservice.pipeline.PipelineService;
import com.funchive.functionservice.pipeline.model.document.Pipeline;
import com.funchive.functionservice.pipeline.model.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    
    @Override
    public PipelineDetailDto createPipeline(PipelineCreateDto pipelineCreateDto) {
        Pipeline pipeline = new Pipeline();
        BeanUtils.copyProperties(pipelineCreateDto, pipeline);
        
        Pipeline savedPipeline = pipelineRepository.save(pipeline);
        
        PipelineDetailDto pipelineDetailDto = new PipelineDetailDto();
        BeanUtils.copyProperties(savedPipeline, pipelineDetailDto);
        
        return pipelineDetailDto;
    }
    
    @Override
    public PipelineDetailDto getPipelineDetail(String pipelineId) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new RuntimeException("Pipeline not found: " + pipelineId));
        
        PipelineDetailDto pipelineDetailDto = new PipelineDetailDto();
        BeanUtils.copyProperties(pipeline, pipelineDetailDto);
        
        return pipelineDetailDto;
    }
    
    @Override
    public Page<PipelineDetailDto> getPipelinePage(PipelineFilter pipelineFilter, Pageable pageable) {
        // For now, returning all pipelines. In a real implementation, you'd filter based on the filter criteria
        Page<Pipeline> pipelinePage = pipelineRepository.findAll(pageable);
        
        return pipelinePage.map(pipeline -> {
            PipelineDetailDto dto = new PipelineDetailDto();
            BeanUtils.copyProperties(pipeline, dto);
            return dto;
        });
    }
    
    @Override
    public PipelineDetailDto updatePipeline(String pipelineId, PipelineUpdateDto pipelineUpdateDto) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new RuntimeException("Pipeline not found: " + pipelineId));
        
        BeanUtils.copyProperties(pipelineUpdateDto, pipeline);
        Pipeline updatedPipeline = pipelineRepository.save(pipeline);
        
        PipelineDetailDto pipelineDetailDto = new PipelineDetailDto();
        BeanUtils.copyProperties(updatedPipeline, pipelineDetailDto);
        
        return pipelineDetailDto;
    }
    
    @Override
    public PipelineDetailDto deletePipeline(String pipelineId) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new RuntimeException("Pipeline not found: " + pipelineId));
        
        pipelineRepository.delete(pipeline);
        
        PipelineDetailDto pipelineDetailDto = new PipelineDetailDto();
        BeanUtils.copyProperties(pipeline, pipelineDetailDto);
        
        return pipelineDetailDto;
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
} 