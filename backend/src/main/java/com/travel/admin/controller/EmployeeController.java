package com.travel.admin.controller;

import com.travel.admin.common.annotation.LogOperation;
import com.travel.admin.common.enums.OperationType;
import com.travel.admin.common.result.PageResult;
import com.travel.admin.common.result.Result;
import com.travel.admin.dto.employee.EmployeeCreateRequest;
import com.travel.admin.dto.employee.EmployeeQueryRequest;
import com.travel.admin.dto.employee.EmployeeResponse;
import com.travel.admin.dto.employee.EmployeeUpdateRequest;
import com.travel.admin.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "员工管理接口")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @Operation(summary = "创建员工")
    @LogOperation(module = "EMPLOYEE", name = "创建员工", type = OperationType.CREATE)
    public Result<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return Result.success(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新员工信息")
    @LogOperation(module = "EMPLOYEE", name = "更新员工信息", type = OperationType.UPDATE)
    public Result<EmployeeResponse> updateEmployee(@PathVariable Long id,
                                                   @Valid @RequestBody EmployeeUpdateRequest request) {
        EmployeeResponse response = employeeService.updateEmployee(id, request);
        return Result.success(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除员工")
    @LogOperation(module = "EMPLOYEE", name = "删除员工", type = OperationType.DELETE)
    public Result<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询员工详情")
    @LogOperation(module = "EMPLOYEE", name = "查询员工详情", type = OperationType.QUERY)
    public Result<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse response = employeeService.getEmployeeById(id);
        return Result.success(response);
    }

    @GetMapping
    @Operation(summary = "分页查询员工列表")
    @LogOperation(module = "EMPLOYEE", name = "分页查询员工列表", type = OperationType.QUERY)
    public Result<PageResult<EmployeeResponse>> getEmployeePage(@Valid EmployeeQueryRequest request) {
        PageResult<EmployeeResponse> pageResult = employeeService.getEmployeePage(request);
        return Result.success(pageResult);
    }
}
