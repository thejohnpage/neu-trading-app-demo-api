package com.neueda.leap.trading.client;

import java.util.List;

import com.neueda.leap.trading.account.AccountResponse;
import com.neueda.leap.trading.cash.CashBalanceResponse;
import com.neueda.leap.trading.position.PositionResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class ClientPortfolioController {

    private final ClientPortfolioService portfolioService;

    public ClientPortfolioController(ClientPortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/accounts")
    public List<AccountResponse> accounts() {
        return portfolioService.accounts();
    }

    @GetMapping("/cash")
    public List<CashBalanceResponse> cash() {
        return portfolioService.cash();
    }

    @GetMapping("/positions")
    public List<PositionResponse> positions() {
        return portfolioService.positions();
    }
}
