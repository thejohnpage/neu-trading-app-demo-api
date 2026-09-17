package com.neueda.leap.trading.cash;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID; import jakarta.persistence.*;
@Entity @Table(name="cash_transactions",schema="trading")
public class CashTransaction {
 @Id @Column(name="cash_transaction_id") private UUID id; @Column(name="account_id",nullable=false) private UUID accountId;
 @Column(name="order_id") private UUID orderId; @Column(nullable=false,length=10) private String currency;
 @Column(nullable=false,precision=20,scale=8) private BigDecimal amount;
 @Column(name="transaction_type",nullable=false,length=30) private String type; @Column(name="created_at",nullable=false) private Instant createdAt;
 protected CashTransaction(){} public static CashTransaction trade(UUID accountId,UUID orderId,String currency,BigDecimal amount,String type,Instant now){CashTransaction t=new CashTransaction();t.id=UUID.randomUUID();t.accountId=accountId;t.orderId=orderId;t.currency=currency;t.amount=amount;t.type=type;t.createdAt=now;return t;}
}
