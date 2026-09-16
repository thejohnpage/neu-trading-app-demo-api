package com.neueda.leap.trading.order;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService){this.orderService=orderService;}

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderResponse submit(@Valid @RequestBody CreateOrderRequest request){
        return orderService.submit(request);
    }
}
