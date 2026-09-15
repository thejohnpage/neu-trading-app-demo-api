package com.neueda.leap.trading.marketdata;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketQuoteRepository extends JpaRepository<MarketQuote, UUID> {

    Optional<MarketQuote> findFirstByInstrumentInstrumentIdOrderByQuotedAtDesc(UUID instrumentId);
}
