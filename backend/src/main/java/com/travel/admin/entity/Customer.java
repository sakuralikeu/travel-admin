package com.travel.admin.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.admin.common.enums.CustomerLevel;
import com.travel.admin.common.enums.CustomerStatus;
import lombok.Data;

@Data
@TableName("customer")
public class Customer {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String phone;

    private String wechat;

    private String email;

    private CustomerLevel level;

    private CustomerStatus status;

    private String preferredDestination;

    private String preferredBudget;

    private String preferredTravelTime;

    private Long assignedTo;

    private String source;

    private String tags;

    private String remark;

    private LocalDateTime lastFollowUpTime;

    private Long createdBy;

    private Long updatedBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
