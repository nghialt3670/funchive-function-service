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
    public ResponseBody<PipelineDetail> createPipeline(
            @RequestBody PipelineCreate pipelineCreate) {
        return ResponseBody.of(pipelineService.createPipeline(pipelineCreate));
    }

    @GetMapping("/{pipelineId}")
    public ResponseBody<PipelineDetail> getPipelineDetail(@PathVariable String pipelineId) {
        return ResponseBody.of(pipelineService.getPipelineDetail(pipelineId));
    }

    @GetMapping
    public ResponseBody<ResponsePage<PipelineDetail>> getPipelinePage(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        PipelineFilter pipelineFilter = PipelineFilter.builder()
                .keyword(keyword)
                .build();

        return ResponseBody.of(ResponsePage.of(pipelineService.getPipelinePage(pipelineFilter, pageable)));
    }

    @PutMapping("/{pipelineId}")
    public ResponseBody<PipelineDetail> updatePipeline(
            @PathVariable String pipelineId,
            @RequestBody PipelineUpdate pipelineUpdate
    ) {
        var updatedPipelineDetailDto = pipelineService.updatePipeline(pipelineId, pipelineUpdate);
        return ResponseBody.of(updatedPipelineDetailDto);
    }

    @DeleteMapping("/{pipelineId}")
    public ResponseBody<PipelineDetail> deletePipeline(@PathVariable String pipelineId) {
        return ResponseBody.of(pipelineService.deletePipeline(pipelineId));
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