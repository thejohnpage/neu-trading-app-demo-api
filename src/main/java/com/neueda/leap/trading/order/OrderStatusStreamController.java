package com.neueda.leap.trading.order;
import com.neueda.leap.trading.execution.OrderStatusStream;import io.swagger.v3.oas.annotations.Operation;import org.springframework.web.bind.annotation.*;import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
@RestController @RequestMapping("/api/v1/orders")
public class OrderStatusStreamController{
 private final OrderStatusStream stream;public OrderStatusStreamController(OrderStatusStream stream){this.stream=stream;}
 @Operation(summary="Stream live order status updates") @GetMapping(value="/status-stream",produces="text/event-stream") public SseEmitter stream(){return stream.subscribe();}
}
