package com.neueda.leap.trading.client;

import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me/portfolio")
@Tag(name="Portfolio Valuation")
public class PortfolioValuationController {
    private final PortfolioValuationService service;
    public PortfolioValuationController(PortfolioValuationService service) { this.service=service; }

    @GetMapping("/valuation")
    @Operation(summary="Value an individual or consolidated portfolio using BigDecimal and FX quotes")
    public PortfolioValuationResponse value(@RequestParam(defaultValue="USD") String currency,
                                            @RequestParam(required=false) UUID accountId) {
        return service.value(accountId,currency);
    }
}
