package com.travel.admin.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.admin.common.enums.PaymentStatus;
import lombok.Data;

@Data
@TableName("customer_order")
public class CustomerOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long customerId;

    private Long employeeId;

    private BigDecimal amount;

    private BigDecimal standardAmount;

    private PaymentStatus paymentStatus;

    private String paymentAccount;

    private Boolean offlinePayment;

    private LocalDateTime paidAt;

    private LocalDateTime lastAmountChangeTime;

    private Integer amountChangeCount;

    private Integer cancelCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
