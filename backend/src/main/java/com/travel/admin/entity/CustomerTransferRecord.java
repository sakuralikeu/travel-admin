package com.travel.admin.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.admin.common.enums.CustomerTransferType;
import lombok.Data;

@Data
@TableName("customer_transfer_record")
public class CustomerTransferRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long customerId;

    private Long fromEmployeeId;

    private Long toEmployeeId;

    private Long operatorId;

    private CustomerTransferType type;

    private String reason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableLogic
    private Integer deleted;
}

