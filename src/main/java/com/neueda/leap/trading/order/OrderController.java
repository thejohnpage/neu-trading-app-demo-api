package com.neueda.leap.trading.order;

import java.util.List; import java.util.UUID; import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*;

@Tag(name="Client Orders",description="Submit orders and inspect the authenticated client's order history and lifecycle")
/** Exposes client-scoped order submission, retrieval, and lifecycle endpoints. */
@RestController @RequestMapping("/api/v1/orders")
public class OrderController {
 private final OrderService commands; private final OrderQueryService queries;
 public OrderController(OrderService commands,OrderQueryService queries){this.commands=commands;this.queries=queries;}
 /** Submit an order. */
 @Operation(summary="Submit an order",description="Validates and accepts a BUY or SELL order for an account owned by the authenticated client.")
 @PostMapping @ResponseStatus(HttpStatus.ACCEPTED) public OrderResponse submit(@Valid @RequestBody CreateOrderRequest request){return commands.submit(request);}
 /** List client orders. */
 @Operation(summary="List client orders",description="Returns orders belonging to the authenticated client's accounts in reverse chronological order.")
 @GetMapping public List<OrderResponse> findAll(){return queries.findAll();}
 /** Get an order. */
 @Operation(summary="Get an order",description="Returns one order belonging to the authenticated client.")
 @GetMapping("/{orderId}") public OrderResponse findOne(@PathVariable UUID orderId){return queries.findOne(orderId);}
 /** Get order lifecycle. */
 @Operation(summary="Get order lifecycle",description="Returns the chronological lifecycle events recorded for an order.")
 @GetMapping("/{orderId}/events") public List<OrderEventResponse> lifecycle(@PathVariable UUID orderId){return queries.lifecycle(orderId);}
}
