package com.neueda.leap.trading.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.neueda.leap.trading.api.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstrumentServiceTest {

    private InstrumentRepository repository;
    private InstrumentService service;

    @BeforeEach
    void setUp() {
        repository = mock(InstrumentRepository.class);
        service = new InstrumentService(repository);
    }

    @Test
    void findAllMapsRepositoryEntitiesToResponses() {
        Instrument apple = instrument("AAPL", "NASDAQ");
        Instrument microsoft = instrument("MSFT", "NASDAQ");
        when(repository.findAllByOrderBySymbolAsc()).thenReturn(List.of(apple, microsoft));

        List<InstrumentResponse> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("AAPL", result.get(0).symbol());
        assertEquals("MSFT", result.get(1).symbol());
    }

    @Test
    void findEntityBySymbolReturnsRepositoryEntity() {
        Instrument apple = instrument("AAPL", "NASDAQ");
        when(repository.findFirstBySymbolIgnoreCaseOrderByExchangeAsc("aapl"))
                .thenReturn(Optional.of(apple));

        Instrument result = service.findEntityBySymbol("aapl");

        assertSame(apple, result);
    }

    @Test
    void findEntityBySymbolThrowsWhenInstrumentDoesNotExist() {
        when(repository.findFirstBySymbolIgnoreCaseOrderByExchangeAsc("BAD"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.findEntityBySymbol("BAD"));

        assertEquals("Instrument not found: BAD", exception.getMessage());
    }

    @Test
    void findBySymbolMapsEntityToResponse() {
        Instrument apple = instrument("AAPL", "NASDAQ");
        when(repository.findFirstBySymbolIgnoreCaseOrderByExchangeAsc("AAPL"))
                .thenReturn(Optional.of(apple));

        InstrumentResponse result = service.findBySymbol("AAPL");

        assertEquals("AAPL", result.symbol());
        assertEquals("NASDAQ", result.exchange());
    }

    private Instrument instrument(String symbol, String exchange) {
        Instrument instrument = mock(Instrument.class);
        when(instrument.getSymbol()).thenReturn(symbol);
        when(instrument.getExchange()).thenReturn(exchange);
        when(instrument.getInstrumentType()).thenReturn("EQUITY");
        when(instrument.getQuoteCurrency()).thenReturn("USD");
        when(instrument.getName()).thenReturn(symbol + " Inc.");
        when(instrument.isTradable()).thenReturn(true);
        return instrument;
    }
}
