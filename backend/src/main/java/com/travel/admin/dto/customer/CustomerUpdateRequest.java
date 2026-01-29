package com.travel.admin.dto.customer;

import com.travel.admin.common.enums.CustomerLevel;
import com.travel.admin.common.enums.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerUpdateRequest {

    @NotBlank(message = "客户姓名不能为空")
    @Size(min = 1, max = 50, message = "客户姓名长度需在1-50个字符之间")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String wechat;

    @Email(message = "邮箱格式不正确")
    private String email;

    @NotNull(message = "客户等级不能为空")
    private CustomerLevel level;

    @NotNull(message = "客户状态不能为空")
    private CustomerStatus status;

    private Long assignedTo;

    private String source;

    private String tags;

    private String remark;
}

