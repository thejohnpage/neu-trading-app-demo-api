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
    public UUID getPricingDecisionId(){return pricingDecisionId;} public void setPricingDecisionId(UUID v){pricingDecisionId=v;}
    public UUID getOrderId(){return orderId;} public void setOrderId(UUID v){orderId=v;} public UUID getInstrumentId(){return instrumentId;} public void setInstrumentId(UUID v){instrumentId=v;}
    public BigDecimal getBidPrice(){return bidPrice;} public void setBidPrice(BigDecimal v){bidPrice=v;} public BigDecimal getAskPrice(){return askPrice;} public void setAskPrice(BigDecimal v){askPrice=v;} public BigDecimal getExecutionPrice(){return executionPrice;} public void setExecutionPrice(BigDecimal v){executionPrice=v;}
    public String getQuoteSource(){return quoteSource;} public void setQuoteSource(String v){quoteSource=v;} public Instant getQuoteTimestamp(){return quoteTimestamp;} public void setQuoteTimestamp(Instant v){quoteTimestamp=v;} public Instant getPricedAt(){return pricedAt;} public void setPricedAt(Instant v){pricedAt=v;}
}
