package com.travel.admin.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.admin.common.enums.CustomerLevel;
import com.travel.admin.common.enums.CustomerStatus;
import com.travel.admin.common.enums.CustomerTransferType;
import com.travel.admin.common.enums.EmployeeRole;
import com.travel.admin.common.exception.BusinessException;
import com.travel.admin.common.result.PageResult;
import com.travel.admin.dto.customer.CustomerAssignRequest;
import com.travel.admin.dto.customer.CustomerCreateRequest;
import com.travel.admin.dto.customer.CustomerQueryRequest;
import com.travel.admin.dto.customer.CustomerResponse;
import com.travel.admin.dto.customer.CustomerTransferRecordResponse;
import com.travel.admin.dto.customer.CustomerUpdateRequest;
import com.travel.admin.dto.customer.EmployeeResignHandleRequest;
import com.travel.admin.dto.customer.PublicPoolClaimRequest;
import com.travel.admin.entity.Customer;
import com.travel.admin.entity.CustomerTransferRecord;
import com.travel.admin.entity.Employee;
import com.travel.admin.mapper.CustomerMapper;
import com.travel.admin.mapper.CustomerTransferRecordMapper;
import com.travel.admin.mapper.EmployeeMapper;
import com.travel.admin.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private static final int DAILY_PUBLIC_POOL_CLAIM_LIMIT = 20;

    private final CustomerMapper customerMapper;

    private final CustomerTransferRecordMapper customerTransferRecordMapper;

    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getPhone, request.getPhone());
        Customer exist = customerMapper.selectOne(wrapper);
        if (exist != null) {
            throw new BusinessException("手机号已存在");
        }
        Customer customer = new Customer();
        BeanUtils.copyProperties(request, customer);
        if (customer.getStatus() == null) {
            customer.setStatus(CustomerStatus.NEW);
        }
        int inserted = customerMapper.insert(customer);
        if (inserted != 1) {
            throw new BusinessException("创建客户失败");
        }
        log.info("创建客户成功, id={}", customer.getId());
        return toResponse(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerResponse updateCustomer(Long id, CustomerUpdateRequest request) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        if (!Objects.equals(request.getPhone(), customer.getPhone())) {
            LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Customer::getPhone, request.getPhone())
                    .ne(Customer::getId, id);
            Customer exist = customerMapper.selectOne(wrapper);
            if (exist != null) {
                throw new BusinessException("手机号已存在");
            }
        }
        BeanUtils.copyProperties(request, customer, "id", "createdAt", "createdBy", "deleted");
        int updated = customerMapper.updateById(customer);
        if (updated != 1) {
            throw new BusinessException("更新客户失败");
        }
        log.info("更新客户成功, id={}", customer.getId());
        return toResponse(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomer(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        EmployeeRole role = currentUserRole();
        if (customer.getLevel() == CustomerLevel.VIP) {
            throw new BusinessException("删除VIP客户需提交审批, 请通过审批流程处理");
        } else {
            if (!isSupervisorOrAbove(role)) {
                throw new BusinessException("仅主管及以上角色可删除客户");
            }
        }
        int affected = customerMapper.deleteById(id);
        if (affected == 0) {
            throw new BusinessException("删除客户失败");
        }
        log.info("删除客户成功, id={}", id);
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        return toResponse(customer);
    }

    @Override
    public PageResult<CustomerResponse> getCustomerPage(CustomerQueryRequest request) {
        long pageNum = request.getPageNum() == null ? 1L : request.getPageNum();
        long pageSize = request.getPageSize() == null ? 10L : request.getPageSize();
        Page<Customer> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Customer> wrapper = buildCustomerQueryWrapper(request);
        wrapper.ne(Customer::getStatus, CustomerStatus.PUBLIC_POOL);
        wrapper.orderByDesc(Customer::getId);
        Page<Customer> resultPage = customerMapper.selectPage(page, wrapper);
        return buildCustomerPageResult(resultPage);
    }

    @Override
    public PageResult<CustomerResponse> getPublicPoolPage(CustomerQueryRequest request) {
        long pageNum = request.getPageNum() == null ? 1L : request.getPageNum();
        long pageSize = request.getPageSize() == null ? 10L : request.getPageSize();
        Page<Customer> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Customer> wrapper = buildCustomerQueryWrapper(request);
        wrapper.eq(Customer::getStatus, CustomerStatus.PUBLIC_POOL);
        wrapper.isNull(Customer::getAssignedTo);
        wrapper.orderByAsc(Customer::getLastFollowUpTime);
        Page<Customer> resultPage = customerMapper.selectPage(page, wrapper);
        return buildCustomerPageResult(resultPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignCustomer(Long customerId, CustomerAssignRequest request) {
        EmployeeRole role = currentUserRole();
        requireSupervisorOrAbove(role);
        Long operatorId = currentUserId();
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        Long originalAssignee = customer.getAssignedTo();
        if (Objects.equals(originalAssignee, request.getTargetEmployeeId())) {
            return;
        }
        if (originalAssignee != null && request.getTargetEmployeeId() != null) {
            if (isCrossDepartment(originalAssignee, request.getTargetEmployeeId())
                    && role == EmployeeRole.SUPERVISOR) {
                throw new BusinessException("跨部门客户转移需经理或超级管理员审批, 请发起审批后再执行");
            }
        }
        customer.setAssignedTo(request.getTargetEmployeeId());
        if (customer.getStatus() == CustomerStatus.PUBLIC_POOL) {
            customer.setStatus(CustomerStatus.FOLLOWING);
        }
        int updated = customerMapper.updateById(customer);
        if (updated != 1) {
            throw new BusinessException("分配客户失败");
        }
        CustomerTransferRecord record = buildTransferRecord(customerId, originalAssignee, request.getTargetEmployeeId(),
                operatorId, CustomerTransferType.ASSIGN, request.getReason());
        customerTransferRecordMapper.insert(record);
        log.info("分配客户成功, customerId={}, from={}, to={}", customerId, originalAssignee, request.getTargetEmployeeId());
    }

    @Override
    public PageResult<CustomerTransferRecordResponse> getCustomerTransferRecords(Long customerId, int pageNum, int pageSize) {
        Page<CustomerTransferRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CustomerTransferRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerTransferRecord::getCustomerId, customerId)
                .orderByDesc(CustomerTransferRecord::getId);
        Page<CustomerTransferRecord> resultPage = customerTransferRecordMapper.selectPage(page, wrapper);
        List<CustomerTransferRecordResponse> records = resultPage.getRecords().stream()
                .map(this::toTransferRecordResponse)
                .collect(Collectors.toList());
        Page<CustomerTransferRecordResponse> responsePage = new Page<>();
        BeanUtils.copyProperties(resultPage, responsePage);
        responsePage.setRecords(records);
        return PageResult.of(responsePage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claimFromPublicPool(PublicPoolClaimRequest request) {
        EmployeeRole operatorRole = currentUserRole();
        Long operatorId = currentUserId();
        Customer customer = customerMapper.selectById(request.getCustomerId());
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        if (customer.getStatus() != CustomerStatus.PUBLIC_POOL || customer.getAssignedTo() != null) {
            throw new BusinessException("客户不在公海池中");
        }
        if (customer.getLevel() == CustomerLevel.VIP && !isSupervisorOrAbove(operatorRole)) {
            throw new BusinessException("仅主管及以上角色可领取公海中的VIP客户");
        }
        int todayClaimCount = countTodayClaims(operatorId);
        if (todayClaimCount >= DAILY_PUBLIC_POOL_CLAIM_LIMIT && !isSupervisorOrAbove(operatorRole)) {
            throw new BusinessException("已超出每日领取上限, 需主管审批后处理");
        }
        customer.setAssignedTo(operatorId);
        customer.setStatus(CustomerStatus.FOLLOWING);
        int updated = customerMapper.updateById(customer);
        if (updated != 1) {
            throw new BusinessException("领取公海客户失败");
        }
        CustomerTransferRecord record = buildTransferRecord(customer.getId(), null, operatorId,
                operatorId, CustomerTransferType.CLAIM_FROM_POOL, null);
        customerTransferRecordMapper.insert(record);
        log.info("领取公海客户成功, customerId={}, operatorId={}", customer.getId(), operatorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleEmployeeResign(EmployeeResignHandleRequest request) {
        EmployeeRole operatorRole = currentUserRole();
        if (!isSupervisorOrAbove(operatorRole)) {
            throw new BusinessException("仅主管及以上角色可处理离职员工客户");
        }
        Long operatorId = currentUserId();
        boolean moveToPublicPool = Boolean.TRUE.equals(request.getMoveToPublicPool());
        if (!moveToPublicPool && request.getTargetEmployeeId() == null) {
            throw new BusinessException("未指定客户处理方式");
        }
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getAssignedTo, request.getEmployeeId());
        List<Customer> customers = customerMapper.selectList(wrapper);
        if (customers.isEmpty()) {
            return;
        }
        if (moveToPublicPool) {
            boolean hasVipCustomer = customers.stream()
                    .anyMatch(customer -> customer.getLevel() == CustomerLevel.VIP);
            if (hasVipCustomer
                    && operatorRole != EmployeeRole.MANAGER
                    && operatorRole != EmployeeRole.SUPER_ADMIN) {
                throw new BusinessException("仅经理或超级管理员可将VIP客户批量转入公海");
            }
        }
        for (Customer customer : customers) {
            Long fromEmployeeId = customer.getAssignedTo();
            Long toEmployeeId = moveToPublicPool ? null : request.getTargetEmployeeId();
            if (moveToPublicPool) {
                moveCustomerToPublicPool(customer, operatorId, CustomerTransferType.EMPLOYEE_RESIGN_TO_POOL,
                        request.getReason());
            } else {
                customer.setAssignedTo(request.getTargetEmployeeId());
                customer.setStatus(CustomerStatus.FOLLOWING);
                int updated = customerMapper.updateById(customer);
                if (updated != 1) {
                    throw new BusinessException("批量转移客户失败");
                }
                CustomerTransferRecord record = buildTransferRecord(customer.getId(), fromEmployeeId, toEmployeeId,
                        operatorId, CustomerTransferType.EMPLOYEE_RESIGN_TRANSFER, request.getReason());
                customerTransferRecordMapper.insert(record);
            }
        }
        log.info("处理离职员工客户完成, employeeId={}", request.getEmployeeId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoRecycleToPublicPool() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getLevel, CustomerLevel.NORMAL)
                .ne(Customer::getStatus, CustomerStatus.PUBLIC_POOL)
                .and(w -> w.le(Customer::getLastFollowUpTime, threshold)
                        .or()
                        .isNull(Customer::getLastFollowUpTime));
        List<Customer> customers = customerMapper.selectList(wrapper);
        if (customers.isEmpty()) {
            return;
        }
        for (Customer customer : customers) {
            moveCustomerToPublicPool(customer, null, CustomerTransferType.AUTO_RECYCLE_TO_POOL, null);
        }
        log.info("自动回收公海客户完成, count={}", customers.size());
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

    private LambdaQueryWrapper<Customer> buildCustomerQueryWrapper(CustomerQueryRequest request) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(qw -> qw.like(Customer::getName, request.getKeyword())
                    .or()
                    .like(Customer::getPhone, request.getKeyword())
                    .or()
                    .like(Customer::getWechat, request.getKeyword()));
        }
        if (request.getLevel() != null) {
            wrapper.eq(Customer::getLevel, request.getLevel());
        }
        if (request.getStatus() != null) {
            wrapper.eq(Customer::getStatus, request.getStatus());
        }
        if (request.getAssignedTo() != null) {
            wrapper.eq(Customer::getAssignedTo, request.getAssignedTo());
        }
        return wrapper;
    }

    private PageResult<CustomerResponse> buildCustomerPageResult(Page<Customer> resultPage) {
        List<CustomerResponse> records = resultPage.getRecords().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        Page<CustomerResponse> responsePage = new Page<>();
        BeanUtils.copyProperties(resultPage, responsePage);
        responsePage.setRecords(records);
        return PageResult.of(responsePage);
    }

    private CustomerResponse toResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        BeanUtils.copyProperties(customer, response);
        if (customer.getStatus() == CustomerStatus.PUBLIC_POOL) {
            fillPublicPoolInfo(response);
        }
        return response;
    }

    private CustomerTransferRecordResponse toTransferRecordResponse(CustomerTransferRecord record) {
        CustomerTransferRecordResponse response = new CustomerTransferRecordResponse();
        BeanUtils.copyProperties(record, response);
        return response;
    }

    private CustomerTransferRecord buildTransferRecord(Long customerId, Long fromEmployeeId, Long toEmployeeId,
                                                       Long operatorId, CustomerTransferType type, String reason) {
        CustomerTransferRecord record = new CustomerTransferRecord();
        record.setCustomerId(customerId);
        record.setFromEmployeeId(fromEmployeeId);
        record.setToEmployeeId(toEmployeeId);
        record.setOperatorId(operatorId);
        record.setType(type);
        record.setReason(reason);
        return record;
    }

    private void fillPublicPoolInfo(CustomerResponse response) {
        if (response.getId() == null) {
            return;
        }
        LambdaQueryWrapper<CustomerTransferRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerTransferRecord::getCustomerId, response.getId())
                .in(CustomerTransferRecord::getType,
                        CustomerTransferType.AUTO_RECYCLE_TO_POOL,
                        CustomerTransferType.EMPLOYEE_RESIGN_TO_POOL)
                .orderByDesc(CustomerTransferRecord::getCreatedAt)
                .last("LIMIT 1");
        CustomerTransferRecord record = customerTransferRecordMapper.selectOne(wrapper);
        if (record == null) {
            return;
        }
        response.setPublicPoolEnterTime(record.getCreatedAt());
        if (record.getReason() != null && !record.getReason().isEmpty()) {
            response.setPublicPoolEnterReason(record.getReason());
        } else if (record.getType() == CustomerTransferType.AUTO_RECYCLE_TO_POOL) {
            response.setPublicPoolEnterReason("超过30天未跟进自动回收");
        } else if (record.getType() == CustomerTransferType.EMPLOYEE_RESIGN_TO_POOL) {
            response.setPublicPoolEnterReason("离职员工客户转入公海");
        }
    }

    private boolean isSupervisorOrAbove(EmployeeRole role) {
        return role == EmployeeRole.SUPER_ADMIN
                || role == EmployeeRole.MANAGER
                || role == EmployeeRole.SUPERVISOR;
    }

    private boolean isCrossDepartment(Long fromEmployeeId, Long toEmployeeId) {
        if (Objects.equals(fromEmployeeId, toEmployeeId)) {
            return false;
        }
        Employee fromEmployee = employeeMapper.selectById(fromEmployeeId);
        Employee toEmployee = employeeMapper.selectById(toEmployeeId);
        if (fromEmployee == null || toEmployee == null) {
            return false;
        }
        if (fromEmployee.getDepartment() == null && toEmployee.getDepartment() == null) {
            return false;
        }
        return !Objects.equals(fromEmployee.getDepartment(), toEmployee.getDepartment());
    }

    private void requireSupervisorOrAbove(EmployeeRole role) {
        if (!isSupervisorOrAbove(role)) {
            throw new BusinessException("普通员工只能发起客户转移申请, 执行操作需主管及以上角色");
        }
    }

    private int countTodayClaims(Long operatorId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        LambdaQueryWrapper<CustomerTransferRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerTransferRecord::getOperatorId, operatorId)
                .eq(CustomerTransferRecord::getType, CustomerTransferType.CLAIM_FROM_POOL)
                .ge(CustomerTransferRecord::getCreatedAt, start)
                .lt(CustomerTransferRecord::getCreatedAt, end);
        Long count = customerTransferRecordMapper.selectCount(wrapper);
        return count == null ? 0 : count.intValue();
    }

    private void moveCustomerToPublicPool(Customer customer, Long operatorId, CustomerTransferType type, String reason) {
        if (customer.getLevel() == CustomerLevel.VIP && type != CustomerTransferType.EMPLOYEE_RESIGN_TO_POOL) {
            throw new BusinessException("VIP客户仅在经理审批通过后才能转入公海");
        }
        Long fromEmployeeId = customer.getAssignedTo();
        customer.setAssignedTo(null);
        customer.setStatus(CustomerStatus.PUBLIC_POOL);
        LambdaUpdateWrapper<Customer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Customer::getId, customer.getId())
                .set(Customer::getAssignedTo, null)
                .set(Customer::getStatus, CustomerStatus.PUBLIC_POOL);
        int updated = customerMapper.update(null, updateWrapper);
        if (updated != 1) {
            throw new BusinessException("移动客户至公海失败");
        }
        CustomerTransferRecord record = buildTransferRecord(customer.getId(), fromEmployeeId, null,
                operatorId, type, reason);
        customerTransferRecordMapper.insert(record);
    }
}
