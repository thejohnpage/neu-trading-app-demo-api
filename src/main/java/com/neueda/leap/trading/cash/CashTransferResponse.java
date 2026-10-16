package com.neueda.leap.trading.cash;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CashTransferResponse(
    UUID transferId, UUID fromAccountId, UUID toAccountId,
    String fromCurrency, String toCurrency,
    BigDecimal debitedAmount, BigDecimal creditedAmount,
    BigDecimal exchangeRate, String rateSource, Instant createdAt
) {}
