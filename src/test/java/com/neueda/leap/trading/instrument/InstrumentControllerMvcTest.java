package com.neueda.leap.trading.instrument;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.neueda.leap.trading.api.ApiExceptionHandler;
import com.neueda.leap.trading.api.ResourceNotFoundException;
import com.neueda.leap.trading.marketdata.MarketDataService;
import com.neueda.leap.trading.marketdata.QuoteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class InstrumentControllerMvcTest {
    private InstrumentService instruments;
    private MarketDataService market;
    private MockMvc mvc;

    @BeforeEach void setUp() {
        instruments=mock(InstrumentService.class);
        market=mock(MarketDataService.class);
        mvc=MockMvcBuilders.standaloneSetup(new InstrumentController(instruments,market))
                .setControllerAdvice(new ApiExceptionHandler()).build();
    }

    @Test void listInstrumentsReturnsJson() throws Exception {
        when(instruments.findAll()).thenReturn(List.of(
                new InstrumentResponse(UUID.randomUUID(),"MSFT","EQUITY","NASDAQ",null,"USD","Microsoft",true)));
        mvc.perform(get("/api/v1/instruments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("MSFT"))
                .andExpect(jsonPath("$[0].tradable").value(true));
    }

    @Test void instrumentLookupPassesSymbolToService() throws Exception {
        when(instruments.findBySymbol("AAPL")).thenReturn(
                new InstrumentResponse(UUID.randomUUID(),"AAPL","EQUITY","NASDAQ",null,"USD","Apple",true));
        mvc.perform(get("/api/v1/instruments/AAPL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("AAPL"));
        verify(instruments).findBySymbol("AAPL");
    }

    @Test void missingInstrumentReturns404() throws Exception {
        when(instruments.findBySymbol("UNKNOWN")).thenThrow(new ResourceNotFoundException("Instrument not found"));
        mvc.perform(get("/api/v1/instruments/UNKNOWN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Instrument not found"));
    }

    @Test void quoteEndpointReturnsBidAndAsk() throws Exception {
        when(market.getCurrentQuote("MSFT")).thenReturn(
                new QuoteResponse(UUID.randomUUID(),"MSFT","USD",
                        new BigDecimal("510"),new BigDecimal("511"),"TEST",
                        Instant.parse("2026-10-08T12:00:00Z")));
        mvc.perform(get("/api/v1/instruments/MSFT/quote"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bid").value(510))
                .andExpect(jsonPath("$.ask").value(511));
    }
}
