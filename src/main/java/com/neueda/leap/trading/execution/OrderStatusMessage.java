package com.neueda.leap.trading.execution;
import java.math.BigDecimal;import java.time.Instant;import java.util.UUID;
public record OrderStatusMessage(UUID orderId,UUID accountId,UUID instrumentId,String side,BigDecimal quantity,String status,BigDecimal executionPrice,Instant timestamp){}
