package com.travel.admin.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerAssignRequest {

    @NotNull(message = "目标员工ID不能为空")
    private Long targetEmployeeId;

    @NotBlank(message = "转移原因不能为空")
    private String reason;
}
