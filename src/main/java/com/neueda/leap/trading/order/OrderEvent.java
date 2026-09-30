package com.neueda.leap.trading.order;
import java.time.Instant; import java.util.UUID;
/** Order lifecycle event persisted by MyBatis. */
public class OrderEvent {
 private Long orderEventId; private UUID orderId; private String eventType; private Instant eventTime; private String details;
 public OrderEvent(){} public static OrderEvent of(UUID id,String type,Instant time){OrderEvent e=new OrderEvent();e.orderId=id;e.eventType=type;e.eventTime=time;e.details="{}";return e;}
 public Long getOrderEventId(){return orderEventId;} public void setOrderEventId(Long v){orderEventId=v;} public UUID getOrderId(){return orderId;} public void setOrderId(UUID v){orderId=v;} public String getEventType(){return eventType;} public void setEventType(String v){eventType=v;} public Instant getEventTime(){return eventTime;} public void setEventTime(Instant v){eventTime=v;} public String getDetails(){return details;} public void setDetails(String v){details=v;}
}
