package com.neueda.leap.trading.execution;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Table(name="pricing_decisions", schema="audit")
public class PricingDecision {
    @Id @Column(name="pricing_decision_id") private UUID pricingDecisionId;
    @Column(name="order_id", nullable=false) private UUID orderId;
    @Column(name="instrument_id", nullable=false) private UUID instrumentId;
    @Column(name="bid_price", nullable=false, precision=20, scale=8) private BigDecimal bidPrice;
    @Column(name="ask_price", nullable=false, precision=20, scale=8) private BigDecimal askPrice;
    @Column(name="execution_price", nullable=false, precision=20, scale=8) private BigDecimal executionPrice;
    @Column(name="quote_source", nullable=false, length=100) private String quoteSource;
    @Column(name="quote_timestamp", nullable=false) private Instant quoteTimestamp;
    @Column(name="priced_at", nullable=false) private Instant pricedAt;
    protected PricingDecision() {}
    public static PricingDecision create(UUID orderId, UUID instrumentId, BigDecimal bid, BigDecimal ask,
            BigDecimal executionPrice, String source, Instant quoteTimestamp, Instant now) {
        PricingDecision p=new PricingDecision(); p.pricingDecisionId=UUID.randomUUID(); p.orderId=orderId;
        p.instrumentId=instrumentId; p.bidPrice=bid; p.askPrice=ask; p.executionPrice=executionPrice;
        p.quoteSource=source; p.quoteTimestamp=quoteTimestamp; p.pricedAt=now; return p;
    }
}
