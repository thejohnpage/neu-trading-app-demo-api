package com.neueda.leap.trading.order;

import java.time.Instant; import java.util.UUID; import jakarta.persistence.*; import org.hibernate.annotations.JdbcTypeCode; import org.hibernate.type.SqlTypes;
@Entity @Table(name="order_events",schema="trading")
public class OrderEvent {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="order_event_id") private Long orderEventId;
 @Column(name="order_id",nullable=false) private UUID orderId; @Column(name="event_type",nullable=false,length=30) private String eventType;
 @Column(name="event_time",nullable=false) private Instant eventTime; @JdbcTypeCode(SqlTypes.JSON) @Column(nullable=false,columnDefinition="jsonb") private String details;
 protected OrderEvent(){} public static OrderEvent of(UUID orderId,String eventType,Instant time){OrderEvent e=new OrderEvent();e.orderId=orderId;e.eventType=eventType;e.eventTime=time;e.details="{}";return e;}
 public Long getOrderEventId(){return orderEventId;} public UUID getOrderId(){return orderId;} public String getEventType(){return eventType;} public Instant getEventTime(){return eventTime;} public String getDetails(){return details;}
}
