package com.travel.admin.service;

import com.travel.admin.common.result.PageResult;
import com.travel.admin.dto.approval.ApprovalCreateRequest;
import com.travel.admin.dto.approval.ApprovalDecisionRequest;
import com.travel.admin.dto.approval.ApprovalQueryRequest;
import com.travel.admin.dto.approval.ApprovalResponse;

public interface ApprovalService {

    void createApproval(ApprovalCreateRequest request);

    PageResult<ApprovalResponse> getApprovalPage(ApprovalQueryRequest request);

    void decide(Long id, ApprovalDecisionRequest request);
}

