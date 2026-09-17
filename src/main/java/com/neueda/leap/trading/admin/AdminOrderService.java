package com.neueda.leap.trading.admin;

import java.util.List; import java.util.Map; import java.util.UUID;
import com.neueda.leap.trading.api.ResourceNotFoundException; import com.neueda.leap.trading.order.*;
import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service @Transactional(readOnly=true)
public class AdminOrderService {
 private final OrderRepository orders; private final OrderEventRepository events; private final JdbcTemplate jdbc;
 public AdminOrderService(OrderRepository orders,OrderEventRepository events,JdbcTemplate jdbc){this.orders=orders;this.events=events;this.jdbc=jdbc;}
 public List<OrderResponse> findAll(){return orders.findAll().stream().sorted((a,b)->b.getSubmittedAt().compareTo(a.getSubmittedAt())).map(OrderResponse::from).toList();}
 public OrderResponse findOne(UUID id){return OrderResponse.from(orders.findById(id).orElseThrow(()->new ResourceNotFoundException("Order not found")));}
 public List<OrderEventResponse> lifecycle(UUID id){findOne(id);return events.findByOrderIdOrderByEventTimeAscOrderEventIdAsc(id).stream().map(OrderEventResponse::from).toList();}
 public Map<String,Object> pricing(UUID id){findOne(id);return jdbc.query("""
  SELECT pricing_decision_id,order_id,instrument_id,bid_price,ask_price,execution_price,
         quote_source,quote_timestamp,priced_at FROM audit.pricing_decisions WHERE order_id=?
  """, rs -> {if(!rs.next())throw new ResourceNotFoundException("Pricing decision not found");return Map.of(
   "pricingDecisionId",rs.getObject("pricing_decision_id"),"orderId",rs.getObject("order_id"),"instrumentId",rs.getObject("instrument_id"),
   "bidPrice",rs.getBigDecimal("bid_price"),"askPrice",rs.getBigDecimal("ask_price"),"executionPrice",rs.getBigDecimal("execution_price"),
   "quoteSource",rs.getString("quote_source"),"quoteTimestamp",rs.getTimestamp("quote_timestamp").toInstant(),"pricedAt",rs.getTimestamp("priced_at").toInstant());},id);
 }
}
