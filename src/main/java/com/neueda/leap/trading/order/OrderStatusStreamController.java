package com.neueda.leap.trading.order;
import com.neueda.leap.trading.client.CurrentClient;import com.neueda.leap.trading.execution.OrderStatusStream;import io.swagger.v3.oas.annotations.Operation;import org.springframework.web.bind.annotation.*;import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
@RestController @RequestMapping("/api/v1/orders")
public class OrderStatusStreamController{
 private final OrderStatusStream stream;private final CurrentClient client;
 public OrderStatusStreamController(OrderStatusStream stream,CurrentClient client){this.stream=stream;this.client=client;}
 @Operation(summary="Stream live order status updates") @GetMapping(value="/status-stream",produces="text/event-stream") public SseEmitter stream(){return stream.subscribe(client.clientId());}
}
