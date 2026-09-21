package com.neueda.leap.trading.position;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PositionResponse(
        UUID accountId,
        UUID instrumentId,
        String symbol,
        String instrumentType,
        String currency,
        BigDecimal quantity,
        BigDecimal currentPrice,
        BigDecimal marketValue,
        BigDecimal costBasis,
        BigDecimal unrealizedGainLoss,
        BigDecimal unrealizedGainLossPercent,
        Instant updatedAt) {
}
