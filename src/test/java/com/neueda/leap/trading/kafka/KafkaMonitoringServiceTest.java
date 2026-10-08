package com.neueda.leap.trading.kafka;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Set;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.junit.jupiter.api.Test;
class KafkaMonitoringServiceTest {
 @Test void returnsDownWhenAdminClientCreationFails() {
  KafkaMonitoringService service=new KafkaMonitoringService("localhost:9092",p->{
   throw new IllegalStateException("broker unavailable");
  });
  KafkaStatusResponse status=service.status();
  assertEquals("DOWN",status.status());
  assertEquals(0,status.brokers());
  assertTrue(status.topics().isEmpty());
  assertTrue(status.consumerGroups().isEmpty());
 }
 @Test void returnsUpForClusterWithNoTopicsOrGroups() throws Exception {
  AdminClient admin=mock(AdminClient.class);
  DescribeClusterResult cluster=mock(DescribeClusterResult.class);
  ListTopicsResult topics=mock(ListTopicsResult.class);
  ListConsumerGroupsResult groups=mock(ListConsumerGroupsResult.class);
  when(admin.describeCluster()).thenReturn(cluster);
  when(cluster.clusterId()).thenReturn(KafkaFuture.completedFuture("test-cluster"));
  when(cluster.nodes()).thenReturn(KafkaFuture.completedFuture(List.of()));
  when(admin.listTopics()).thenReturn(topics);
  when(topics.names()).thenReturn(KafkaFuture.completedFuture(Set.of()));
  when(admin.listConsumerGroups()).thenReturn(groups);
  when(groups.all()).thenReturn(KafkaFuture.completedFuture(List.of()));
  KafkaStatusResponse status=new KafkaMonitoringService("localhost:9092",p->admin).status();
  assertEquals("UP",status.status());
  assertEquals("test-cluster",status.clusterId());
  assertEquals(0,status.brokers());
  assertTrue(status.topics().isEmpty());
  assertTrue(status.consumerGroups().isEmpty());
  verify(admin).close();
 }
}
