package com.neueda.leap.trading.execution;

import java.time.Instant;
import tools.jackson.databind.ObjectMapper;
import com.neueda.leap.trading.order.OutboxEvent;
import com.neueda.leap.trading.order.OutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {
 private final OutboxEventRepository outbox; private final KafkaTemplate<String,String> kafka; private final ObjectMapper json;
 public OutboxPublisher(OutboxEventRepository outbox,KafkaTemplate<String,String> kafka,ObjectMapper json){this.outbox=outbox;this.kafka=kafka;this.json=json;}
 @Scheduled(fixedDelayString="${app.outbox.poll-ms:1000}") @Transactional
 public void publish(){for(OutboxEvent event:outbox.findTop50ByPublishedAtIsNullOrderByCreatedAtAsc()){
   if(!"order.accepted".equals(event.getEventType()))continue;
   try{OrderAcceptedMessage message=json.readValue(event.getPayload(),OrderAcceptedMessage.class);
       kafka.send("order.accepted",message.orderId().toString(),event.getPayload()).get(); event.markPublished(Instant.now());}
   catch(Exception e){throw new IllegalStateException("Unable to publish outbox event "+event.getEventId(),e);}
 }}
}
