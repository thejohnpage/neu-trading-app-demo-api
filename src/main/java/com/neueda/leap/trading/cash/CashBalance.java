package com.neueda.leap.trading.cash;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.*;

@Entity
@IdClass(CashBalanceId.class)
@Table(name="cash_balances",schema="trading")
public class CashBalance {
 @Id @Column(name="account_id",nullable=false) private UUID accountId;
 @Id @Column(nullable=false,length=10) private String currency;
 @Column(nullable=false,precision=20,scale=8) private BigDecimal balance;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @Version @Column(nullable=false) private long version;

 protected CashBalance(){}

 public static CashBalance open(UUID accountId,String currency,Instant now){
  CashBalance b=new CashBalance();
  b.accountId=accountId;b.currency=currency;b.balance=BigDecimal.ZERO;b.updatedAt=now;
  return b;
 }

 public void apply(BigDecimal amount,Instant now){
  BigDecimal next=balance.add(amount);
  if(next.signum()<0)throw new IllegalStateException("Cash balance cannot become negative");
  balance=next;updatedAt=now;
 }

 public UUID getAccountId(){return accountId;}
 public String getCurrency(){return currency;}
 public BigDecimal getBalance(){return balance;}
 public Instant getUpdatedAt(){return updatedAt;}
}
