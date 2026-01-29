package com.travel.admin.config;

import java.time.LocalDate;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.admin.common.enums.EmployeeRole;
import com.travel.admin.common.enums.EmployeeStatus;
import com.travel.admin.entity.Employee;
import com.travel.admin.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultAdminInitializer implements CommandLineRunner {

    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Employee existing = employeeMapper.selectOne(
                new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getUsername, "admin")
                        .eq(Employee::getDeleted, 0)
        );

        if (existing == null) {
            Employee admin = new Employee();
            admin.setUsername("admin");
            admin.setName("系统管理员");
            admin.setPhone("13800000000");
            admin.setEmail("admin@example.com");
            admin.setDepartment("管理部");
            admin.setPosition("超级管理员");
            admin.setRole(EmployeeRole.SUPER_ADMIN);
            admin.setStatus(EmployeeStatus.ACTIVE);
            admin.setHireDate(LocalDate.now());
            admin.setPassword(passwordEncoder.encode("admin123"));

            employeeMapper.insert(admin);
            log.info("已自动创建默认超级管理员账号，用户名: admin, 初始密码: admin123");
        } else {
            existing.setRole(EmployeeRole.SUPER_ADMIN);
            existing.setStatus(EmployeeStatus.ACTIVE);
            existing.setPassword(passwordEncoder.encode("admin123"));
            employeeMapper.updateById(existing);
            log.info("已重置现有管理员账号密码，用户名: admin, 新密码: admin123");
        }
    }
}

