package com.neueda.leap.trading.marketdata;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.neueda.leap.trading.instrument.Instrument;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "market_quotes", schema = "trading")
public class MarketQuote {

    @Id
    @Column(name = "quote_id", nullable = false)
    private UUID quoteId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @Column(name = "bid_price", nullable = false, precision = 20, scale = 8)
    private BigDecimal bidPrice;

    @Column(name = "ask_price", nullable = false, precision = 20, scale = 8)
    private BigDecimal askPrice;

    @Column(nullable = false, length = 100)
    private String source;

    @Column(name = "quoted_at", nullable = false)
    private Instant quotedAt;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    protected MarketQuote() {
    }

    public UUID getQuoteId() { return quoteId; }
    public Instrument getInstrument() { return instrument; }
    public BigDecimal getBidPrice() { return bidPrice; }
    public BigDecimal getAskPrice() { return askPrice; }
    public String getSource() { return source; }
    public Instant getQuotedAt() { return quotedAt; }
    public Instant getReceivedAt() { return receivedAt; }
}
