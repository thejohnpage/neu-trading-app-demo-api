package com.neueda.leap.trading.cash;

import java.math.BigDecimal;
import java.util.UUID;

public record CashConversionRequest(UUID accountId,String fromCurrency,String toCurrency,BigDecimal amount) {}
