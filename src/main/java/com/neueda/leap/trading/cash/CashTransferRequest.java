package com.neueda.leap.trading.cash;

import java.math.BigDecimal;
import java.util.UUID;

public record CashTransferRequest(
    UUID transferId,
    UUID fromAccountId,
    UUID toAccountId,
    BigDecimal amount
) {}
