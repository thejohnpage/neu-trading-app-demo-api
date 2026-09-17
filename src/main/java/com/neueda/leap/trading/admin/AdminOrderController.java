package com.neueda.leap.trading.admin;
import java.util.List; import java.util.Map; import java.util.UUID; import com.neueda.leap.trading.order.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {
 private final AdminOrderService service; public AdminOrderController(AdminOrderService service){this.service=service;}
 @GetMapping public List<OrderResponse> findAll(){return service.findAll();}
 @GetMapping("/{orderId}") public OrderResponse findOne(@PathVariable UUID orderId){return service.findOne(orderId);}
 @GetMapping("/{orderId}/events") public List<OrderEventResponse> lifecycle(@PathVariable UUID orderId){return service.lifecycle(orderId);}
 @GetMapping("/{orderId}/pricing") public Map<String,Object> pricing(@PathVariable UUID orderId){return service.pricing(orderId);}
}
