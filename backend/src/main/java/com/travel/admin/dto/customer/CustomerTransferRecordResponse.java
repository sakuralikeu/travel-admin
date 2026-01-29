package com.travel.admin.dto.customer;

import java.time.LocalDateTime;

import com.travel.admin.common.enums.CustomerTransferType;
import lombok.Data;

@Data
public class CustomerTransferRecordResponse {

    private Long id;

    private Long customerId;

    private Long fromEmployeeId;

    private Long toEmployeeId;

    private Long operatorId;

    private CustomerTransferType type;

    private String reason;

    private LocalDateTime createdAt;
}

