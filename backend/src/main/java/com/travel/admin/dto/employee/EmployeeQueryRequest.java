package com.travel.admin.dto.employee;

import com.travel.admin.common.enums.EmployeeRole;
import com.travel.admin.common.enums.EmployeeStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class EmployeeQueryRequest {

    private String keyword;

    private EmployeeRole role;

    private EmployeeStatus status;

    private String department;

    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页数量最小为1")
    @Max(value = 100, message = "每页数量最大为100")
    private Integer pageSize = 10;
}

