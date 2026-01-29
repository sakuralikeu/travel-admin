package com.travel.admin.controller;

import com.travel.admin.common.result.Result;
import com.travel.admin.dto.auth.LoginRequest;
import com.travel.admin.dto.auth.LoginResponse;
import com.travel.admin.dto.employee.EmployeeResponse;
import com.travel.admin.entity.Employee;
import com.travel.admin.mapper.EmployeeMapper;
import com.travel.admin.security.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travel.admin.service.EmployeeService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证接口")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final EmployeeMapper employeeMapper;
    private final EmployeeService employeeService;

    @PostMapping("/login")
    @Operation(summary = "用户名密码登录")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("账号或密码错误");
        }

        Employee employee = employeeMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Employee>()
                        .eq(Employee::getUsername, request.getUsername())
                        .eq(Employee::getDeleted, 0)
        );

        String token = jwtTokenUtil.generateToken(employee.getId(), employee.getUsername(),
                employee.getRole().name());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(employee.getId());
        response.setUsername(employee.getUsername());
        response.setName(employee.getName());
        response.setRole(employee.getRole());

        return Result.success(response);
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前登录员工信息")
    public Result<EmployeeResponse> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new BadCredentialsException("未登录");
        }
        Long userId = (Long) authentication.getPrincipal();
        EmployeeResponse response = employeeService.getEmployeeById(userId);
        return Result.success(response);
    }
}
