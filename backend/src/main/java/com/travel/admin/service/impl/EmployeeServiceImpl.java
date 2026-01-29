package com.travel.admin.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.admin.common.enums.EmployeeStatus;
import com.travel.admin.common.exception.BusinessException;
import com.travel.admin.common.result.PageResult;
import com.travel.admin.dto.employee.EmployeeCreateRequest;
import com.travel.admin.dto.employee.EmployeeQueryRequest;
import com.travel.admin.dto.employee.EmployeeResponse;
import com.travel.admin.dto.employee.EmployeeUpdateRequest;
import com.travel.admin.entity.Employee;
import com.travel.admin.mapper.EmployeeMapper;
import com.travel.admin.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;

    @Override
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, request.getUsername())
                .or()
                .eq(Employee::getPhone, request.getPhone());
        Employee exist = employeeMapper.selectOne(wrapper);
        if (exist != null) {
            throw new BusinessException("登录账号或手机号已存在");
        }
        Employee employee = new Employee();
        BeanUtils.copyProperties(request, employee);
        if (employee.getStatus() == null) {
            employee.setStatus(EmployeeStatus.ACTIVE);
        }
        employee.setPassword(encodePassword(request.getPassword()));
        int inserted = employeeMapper.insert(employee);
        if (inserted != 1) {
            throw new BusinessException("创建员工失败");
        }
        log.info("创建员工成功, id={}", employee.getId());
        return toResponse(employee);
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        if (!Objects.equals(request.getUsername(), employee.getUsername())) {
            LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Employee::getUsername, request.getUsername())
                    .ne(Employee::getId, id);
            Employee exist = employeeMapper.selectOne(wrapper);
            if (exist != null) {
                throw new BusinessException("登录账号已存在");
            }
        }
        if (!Objects.equals(request.getPhone(), employee.getPhone())) {
            LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Employee::getPhone, request.getPhone())
                    .ne(Employee::getId, id);
            Employee exist = employeeMapper.selectOne(wrapper);
            if (exist != null) {
                throw new BusinessException("手机号已存在");
            }
        }
        BeanUtils.copyProperties(request, employee, "id", "createdAt", "createdBy", "deleted", "password");
        if (StringUtils.hasText(request.getPassword())) {
            employee.setPassword(encodePassword(request.getPassword()));
        }
        int updated = employeeMapper.updateById(employee);
        if (updated != 1) {
            throw new BusinessException("更新员工失败");
        }
        log.info("更新员工成功, id={}", employee.getId());
        return toResponse(employee);
    }

    @Override
    public void deleteEmployee(Long id) {
        int affected = employeeMapper.deleteById(id);
        if (affected == 0) {
            throw new BusinessException("员工不存在");
        }
        log.info("删除员工成功, id={}", id);
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        return toResponse(employee);
    }

    @Override
    public PageResult<EmployeeResponse> getEmployeePage(EmployeeQueryRequest request) {
        long pageNum = request.getPageNum() == null ? 1L : request.getPageNum();
        long pageSize = request.getPageSize() == null ? 10L : request.getPageSize();
        Page<Employee> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(qw -> qw.like(Employee::getName, request.getKeyword())
                    .or()
                    .like(Employee::getUsername, request.getKeyword())
                    .or()
                    .like(Employee::getPhone, request.getKeyword()));
        }
        if (request.getRole() != null) {
            wrapper.eq(Employee::getRole, request.getRole());
        }
        if (request.getStatus() != null) {
            wrapper.eq(Employee::getStatus, request.getStatus());
        }
        if (StringUtils.hasText(request.getDepartment())) {
            wrapper.eq(Employee::getDepartment, request.getDepartment());
        }
        wrapper.orderByDesc(Employee::getId);
        Page<Employee> resultPage = employeeMapper.selectPage(page, wrapper);
        List<EmployeeResponse> records = resultPage.getRecords().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        Page<EmployeeResponse> responsePage = new Page<>();
        BeanUtils.copyProperties(resultPage, responsePage);
        responsePage.setRecords(records);
        return PageResult.of(responsePage);
    }

    private EmployeeResponse toResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        BeanUtils.copyProperties(employee, response);
        return response;
    }

    private String encodePassword(String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(rawPassword);
    }
}

