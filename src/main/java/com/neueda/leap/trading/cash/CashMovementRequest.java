package com.neueda.leap.trading.cash;

import java.math.BigDecimal;
import java.util.UUID;

public record CashMovementRequest(UUID accountId,String currency,BigDecimal amount) {}
