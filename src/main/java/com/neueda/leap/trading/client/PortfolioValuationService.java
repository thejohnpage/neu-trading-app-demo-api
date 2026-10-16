package com.neueda.leap.trading.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import com.neueda.leap.trading.account.AccountRepository;
import com.neueda.leap.trading.api.ResourceNotFoundException;
import com.neueda.leap.trading.cash.CashWalletService;
import com.neueda.leap.trading.cash.FxRateResponse;
import com.neueda.leap.trading.position.PositionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PortfolioValuationService {
    private static final Set<String> SUPPORTED = Set.of("USD","GBP","EUR","CAD","AUD");
    private final CurrentClient client;
    private final AccountRepository accounts;
    private final ClientPortfolioService portfolio;
    private final CashWalletService wallet;

    public PortfolioValuationService(CurrentClient client, AccountRepository accounts,
                                     ClientPortfolioService portfolio, CashWalletService wallet) {
        this.client=client; this.accounts=accounts; this.portfolio=portfolio; this.wallet=wallet;
    }

    public PortfolioValuationResponse value(UUID accountId, String reportingCurrency) {
        if (reportingCurrency == null) throw new IllegalArgumentException("Reporting currency required");
        String target = reportingCurrency.trim().toUpperCase(Locale.ROOT);
        if (!SUPPORTED.contains(target)) throw new IllegalArgumentException("Unsupported reporting currency");
        if (accountId != null && accounts.findByAccountIdAndClientId(accountId,client.clientId()).isEmpty())
            throw new ResourceNotFoundException("Account not found");

        var cash = portfolio.cash().stream().filter(c -> accountId == null || accountId.equals(c.accountId())).toList();
        var positions = portfolio.positions().stream().filter(p -> accountId == null || accountId.equals(p.accountId())).toList();
        Set<String> currencies = new TreeSet<>();
        cash.forEach(c -> currencies.add(c.currency()));
        positions.forEach(p -> currencies.add(p.currency()));

        Map<String,FxRateResponse> rates = new HashMap<>();
        for (String currency : currencies) {
            FxRateResponse fx = currency.equals(target)
                ? new FxRateResponse(target,target,BigDecimal.ONE,"IDENTITY",null)
                : wallet.rate(currency,target);
            if (fx.rate()==null || fx.rate().signum()<=0)
                throw new IllegalArgumentException("Invalid FX rate for " + currency + "/" + target);
            rates.put(currency,fx);
        }

        var valuedCash = cash.stream().map(c -> new PortfolioValuationResponse.ValuedCash(
            c.accountId(),c.currency(),c.balance(),money(c.balance().multiply(rates.get(c.currency()).rate()))
        )).toList();
        var valuedPositions = positions.stream().map(p -> position(p,rates.get(p.currency()).rate())).toList();

        BigDecimal totalCash=valuedCash.stream().map(PortfolioValuationResponse.ValuedCash::reportedBalance)
            .reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal market=valuedPositions.stream().map(PortfolioValuationResponse.ValuedPosition::reportedMarketValue)
            .reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal cost=valuedPositions.stream().map(PortfolioValuationResponse.ValuedPosition::reportedCostBasis)
            .reduce(BigDecimal.ZERO,BigDecimal::add);
        var applied = currencies.stream().map(c -> {
            var fx=rates.get(c);
            return new PortfolioValuationResponse.AppliedRate(c,target,fx.rate(),fx.source(),fx.quotedAt());
        }).toList();
        return new PortfolioValuationResponse(target,accountId,money(totalCash),money(market),money(cost),
            money(market.subtract(cost)),money(totalCash.add(market)),valuedCash,valuedPositions,applied);
    }

    private PortfolioValuationResponse.ValuedPosition position(PositionResponse p,BigDecimal rate) {
        BigDecimal market=money(p.marketValue().multiply(rate));
        BigDecimal cost=money(p.costBasis().multiply(rate));
        return new PortfolioValuationResponse.ValuedPosition(p.accountId(),p.instrumentId(),p.symbol(),p.currency(),
            p.marketValue(),p.costBasis(),p.unrealizedGainLoss(),market,cost,money(market.subtract(cost)));
    }

    private static BigDecimal money(BigDecimal amount) {
        return amount.setScale(8,RoundingMode.HALF_UP);
    }
}
