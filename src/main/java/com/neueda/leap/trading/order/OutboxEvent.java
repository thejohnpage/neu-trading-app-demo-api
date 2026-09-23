package com.neueda.leap.trading.order;
import java.time.Instant; import java.util.UUID;
/** Transactional-outbox event persisted by MyBatis. */
public class OutboxEvent {
 private UUID eventId; private String aggregateType; private UUID aggregateId; private String eventType; private String payload; private Instant createdAt; private Instant publishedAt;
 public OutboxEvent(){} public static OutboxEvent orderAccepted(Order o,Instant now){OutboxEvent e=new OutboxEvent();e.eventId=UUID.randomUUID();e.aggregateType="ORDER";e.aggregateId=o.getOrderId();e.eventType="order.accepted";e.createdAt=now;e.payload="{\"orderId\":\""+o.getOrderId()+"\"}";return e;}
 public void markPublished(Instant now){publishedAt=now;} public UUID getEventId(){return eventId;} public void setEventId(UUID v){eventId=v;} public String getAggregateType(){return aggregateType;} public void setAggregateType(String v){aggregateType=v;} public UUID getAggregateId(){return aggregateId;} public void setAggregateId(UUID v){aggregateId=v;} public String getEventType(){return eventType;} public void setEventType(String v){eventType=v;} public String getPayload(){return payload;} public void setPayload(String v){payload=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;} public Instant getPublishedAt(){return publishedAt;} public void setPublishedAt(Instant v){publishedAt=v;}
}
