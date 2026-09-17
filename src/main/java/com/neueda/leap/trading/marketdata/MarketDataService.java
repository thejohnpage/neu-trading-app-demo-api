package com.neueda.leap.trading.marketdata;

import java.util.UUID; import com.neueda.leap.trading.api.ResourceNotFoundException; import com.neueda.leap.trading.instrument.Instrument; import com.neueda.leap.trading.instrument.InstrumentService; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service @Transactional(readOnly=true)
public class MarketDataService {
 private final InstrumentService instruments; private final MarketQuoteRepository quotes;
 public MarketDataService(InstrumentService instruments,MarketQuoteRepository quotes){this.instruments=instruments;this.quotes=quotes;}
 public QuoteResponse getCurrentQuote(String symbol){Instrument i=instruments.findEntityBySymbol(symbol);return getCurrentQuoteByInstrumentId(i.getInstrumentId());}
 public QuoteResponse getCurrentQuoteByInstrumentId(UUID instrumentId){MarketQuote q=quotes.findFirstByInstrumentInstrumentIdOrderByQuotedAtDesc(instrumentId).orElseThrow(() -> new ResourceNotFoundException("No quote available for instrument: "+instrumentId));return QuoteResponse.from(q);}
}
