package com.neueda.leap.trading.instrument;

import java.util.List;

import com.neueda.leap.trading.marketdata.MarketDataService;
import com.neueda.leap.trading.marketdata.QuoteResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/instruments")
public class InstrumentController {

    private final InstrumentService instrumentService;
    private final MarketDataService marketDataService;

    public InstrumentController(
            InstrumentService instrumentService,
            MarketDataService marketDataService) {
        this.instrumentService = instrumentService;
        this.marketDataService = marketDataService;
    }

    @GetMapping
    public List<InstrumentResponse> findAll() {
        return instrumentService.findAll();
    }

    @GetMapping("/{symbol}")
    public InstrumentResponse findBySymbol(@PathVariable String symbol) {
        return instrumentService.findBySymbol(symbol);
    }

    @GetMapping("/{symbol}/quote")
    public QuoteResponse getQuote(@PathVariable String symbol) {
        return marketDataService.getCurrentQuote(symbol);
    }
}
