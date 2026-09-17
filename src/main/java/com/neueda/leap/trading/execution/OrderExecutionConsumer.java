package com.neueda.leap.trading.execution;

import com.fasterxml.jackson.databind.ObjectMapper; import org.springframework.kafka.annotation.KafkaListener; import org.springframework.stereotype.Component;
@Component
public class OrderExecutionConsumer {
 private final ObjectMapper json; private final OrderExecutionService execution;
 public OrderExecutionConsumer(ObjectMapper json,OrderExecutionService execution){this.json=json;this.execution=execution;}
 @KafkaListener(topics="order.accepted",groupId="${spring.kafka.consumer.group-id:trading-execution}")
 public void consume(String payload){try{OrderAcceptedMessage message=json.readValue(payload,OrderAcceptedMessage.class);execution.execute(message.orderId());}catch(Exception e){throw new IllegalStateException("Unable to execute accepted order",e);}}
}
