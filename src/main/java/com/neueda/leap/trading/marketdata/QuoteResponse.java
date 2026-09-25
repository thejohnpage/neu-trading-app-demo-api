package com.neueda.leap.trading.marketdata;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record QuoteResponse(
        UUID instrumentId,
        String symbol,
        String quoteCurrency,
        BigDecimal bid,
        BigDecimal ask,
        String source,
        Instant quotedAt) {
}
