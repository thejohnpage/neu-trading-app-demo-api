package com.neueda.leap.trading.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.neueda.leap.trading.account.AccountResponse;
import com.neueda.leap.trading.cash.CashBalanceResponse;
import com.neueda.leap.trading.position.PositionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Builds the client portfolio read model from explicit MyBatis queries.
 *
 * <p>Financial calculations use {@link BigDecimal}; no floating-point
 * arithmetic is used for market value or unrealized profit/loss.</p>
 */
@Service
@Transactional(readOnly = true)
public class ClientPortfolioService {
    private final CurrentClient currentClient;
    private final ClientPortfolioMapper mapper;

    /** Creates the portfolio service. */
    public ClientPortfolioService(CurrentClient currentClient,ClientPortfolioMapper mapper){
        this.currentClient=currentClient;this.mapper=mapper;
    }

    /** Returns accounts owned by the current client. */
    public List<AccountResponse> accounts(){
        return mapper.accounts(currentClient.clientId()).stream()
                .map(a -> new AccountResponse(a.accountId(),a.accountNumber(),a.baseCurrency(),a.status()))
                .toList();
    }

    /** Returns cash balances owned by the current client. */
    public List<CashBalanceResponse> cash(){
        return mapper.cash(currentClient.clientId()).stream()
                .map(c -> new CashBalanceResponse(c.accountId(),c.currency(),c.balance(),c.updatedAt()))
                .toList();
    }

    /** Returns current positions valued at the latest bid quote. */
    public List<PositionResponse> positions(){
        return mapper.positions(currentClient.clientId()).stream().map(this::value).toList();
    }

    private PositionResponse value(ClientPortfolioMapper.PositionRow p){
        BigDecimal marketValue=p.quantity().multiply(p.currentPrice()).setScale(8,RoundingMode.HALF_UP);
        BigDecimal gain=marketValue.subtract(p.costBasis()).setScale(8,RoundingMode.HALF_UP);
        BigDecimal gainPct=p.costBasis().signum()==0?BigDecimal.ZERO:
                gain.multiply(BigDecimal.valueOf(100)).divide(p.costBasis(),8,RoundingMode.HALF_UP);
        return new PositionResponse(p.accountId(),p.instrumentId(),p.symbol(),p.instrumentType(),p.currency(),
                p.quantity(),p.currentPrice(),marketValue,p.costBasis(),gain,gainPct,p.updatedAt());
    }
}
