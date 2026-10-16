package com.neueda.leap.trading.cash;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me/cash/transfers")
@Tag(name="Cash Transfers", description="Atomic same-currency and FX transfers between owned trading accounts")
public class CashTransferController {
    private final CashTransferService service;
    public CashTransferController(CashTransferService service) { this.service=service; }

    @PostMapping
    @Operation(summary="Transfer cash between two owned accounts",
        description="Uses each account's base currency, current FX market rate if needed, and an idempotent client-generated transfer ID.")
    public CashTransferResponse transfer(@RequestBody CashTransferRequest request) {
        return service.transfer(request);
    }
}
