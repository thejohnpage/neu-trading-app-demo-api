package com.neueda.leap.trading.order;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
/** Order aggregate persisted explicitly by MyBatis. */
public class Order {
 private UUID orderId; private UUID accountId; private UUID instrumentId; private String side; private BigDecimal quantity; private String status; private String rejectionReason; private Instant submittedAt; private Instant acceptedAt; private Instant completedAt; private long version;
 public Order(){}
 public static Order accepted(UUID accountId,UUID instrumentId,String side,BigDecimal quantity,Instant now){Order o=new Order();o.orderId=UUID.randomUUID();o.accountId=accountId;o.instrumentId=instrumentId;o.side=side;o.quantity=quantity;o.status="ACCEPTED";o.submittedAt=now;o.acceptedAt=now;return o;}
 public void markFilled(Instant now){if(!"ACCEPTED".equals(status))throw new IllegalStateException("Only ACCEPTED orders can be filled");status="FILLED";completedAt=now;}
 public UUID getOrderId(){return orderId;} public void setOrderId(UUID v){orderId=v;} public UUID getAccountId(){return accountId;} public void setAccountId(UUID v){accountId=v;} public UUID getInstrumentId(){return instrumentId;} public void setInstrumentId(UUID v){instrumentId=v;}
 public String getSide(){return side;} public void setSide(String v){side=v;} public BigDecimal getQuantity(){return quantity;} public void setQuantity(BigDecimal v){quantity=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;}
 public String getRejectionReason(){return rejectionReason;} public void setRejectionReason(String v){rejectionReason=v;} public Instant getSubmittedAt(){return submittedAt;} public void setSubmittedAt(Instant v){submittedAt=v;} public Instant getAcceptedAt(){return acceptedAt;} public void setAcceptedAt(Instant v){acceptedAt=v;} public Instant getCompletedAt(){return completedAt;} public void setCompletedAt(Instant v){completedAt=v;} public long getVersion(){return version;} public void setVersion(long v){version=v;}
}
