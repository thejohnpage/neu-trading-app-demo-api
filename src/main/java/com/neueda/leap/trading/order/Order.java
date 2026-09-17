package com.neueda.leap.trading.order;

import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
import jakarta.persistence.*;
@Entity @Table(name="orders",schema="trading")
public class Order {
 @Id @Column(name="order_id") private UUID orderId; @Column(name="account_id",nullable=false) private UUID accountId;
 @Column(name="instrument_id",nullable=false) private UUID instrumentId; @Column(nullable=false,length=4) private String side;
 @Column(nullable=false,precision=20,scale=8) private BigDecimal quantity; @Column(nullable=false,length=20) private String status;
 @Column(name="rejection_reason",length=500) private String rejectionReason; @Column(name="submitted_at",nullable=false) private Instant submittedAt;
 @Column(name="accepted_at") private Instant acceptedAt; @Column(name="completed_at") private Instant completedAt; @Version @Column(nullable=false) private long version;
 protected Order(){}
 public static Order accepted(UUID accountId,UUID instrumentId,String side,BigDecimal quantity,Instant now){Order o=new Order();o.orderId=UUID.randomUUID();o.accountId=accountId;o.instrumentId=instrumentId;o.side=side;o.quantity=quantity;o.status="ACCEPTED";o.submittedAt=now;o.acceptedAt=now;return o;}
 public void markFilled(Instant now){if(!"ACCEPTED".equals(status))throw new IllegalStateException("Only ACCEPTED orders can be filled");status="FILLED";completedAt=now;}
 public UUID getOrderId(){return orderId;} public UUID getAccountId(){return accountId;} public UUID getInstrumentId(){return instrumentId;} public String getSide(){return side;} public BigDecimal getQuantity(){return quantity;} public String getStatus(){return status;} public String getRejectionReason(){return rejectionReason;} public Instant getSubmittedAt(){return submittedAt;} public Instant getAcceptedAt(){return acceptedAt;} public Instant getCompletedAt(){return completedAt;}
}
