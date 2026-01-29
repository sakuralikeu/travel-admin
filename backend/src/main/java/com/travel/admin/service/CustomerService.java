package com.travel.admin.service;

import com.travel.admin.common.result.PageResult;
import com.travel.admin.dto.customer.CustomerAssignRequest;
import com.travel.admin.dto.customer.CustomerCreateRequest;
import com.travel.admin.dto.customer.CustomerQueryRequest;
import com.travel.admin.dto.customer.CustomerResponse;
import com.travel.admin.dto.customer.CustomerTransferRecordResponse;
import com.travel.admin.dto.customer.CustomerUpdateRequest;
import com.travel.admin.dto.customer.EmployeeResignHandleRequest;
import com.travel.admin.dto.customer.PublicPoolClaimRequest;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerCreateRequest request);

    CustomerResponse updateCustomer(Long id, CustomerUpdateRequest request);

    void deleteCustomer(Long id);

    CustomerResponse getCustomerById(Long id);

    PageResult<CustomerResponse> getCustomerPage(CustomerQueryRequest request);

    PageResult<CustomerResponse> getPublicPoolPage(CustomerQueryRequest request);

    void assignCustomer(Long customerId, CustomerAssignRequest request);

    PageResult<CustomerTransferRecordResponse> getCustomerTransferRecords(Long customerId, int pageNum, int pageSize);

    void claimFromPublicPool(PublicPoolClaimRequest request);

    void handleEmployeeResign(EmployeeResignHandleRequest request);

    void autoRecycleToPublicPool();
}

