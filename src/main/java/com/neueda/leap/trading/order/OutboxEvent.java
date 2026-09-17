package com.neueda.leap.trading.order;

import java.time.Instant; import java.util.UUID; import jakarta.persistence.*; import org.hibernate.annotations.JdbcTypeCode; import org.hibernate.type.SqlTypes;
@Entity @Table(name="outbox_events",schema="trading")
public class OutboxEvent {
 @Id @Column(name="event_id") private UUID eventId; @Column(name="aggregate_type",nullable=false,length=50) private String aggregateType;
 @Column(name="aggregate_id",nullable=false) private UUID aggregateId; @Column(name="event_type",nullable=false,length=100) private String eventType;
 @JdbcTypeCode(SqlTypes.JSON) @Column(nullable=false,columnDefinition="jsonb") private String payload;
 @Column(name="created_at",nullable=false) private Instant createdAt; @Column(name="published_at") private Instant publishedAt; protected OutboxEvent(){}
 public static OutboxEvent orderAccepted(Order order,Instant now){OutboxEvent e=new OutboxEvent();e.eventId=UUID.randomUUID();e.aggregateType="ORDER";e.aggregateId=order.getOrderId();e.eventType="order.accepted";e.createdAt=now;e.payload="{\"orderId\":\""+order.getOrderId()+"\"}";return e;}
 public void markPublished(Instant now){publishedAt=now;} public UUID getEventId(){return eventId;} public UUID getAggregateId(){return aggregateId;} public String getEventType(){return eventType;} public String getPayload(){return payload;} public Instant getPublishedAt(){return publishedAt;}
}
