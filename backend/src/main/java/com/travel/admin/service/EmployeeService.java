package com.travel.admin.service;

import com.travel.admin.common.result.PageResult;
import com.travel.admin.dto.employee.EmployeeCreateRequest;
import com.travel.admin.dto.employee.EmployeeQueryRequest;
import com.travel.admin.dto.employee.EmployeeResponse;
import com.travel.admin.dto.employee.EmployeeUpdateRequest;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeCreateRequest request);

    EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request);

    void deleteEmployee(Long id);

    EmployeeResponse getEmployeeById(Long id);

    PageResult<EmployeeResponse> getEmployeePage(EmployeeQueryRequest request);
}

