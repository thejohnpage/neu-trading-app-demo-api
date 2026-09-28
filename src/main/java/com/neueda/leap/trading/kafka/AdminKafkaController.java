package com.neueda.leap.trading.kafka;
import io.swagger.v3.oas.annotations.Operation;import io.swagger.v3.oas.annotations.tags.Tag;import org.springframework.web.bind.annotation.*;
@Tag(name="Admin Kafka",description="Kafka cluster and consumer monitoring")
@RestController @RequestMapping("/api/v1/admin/kafka")
public class AdminKafkaController{
 private final KafkaMonitoringService monitoring;private final KafkaEventMonitoringService events;public AdminKafkaController(KafkaMonitoringService monitoring,KafkaEventMonitoringService events){this.monitoring=monitoring;this.events=events;}
 @Operation(summary="Kafka operational status",description="Returns cluster metadata, topic offsets and consumer-group lag.")
 @GetMapping("/status") public KafkaStatusResponse status(){return monitoring.status();}
 @Operation(summary="Recent Kafka business events",description="Returns recent order-related events from monitored application topics.")
 @GetMapping("/events") public java.util.List<KafkaEventResponse> events(@RequestParam(defaultValue="20") int limit){return events.recent(Math.max(1,Math.min(limit,100)));}
}

