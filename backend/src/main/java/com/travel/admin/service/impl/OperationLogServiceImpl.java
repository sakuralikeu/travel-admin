package com.travel.admin.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.admin.common.result.PageResult;
import com.travel.admin.dto.log.OperationLogQueryRequest;
import com.travel.admin.dto.log.OperationLogResponse;
import com.travel.admin.entity.OperationLog;
import com.travel.admin.mapper.OperationLogMapper;
import com.travel.admin.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    @Override
    public void save(OperationLog log) {
        operationLogMapper.insert(log);
    }

    @Override
    public PageResult<OperationLogResponse> getOperationLogPage(OperationLogQueryRequest request) {
        long pageNum = request.getPageNum() == null ? 1L : request.getPageNum();
        long pageSize = request.getPageSize() == null ? 10L : request.getPageSize();
        Page<OperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getModule())) {
            wrapper.eq(OperationLog::getModule, request.getModule());
        }
        if (request.getType() != null) {
            wrapper.eq(OperationLog::getType, request.getType());
        }
        if (request.getOperatorId() != null) {
            wrapper.eq(OperationLog::getOperatorId, request.getOperatorId());
        }
        if (request.getSuccess() != null) {
            wrapper.eq(OperationLog::getSuccess, request.getSuccess());
        }
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(qw -> qw.like(OperationLog::getName, request.getKeyword())
                    .or()
                    .like(OperationLog::getRequestUri, request.getKeyword())
                    .or()
                    .like(OperationLog::getIp, request.getKeyword()));
        }
        if (request.getStartTime() != null) {
            wrapper.ge(OperationLog::getCreatedAt, request.getStartTime());
        }
        if (request.getEndTime() != null) {
            wrapper.le(OperationLog::getCreatedAt, request.getEndTime());
        }
        wrapper.orderByDesc(OperationLog::getId);
        Page<OperationLog> resultPage = operationLogMapper.selectPage(page, wrapper);
        List<OperationLogResponse> records = resultPage.getRecords().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        Page<OperationLogResponse> responsePage = new Page<>();
        BeanUtils.copyProperties(resultPage, responsePage);
        responsePage.setRecords(records);
        return PageResult.of(responsePage);
    }

    private OperationLogResponse toResponse(OperationLog log) {
        OperationLogResponse response = new OperationLogResponse();
        BeanUtils.copyProperties(log, response);
        return response;
    }
}
