package com.neueda.leap.trading.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.neueda.leap.trading.api.ResourceNotFoundException;
import com.neueda.leap.trading.order.*;
import com.neueda.leap.trading.execution.PricingDecisionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminOrderServiceTest {
    private OrderRepository orders;
    private OrderEventRepository events;
    private PricingDecisionRepository pricing;
    private AdminOrderService service;

    @BeforeEach void setUp() {
        orders=mock(OrderRepository.class);
        events=mock(OrderEventRepository.class);
        pricing=mock(PricingDecisionRepository.class);
        service=new AdminOrderService(orders,events,pricing);
    }

    @Test void emptyOrderListReturnsEmptyResponses() {
        when(orders.findAll()).thenReturn(List.of());
        assertTrue(service.findAll().isEmpty());
    }

    @Test void unknownOrderReturnsNotFound() {
        UUID id=UUID.randomUUID();
        when(orders.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,()->service.findOne(id));
    }

    @Test void lifecycleRequiresExistingOrder() {
        UUID id=UUID.randomUUID();
        when(orders.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,()->service.lifecycle(id));
        verifyNoInteractions(events);
    }

    @Test void pricingRequiresExistingOrder() {
        UUID id=UUID.randomUUID();
        when(orders.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,()->service.pricing(id));
        verifyNoInteractions(pricing);
    }
}
