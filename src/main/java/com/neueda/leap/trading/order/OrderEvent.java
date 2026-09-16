package com.neueda.leap.trading.order;

import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Table(name="order_events", schema="trading")
public class OrderEvent {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="order_event_id") private Long orderEventId;
    @Column(name="order_id", nullable=false) private UUID orderId;
    @Column(name="event_type", nullable=false, length=30) private String eventType;
    @Column(name="event_time", nullable=false) private Instant eventTime;
    @Column(nullable=false, columnDefinition="jsonb") private String details;
    protected OrderEvent() {}
    public static OrderEvent of(UUID orderId, String eventType, Instant time) {
        OrderEvent event = new OrderEvent(); event.orderId=orderId; event.eventType=eventType;
        event.eventTime=time; event.details="{}"; return event;
    }
}
