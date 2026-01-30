package com.travel.admin.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.admin.common.enums.ApprovalStatus;
import com.travel.admin.common.enums.CustomerLevel;
import com.travel.admin.common.enums.CustomerTransferType;
import com.travel.admin.common.enums.EmployeeRole;
import com.travel.admin.common.enums.SensitiveOperationType;
import com.travel.admin.common.exception.BusinessException;
import com.travel.admin.common.result.PageResult;
import com.travel.admin.dto.approval.ApprovalCreateRequest;
import com.travel.admin.dto.approval.ApprovalDecisionRequest;
import com.travel.admin.dto.approval.ApprovalQueryRequest;
import com.travel.admin.dto.approval.ApprovalResponse;
import com.travel.admin.entity.ApprovalRequest;
import com.travel.admin.entity.Customer;
import com.travel.admin.entity.CustomerTransferRecord;
import com.travel.admin.entity.Employee;
import com.travel.admin.mapper.ApprovalRequestMapper;
import com.travel.admin.mapper.CustomerMapper;
import com.travel.admin.mapper.CustomerTransferRecordMapper;
import com.travel.admin.mapper.EmployeeMapper;
import com.travel.admin.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRequestMapper approvalRequestMapper;

    private final CustomerMapper customerMapper;

    private final CustomerTransferRecordMapper customerTransferRecordMapper;

    private final EmployeeMapper employeeMapper;

    @Override
    public void createApproval(ApprovalCreateRequest request) {
        Long requesterId = currentUserId();
        if (request.getOperationType() == SensitiveOperationType.VIP_CUSTOMER_DELETE) {
            createVipDeleteApproval(request, requesterId);
        } else if (request.getOperationType() == SensitiveOperationType.CUSTOMER_TRANSFER) {
            createCustomerTransferApproval(request, requesterId);
        } else {
            throw new BusinessException("不支持的审批类型");
        }
    }

    @Override
    public PageResult<ApprovalResponse> getApprovalPage(ApprovalQueryRequest request) {
        long pageNum = request.getPageNum() == null ? 1L : request.getPageNum();
        long pageSize = request.getPageSize() == null ? 10L : request.getPageSize();
        Page<ApprovalRequest> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ApprovalRequest> wrapper = new LambdaQueryWrapper<>();
        if (request.getOperationType() != null) {
            wrapper.eq(ApprovalRequest::getOperationType, request.getOperationType());
        }
        if (request.getStatus() != null) {
            wrapper.eq(ApprovalRequest::getStatus, request.getStatus());
        }
        wrapper.orderByDesc(ApprovalRequest::getId);
        Page<ApprovalRequest> resultPage = approvalRequestMapper.selectPage(page, wrapper);
        List<ApprovalResponse> records = resultPage.getRecords().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        Page<ApprovalResponse> responsePage = new Page<>();
        BeanUtils.copyProperties(resultPage, responsePage);
        responsePage.setRecords(records);
        return PageResult.of(responsePage);
    }

    @Override
    public void decide(Long id, ApprovalDecisionRequest request) {
        ApprovalRequest approvalRequest = approvalRequestMapper.selectById(id);
        if (approvalRequest == null || approvalRequest.getDeleted() != null && approvalRequest.getDeleted() != 0) {
            throw new BusinessException("审批记录不存在");
        }
        if (approvalRequest.getStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException("审批记录已处理");
        }
        Long approverId = currentUserId();
        EmployeeRole role = currentUserRole();
        boolean approved = Boolean.TRUE.equals(request.getApproved());
        if (!approved) {
            approvalRequest.setStatus(ApprovalStatus.REJECTED);
            approvalRequest.setApproverId(approverId);
            approvalRequest.setDecisionReason(request.getDecisionReason());
            approvalRequestMapper.updateById(approvalRequest);
            return;
        }
        if (approvalRequest.getOperationType() == SensitiveOperationType.VIP_CUSTOMER_DELETE) {
            approveVipDelete(approvalRequest, approverId, role);
        } else if (approvalRequest.getOperationType() == SensitiveOperationType.CUSTOMER_TRANSFER) {
            approveCustomerTransfer(approvalRequest, approverId, role);
        } else {
            throw new BusinessException("不支持的审批类型");
        }
        approvalRequest.setStatus(ApprovalStatus.APPROVED);
        approvalRequest.setApproverId(approverId);
        approvalRequest.setDecisionReason(request.getDecisionReason());
        approvalRequestMapper.updateById(approvalRequest);
    }

    private void createVipDeleteApproval(ApprovalCreateRequest request, Long requesterId) {
        Customer customer = customerMapper.selectById(request.getCustomerId());
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        if (customer.getLevel() != CustomerLevel.VIP) {
            throw new BusinessException("仅VIP客户需要删除审批");
        }
        LambdaQueryWrapper<ApprovalRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalRequest::getOperationType, SensitiveOperationType.VIP_CUSTOMER_DELETE)
                .eq(ApprovalRequest::getCustomerId, request.getCustomerId())
                .eq(ApprovalRequest::getStatus, ApprovalStatus.PENDING);
        Long count = approvalRequestMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException("该客户已存在待处理的删除审批");
        }
        ApprovalRequest approvalRequest = new ApprovalRequest();
        approvalRequest.setOperationType(SensitiveOperationType.VIP_CUSTOMER_DELETE);
        approvalRequest.setCustomerId(request.getCustomerId());
        approvalRequest.setRequesterId(requesterId);
        approvalRequest.setReason(request.getReason());
        approvalRequest.setStatus(ApprovalStatus.PENDING);
        approvalRequestMapper.insert(approvalRequest);
    }

    private void createCustomerTransferApproval(ApprovalCreateRequest request, Long requesterId) {
        if (request.getTargetEmployeeId() == null) {
            throw new BusinessException("目标员工ID不能为空");
        }
        Customer customer = customerMapper.selectById(request.getCustomerId());
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        if (customer.getAssignedTo() == null) {
            throw new BusinessException("当前客户未分配员工");
        }
        if (Objects.equals(customer.getAssignedTo(), request.getTargetEmployeeId())) {
            throw new BusinessException("目标员工与当前负责员工相同");
        }
        Employee fromEmployee = employeeMapper.selectById(customer.getAssignedTo());
        Employee toEmployee = employeeMapper.selectById(request.getTargetEmployeeId());
        if (fromEmployee == null || toEmployee == null) {
            throw new BusinessException("员工信息不存在");
        }
        boolean crossDepartment = false;
        if (fromEmployee.getDepartment() != null || toEmployee.getDepartment() != null) {
            crossDepartment = !Objects.equals(fromEmployee.getDepartment(), toEmployee.getDepartment());
        }
        if (!crossDepartment) {
            throw new BusinessException("当前客户转移不属于跨部门, 无需发起审批");
        }
        LambdaQueryWrapper<ApprovalRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalRequest::getOperationType, SensitiveOperationType.CUSTOMER_TRANSFER)
                .eq(ApprovalRequest::getCustomerId, request.getCustomerId())
                .eq(ApprovalRequest::getStatus, ApprovalStatus.PENDING);
        Long count = approvalRequestMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException("该客户已存在待处理的转移审批");
        }
        ApprovalRequest approvalRequest = new ApprovalRequest();
        approvalRequest.setOperationType(SensitiveOperationType.CUSTOMER_TRANSFER);
        approvalRequest.setCustomerId(request.getCustomerId());
        approvalRequest.setFromEmployeeId(customer.getAssignedTo());
        approvalRequest.setToEmployeeId(request.getTargetEmployeeId());
        approvalRequest.setRequesterId(requesterId);
        approvalRequest.setReason(request.getReason());
        approvalRequest.setStatus(ApprovalStatus.PENDING);
        approvalRequestMapper.insert(approvalRequest);
    }

    private void approveVipDelete(ApprovalRequest approvalRequest, Long approverId, EmployeeRole role) {
        if (role != EmployeeRole.MANAGER && role != EmployeeRole.SUPER_ADMIN) {
            throw new BusinessException("仅经理或超级管理员可审批VIP客户删除");
        }
        Customer customer = customerMapper.selectById(approvalRequest.getCustomerId());
        if (customer == null) {
            return;
        }
        if (customer.getLevel() != CustomerLevel.VIP) {
            throw new BusinessException("当前客户已不再是VIP, 无需通过该审批删除");
        }
        int affected = customerMapper.deleteById(customer.getId());
        if (affected == 0) {
            throw new BusinessException("删除客户失败");
        }
    }

    private void approveCustomerTransfer(ApprovalRequest approvalRequest, Long approverId, EmployeeRole role) {
        if (role != EmployeeRole.MANAGER && role != EmployeeRole.SUPER_ADMIN) {
            throw new BusinessException("仅经理或超级管理员可审批跨部门客户转移");
        }
        Customer customer = customerMapper.selectById(approvalRequest.getCustomerId());
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        if (!Objects.equals(customer.getAssignedTo(), approvalRequest.getFromEmployeeId())) {
            throw new BusinessException("客户当前负责员工已发生变化, 请重新发起审批");
        }
        Long toEmployeeId = approvalRequest.getToEmployeeId();
        if (toEmployeeId == null) {
            throw new BusinessException("审批记录缺少目标员工信息");
        }
        customer.setAssignedTo(toEmployeeId);
        int updated = customerMapper.updateById(customer);
        if (updated != 1) {
            throw new BusinessException("客户转移失败");
        }
        CustomerTransferRecord record = new CustomerTransferRecord();
        record.setCustomerId(customer.getId());
        record.setFromEmployeeId(approvalRequest.getFromEmployeeId());
        record.setToEmployeeId(toEmployeeId);
        record.setOperatorId(approverId);
        record.setType(CustomerTransferType.TRANSFER);
        record.setReason(approvalRequest.getReason());
        customerTransferRecordMapper.insert(record);
    }

    private Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new BusinessException("未登录");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new BusinessException("无法获取当前登录用户ID");
    }

    private EmployeeRole currentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BusinessException("未登录");
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority != null && authority.startsWith("ROLE_"))
                .findFirst()
                .map(authority -> authority.substring("ROLE_".length()))
                .map(EmployeeRole::valueOf)
                .orElseThrow(() -> new BusinessException("无法获取当前用户角色"));
    }

    private ApprovalResponse toResponse(ApprovalRequest request) {
        ApprovalResponse response = new ApprovalResponse();
        BeanUtils.copyProperties(request, response);
        return response;
    }
}
