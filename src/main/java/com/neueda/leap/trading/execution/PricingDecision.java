package com.neueda.leap.trading.execution;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PricingDecision {
    private UUID pricingDecisionId;
    private UUID orderId;
    private UUID instrumentId;
    private BigDecimal bidPrice;
    private BigDecimal askPrice;
    private BigDecimal executionPrice;
    private String quoteSource;
    private Instant quoteTimestamp;
    private Instant pricedAt;
    public PricingDecision() {}
    public static PricingDecision create(UUID orderId, UUID instrumentId, BigDecimal bid, BigDecimal ask,
            BigDecimal executionPrice, String source, Instant quoteTimestamp, Instant now) {
        PricingDecision p=new PricingDecision(); p.pricingDecisionId=UUID.randomUUID(); p.orderId=orderId;
        p.instrumentId=instrumentId; p.bidPrice=bid; p.askPrice=ask; p.executionPrice=executionPrice;
        p.quoteSource=source; p.quoteTimestamp=quoteTimestamp; p.pricedAt=now; return p;
    }
}
