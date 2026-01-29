package com.travel.admin.dto.customer;

import com.travel.admin.common.enums.EmployeeRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PublicPoolClaimRequest {

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotNull(message = "操作人ID不能为空")
    private Long operatorId;

    @NotNull(message = "操作人角色不能为空")
    private EmployeeRole operatorRole;
}

