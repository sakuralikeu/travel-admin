package com.travel.admin.service;

import com.travel.admin.common.result.PageResult;
import com.travel.admin.dto.warning.TradeWarningQueryRequest;
import com.travel.admin.dto.warning.TradeWarningResponse;

public interface TradeWarningService {

    PageResult<TradeWarningResponse> getTradeWarningPage(TradeWarningQueryRequest request);

    void scanAndGenerateWarnings();

    void closeWarning(Long id, String reason);
}
