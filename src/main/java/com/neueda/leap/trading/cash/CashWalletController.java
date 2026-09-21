package com.neueda.leap.trading.cash;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me/cash")
public class CashWalletController {
 private final CashWalletService service;
 public CashWalletController(CashWalletService service){this.service=service;}

 @PostMapping("/deposits")
 public CashBalanceResponse deposit(@RequestBody CashMovementRequest request){return service.deposit(request);}

 @PostMapping("/withdrawals")
 public CashBalanceResponse withdraw(@RequestBody CashMovementRequest request){return service.withdraw(request);}

 @GetMapping("/rates")
 public FxRateResponse rate(@RequestParam String from,@RequestParam String to){return service.rate(from,to);}

 @PostMapping("/conversions")
 public CashConversionResponse convert(@RequestBody CashConversionRequest request){return service.convert(request);}

 @GetMapping("/transactions")
 public List<CashTransactionResponse> transactions(@RequestParam UUID accountId){return service.transactions(accountId);}
}
