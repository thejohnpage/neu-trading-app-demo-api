package com.neueda.leap.trading.marketdata;

import com.neueda.leap.trading.api.ResourceNotFoundException;
import com.neueda.leap.trading.instrument.Instrument;
import com.neueda.leap.trading.instrument.InstrumentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MarketDataService {

    private final InstrumentService instrumentService;
    private final MarketQuoteRepository marketQuoteRepository;

    public MarketDataService(
            InstrumentService instrumentService,
            MarketQuoteRepository marketQuoteRepository) {
        this.instrumentService = instrumentService;
        this.marketQuoteRepository = marketQuoteRepository;
    }

    public QuoteResponse getCurrentQuote(String symbol) {
        Instrument instrument = instrumentService.findEntityBySymbol(symbol);
        MarketQuote quote = marketQuoteRepository
                .findFirstByInstrumentInstrumentIdOrderByQuotedAtDesc(instrument.getInstrumentId())
                .orElseThrow(() -> new ResourceNotFoundException("No quote available for instrument: " + symbol));
        return QuoteResponse.from(quote);
    }
}
