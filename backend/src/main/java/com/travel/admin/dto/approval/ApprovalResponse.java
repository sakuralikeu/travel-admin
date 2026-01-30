package com.travel.admin.dto.approval;

import java.time.LocalDateTime;

import com.travel.admin.common.enums.ApprovalStatus;
import com.travel.admin.common.enums.SensitiveOperationType;
import lombok.Data;

@Data
public class ApprovalResponse {

    private Long id;

    private SensitiveOperationType operationType;

    private Long customerId;

    private Long fromEmployeeId;

    private Long toEmployeeId;

    private Long requesterId;

    private String reason;

    private ApprovalStatus status;

    private Long approverId;

    private String decisionReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

