package com.travel.admin.dto.customer;

import java.time.LocalDateTime;

import com.travel.admin.common.enums.CustomerLevel;
import com.travel.admin.common.enums.CustomerStatus;
import lombok.Data;

@Data
public class CustomerResponse {

    private Long id;

    private String name;

    private String phone;

    private String wechat;

    private String email;

    private CustomerLevel level;

    private CustomerStatus status;

    private Long assignedTo;

    private String source;

    private String tags;

    private String remark;

    private LocalDateTime lastFollowUpTime;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

