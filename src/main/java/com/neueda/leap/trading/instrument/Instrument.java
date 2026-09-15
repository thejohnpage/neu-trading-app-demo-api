package com.neueda.leap.trading.instrument;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "instruments", schema = "trading")
public class Instrument {

    @Id
    @Column(name = "instrument_id", nullable = false)
    private UUID instrumentId;

    @Column(nullable = false, length = 30)
    private String symbol;

    @Column(name = "instrument_type", nullable = false, length = 20)
    private String instrumentType;

    @Column(length = 30)
    private String exchange;

    @Column(name = "base_currency", length = 10)
    private String baseCurrency;

    @Column(name = "quote_currency", nullable = false, length = 10)
    private String quoteCurrency;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private boolean tradable;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Instrument() {
    }

    public UUID getInstrumentId() { return instrumentId; }
    public String getSymbol() { return symbol; }
    public String getInstrumentType() { return instrumentType; }
    public String getExchange() { return exchange; }
    public String getBaseCurrency() { return baseCurrency; }
    public String getQuoteCurrency() { return quoteCurrency; }
    public String getName() { return name; }
    public boolean isTradable() { return tradable; }
    public Instant getCreatedAt() { return createdAt; }
}
