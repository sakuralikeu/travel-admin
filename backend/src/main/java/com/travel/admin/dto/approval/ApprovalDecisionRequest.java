package com.travel.admin.dto.approval;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApprovalDecisionRequest {

    @NotNull(message = "审批结果不能为空")
    private Boolean approved;

    private String decisionReason;
}

