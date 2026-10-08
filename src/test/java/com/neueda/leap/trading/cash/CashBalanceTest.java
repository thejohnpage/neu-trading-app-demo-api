package com.neueda.leap.trading.cash;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CashBalanceTest {
    @Test void openingBalanceStartsAtZero() {
        UUID account=UUID.randomUUID();
        Instant now=Instant.parse("2026-10-08T12:00:00Z");
        CashBalance balance=CashBalance.open(account,"USD",now);
        assertEquals(account,balance.getAccountId());
        assertEquals("USD",balance.getCurrency());
        assertEquals(0,BigDecimal.ZERO.compareTo(balance.getBalance()));
        assertEquals(now,balance.getUpdatedAt());
    }

    @Test void applyingCreditAndDebitPreservesDecimalPrecision() {
        CashBalance balance=CashBalance.open(UUID.randomUUID(),"USD",Instant.now());
        Instant later=Instant.parse("2026-10-08T13:00:00Z");
        balance.apply(new BigDecimal("100.25"),Instant.now());
        balance.apply(new BigDecimal("-30.10"),later);
        assertEquals(0,new BigDecimal("70.15").compareTo(balance.getBalance()));
        assertEquals(later,balance.getUpdatedAt());
    }

    @Test void debitCannotMakeBalanceNegative() {
        CashBalance balance=CashBalance.open(UUID.randomUUID(),"USD",Instant.now());
        assertThrows(IllegalStateException.class,
            ()->balance.apply(new BigDecimal("-0.01"),Instant.now()));
        assertEquals(0,BigDecimal.ZERO.compareTo(balance.getBalance()));
    }

    @Test void zeroBalanceIsPermitted() {
        CashBalance balance=CashBalance.open(UUID.randomUUID(),"GBP",Instant.now());
        balance.apply(new BigDecimal("10.00"),Instant.now());
        balance.apply(new BigDecimal("-10.00"),Instant.now());
        assertEquals(0,BigDecimal.ZERO.compareTo(balance.getBalance()));
    }
}
