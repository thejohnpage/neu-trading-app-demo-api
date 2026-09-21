package com.neueda.leap.trading.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.neueda.leap.trading.account.Account;
import com.neueda.leap.trading.account.AccountRepository;
import com.neueda.leap.trading.account.AccountResponse;
import com.neueda.leap.trading.cash.CashBalanceRepository;
import com.neueda.leap.trading.cash.CashBalanceResponse;
import com.neueda.leap.trading.marketdata.MarketDataService;
import com.neueda.leap.trading.marketdata.QuoteResponse;
import com.neueda.leap.trading.position.Position;
import com.neueda.leap.trading.position.PositionRepository;
import com.neueda.leap.trading.position.PositionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ClientPortfolioService {
    private final CurrentClient currentClient; private final AccountRepository accountRepository;
    private final CashBalanceRepository cashBalanceRepository; private final PositionRepository positionRepository;
    private final MarketDataService marketData;

    public ClientPortfolioService(CurrentClient currentClient,AccountRepository accountRepository,CashBalanceRepository cashBalanceRepository,
                                  PositionRepository positionRepository,MarketDataService marketData) {
        this.currentClient=currentClient;this.accountRepository=accountRepository;this.cashBalanceRepository=cashBalanceRepository;
        this.positionRepository=positionRepository;this.marketData=marketData;
    }

    public List<AccountResponse> accounts(){return ownedAccounts().stream().map(AccountResponse::from).toList();}
    public List<CashBalanceResponse> cash(){Set<UUID> ids=ownedAccountIds();if(ids.isEmpty())return List.of();return cashBalanceRepository.findByAccountIdInOrderByAccountIdAscCurrencyAsc(ids).stream().map(CashBalanceResponse::from).toList();}

    public List<PositionResponse> positions(){
        Set<UUID> ids=ownedAccountIds();if(ids.isEmpty())return List.of();
        return positionRepository.findByAccountIdInOrderByAccountIdAscInstrumentIdAsc(ids).stream().map(this::value).toList();
    }

    private PositionResponse value(Position p){
        QuoteResponse q=marketData.getCurrentQuoteByInstrumentId(p.getInstrumentId());
        BigDecimal currentPrice=q.bid();
        BigDecimal marketValue=p.getQuantity().multiply(currentPrice).setScale(8,RoundingMode.HALF_UP);
        BigDecimal cost=p.getCostBasis();
        BigDecimal gain=marketValue.subtract(cost).setScale(8,RoundingMode.HALF_UP);
        BigDecimal gainPct=cost.signum()==0?BigDecimal.ZERO:gain.multiply(BigDecimal.valueOf(100)).divide(cost,8,RoundingMode.HALF_UP);
        return new PositionResponse(p.getAccountId(),p.getInstrumentId(),p.getInstrument().getSymbol(),p.getInstrument().getInstrumentType(),
                p.getInstrument().getQuoteCurrency(),p.getQuantity(),currentPrice,marketValue,cost,gain,gainPct,p.getUpdatedAt());
    }

    private List<Account> ownedAccounts(){return accountRepository.findByClientIdOrderByAccountNumber(currentClient.clientId());}
    private Set<UUID> ownedAccountIds(){return ownedAccounts().stream().map(Account::getAccountId).collect(Collectors.toSet());}
}
