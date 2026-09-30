package com.neueda.leap.trading.execution;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
/** Executed fill persisted by MyBatis. */
public class Fill {
 private UUID fillId; private UUID orderId; private BigDecimal quantity; private BigDecimal price; private Instant filledAt;
 public Fill(){} public static Fill create(UUID o,BigDecimal q,BigDecimal p,Instant now){Fill f=new Fill();f.fillId=UUID.randomUUID();f.orderId=o;f.quantity=q;f.price=p;f.filledAt=now;return f;}
 public UUID getFillId(){return fillId;} public void setFillId(UUID v){fillId=v;} public UUID getOrderId(){return orderId;} public void setOrderId(UUID v){orderId=v;} public BigDecimal getQuantity(){return quantity;} public void setQuantity(BigDecimal v){quantity=v;} public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;} public Instant getFilledAt(){return filledAt;} public void setFilledAt(Instant v){filledAt=v;}
}
