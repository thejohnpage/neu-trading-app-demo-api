package com.neueda.leap.trading.cash;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CashBalanceResponse(
        UUID accountId,
        String currency,
        BigDecimal balance,
        Instant updatedAt) {

    public static CashBalanceResponse from(CashBalance cashBalance) {
        return new CashBalanceResponse(
                cashBalance.getAccountId(),
                cashBalance.getCurrency(),
                cashBalance.getBalance(),
                cashBalance.getUpdatedAt());
    }
}
