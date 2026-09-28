package com.neueda.leap.trading.kafka;
import java.time.Instant;
public record KafkaEventResponse(String topic,int partition,long offset,String key,String orderId,String event,String side,String quantity,String executionPrice,Instant timestamp){}
