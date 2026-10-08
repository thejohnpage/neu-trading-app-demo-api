package com.neueda.leap.trading.marketdata;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void resolvesSymbolAndMapsLatestQuote() {
        UUID id = UUID.randomUUID();
        Instrument instrument = new Instrument();
        instrument.setInstrumentId(id);
        instrument.setSymbol("MSFT");
        MarketQuote quote = new MarketQuote();
        quote.setInstrumentId(id);
        quote.setSymbol("MSFT");
        quote.setQuoteCurrency("USD");
        quote.setBidPrice(new BigDecimal("510.00"));
        quote.setAskPrice(new BigDecimal("511.00"));
        quote.setSource("TEST");
        quote.setQuotedAt(Instant.parse("2026-10-07T12:00:00Z"));
        when(instruments.findEntityBySymbol("MSFT")).thenReturn(instrument);
        when(quotes.findLatest(id)).thenReturn(Optional.of(quote));

        QuoteResponse response = service.getCurrentQuote("MSFT");

        assertEquals(id, response.instrumentId());
        assertEquals("MSFT", response.symbol());
        assertEquals("USD", response.quoteCurrency());
        assertEquals(new BigDecimal("510.00"), response.bid());
        assertEquals(new BigDecimal("511.00"), response.ask());
        assertEquals("TEST", response.source());
        verify(quotes).findLatest(id);
    }

    @Test
    void missingQuoteThrowsNotFound() {
        UUID id = UUID.randomUUID();
        when(quotes.findLatest(id)).thenReturn(Optional.empty());
        ResourceNotFoundException error = assertThrows(
                ResourceNotFoundException.class,
                () -> service.getCurrentQuoteByInstrumentId(id));
        assertEquals("No quote available for instrument: " + id, error.getMessage());
    }

    @Test
    void missingInstrumentPropagatesNotFound() {
        when(instruments.findEntityBySymbol("INVALID"))
                .thenThrow(new ResourceNotFoundException("Instrument not found: INVALID"));
        assertThrows(ResourceNotFoundException.class, () -> service.getCurrentQuote("INVALID"));
        verifyNoInteractions(quotes);
    }
}
