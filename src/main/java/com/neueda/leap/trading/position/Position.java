package com.neueda.leap.trading.position;
import java.math.BigDecimal; import java.math.RoundingMode; import java.time.Instant; import java.util.UUID;
/** Security position with average-cost basis and explicit optimistic-lock version. */
public class Position {
 private UUID accountId; private UUID instrumentId; private BigDecimal quantity; private BigDecimal costBasis; private Instant updatedAt; private long version;
 public Position(){}
 public static Position create(UUID accountId,UUID instrumentId,BigDecimal quantity,BigDecimal executionPrice,Instant now){Position p=new Position();p.accountId=accountId;p.instrumentId=instrumentId;p.quantity=quantity;p.costBasis=quantity.multiply(executionPrice);p.updatedAt=now;return p;}
 public void buy(BigDecimal q,BigDecimal price,Instant now){quantity=quantity.add(q);costBasis=costBasis.add(q.multiply(price));updatedAt=now;}
 public void sell(BigDecimal q,Instant now){if(quantity.compareTo(q)<0)throw new IllegalStateException("Position cannot become negative");if(quantity.compareTo(q)==0){quantity=BigDecimal.ZERO;costBasis=BigDecimal.ZERO;}else{BigDecimal avg=costBasis.divide(quantity,12,RoundingMode.HALF_UP);quantity=quantity.subtract(q);costBasis=costBasis.subtract(avg.multiply(q)).max(BigDecimal.ZERO);}updatedAt=now;}
 public UUID getAccountId(){return accountId;} public void setAccountId(UUID v){accountId=v;} public UUID getInstrumentId(){return instrumentId;} public void setInstrumentId(UUID v){instrumentId=v;} public BigDecimal getQuantity(){return quantity;} public void setQuantity(BigDecimal v){quantity=v;} public BigDecimal getCostBasis(){return costBasis;} public void setCostBasis(BigDecimal v){costBasis=v;} public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;} public long getVersion(){return version;} public void setVersion(long v){version=v;}
}
