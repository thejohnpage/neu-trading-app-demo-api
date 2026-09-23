package com.neueda.leap.trading.cash;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
/** Currency cash balance with explicit optimistic-lock version. */
public class CashBalance {
 private UUID accountId; private String currency; private BigDecimal balance; private Instant updatedAt; private long version;
 public CashBalance(){}
 public static CashBalance open(UUID accountId,String currency,Instant now){CashBalance b=new CashBalance();b.accountId=accountId;b.currency=currency;b.balance=BigDecimal.ZERO;b.updatedAt=now;return b;}
 public void apply(BigDecimal amount,Instant now){BigDecimal next=balance.add(amount);if(next.signum()<0)throw new IllegalStateException("Cash balance cannot become negative");balance=next;updatedAt=now;}
 public UUID getAccountId(){return accountId;} public void setAccountId(UUID v){accountId=v;} public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;} public BigDecimal getBalance(){return balance;} public void setBalance(BigDecimal v){balance=v;} public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;} public long getVersion(){return version;} public void setVersion(long v){version=v;}
}
