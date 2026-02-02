package com.travel.admin.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.admin.common.enums.PaymentStatus;
import com.travel.admin.common.enums.TradeWarningStatus;
import com.travel.admin.common.enums.TradeWarningType;
import com.travel.admin.common.enums.WarningLevel;
import com.travel.admin.common.exception.BusinessException;
import com.travel.admin.common.result.PageResult;
import com.travel.admin.dto.warning.TradeWarningQueryRequest;
import com.travel.admin.dto.warning.TradeWarningResponse;
import com.travel.admin.entity.CustomerOrder;
import com.travel.admin.entity.TradeWarning;
import com.travel.admin.mapper.CustomerOrderMapper;
import com.travel.admin.mapper.TradeWarningMapper;
import com.travel.admin.service.TradeWarningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeWarningServiceImpl implements TradeWarningService {

    private final CustomerOrderMapper customerOrderMapper;

    private final TradeWarningMapper tradeWarningMapper;

    @Value("${trade-warning.price-deviation-threshold:0.3}")
    private BigDecimal priceDeviationThreshold;

    @Value("${trade-warning.long-unpaid-days:7}")
    private long longUnpaidDays;

    @Value("${trade-warning.company-payment-account:COMPANY_ACCOUNT}")
    private String companyPaymentAccount;

    @Value("${trade-warning.max-amount-change-count:3}")
    private int maxAmountChangeCount;

    @Value("${trade-warning.max-cancel-count:3}")
    private int maxCancelCount;

    @Override
    public PageResult<TradeWarningResponse> getTradeWarningPage(TradeWarningQueryRequest request) {
        long pageNum = request.getPageNum() == null ? 1L : request.getPageNum();
        long pageSize = request.getPageSize() == null ? 10L : request.getPageSize();
        Page<TradeWarning> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TradeWarning> wrapper = new LambdaQueryWrapper<>();
        if (request.getEmployeeId() != null) {
            wrapper.eq(TradeWarning::getEmployeeId, request.getEmployeeId());
        }
        if (request.getCustomerId() != null) {
            wrapper.eq(TradeWarning::getCustomerId, request.getCustomerId());
        }
        if (request.getType() != null) {
            wrapper.eq(TradeWarning::getType, request.getType());
        }
        if (request.getLevel() != null) {
            wrapper.eq(TradeWarning::getLevel, request.getLevel());
        }
        if (request.getStatus() != null) {
            wrapper.eq(TradeWarning::getStatus, request.getStatus());
        }
        if (request.getStartTime() != null) {
            wrapper.ge(TradeWarning::getCreatedAt, request.getStartTime());
        }
        if (request.getEndTime() != null) {
            wrapper.le(TradeWarning::getCreatedAt, request.getEndTime());
        }
        wrapper.orderByDesc(TradeWarning::getId);
        Page<TradeWarning> resultPage = tradeWarningMapper.selectPage(page, wrapper);
        List<TradeWarningResponse> records = resultPage.getRecords().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        Page<TradeWarningResponse> responsePage = new Page<>();
        BeanUtils.copyProperties(resultPage, responsePage);
        responsePage.setRecords(records);
        return PageResult.of(responsePage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scanAndGenerateWarnings() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDate.now().minusDays(30).atStartOfDay();
        LambdaQueryWrapper<CustomerOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(CustomerOrder::getCreatedAt, start);
        List<CustomerOrder> orders = customerOrderMapper.selectList(wrapper);
        if (orders.isEmpty()) {
            return;
        }
        for (CustomerOrder order : orders) {
            checkPriceAbnormal(order, now);
            checkPaymentAccountAbnormal(order, now);
            checkLongUnpaid(order, now);
            checkFrequentAmountChange(order, now);
            checkFrequentCancel(order, now);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeWarning(Long id, String reason) {
        TradeWarning warning = tradeWarningMapper.selectById(id);
        if (warning == null) {
            throw new BusinessException("预警记录不存在");
        }
        if (warning.getStatus() == TradeWarningStatus.CLOSED) {
            return;
        }
        warning.setStatus(TradeWarningStatus.CLOSED);
        warning.setClosedAt(LocalDateTime.now());
        warning.setClosedBy(currentUserId());
        if (StringUtils.hasText(reason)) {
            warning.setClosedReason(reason);
        }
        int updated = tradeWarningMapper.updateById(warning);
        if (updated != 1) {
            throw new BusinessException("关闭预警失败");
        }
    }

    private void checkPriceAbnormal(CustomerOrder order, LocalDateTime now) {
        if (order.getAmount() == null || order.getStandardAmount() == null) {
            return;
        }
        if (order.getStandardAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal diff = order.getAmount().subtract(order.getStandardAmount()).abs();
        BigDecimal ratio = diff.divide(order.getStandardAmount(), 4, RoundingMode.HALF_UP);
        if (ratio.compareTo(priceDeviationThreshold) < 0) {
            return;
        }
        WarningLevel level = WarningLevel.WARNING;
        if (ratio.compareTo(BigDecimal.valueOf(0.5)) >= 0) {
            level = WarningLevel.CRITICAL;
        }
        String message = buildPriceMessage(order, ratio);
        createOrUpdateWarning(order, TradeWarningType.PRICE_ABNORMAL, level, message, now);
    }

    private void checkPaymentAccountAbnormal(CustomerOrder order, LocalDateTime now) {
        boolean offline = Boolean.TRUE.equals(order.getOfflinePayment());
        boolean accountAbnormal = order.getPaymentAccount() != null
                && !order.getPaymentAccount().isEmpty()
                && !order.getPaymentAccount().equals(companyPaymentAccount);
        if (!offline && !accountAbnormal) {
            return;
        }
        WarningLevel level = offline ? WarningLevel.WARNING : WarningLevel.CRITICAL;
        String message;
        if (offline && accountAbnormal) {
            message = "订单存在非公司账户且标记为线下收款";
        } else if (offline) {
            message = "订单标记为线下收款";
        } else {
            message = "订单收款账户与公司账户不一致";
        }
        createOrUpdateWarning(order, TradeWarningType.PAYMENT_ACCOUNT_ABNORMAL, level, message, now);
    }

    private void checkLongUnpaid(CustomerOrder order, LocalDateTime now) {
        if (order.getPaymentStatus() != PaymentStatus.UNPAID) {
            return;
        }
        if (order.getCreatedAt() == null) {
            return;
        }
        long days = ChronoUnit.DAYS.between(order.getCreatedAt(), now);
        if (days < longUnpaidDays) {
            return;
        }
        WarningLevel level = days >= longUnpaidDays * 2 ? WarningLevel.CRITICAL : WarningLevel.WARNING;
        String message = "订单创建超过" + days + "天仍未收款";
        createOrUpdateWarning(order, TradeWarningType.LONG_UNPAID, level, message, now);
    }

    private void checkFrequentAmountChange(CustomerOrder order, LocalDateTime now) {
        Integer count = order.getAmountChangeCount();
        if (count == null || count < maxAmountChangeCount) {
            return;
        }
        WarningLevel level = count >= maxAmountChangeCount + 2 ? WarningLevel.CRITICAL : WarningLevel.WARNING;
        String message = "订单金额修改次数过多, 次数=" + count;
        createOrUpdateWarning(order, TradeWarningType.FREQUENT_AMOUNT_CHANGE, level, message, now);
    }

    private void checkFrequentCancel(CustomerOrder order, LocalDateTime now) {
        Integer count = order.getCancelCount();
        if (count == null || count < maxCancelCount) {
            return;
        }
        WarningLevel level = count >= maxCancelCount + 2 ? WarningLevel.CRITICAL : WarningLevel.WARNING;
        String message = "订单作废或取消次数过多, 次数=" + count;
        createOrUpdateWarning(order, TradeWarningType.FREQUENT_CANCEL, level, message, now);
    }

    private String buildPriceMessage(CustomerOrder order, BigDecimal ratio) {
        BigDecimal percent = ratio.multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        return "订单金额与标准金额偏差" + percent.toPlainString() + "%";
    }

    private void createOrUpdateWarning(CustomerOrder order, TradeWarningType type, WarningLevel level, String message,
                                       LocalDateTime now) {
        LambdaQueryWrapper<TradeWarning> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TradeWarning::getOrderId, order.getId())
                .eq(TradeWarning::getType, type)
                .eq(TradeWarning::getStatus, TradeWarningStatus.OPEN);
        TradeWarning exist = tradeWarningMapper.selectOne(wrapper);
        if (exist != null) {
            exist.setLastDetectedAt(now);
            if (level.ordinal() > exist.getLevel().ordinal()) {
                exist.setLevel(level);
            }
            if (StringUtils.hasText(message)) {
                exist.setMessage(message);
            }
            tradeWarningMapper.updateById(exist);
            return;
        }
        TradeWarning warning = new TradeWarning();
        warning.setOrderId(order.getId());
        warning.setCustomerId(order.getCustomerId());
        warning.setEmployeeId(order.getEmployeeId());
        warning.setType(type);
        warning.setLevel(level);
        warning.setStatus(TradeWarningStatus.OPEN);
        warning.setMessage(message);
        warning.setFirstDetectedAt(now);
        warning.setLastDetectedAt(now);
        tradeWarningMapper.insert(warning);
        log.info("创建异常交易预警, orderId={}, type={}", order.getId(), type);
    }

    private TradeWarningResponse toResponse(TradeWarning warning) {
        TradeWarningResponse response = new TradeWarningResponse();
        BeanUtils.copyProperties(warning, response);
        return response;
    }

    private Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new BusinessException("未登录");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new BusinessException("无法获取当前登录用户ID");
    }
}
