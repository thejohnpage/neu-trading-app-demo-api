package com.neueda.leap.trading.kafka;
import io.swagger.v3.oas.annotations.Operation;import io.swagger.v3.oas.annotations.tags.Tag;import org.springframework.web.bind.annotation.*;
@Tag(name="Admin Kafka",description="Kafka cluster and consumer monitoring")
@RestController @RequestMapping("/api/v1/admin/kafka")
public class AdminKafkaController{
 private final KafkaMonitoringService monitoring;public AdminKafkaController(KafkaMonitoringService monitoring){this.monitoring=monitoring;}
 @Operation(summary="Kafka operational status",description="Returns cluster metadata, topic offsets and consumer-group lag.")
 @GetMapping("/status") public KafkaStatusResponse status(){return monitoring.status();}
}
