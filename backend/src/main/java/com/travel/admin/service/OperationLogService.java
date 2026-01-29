package com.travel.admin.service;

import com.travel.admin.common.result.PageResult;
import com.travel.admin.dto.log.OperationLogQueryRequest;
import com.travel.admin.dto.log.OperationLogResponse;
import com.travel.admin.entity.OperationLog;

public interface OperationLogService {

    void save(OperationLog log);

    PageResult<OperationLogResponse> getOperationLogPage(OperationLogQueryRequest request);
}
