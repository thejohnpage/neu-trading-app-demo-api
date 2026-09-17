package com.neueda.leap.trading.order;
import java.time.Instant;
public record OrderEventResponse(Long eventId,String eventType,Instant eventTime,String details){public static OrderEventResponse from(OrderEvent e){return new OrderEventResponse(e.getOrderEventId(),e.getEventType(),e.getEventTime(),e.getDetails());}}
