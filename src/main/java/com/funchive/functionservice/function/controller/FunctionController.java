package com.funchive.functionservice.function.controller;

import com.funchive.functionservice.common.model.dto.ResponseBody;
import com.funchive.functionservice.common.model.dto.ResponsePage;
import com.funchive.functionservice.function.FunctionService;
import com.funchive.functionservice.function.model.dto.function.FunctionCreate;
import com.funchive.functionservice.function.model.dto.function.FunctionDetail;
import com.funchive.functionservice.function.model.dto.function.FunctionFilter;
import com.funchive.functionservice.function.model.dto.function.FunctionUpdate;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationCreate;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationDetail;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationFilter;
import com.funchive.functionservice.function.model.dto.implementation.ImplementationUpdate;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Tag(name = "Function API")
@RestController
@RequestMapping("/functions")
@RequiredArgsConstructor
public class FunctionController {
    private final FunctionService functionService;

    @PostMapping
    public ResponseBody<FunctionDetail> createFunction(
            @RequestBody @Valid FunctionCreate functionCreate
    ) {
        FunctionDetail createdFuncDetail = functionService.createFunction(functionCreate);
        return ResponseBody.of(createdFuncDetail);
    }

    @GetMapping("/{functionId}")
    public ResponseBody<FunctionDetail> getFunctionDetail(
            @PathVariable String functionId
    ) {
        FunctionDetail funcDetail = functionService.getFunctionDetail(functionId);
        return ResponseBody.of(funcDetail);
    }

    @GetMapping
    public ResponseBody<ResponsePage<FunctionDetail>> getFunctionPage(
            @RequestParam String keyword,
            @RequestParam String language,
            Pageable pageable) {

        FunctionFilter funcFilter = FunctionFilter.builder()
                .keyword(keyword)
                .language(language)
                .build();

        Page<FunctionDetail> funcPage = functionService.getFunctionPage(funcFilter, pageable);

        return ResponseBody.of(ResponsePage.of(funcPage));
    }

    @PutMapping("/{functionId}")
    public ResponseBody<FunctionDetail> updateFunction(
            @PathVariable String functionId,
            @RequestBody @Valid FunctionUpdate functionUpdate
    ) {
        FunctionDetail updatedFuncDetail = functionService.updateFunction(
                functionId, functionUpdate);

        return ResponseBody.of(updatedFuncDetail);
    }

    @DeleteMapping("/{functionId}")
    public ResponseBody<ObjectUtils.Null> deleteFunction(@PathVariable String functionId) {
        functionService.deleteFunction(functionId);
        return ResponseBody.ok();
    }

    @PostMapping("/{functionId}/implementations")
    public ResponseBody<ImplementationDetail> createImplementation(
            @PathVariable String functionId,
            @RequestBody @Valid ImplementationCreate implementationCreate) {

        ImplementationDetail createdImplDetail = functionService.createImplementation(
                functionId, implementationCreate);

        return ResponseBody.of(createdImplDetail);
    }

    @GetMapping("/{functionId}/implementations/{implementationId}")
    public ResponseBody<ImplementationDetail> getImplementationDetail(
            @PathVariable String functionId,
            @PathVariable String implementationId
    ) {
        ImplementationDetail implDetail = functionService.getImplementationDetail(
                functionId, implementationId);

        return ResponseBody.of(implDetail);
    }

    @GetMapping("/{functionId}/implementations")
    public ResponseBody<ResponsePage<ImplementationDetail>> getImplementationPage(
            @PathVariable String functionId,
            @RequestParam String keyword,
            Pageable pageable
    ) {
        ImplementationFilter implFilter = ImplementationFilter.builder()
                .keyword(keyword)
                .build();

        Page<ImplementationDetail> implPage = functionService.getImplementationPage(
                functionId, implFilter, pageable);

        return ResponseBody.of(ResponsePage.of(implPage));
    }

    @PutMapping("/{functionId}/implementations/{implementationId}")
    public ResponseBody<ImplementationDetail> updateImplementation(
            @PathVariable String functionId,
            @PathVariable String implementationId,
            @RequestBody @Valid ImplementationUpdate implementationUpdate
    ) {
        ImplementationDetail updatedImplDetail = functionService.updateImplementation(
                functionId, implementationId, implementationUpdate);

        return ResponseBody.of(updatedImplDetail);
    }

    @DeleteMapping("/{functionId}/implementations/{implementationId}")
    public ResponseBody<ObjectUtils.Null> deleteImplementation(
            @PathVariable String functionId,
            @PathVariable String implementationId
    ) {
        functionService.deleteImplementation(functionId, implementationId);
        return ResponseBody.ok();
    }

    @PostMapping("/{functionId}/implementations/{implementationId}/compile")
    public ResponseBody<ObjectUtils.Null> compileImplementation(
            @PathVariable String functionId,
            @PathVariable String implementationId
    ) {
        functionService.compileFunction(functionId, implementationId);
        return ResponseBody.ok();
    }
}
