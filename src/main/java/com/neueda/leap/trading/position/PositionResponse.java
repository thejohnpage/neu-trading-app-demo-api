package com.neueda.leap.trading.position;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PositionResponse(
        UUID accountId,
        UUID instrumentId,
        String symbol,
        String instrumentType,
        BigDecimal quantity,
        Instant updatedAt) {

    public static PositionResponse from(Position position) {
        return new PositionResponse(
                position.getAccountId(),
                position.getInstrumentId(),
                position.getInstrument().getSymbol(),
                position.getInstrument().getInstrumentType(),
                position.getQuantity(),
                position.getUpdatedAt());
    }
}
