package com.travel.admin.dto.log;

import java.time.LocalDateTime;

import com.travel.admin.common.enums.OperationType;
import lombok.Data;

@Data
public class OperationLogResponse {

    private Long id;

    private String module;

    private String name;

    private OperationType type;

    private String requestUri;

    private String httpMethod;

    private String ip;

    private String userAgent;

    private Long operatorId;

    private String operatorName;

    private String operatorRole;

    private Boolean success;

    private String errorMessage;

    private Long executionTimeMillis;

    private String requestParams;

    private LocalDateTime createdAt;
}
