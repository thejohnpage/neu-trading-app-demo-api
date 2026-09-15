package com.neueda.leap.trading.instrument;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentRepository extends JpaRepository<Instrument, UUID> {

    List<Instrument> findAllByOrderBySymbolAsc();

    Optional<Instrument> findFirstBySymbolIgnoreCaseOrderByExchangeAsc(String symbol);
}
