package com.funchive.functionservice.pipeline.controller;

import com.funchive.functionservice.common.model.dto.ResponseBody;
import com.funchive.functionservice.common.model.dto.ResponsePage;
import com.funchive.functionservice.pipeline.PipelineService;
import com.funchive.functionservice.pipeline.model.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Pipeline API")
@RestController
@RequestMapping("/pipelines")
@RequiredArgsConstructor
public class PipelineController {
    
    private final PipelineService pipelineService;
    
    @PostMapping
    public ResponseBody<PipelineDetailDto> createPipeline(
            @RequestBody PipelineCreateDto pipelineCreateDto
    ) {
        var createdPipelineDetailDto = pipelineService.createPipeline(pipelineCreateDto);
        return ResponseBody.of(createdPipelineDetailDto);
    }
    
    @GetMapping("/{pipelineId}")
    public ResponseBody<PipelineDetailDto> getPipelineDetail(@PathVariable String pipelineId) {
        var pipelineDetailDto = pipelineService.getPipelineDetail(pipelineId);
        return ResponseBody.of(pipelineDetailDto);
    }
    
    @GetMapping
    public ResponseBody<ResponsePage<PipelineDetailDto>> getPipelinePage(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String createdBy,
            Pageable pageable
    ) {
        var pipelineFilter = PipelineFilter.builder()
                .keyword(keyword)
                .createdBy(createdBy)
                .build();
        
        var pipelinePage = pipelineService.getPipelinePage(pipelineFilter, pageable);
        
        return ResponseBody.of(ResponsePage.of(pipelinePage));
    }
    
    @PutMapping("/{pipelineId}")
    public ResponseBody<PipelineDetailDto> updatePipeline(
            @PathVariable String pipelineId,
            @RequestBody PipelineUpdateDto pipelineUpdateDto
    ) {
        var updatedPipelineDetailDto = pipelineService.updatePipeline(pipelineId, pipelineUpdateDto);
        return ResponseBody.of(updatedPipelineDetailDto);
    }
    
    @DeleteMapping("/{pipelineId}")
    public ResponseBody<PipelineDetailDto> deletePipeline(@PathVariable String pipelineId) {
        var deletedPipelineDetailDto = pipelineService.deletePipeline(pipelineId);
        return ResponseBody.of(deletedPipelineDetailDto);
    }
    
    @PostMapping("/{pipelineId}/execute")
    public ResponseBody<ObjectUtils.Null> executePipeline(
            @PathVariable String pipelineId,
            @RequestBody PipelineExecutionTriggerDto executionTriggerDto
    ) {
        pipelineService.executePipeline(pipelineId, executionTriggerDto);
        return ResponseBody.of(null);
    }
} 