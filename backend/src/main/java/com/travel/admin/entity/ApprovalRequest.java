package com.travel.admin.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.admin.common.enums.ApprovalStatus;
import com.travel.admin.common.enums.SensitiveOperationType;
import lombok.Data;

@Data
@TableName("approval_request")
public class ApprovalRequest {

    @TableId(type = IdType.AUTO)
    private Long id;

    private SensitiveOperationType operationType;

    private Long customerId;

    private Long fromEmployeeId;

    private Long toEmployeeId;

    private Long requesterId;

    private String reason;

    private ApprovalStatus status;

    private Long approverId;

    private String decisionReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}

