package com.travel.admin.dto.warning;

import java.time.LocalDateTime;

import com.travel.admin.common.enums.TradeWarningStatus;
import com.travel.admin.common.enums.TradeWarningType;
import com.travel.admin.common.enums.WarningLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class TradeWarningQueryRequest {

    private Long employeeId;

    private Long customerId;

    private TradeWarningType type;

    private WarningLevel level;

    private TradeWarningStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页数量最小为1")
    @Max(value = 100, message = "每页数量最大为100")
    private Integer pageSize = 10;
}
