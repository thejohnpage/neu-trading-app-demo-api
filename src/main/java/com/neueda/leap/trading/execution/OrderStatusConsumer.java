package com.neueda.leap.trading.execution;
import com.neueda.leap.trading.account.AccountRepository;import tools.jackson.databind.ObjectMapper;import org.springframework.kafka.annotation.KafkaListener;import org.springframework.stereotype.Component;
@Component public class OrderStatusConsumer{
 private final ObjectMapper json;private final OrderStatusStream stream;private final AccountRepository accounts;
 public OrderStatusConsumer(ObjectMapper json,OrderStatusStream stream,AccountRepository accounts){this.json=json;this.stream=stream;this.accounts=accounts;}
 @KafkaListener(topics="order.status",groupId="trading-client-status")
 public void consume(String payload){try{OrderStatusMessage m=json.readValue(payload,OrderStatusMessage.class);accounts.findByAccountId(m.accountId()).ifPresent(a->stream.emit(a.getClientId(),m));}catch(Exception e){throw new IllegalStateException("Unable to consume order status",e);}}
}
