package com.neueda.leap.trading.execution;
import tools.jackson.databind.ObjectMapper;import org.springframework.kafka.core.KafkaTemplate;import org.springframework.stereotype.Component;
@Component public class OrderStatusPublisher{
 private final KafkaTemplate<String,String> kafka;private final ObjectMapper json;
 public OrderStatusPublisher(KafkaTemplate<String,String> kafka,ObjectMapper json){this.kafka=kafka;this.json=json;}
 public void publish(OrderStatusMessage message){try{kafka.send("order.status",message.orderId().toString(),json.writeValueAsString(message)).get();}catch(Exception e){throw new IllegalStateException("Unable to publish order status "+message.orderId(),e);}}
}
