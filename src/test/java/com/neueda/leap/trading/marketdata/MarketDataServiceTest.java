package com.neueda.leap.trading.marketdata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.neueda.leap.trading.api.ResourceNotFoundException;
import com.neueda.leap.trading.instrument.Instrument;
import com.neueda.leap.trading.instrument.InstrumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MarketDataServiceTest {

    private InstrumentService instruments;
    private MarketQuoteRepository quotes;
    private MarketDataService service;

    @BeforeEach
    void setUp() {
        instruments = mock(InstrumentService.class);
        quotes = mock(MarketQuoteRepository.class);
        service = new MarketDataService(instruments, quotes);
    }

    @Test
    void getCurrentQuoteResolvesSymbolThenReturnsLatestQuote() {
        UUID instrumentId = UUID.randomUUID();
        Instrument instrument = instrument(instrumentId, "MSFT");
        MarketQuote quote = quote(instrument);

        when(instruments.findEntityBySymbol("MSFT")).thenReturn(instrument);
        when(quotes.findFirstByInstrumentInstrumentIdOrderByQuotedAtDesc(instrumentId))
                .thenReturn(Optional.of(quote));

        QuoteResponse result = service.getCurrentQuote("MSFT");

        assertEquals(instrumentId, result.instrumentId());
        assertEquals("MSFT", result.symbol());
        assertEquals(new BigDecimal("510.00"), result.bid());
        assertEquals(new BigDecimal("511.00"), result.ask());
    }

    @Test
    void getCurrentQuoteByInstrumentIdThrowsWhenNoQuoteExists() {
        UUID instrumentId = UUID.randomUUID();
        when(quotes.findFirstByInstrumentInstrumentIdOrderByQuotedAtDesc(instrumentId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.getCurrentQuoteByInstrumentId(instrumentId));

        assertEquals("No quote available for instrument: " + instrumentId, exception.getMessage());
    }

    private Instrument instrument(UUID id, String symbol) {
        Instrument instrument = mock(Instrument.class);
        when(instrument.getInstrumentId()).thenReturn(id);
        when(instrument.getSymbol()).thenReturn(symbol);
        when(instrument.getQuoteCurrency()).thenReturn("USD");
        return instrument;
    }

    private MarketQuote quote(Instrument instrument) {
        MarketQuote quote = mock(MarketQuote.class);
        when(quote.getInstrument()).thenReturn(instrument);
        when(quote.getBidPrice()).thenReturn(new BigDecimal("510.00"));
        when(quote.getAskPrice()).thenReturn(new BigDecimal("511.00"));
        when(quote.getSource()).thenReturn("TEST");
        when(quote.getQuotedAt()).thenReturn(Instant.parse("2026-10-07T12:00:00Z"));
        return quote;
    }
}
