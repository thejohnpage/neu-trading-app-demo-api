package com.neueda.leap.trading.admin;

import java.util.List; import java.util.Map; import java.util.UUID; import com.neueda.leap.trading.order.*;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name="Admin Orders",description="Administrative inspection of orders, lifecycle events and pricing decisions")
@RestController @RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {
 private final AdminOrderService service; public AdminOrderController(AdminOrderService service){this.service=service;}
 @Operation(summary="List all orders",description="Returns orders across client accounts for authorized internal users.")
 @GetMapping public List<OrderResponse> findAll(){return service.findAll();}
 @Operation(summary="Get an order",description="Returns a single order for administrative inspection.")
 @GetMapping("/{orderId}") public OrderResponse findOne(@PathVariable UUID orderId){return service.findOne(orderId);}
 @Operation(summary="Get order lifecycle",description="Returns the permanent lifecycle events for an order.")
 @GetMapping("/{orderId}/events") public List<OrderEventResponse> lifecycle(@PathVariable UUID orderId){return service.lifecycle(orderId);}
 @Operation(summary="Get pricing decision",description="Returns the quote and execution pricing information recorded for an order.")
 @GetMapping("/{orderId}/pricing") public Map<String,Object> pricing(@PathVariable UUID orderId){return service.pricing(orderId);}
}
