package com.neueda.leap.trading.execution;
import tools.jackson.databind.ObjectMapper;import org.springframework.kafka.annotation.KafkaListener;import org.springframework.stereotype.Component;
@Component public class OrderStatusConsumer{
 private final ObjectMapper json;private final OrderStatusStream stream;
 public OrderStatusConsumer(ObjectMapper json,OrderStatusStream stream){this.json=json;this.stream=stream;}
 @KafkaListener(topics="order.status",groupId="trading-client-status")
 public void consume(String payload){try{stream.emit(json.readValue(payload,OrderStatusMessage.class));}catch(Exception e){throw new IllegalStateException("Unable to consume order status",e);}}
}
