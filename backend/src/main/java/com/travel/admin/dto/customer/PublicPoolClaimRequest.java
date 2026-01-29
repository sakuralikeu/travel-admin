package com.travel.admin.dto.customer;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PublicPoolClaimRequest {

    @NotNull(message = "客户ID不能为空")
    private Long customerId;
}
