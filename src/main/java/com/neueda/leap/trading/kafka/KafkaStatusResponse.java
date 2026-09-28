package com.neueda.leap.trading.kafka;
import java.util.List;
public record KafkaStatusResponse(String status,String clusterId,int brokers,List<TopicStatus> topics,List<ConsumerGroupStatus> consumerGroups){
 public record TopicStatus(String name,int partitions,long latestOffset){}
 public record ConsumerGroupStatus(String groupId,String state,long lag){}
}
