package com.neueda.leap.trading.execution;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID; import jakarta.persistence.*;
@Entity @Table(name="fills",schema="trading")
public class Fill {
 @Id @Column(name="fill_id") private UUID fillId; @Column(name="order_id",nullable=false) private UUID orderId;
 @Column(nullable=false,precision=20,scale=8) private BigDecimal quantity;
 @Column(nullable=false,precision=20,scale=8) private BigDecimal price;
 @Column(name="filled_at",nullable=false) private Instant filledAt; protected Fill(){}
 public static Fill create(UUID orderId,BigDecimal quantity,BigDecimal price,Instant now){Fill f=new Fill();f.fillId=UUID.randomUUID();f.orderId=orderId;f.quantity=quantity;f.price=price;f.filledAt=now;return f;}
}
