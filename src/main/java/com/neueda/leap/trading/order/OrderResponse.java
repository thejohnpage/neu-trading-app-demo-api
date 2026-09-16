package com.neueda.leap.trading.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(UUID orderId, UUID accountId, UUID instrumentId, String side,
                            BigDecimal quantity, String status, Instant submittedAt, Instant acceptedAt) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getOrderId(), order.getAccountId(), order.getInstrumentId(),
                order.getSide(), order.getQuantity(), order.getStatus(), order.getSubmittedAt(), order.getAcceptedAt());
    }
}
