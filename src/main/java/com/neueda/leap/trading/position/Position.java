package com.neueda.leap.trading.position;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;
import com.neueda.leap.trading.instrument.Instrument;
import jakarta.persistence.*;

@Entity @IdClass(PositionId.class) @Table(name="positions",schema="trading")
public class Position {
 @Id @Column(name="account_id",nullable=false) private UUID accountId;
 @Id @Column(name="instrument_id",nullable=false) private UUID instrumentId;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="instrument_id",insertable=false,updatable=false) private Instrument instrument;
 @Column(nullable=false,precision=20,scale=8) private BigDecimal quantity;
 @Column(name="cost_basis",nullable=false,precision=28,scale=8) private BigDecimal costBasis;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @Version @Column(nullable=false) private long version;

 protected Position(){}

 public static Position create(UUID accountId,UUID instrumentId,BigDecimal quantity,BigDecimal executionPrice,Instant now){
  Position p=new Position();p.accountId=accountId;p.instrumentId=instrumentId;p.quantity=quantity;
  p.costBasis=quantity.multiply(executionPrice);p.updatedAt=now;return p;
 }

 public void buy(BigDecimal addedQuantity,BigDecimal executionPrice,Instant now){
  quantity=quantity.add(addedQuantity);costBasis=costBasis.add(addedQuantity.multiply(executionPrice));updatedAt=now;
 }

 public void sell(BigDecimal soldQuantity,Instant now){
  if(quantity.compareTo(soldQuantity)<0)throw new IllegalStateException("Position cannot become negative");
  if(quantity.compareTo(soldQuantity)==0){quantity=BigDecimal.ZERO;costBasis=BigDecimal.ZERO;}
  else{
   BigDecimal averageCost=costBasis.divide(quantity,12,RoundingMode.HALF_UP);
   quantity=quantity.subtract(soldQuantity);
   costBasis=costBasis.subtract(averageCost.multiply(soldQuantity)).max(BigDecimal.ZERO);
  }
  updatedAt=now;
 }

 public UUID getAccountId(){return accountId;} public UUID getInstrumentId(){return instrumentId;} public Instrument getInstrument(){return instrument;}
 public BigDecimal getQuantity(){return quantity;} public BigDecimal getCostBasis(){return costBasis;} public Instant getUpdatedAt(){return updatedAt;}
}
