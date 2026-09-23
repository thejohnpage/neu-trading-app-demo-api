package com.neueda.leap.trading.execution;

import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
import com.neueda.leap.trading.cash.*; import com.neueda.leap.trading.marketdata.*; import com.neueda.leap.trading.order.*; import com.neueda.leap.trading.position.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderExecutionService {
 private final OrderRepository orders; private final FillRepository fills; private final MarketDataService marketData;
 private final PricingDecisionRepository pricing; private final CashBalanceRepository cash; private final CashTransactionRepository cashTx;
 private final PositionRepository positions; private final OrderEventRepository events;
 public OrderExecutionService(OrderRepository orders,FillRepository fills,MarketDataService marketData,PricingDecisionRepository pricing,CashBalanceRepository cash,CashTransactionRepository cashTx,PositionRepository positions,OrderEventRepository events){this.orders=orders;this.fills=fills;this.marketData=marketData;this.pricing=pricing;this.cash=cash;this.cashTx=cashTx;this.positions=positions;this.events=events;}

 @Transactional
 public void execute(UUID orderId){
  Order order=orders.findById(orderId).orElseThrow();
  if("FILLED".equals(order.getStatus())||fills.existsByOrderId(orderId))return;
  if(!"ACCEPTED".equals(order.getStatus()))return;
  QuoteResponse quote=marketData.getCurrentQuoteByInstrumentId(order.getInstrumentId());
  BigDecimal executionPrice="BUY".equals(order.getSide())?quote.ask():quote.bid();
  BigDecimal notional=order.getQuantity().multiply(executionPrice); Instant now=Instant.now();
  pricing.insert(PricingDecision.create(orderId,order.getInstrumentId(),quote.bid(),quote.ask(),executionPrice,quote.source(),quote.quotedAt(),now));
  CashBalance balance=cash.find(order.getAccountId(),quote.quoteCurrency()).orElseThrow();
  BigDecimal cashDelta="BUY".equals(order.getSide())?notional.negate():notional; balance.apply(cashDelta,now); if(cash.update(balance)!=1)throw new IllegalStateException("Concurrent cash update");
  cashTx.insert(CashTransaction.trade(order.getAccountId(),orderId,quote.quoteCurrency(),cashDelta,"BUY".equals(order.getSide())?"TRADE_BUY":"TRADE_SELL",now));

  PositionId positionId=new PositionId(order.getAccountId(),order.getInstrumentId());
  Position position=positions.find(order.getAccountId(),order.getInstrumentId()).orElse(null);
  if("BUY".equals(order.getSide())){
   if(position==null)positions.insert(Position.create(order.getAccountId(),order.getInstrumentId(),order.getQuantity(),executionPrice,now));
   else {position.buy(order.getQuantity(),executionPrice,now);if(positions.update(position)!=1)throw new IllegalStateException("Concurrent position update");}
  }else{
   if(position==null)throw new IllegalStateException("Cannot sell absent position");
   position.sell(order.getQuantity(),now);if(positions.update(position)!=1)throw new IllegalStateException("Concurrent position update");
  }

  fills.insert(Fill.create(orderId,order.getQuantity(),executionPrice,now)); order.markFilled(now); if(orders.update(order)!=1)throw new IllegalStateException("Concurrent order update");
  events.insert(OrderEvent.of(orderId,"PRICED",now)); events.insert(OrderEvent.of(orderId,"FILLED",now));
 }
}
