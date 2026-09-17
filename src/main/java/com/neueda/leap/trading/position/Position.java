package com.neueda.leap.trading.position;

import java.math.BigDecimal; import java.time.Instant; import java.util.UUID; import com.neueda.leap.trading.instrument.Instrument; import jakarta.persistence.*;
@Entity @IdClass(PositionId.class) @Table(name="positions",schema="trading")
public class Position {
 @Id @Column(name="account_id",nullable=false) private UUID accountId; @Id @Column(name="instrument_id",nullable=false) private UUID instrumentId;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="instrument_id",insertable=false,updatable=false) private Instrument instrument;
 @Column(nullable=false,precision=20,scale=8) private BigDecimal quantity; @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @Version @Column(nullable=false) private long version; protected Position(){}
 public static Position create(UUID accountId,UUID instrumentId,BigDecimal quantity,Instant now){Position p=new Position();p.accountId=accountId;p.instrumentId=instrumentId;p.quantity=quantity;p.updatedAt=now;return p;}
 public void apply(BigDecimal delta,Instant now){BigDecimal next=quantity.add(delta);if(next.signum()<0)throw new IllegalStateException("Position cannot become negative");quantity=next;updatedAt=now;}
 public UUID getAccountId(){return accountId;} public UUID getInstrumentId(){return instrumentId;} public Instrument getInstrument(){return instrument;} public BigDecimal getQuantity(){return quantity;} public Instant getUpdatedAt(){return updatedAt;}
}
