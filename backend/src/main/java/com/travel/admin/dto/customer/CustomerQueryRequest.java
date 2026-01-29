package com.travel.admin.dto.customer;

import com.travel.admin.common.enums.CustomerLevel;
import com.travel.admin.common.enums.CustomerStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CustomerQueryRequest {

    private String keyword;

    private CustomerLevel level;

    private CustomerStatus status;

    private Long assignedTo;

    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页数量最小为1")
    @Max(value = 100, message = "每页数量最大为100")
    private Integer pageSize = 10;
}

