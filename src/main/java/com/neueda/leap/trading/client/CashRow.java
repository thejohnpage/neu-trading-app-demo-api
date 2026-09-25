package com.neueda.leap.trading.client;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
/** MyBatis result object for a client cash balance. */
public class CashRow {
 private UUID accountId; private String currency; private BigDecimal balance; private Instant updatedAt;
 public CashRow(){}
 public UUID getAccountId(){return accountId;} public void setAccountId(UUID v){accountId=v;}
 public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;}
 public BigDecimal getBalance(){return balance;} public void setBalance(BigDecimal v){balance=v;}
 public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
