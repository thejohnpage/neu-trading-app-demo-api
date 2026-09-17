package com.neueda.leap.trading.order;
import java.util.List; import java.util.UUID; import jakarta.validation.Valid; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/orders")
public class OrderController {
 private final OrderService commands; private final OrderQueryService queries;
 public OrderController(OrderService commands,OrderQueryService queries){this.commands=commands;this.queries=queries;}
 @PostMapping @ResponseStatus(HttpStatus.ACCEPTED) public OrderResponse submit(@Valid @RequestBody CreateOrderRequest request){return commands.submit(request);}
 @GetMapping public List<OrderResponse> findAll(){return queries.findAll();}
 @GetMapping("/{orderId}") public OrderResponse findOne(@PathVariable UUID orderId){return queries.findOne(orderId);}
 @GetMapping("/{orderId}/events") public List<OrderEventResponse> lifecycle(@PathVariable UUID orderId){return queries.lifecycle(orderId);}
}
