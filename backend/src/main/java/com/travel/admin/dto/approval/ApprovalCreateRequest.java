package com.travel.admin.dto.approval;

import com.travel.admin.common.enums.SensitiveOperationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApprovalCreateRequest {

    @NotNull(message = "审批类型不能为空")
    private SensitiveOperationType operationType;

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    private Long targetEmployeeId;

    @NotBlank(message = "申请原因不能为空")
    private String reason;
}

