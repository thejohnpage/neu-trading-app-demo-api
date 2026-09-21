package com.neueda.leap.trading.cash;

import java.math.BigDecimal;
import java.time.Instant;

public record FxRateResponse(String fromCurrency,String toCurrency,BigDecimal rate,String source,Instant quotedAt) {}
