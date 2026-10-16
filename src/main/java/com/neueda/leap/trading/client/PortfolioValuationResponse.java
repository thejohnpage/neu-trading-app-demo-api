package com.neueda.leap.trading.client;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PortfolioValuationResponse(
    String reportingCurrency, UUID accountId,
    BigDecimal totalCash, BigDecimal totalMarketValue, BigDecimal totalCostBasis,
    BigDecimal unrealizedProfitLoss, BigDecimal totalEquity,
    List<ValuedCash> cash, List<ValuedPosition> positions, List<AppliedRate> rates
) {
    public record ValuedCash(UUID accountId, String currency, BigDecimal nativeBalance,
                             BigDecimal reportedBalance) {}
    public record ValuedPosition(UUID accountId, UUID instrumentId, String symbol,
                                 String currency, BigDecimal nativeMarketValue,
                                 BigDecimal nativeCostBasis, BigDecimal nativeProfitLoss,
                                 BigDecimal reportedMarketValue, BigDecimal reportedCostBasis,
                                 BigDecimal reportedProfitLoss) {}
    public record AppliedRate(String fromCurrency, String toCurrency, BigDecimal rate,
                              String source, Instant quotedAt) {}
}
