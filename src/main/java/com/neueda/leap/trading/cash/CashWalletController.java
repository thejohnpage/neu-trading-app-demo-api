package com.neueda.leap.trading.cash;

import java.util.List; import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name="Cash Wallet",description="Client deposits, withdrawals, cash history and explicit foreign-exchange conversion")
@RestController @RequestMapping("/api/v1/me/cash")
public class CashWalletController {
 private final CashWalletService service; public CashWalletController(CashWalletService service){this.service=service;}
 @Operation(summary="Deposit cash",description="Credits the selected client trading account in the specified currency.") @PostMapping("/deposits") public CashBalanceResponse deposit(@RequestBody CashMovementRequest request){return service.deposit(request);}
 @Operation(summary="Withdraw cash",description="Debits available cash from the selected client trading account.") @PostMapping("/withdrawals") public CashBalanceResponse withdraw(@RequestBody CashMovementRequest request){return service.withdraw(request);}
 @Operation(summary="Get FX rate",description="Returns the current direct or inverse market rate between two currencies.") @GetMapping("/rates") public FxRateResponse rate(@RequestParam String from,@RequestParam String to){return service.rate(from,to);}
 @Operation(summary="Convert cash",description="Explicitly converts cash from one currency wallet to another using the current FX market rate.") @PostMapping("/conversions") public CashConversionResponse convert(@RequestBody CashConversionRequest request){return service.convert(request);}
 @Operation(summary="Get cash transactions",description="Returns deposits, withdrawals, FX movements and trade-related cash transactions for an owned account.") @GetMapping("/transactions") public List<CashTransactionResponse> transactions(@RequestParam UUID accountId){return service.transactions(accountId);}
}
