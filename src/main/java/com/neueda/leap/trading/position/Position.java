package com.neueda.leap.trading.position;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.neueda.leap.trading.instrument.Instrument;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@IdClass(PositionId.class)
@Table(name = "positions", schema = "trading")
public class Position {

    @Id
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Id
    @Column(name = "instrument_id", nullable = false)
    private UUID instrumentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrument_id", insertable = false, updatable = false)
    private Instrument instrument;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal quantity;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(nullable = false)
    private long version;

    protected Position() {
    }

    public UUID getAccountId() { return accountId; }
    public UUID getInstrumentId() { return instrumentId; }
    public Instrument getInstrument() { return instrument; }
    public BigDecimal getQuantity() { return quantity; }
    public Instant getUpdatedAt() { return updatedAt; }
}
