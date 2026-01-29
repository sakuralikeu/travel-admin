package com.travel.admin.dto.employee;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.travel.admin.common.enums.EmployeeRole;
import com.travel.admin.common.enums.EmployeeStatus;
import lombok.Data;

@Data
public class EmployeeResponse {

    private Long id;

    private String username;

    private String name;

    private String phone;

    private String email;

    private String department;

    private String position;

    private EmployeeRole role;

    private EmployeeStatus status;

    private LocalDate hireDate;

    private LocalDate resignDate;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

