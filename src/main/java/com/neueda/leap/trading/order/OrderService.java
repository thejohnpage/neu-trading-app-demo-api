package com.neueda.leap.trading.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;

import com.neueda.leap.trading.account.Account;
import com.neueda.leap.trading.account.AccountRepository;
import com.neueda.leap.trading.api.ResourceNotFoundException;
import com.neueda.leap.trading.cash.CashBalanceId;
import com.neueda.leap.trading.cash.CashBalanceRepository;
import com.neueda.leap.trading.client.CurrentClient;
import com.neueda.leap.trading.instrument.Instrument;
import com.neueda.leap.trading.instrument.InstrumentService;
import com.neueda.leap.trading.marketdata.MarketDataService;
import com.neueda.leap.trading.marketdata.QuoteResponse;
import com.neueda.leap.trading.position.PositionId;
import com.neueda.leap.trading.position.PositionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final CurrentClient currentClient; private final AccountRepository accounts;
    private final InstrumentService instruments; private final MarketDataService marketData;
    private final CashBalanceRepository cash; private final PositionRepository positions;
    private final OrderRepository orders; private final OrderEventRepository events; private final OutboxEventRepository outbox;

    public OrderService(CurrentClient currentClient, AccountRepository accounts, InstrumentService instruments,
            MarketDataService marketData, CashBalanceRepository cash, PositionRepository positions,
            OrderRepository orders, OrderEventRepository events, OutboxEventRepository outbox) {
        this.currentClient=currentClient; this.accounts=accounts; this.instruments=instruments; this.marketData=marketData;
        this.cash=cash; this.positions=positions; this.orders=orders; this.events=events; this.outbox=outbox;
    }

    @Transactional
    public OrderResponse submit(CreateOrderRequest request) {
        Account account=accounts.findByAccountIdAndClientId(request.accountId(), currentClient.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        Instrument instrument=instruments.findEntityBySymbol(request.symbol());
        if (!instrument.isTradable()) throw new OrderRejectedException("Instrument is not currently tradable");
        String side=request.side().toUpperCase(Locale.ROOT);
        if (!side.equals("BUY") && !side.equals("SELL")) throw new OrderRejectedException("Side must be BUY or SELL");

        QuoteResponse quote=marketData.getCurrentQuote(instrument.getSymbol());
        if (side.equals("BUY")) {
            BigDecimal required=request.quantity().multiply(quote.ask());
            BigDecimal available=cash.find(account.getAccountId(), quote.quoteCurrency())
                    .orElseThrow(() -> new OrderRejectedException("No cash balance in " + quote.quoteCurrency())).getBalance();
            if (available.compareTo(required)<0) throw new OrderRejectedException("Insufficient cash");
        } else {
            BigDecimal held=positions.find(account.getAccountId(), instrument.getInstrumentId())
                    .map(p -> p.getQuantity()).orElse(BigDecimal.ZERO);
            if (held.compareTo(request.quantity())<0) throw new OrderRejectedException("Insufficient holding");
        }

        Instant now=Instant.now();
        Order order=Order.accepted(account.getAccountId(), instrument.getInstrumentId(), side, request.quantity(), now); orders.insert(order);
        // Preserve both lifecycle facts even though validation and acceptance occur in one synchronous request.
        events.insert(OrderEvent.of(order.getOrderId(), "SUBMITTED", now));
        events.insert(OrderEvent.of(order.getOrderId(), "ACCEPTED", now));
        outbox.insert(OutboxEvent.orderAccepted(order, now));
        return OrderResponse.from(order);
    }
}
