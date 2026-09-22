package com.neueda.leap.trading.client;

import java.util.List;
import com.neueda.leap.trading.account.AccountResponse; import com.neueda.leap.trading.cash.CashBalanceResponse; import com.neueda.leap.trading.position.PositionResponse;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name="Client Portfolio",description="Authenticated client's accounts, cash balances and valued positions")
@RestController @RequestMapping("/api/v1/me")
public class ClientPortfolioController {
 private final ClientPortfolioService portfolioService;
 public ClientPortfolioController(ClientPortfolioService portfolioService){this.portfolioService=portfolioService;}
 @Operation(summary="List my accounts",description="Returns trading accounts owned by the authenticated client.") @GetMapping("/accounts") public List<AccountResponse> accounts(){return portfolioService.accounts();}
 @Operation(summary="Get my cash balances",description="Returns current cash balances by account and currency.") @GetMapping("/cash") public List<CashBalanceResponse> cash(){return portfolioService.cash();}
 @Operation(summary="Get my positions",description="Returns current positions including market value, cost basis and unrealized gain/loss.") @GetMapping("/positions") public List<PositionResponse> positions(){return portfolioService.positions();}
}
