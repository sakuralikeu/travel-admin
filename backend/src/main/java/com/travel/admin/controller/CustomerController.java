package com.travel.admin.controller;

import com.travel.admin.common.annotation.LogOperation;
import com.travel.admin.common.enums.OperationType;
import com.travel.admin.common.result.PageResult;
import com.travel.admin.common.result.Result;
import com.travel.admin.dto.customer.CustomerAssignRequest;
import com.travel.admin.dto.customer.CustomerCreateRequest;
import com.travel.admin.dto.customer.CustomerQueryRequest;
import com.travel.admin.dto.customer.CustomerResponse;
import com.travel.admin.dto.customer.CustomerTransferRecordResponse;
import com.travel.admin.dto.customer.CustomerUpdateRequest;
import com.travel.admin.dto.customer.EmployeeResignHandleRequest;
import com.travel.admin.dto.customer.PublicPoolClaimRequest;
import com.travel.admin.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "客户管理与公海池接口")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "创建客户")
    @LogOperation(module = "CUSTOMER", name = "创建客户", type = OperationType.CREATE)
    public Result<CustomerResponse> createCustomer(@Valid @RequestBody CustomerCreateRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return Result.success(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新客户信息")
    @LogOperation(module = "CUSTOMER", name = "更新客户信息", type = OperationType.UPDATE)
    public Result<CustomerResponse> updateCustomer(@PathVariable Long id,
                                                   @Valid @RequestBody CustomerUpdateRequest request) {
        CustomerResponse response = customerService.updateCustomer(id, request);
        return Result.success(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除客户")
    @LogOperation(module = "CUSTOMER", name = "删除客户", type = OperationType.DELETE)
    @PreAuthorize("hasAnyRole('SUPERVISOR','MANAGER','SUPER_ADMIN')")
    public Result<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询客户详情")
    @LogOperation(module = "CUSTOMER", name = "查询客户详情", type = OperationType.QUERY)
    @PreAuthorize("hasAnyRole('EMPLOYEE','SUPERVISOR','MANAGER','SUPER_ADMIN')")
    public Result<CustomerResponse> getCustomerById(@PathVariable Long id) {
        CustomerResponse response = customerService.getCustomerById(id);
        return Result.success(response);
    }

    @GetMapping
    @Operation(summary = "分页查询客户列表")
    @LogOperation(module = "CUSTOMER", name = "分页查询客户列表", type = OperationType.QUERY)
    @PreAuthorize("hasAnyRole('EMPLOYEE','SUPERVISOR','MANAGER','SUPER_ADMIN')")
    public Result<PageResult<CustomerResponse>> getCustomerPage(@Valid CustomerQueryRequest request) {
        PageResult<CustomerResponse> pageResult = customerService.getCustomerPage(request);
        return Result.success(pageResult);
    }

    @GetMapping("/public-pool")
    @Operation(summary = "分页查询公海客户列表")
    @LogOperation(module = "CUSTOMER", name = "分页查询公海客户列表", type = OperationType.QUERY)
    @PreAuthorize("hasAnyRole('EMPLOYEE','SUPERVISOR','MANAGER','SUPER_ADMIN')")
    public Result<PageResult<CustomerResponse>> getPublicPoolPage(@Valid CustomerQueryRequest request) {
        PageResult<CustomerResponse> pageResult = customerService.getPublicPoolPage(request);
        return Result.success(pageResult);
    }

    @PostMapping("/{id}/assign")
    @Operation(summary = "分配或转移客户给指定员工")
    @LogOperation(module = "CUSTOMER", name = "分配或转移客户", type = OperationType.UPDATE)
    @PreAuthorize("hasAnyRole('SUPERVISOR','MANAGER','SUPER_ADMIN')")
    public Result<Void> assignCustomer(@PathVariable Long id,
                                       @Valid @RequestBody CustomerAssignRequest request) {
        customerService.assignCustomer(id, request);
        return Result.success();
    }

    @GetMapping("/{id}/transfers")
    @Operation(summary = "分页查询客户流转记录")
    @LogOperation(module = "CUSTOMER", name = "分页查询客户流转记录", type = OperationType.QUERY)
    @PreAuthorize("hasAnyRole('EMPLOYEE','SUPERVISOR','MANAGER','SUPER_ADMIN')")
    public Result<PageResult<CustomerTransferRecordResponse>> getCustomerTransferRecords(@PathVariable Long id,
                                                                                         @RequestParam(defaultValue = "1") int pageNum,
                                                                                         @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<CustomerTransferRecordResponse> pageResult = customerService.getCustomerTransferRecords(id, pageNum, pageSize);
        return Result.success(pageResult);
    }

    @PostMapping("/public-pool/claim")
    @Operation(summary = "领取公海客户")
    @LogOperation(module = "CUSTOMER", name = "领取公海客户", type = OperationType.UPDATE)
    @PreAuthorize("hasAnyRole('EMPLOYEE','SUPERVISOR','MANAGER','SUPER_ADMIN')")
    public Result<Void> claimFromPublicPool(@Valid @RequestBody PublicPoolClaimRequest request) {
        customerService.claimFromPublicPool(request);
        return Result.success();
    }

    @PostMapping("/employee/resign")
    @Operation(summary = "处理离职员工名下客户")
    @LogOperation(module = "CUSTOMER", name = "处理离职员工名下客户", type = OperationType.UPDATE)
    @PreAuthorize("hasAnyRole('SUPERVISOR','MANAGER','SUPER_ADMIN')")
    public Result<Void> handleEmployeeResign(@Valid @RequestBody EmployeeResignHandleRequest request) {
        customerService.handleEmployeeResign(request);
        return Result.success();
    }

    @PostMapping("/public-pool/auto-recycle")
    @Operation(summary = "执行公海自动回收任务")
    @LogOperation(module = "CUSTOMER", name = "执行公海自动回收任务", type = OperationType.UPDATE)
    @PreAuthorize("hasAnyRole('MANAGER','SUPER_ADMIN')")
    public Result<Void> autoRecycleToPublicPool() {
        customerService.autoRecycleToPublicPool();
        return Result.success();
    }
}
