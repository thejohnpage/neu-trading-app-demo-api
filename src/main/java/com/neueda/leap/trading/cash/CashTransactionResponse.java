package com.neueda.leap.trading.cash;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CashTransactionResponse(UUID transactionId,UUID accountId,UUID orderId,String currency,BigDecimal amount,String type,Instant createdAt) {
 public static CashTransactionResponse from(CashTransaction t){
  return new CashTransactionResponse(t.getId(),t.getAccountId(),t.getOrderId(),t.getCurrency(),t.getAmount(),t.getType(),t.getCreatedAt());
 }
}
