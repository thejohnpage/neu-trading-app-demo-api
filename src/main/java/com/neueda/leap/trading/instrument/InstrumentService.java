package com.neueda.leap.trading.instrument;

import java.util.List;

import com.neueda.leap.trading.api.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;

    public InstrumentService(InstrumentRepository instrumentRepository) {
        this.instrumentRepository = instrumentRepository;
    }

    public List<InstrumentResponse> findAll() {
        return instrumentRepository.findAllByOrderBySymbolAsc().stream()
                .map(InstrumentResponse::from)
                .toList();
    }

    public Instrument findEntityBySymbol(String symbol) {
        return instrumentRepository.findFirstBySymbolIgnoreCaseOrderByExchangeAsc(symbol)
                .orElseThrow(() -> new ResourceNotFoundException("Instrument not found: " + symbol));
    }

    public InstrumentResponse findBySymbol(String symbol) {
        return InstrumentResponse.from(findEntityBySymbol(symbol));
    }
}
