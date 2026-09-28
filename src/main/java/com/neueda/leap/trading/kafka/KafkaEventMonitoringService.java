package com.neueda.leap.trading.kafka;
import java.time.Duration;import java.time.Instant;import java.util.*;import org.apache.kafka.clients.consumer.*;import org.apache.kafka.common.*;import org.springframework.beans.factory.annotation.Value;import org.springframework.stereotype.Service;import tools.jackson.databind.*;
@Service public class KafkaEventMonitoringService{
 private final String bootstrap;private final ObjectMapper json;
 public KafkaEventMonitoringService(@Value("${spring.kafka.bootstrap-servers}")String bootstrap,ObjectMapper json){this.bootstrap=bootstrap;this.json=json;}
 public List<KafkaEventResponse> recent(int limit){
  Properties p=new Properties();p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrap);p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");p.put(ConsumerConfig.GROUP_ID_CONFIG,"kafka-monitor-"+UUID.randomUUID());p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"false");
  List<KafkaEventResponse> result=new ArrayList<>();
  try(KafkaConsumer<String,String> c=new KafkaConsumer<>(p)){
   List<TopicPartition> parts=new ArrayList<>();for(String topic:List.of("order.accepted","order.status"))for(PartitionInfo pi:c.partitionsFor(topic,Duration.ofSeconds(3)))parts.add(new TopicPartition(topic,pi.partition()));
   c.assign(parts);Map<TopicPartition,Long> ends=c.endOffsets(parts);for(TopicPartition tp:parts)c.seek(tp,Math.max(0,ends.getOrDefault(tp,0L)-limit));
   ConsumerRecords<String,String> records=c.poll(Duration.ofSeconds(2));
   for(ConsumerRecord<String,String> r:records)result.add(toEvent(r));
  }
  result.sort(Comparator.comparing(KafkaEventResponse::timestamp,Comparator.nullsLast(Comparator.naturalOrder())).reversed().thenComparing(KafkaEventResponse::offset,Comparator.reverseOrder()));
  return result.stream().limit(limit).toList();
 }
 private KafkaEventResponse toEvent(ConsumerRecord<String,String> r){try{JsonNode n=json.readTree(r.value());String event=r.topic().equals("order.accepted")?"ACCEPTED":text(n,"status");return new KafkaEventResponse(r.topic(),r.partition(),r.offset(),r.key(),text(n,"orderId"),event,text(n,"side"),text(n,"quantity"),text(n,"executionPrice"),instant(n,"timestamp"));}catch(Exception e){return new KafkaEventResponse(r.topic(),r.partition(),r.offset(),r.key(),r.key(),r.topic().equals("order.accepted")?"ACCEPTED":"STATUS",null,null,null,null);}}
 private String text(JsonNode n,String field){JsonNode v=n.get(field);return v==null||v.isNull()?null:v.asText();}
 private Instant instant(JsonNode n,String field){try{String v=text(n,field);return v==null?null:Instant.parse(v);}catch(Exception e){return null;}}
}
