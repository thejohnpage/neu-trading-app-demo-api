package com.neueda.leap.trading.kafka;

import java.util.*;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Reads operational Kafka metadata without participating in message processing. */
@Service
public class KafkaMonitoringService {
 private static final long TIMEOUT_SECONDS=5;
 private final String bootstrap;
 public KafkaMonitoringService(@Value("${spring.kafka.bootstrap-servers}")String bootstrap){this.bootstrap=bootstrap;}

 public KafkaStatusResponse status(){
  Properties p=new Properties();p.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrap);
  try(AdminClient admin=AdminClient.create(p)){
   String clusterId=admin.describeCluster().clusterId().get(TIMEOUT_SECONDS,TimeUnit.SECONDS);
   int brokers=admin.describeCluster().nodes().get(TIMEOUT_SECONDS,TimeUnit.SECONDS).size();
   Set<String> names=admin.listTopics().names().get(TIMEOUT_SECONDS,TimeUnit.SECONDS);
   List<KafkaStatusResponse.TopicStatus> topics=new ArrayList<>();
   Map<TopicPartition,OffsetSpec> requests=new HashMap<>();
   Map<String,Integer> partitionCounts=new HashMap<>();
   for(String name:names){if(name.startsWith("__"))continue;var d=admin.describeTopics(List.of(name)).allTopicNames().get(TIMEOUT_SECONDS,TimeUnit.SECONDS).get(name);partitionCounts.put(name,d.partitions().size());for(var part:d.partitions())requests.put(new TopicPartition(name,part.partition()),OffsetSpec.latest());}
   Map<TopicPartition,ListOffsetsResult.ListOffsetsResultInfo> latest=requests.isEmpty()?Map.of():admin.listOffsets(requests).all().get(TIMEOUT_SECONDS,TimeUnit.SECONDS);
   for(var e:partitionCounts.entrySet()){long total=latest.entrySet().stream().filter(x->x.getKey().topic().equals(e.getKey())).mapToLong(x->x.getValue().offset()).sum();topics.add(new KafkaStatusResponse.TopicStatus(e.getKey(),e.getValue(),total));}
   topics.sort(Comparator.comparing(KafkaStatusResponse.TopicStatus::name));

   List<KafkaStatusResponse.ConsumerGroupStatus> groups=new ArrayList<>();
   for(ConsumerGroupListing listing:admin.listConsumerGroups().all().get(TIMEOUT_SECONDS,TimeUnit.SECONDS)){
    String group=listing.groupId();var desc=admin.describeConsumerGroups(List.of(group)).all().get(TIMEOUT_SECONDS,TimeUnit.SECONDS).get(group);
    Map<TopicPartition,OffsetAndMetadata> committed=admin.listConsumerGroupOffsets(group).partitionsToOffsetAndMetadata().get(TIMEOUT_SECONDS,TimeUnit.SECONDS);
    Map<TopicPartition,OffsetSpec> lagReq=new HashMap<>();for(TopicPartition tp:committed.keySet())lagReq.put(tp,OffsetSpec.latest());
    Map<TopicPartition,ListOffsetsResult.ListOffsetsResultInfo> ends=lagReq.isEmpty()?Map.of():admin.listOffsets(lagReq).all().get(TIMEOUT_SECONDS,TimeUnit.SECONDS);
    long lag=0;for(var e:committed.entrySet()){var end=ends.get(e.getKey());if(end!=null)lag+=Math.max(0,end.offset()-e.getValue().offset());}
    groups.add(new KafkaStatusResponse.ConsumerGroupStatus(group,desc.state().toString(),lag));
   }
   groups.sort(Comparator.comparing(KafkaStatusResponse.ConsumerGroupStatus::groupId));
   return new KafkaStatusResponse("UP",clusterId,brokers,topics,groups);
  }catch(Exception e){return new KafkaStatusResponse("DOWN",null,0,List.of(),List.of());}
 }
}
