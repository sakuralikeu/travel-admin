package com.travel.admin.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.admin.common.enums.TradeWarningStatus;
import com.travel.admin.common.enums.TradeWarningType;
import com.travel.admin.common.enums.WarningLevel;
import lombok.Data;

@Data
@TableName("trade_warning")
public class TradeWarning {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long customerId;

    private Long employeeId;

    private TradeWarningType type;

    private WarningLevel level;

    private TradeWarningStatus status;

    private String message;

    private LocalDateTime firstDetectedAt;

    private LocalDateTime lastDetectedAt;

    private LocalDateTime closedAt;

    private Long closedBy;

    private String closedReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
