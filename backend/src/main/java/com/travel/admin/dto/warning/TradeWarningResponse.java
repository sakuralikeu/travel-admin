package com.travel.admin.dto.warning;

import java.time.LocalDateTime;

import com.travel.admin.common.enums.TradeWarningStatus;
import com.travel.admin.common.enums.TradeWarningType;
import com.travel.admin.common.enums.WarningLevel;
import lombok.Data;

@Data
public class TradeWarningResponse {

    private Long id;

    private Long orderId;

    private Long customerId;

    private Long employeeId;

    private TradeWarningType type;

    private WarningLevel level;

    private TradeWarningStatus status;

    private String message;

    private LocalDateTime firstDetectedAt;

    private LocalDateTime lastDetectedAt;

    private LocalDateTime closedAt;

    private Long closedBy;

    private String closedReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
