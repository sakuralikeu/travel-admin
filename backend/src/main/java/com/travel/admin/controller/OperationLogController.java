package com.travel.admin.controller;

import com.travel.admin.common.result.PageResult;
import com.travel.admin.common.result.Result;
import com.travel.admin.dto.log.OperationLogQueryRequest;
import com.travel.admin.dto.log.OperationLogResponse;
import com.travel.admin.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operation-logs")
@RequiredArgsConstructor
@Tag(name = "操作日志查询接口")
public class OperationLogController {

    private final OperationLogService operationLogService;

    @GetMapping
    @Operation(summary = "分页查询操作日志")
    public Result<PageResult<OperationLogResponse>> getOperationLogPage(@Valid OperationLogQueryRequest request) {
        PageResult<OperationLogResponse> pageResult = operationLogService.getOperationLogPage(request);
        return Result.success(pageResult);
    }
}
