package com.travel.admin.dto.customer;

import com.travel.admin.common.enums.EmployeeRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerAssignRequest {

    @NotNull(message = "目标员工ID不能为空")
    private Long targetEmployeeId;

    @NotNull(message = "操作人ID不能为空")
    private Long operatorId;

    @NotNull(message = "操作人角色不能为空")
    private EmployeeRole operatorRole;

    @NotBlank(message = "转移原因不能为空")
    private String reason;
}

