package com.neueda.leap.trading.cash;

import java.math.BigDecimal;

public record CashConversionResponse(String fromCurrency,String toCurrency,BigDecimal debitedAmount,BigDecimal creditedAmount,BigDecimal rate,String source) {}
