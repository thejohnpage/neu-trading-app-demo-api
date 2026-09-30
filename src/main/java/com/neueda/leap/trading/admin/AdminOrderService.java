package com.neueda.leap.trading.admin;
import java.util.*; import com.neueda.leap.trading.api.ResourceNotFoundException; import com.neueda.leap.trading.order.*; import com.neueda.leap.trading.execution.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service @Transactional(readOnly=true)
public class AdminOrderService {
 private final OrderRepository orders; private final OrderEventRepository events; private final PricingDecisionRepository pricing;
 public AdminOrderService(OrderRepository orders,OrderEventRepository events,PricingDecisionRepository pricing){this.orders=orders;this.events=events;this.pricing=pricing;}
 public List<OrderResponse> findAll(){return orders.findAll().stream().map(OrderResponse::from).toList();}
 public OrderResponse findOne(UUID id){return OrderResponse.from(orders.findById(id).orElseThrow(()->new ResourceNotFoundException("Order not found")));}
 public List<OrderEventResponse> lifecycle(UUID id){findOne(id);return events.findByOrderIdOrderByEventTimeAscOrderEventIdAsc(id).stream().map(OrderEventResponse::from).toList();}
 public Map<String,Object> pricing(UUID id){findOne(id);PricingDecision p=pricing.findByOrderId(id).orElseThrow(()->new ResourceNotFoundException("Pricing decision not found"));return Map.of("pricingDecisionId",p.getPricingDecisionId(),"orderId",p.getOrderId(),"instrumentId",p.getInstrumentId(),"bidPrice",p.getBidPrice(),"askPrice",p.getAskPrice(),"executionPrice",p.getExecutionPrice(),"quoteSource",p.getQuoteSource(),"quoteTimestamp",p.getQuoteTimestamp(),"pricedAt",p.getPricedAt());}
}