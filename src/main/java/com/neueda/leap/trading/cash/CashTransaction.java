package com.neueda.leap.trading.cash;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
/** Immutable cash-ledger transaction persisted by MyBatis. */
public class CashTransaction {
 private UUID id; private UUID accountId; private UUID orderId; private String currency; private BigDecimal amount; private String type; private Instant createdAt;
 public CashTransaction(){}
 public static CashTransaction trade(UUID a,UUID o,String c,BigDecimal amount,String type,Instant now){return create(a,o,c,amount,type,now);} public static CashTransaction movement(UUID a,String c,BigDecimal amount,String type,Instant now){return create(a,null,c,amount,type,now);}
 private static CashTransaction create(UUID a,UUID o,String c,BigDecimal amount,String type,Instant now){CashTransaction t=new CashTransaction();t.id=UUID.randomUUID();t.accountId=a;t.orderId=o;t.currency=c;t.amount=amount;t.type=type;t.createdAt=now;return t;}
 public UUID getId(){return id;} public void setId(UUID v){id=v;} public UUID getAccountId(){return accountId;} public void setAccountId(UUID v){accountId=v;} public UUID getOrderId(){return orderId;} public void setOrderId(UUID v){orderId=v;} public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;} public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;} public String getType(){return type;} public void setType(String v){type=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
}
