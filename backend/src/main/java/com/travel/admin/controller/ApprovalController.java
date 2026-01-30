package com.travel.admin.controller;

import com.travel.admin.common.annotation.LogOperation;
import com.travel.admin.common.enums.OperationType;
import com.travel.admin.common.result.PageResult;
import com.travel.admin.common.result.Result;
import com.travel.admin.dto.approval.ApprovalCreateRequest;
import com.travel.admin.dto.approval.ApprovalDecisionRequest;
import com.travel.admin.dto.approval.ApprovalQueryRequest;
import com.travel.admin.dto.approval.ApprovalResponse;
import com.travel.admin.service.ApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
@Tag(name = "敏感操作审批接口")
public class ApprovalController {

    private final ApprovalService approvalService;

    @PostMapping
    @Operation(summary = "提交敏感操作审批请求")
    @LogOperation(module = "APPROVAL", name = "提交敏感操作审批请求", type = OperationType.CREATE)
    @PreAuthorize("hasAnyRole('EMPLOYEE','SUPERVISOR','MANAGER','SUPER_ADMIN')")
    public Result<Void> createApproval(@Valid @RequestBody ApprovalCreateRequest request) {
        approvalService.createApproval(request);
        return Result.success();
    }

    @GetMapping
    @Operation(summary = "分页查询敏感操作审批记录")
    @LogOperation(module = "APPROVAL", name = "分页查询敏感操作审批记录", type = OperationType.QUERY)
    @PreAuthorize("hasAnyRole('SUPERVISOR','MANAGER','SUPER_ADMIN')")
    public Result<PageResult<ApprovalResponse>> getApprovalPage(@Valid ApprovalQueryRequest request) {
        PageResult<ApprovalResponse> pageResult = approvalService.getApprovalPage(request);
        return Result.success(pageResult);
    }

    @PostMapping("/{id}/decision")
    @Operation(summary = "审批敏感操作请求")
    @LogOperation(module = "APPROVAL", name = "审批敏感操作请求", type = OperationType.UPDATE)
    @PreAuthorize("hasAnyRole('SUPERVISOR','MANAGER','SUPER_ADMIN')")
    public Result<Void> decide(@PathVariable Long id, @Valid @RequestBody ApprovalDecisionRequest request) {
        approvalService.decide(id, request);
        return Result.success();
    }
}

