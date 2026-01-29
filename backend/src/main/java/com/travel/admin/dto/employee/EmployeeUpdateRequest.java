package com.travel.admin.dto.employee;

import java.time.LocalDate;

import com.travel.admin.common.enums.EmployeeRole;
import com.travel.admin.common.enums.EmployeeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmployeeUpdateRequest {

    @NotBlank(message = "登录账号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,32}$", message = "登录账号仅支持4-32位字母、数字或下划线")
    private String username;

    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 50, message = "姓名长度需在2-50个字符之间")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String department;

    private String position;

    @NotNull(message = "角色不能为空")
    private EmployeeRole role;

    @NotNull(message = "员工状态不能为空")
    private EmployeeStatus status;

    private LocalDate hireDate;

    private LocalDate resignDate;

    @Size(min = 6, max = 50, message = "密码长度需在6-50个字符之间")
    private String password;
}

