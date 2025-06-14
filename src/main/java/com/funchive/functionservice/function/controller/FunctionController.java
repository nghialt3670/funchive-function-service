package com.funchive.functionservice.function.controller;

import com.funchive.functionservice.common.model.dto.ResponseBody;
import com.funchive.functionservice.common.model.dto.ResponsePage;
import com.funchive.functionservice.function.FunctionService;
import com.funchive.functionservice.function.SandboxService;
import com.funchive.functionservice.function.model.document.Value;
import com.funchive.functionservice.function.model.dto.FunctionCreateDto;
import com.funchive.functionservice.function.model.dto.FunctionDetailDto;
import com.funchive.functionservice.function.model.dto.FunctionFilter;
import com.funchive.functionservice.function.model.dto.FunctionUpdateDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Function API")
@RestController
@RequestMapping("/functions")
@RequiredArgsConstructor
public class FunctionController {
    private final FunctionService functionService;
    private final SandboxService sandboxService;

    @PostMapping
    public ResponseBody<FunctionDetailDto> createFunction(
            @RequestParam boolean compile,
            @RequestBody FunctionCreateDto functionCreateDto
    ) {
        var createdFunctionDetailDto = functionService.createFunction(functionCreateDto);

        if (compile) {
            sandboxService.compileFunction(createdFunctionDetailDto);
        }

        return ResponseBody.of(createdFunctionDetailDto);
    }

    @GetMapping("/{functionId}")
    public ResponseBody<FunctionDetailDto> getFunctionDetail(@PathVariable String functionId) {
        var functionDetailDto = functionService.getFunctionDetail(functionId);
        return ResponseBody.of(functionDetailDto);
    }

    @GetMapping
    public ResponseBody<ResponsePage<FunctionDetailDto>> getFunctionPage(
            @RequestParam String keyword,
            @RequestParam String language,
            Pageable pageable
    ) {
        var functionFilter = FunctionFilter.builder()
                .keyword(keyword)
                .language(language)
                .build();

        var functionPage = functionService.getFunctionPage(functionFilter, pageable);

        return ResponseBody.of(ResponsePage.of(functionPage));
    }

    @PutMapping("/{functionId}")
    public ResponseBody<FunctionDetailDto> updateFunction(
            @PathVariable String functionId,
            @RequestParam boolean compile,
            @RequestBody FunctionUpdateDto functionUpdateDto
    ) {
        var updatedFunctionDetailDto = functionService.updateFunction(functionId, functionUpdateDto);

        if (compile) {
            sandboxService.compileFunction(updatedFunctionDetailDto);
        }

        return ResponseBody.of(updatedFunctionDetailDto);
    }

    @DeleteMapping("/{functionId}")
    public ResponseBody<FunctionDetailDto> deleteFunction(@PathVariable String functionId) {
        var deletedFunctionDetailDto = functionService.deleteFunction(functionId);
        return ResponseBody.of(deletedFunctionDetailDto);
    }

    @PostMapping("/{functionId}/compile")
    public ResponseBody<ObjectUtils.Null> compileFunction(
            @PathVariable String functionId
    ) {
        var functionDetailDto = functionService.getFunctionDetail(functionId);

        sandboxService.compileFunction(functionDetailDto);

        return ResponseBody.of(null);
    }

    @PostMapping("/{functionId}/execute")
    public ResponseBody<ObjectUtils.Null> executeFunction(
            @PathVariable String functionId,
            @RequestBody List<Value<?>> inputs
    ) {
        var functionDetailDto = functionService.getFunctionDetail(functionId);

        sandboxService.executeFunction(functionDetailDto);

        return ResponseBody.of(null);
    }
}
