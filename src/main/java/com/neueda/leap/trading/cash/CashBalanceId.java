package com.neueda.leap.trading.cash;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class CashBalanceId implements Serializable {
    private UUID accountId;
    private String currency;

    public CashBalanceId() {}

    public CashBalanceId(UUID accountId, String currency) {
        this.accountId = accountId;
        this.currency = currency;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof CashBalanceId that)) return false;
        return Objects.equals(accountId, that.accountId) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, currency);
    }
}
