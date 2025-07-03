package com.funchive.functionservice.pipeline.service;

import com.funchive.functionservice.pipeline.repository.PipelineRepository;
import com.funchive.functionservice.pipeline.PipelineService;
import com.funchive.functionservice.pipeline.exception.PipelineNotFoundException;
import com.funchive.functionservice.pipeline.model.dao.Pipeline;
import com.funchive.functionservice.pipeline.model.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineServiceImpl implements PipelineService {

    private final PipelineRepository pipelineRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public PipelineDetail createPipeline(PipelineCreate pipelineCreate) {
        Pipeline createdPipeline = modelMapper.map(pipelineCreate, Pipeline.class);
        Pipeline savedPipeline = pipelineRepository.save(createdPipeline);
        return modelMapper.map(savedPipeline, PipelineDetail.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PipelineDetail getPipelineDetail(String pipelineId) {
        Pipeline pipeline = findPipelineById(pipelineId);
        return modelMapper.map(pipeline, PipelineDetail.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PipelineDetail> getPipelinePage(PipelineFilter pipelineFilter, Pageable pageable) {
        Page<Pipeline> pipelinePage = pipelineRepository.findAll(pageable);
        return pipelinePage.map(pipeline -> modelMapper.map(pipeline, PipelineDetail.class));
    }

    @Override
    @Transactional
    public PipelineDetail updatePipeline(String pipelineId, PipelineUpdate pipelineUpdate) {
        Pipeline pipeline = findPipelineById(pipelineId);
        modelMapper.map(pipelineUpdate, pipeline);
        Pipeline updatedPipeline = pipelineRepository.save(pipeline);
        return modelMapper.map(updatedPipeline, PipelineDetail.class);
    }

    @Override
    @Transactional
    public void deletePipeline(String pipelineId) {
        Pipeline pipeline = findPipelineById(pipelineId);
        pipelineRepository.delete(pipeline);
    }

    @Override
    @Transactional
    public void executePipeline(String pipelineId) {
        Pipeline pipeline = findPipelineById(pipelineId);
    }

    private Pipeline findPipelineById(String pipelineId) {
        return pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new PipelineNotFoundException(pipelineId));
    }
} 