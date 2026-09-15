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

    public static QuoteResponse from(MarketQuote quote) {
        return new QuoteResponse(
                quote.getInstrument().getInstrumentId(),
                quote.getInstrument().getSymbol(),
                quote.getInstrument().getQuoteCurrency(),
                quote.getBidPrice(),
                quote.getAskPrice(),
                quote.getSource(),
                quote.getQuotedAt());
    }
}
