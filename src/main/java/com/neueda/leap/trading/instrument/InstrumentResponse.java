package com.neueda.leap.trading.instrument;

import java.util.UUID;

public record InstrumentResponse(
        UUID instrumentId,
        String symbol,
        String instrumentType,
        String exchange,
        String baseCurrency,
        String quoteCurrency,
        String name,
        boolean tradable) {

    public static InstrumentResponse from(Instrument instrument) {
        return new InstrumentResponse(
                instrument.getInstrumentId(),
                instrument.getSymbol(),
                instrument.getInstrumentType(),
                instrument.getExchange(),
                instrument.getBaseCurrency(),
                instrument.getQuoteCurrency(),
                instrument.getName(),
                instrument.isTradable());
    }
}
