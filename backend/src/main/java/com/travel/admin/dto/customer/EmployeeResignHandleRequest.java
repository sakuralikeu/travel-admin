package com.travel.admin.dto.customer;

import com.travel.admin.common.enums.EmployeeRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeeResignHandleRequest {

    @NotNull(message = "离职员工ID不能为空")
    private Long employeeId;

    @NotNull(message = "操作人ID不能为空")
    private Long operatorId;

    @NotNull(message = "操作人角色不能为空")
    private EmployeeRole operatorRole;

    private Long targetEmployeeId;

    private Boolean moveToPublicPool;

    private String reason;
}

