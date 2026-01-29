package com.travel.admin.security;

import com.travel.admin.common.enums.EmployeeStatus;
import com.travel.admin.entity.Employee;
import com.travel.admin.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeMapper employeeMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Employee>()
                        .eq(Employee::getUsername, username)
                        .eq(Employee::getDeleted, 0)
        );
        if (employee == null) {
            throw new UsernameNotFoundException("账号不存在");
        }
        if (employee.getStatus() == EmployeeStatus.RESIGNED || employee.getStatus() == EmployeeStatus.DISABLED) {
            throw new UsernameNotFoundException("账号已停用");
        }
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + employee.getRole().name());
        return new User(employee.getUsername(), employee.getPassword(), Collections.singletonList(authority));
    }
}

