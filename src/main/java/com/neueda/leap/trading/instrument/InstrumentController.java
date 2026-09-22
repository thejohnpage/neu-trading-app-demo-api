package com.neueda.leap.trading.instrument;

import java.util.List;
import com.neueda.leap.trading.marketdata.MarketDataService; import com.neueda.leap.trading.marketdata.QuoteResponse;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name="Instruments & Market Data",description="Tradable instruments and current indicative market quotes")
/** Exposes tradable instrument metadata and indicative market quotes. */
@RestController @RequestMapping("/api/v1/instruments")
public class InstrumentController {
 private final InstrumentService instrumentService; private final MarketDataService marketDataService;
 public InstrumentController(InstrumentService instrumentService,MarketDataService marketDataService){this.instrumentService=instrumentService;this.marketDataService=marketDataService;}
 /** List instruments. */
 @Operation(summary="List instruments",description="Returns the instruments available to the trading application.") @GetMapping public List<InstrumentResponse> findAll(){return instrumentService.findAll();}
 /** Get instrument. */
 @Operation(summary="Get instrument",description="Returns instrument metadata for a symbol.") @GetMapping("/{symbol}") public InstrumentResponse findBySymbol(@PathVariable String symbol){return instrumentService.findBySymbol(symbol);}
 /** Get current quote. */
 @Operation(summary="Get current quote",description="Returns the latest indicative bid/ask quote available for an instrument.") @GetMapping("/{symbol}/quote") public QuoteResponse getQuote(@PathVariable String symbol){return marketDataService.getCurrentQuote(symbol);}
}
