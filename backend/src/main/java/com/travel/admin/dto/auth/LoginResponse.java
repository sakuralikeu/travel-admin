package com.travel.admin.dto.auth;

import com.travel.admin.common.enums.EmployeeRole;
import lombok.Data;

@Data
public class LoginResponse {

    private String token;

    private Long userId;

    private String username;

    private String name;

    private EmployeeRole role;
}

