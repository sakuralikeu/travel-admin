package com.travel.admin.dto.customer;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeeResignHandleRequest {

    @NotNull(message = "离职员工ID不能为空")
    private Long employeeId;

    private Long targetEmployeeId;

    private Boolean moveToPublicPool;

    private String reason;
}
