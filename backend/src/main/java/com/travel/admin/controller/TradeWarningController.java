package com.travel.admin.controller;

import com.travel.admin.common.annotation.LogOperation;
import com.travel.admin.common.enums.OperationType;
import com.travel.admin.common.result.PageResult;
import com.travel.admin.common.result.Result;
import com.travel.admin.dto.warning.TradeWarningCloseRequest;
import com.travel.admin.dto.warning.TradeWarningQueryRequest;
import com.travel.admin.dto.warning.TradeWarningResponse;
import com.travel.admin.service.TradeWarningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade-warnings")
@RequiredArgsConstructor
@Tag(name = "异常交易预警接口")
public class TradeWarningController {

    private final TradeWarningService tradeWarningService;

    @GetMapping
    @Operation(summary = "分页查询异常交易预警")
    @LogOperation(module = "TRADE_WARNING", name = "分页查询异常交易预警", type = OperationType.QUERY)
    @PreAuthorize("hasAnyRole('EMPLOYEE','SUPERVISOR','MANAGER','SUPER_ADMIN')")
    public Result<PageResult<TradeWarningResponse>> getTradeWarningPage(@Valid TradeWarningQueryRequest request) {
        PageResult<TradeWarningResponse> pageResult = tradeWarningService.getTradeWarningPage(request);
        return Result.success(pageResult);
    }

    @PostMapping("/scan")
    @Operation(summary = "扫描订单生成异常交易预警")
    @LogOperation(module = "TRADE_WARNING", name = "扫描生成异常交易预警", type = OperationType.CREATE)
    @PreAuthorize("hasAnyRole('MANAGER','SUPER_ADMIN')")
    public Result<Void> scanAndGenerateWarnings() {
        tradeWarningService.scanAndGenerateWarnings();
        return Result.success();
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "关闭异常交易预警")
    @LogOperation(module = "TRADE_WARNING", name = "关闭异常交易预警", type = OperationType.UPDATE)
    @PreAuthorize("hasAnyRole('SUPERVISOR','MANAGER','SUPER_ADMIN')")
    public Result<Void> closeWarning(@PathVariable Long id, @Valid @RequestBody TradeWarningCloseRequest request) {
        tradeWarningService.closeWarning(id, request.getReason());
        return Result.success();
    }
}
