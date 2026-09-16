package com.neueda.leap.trading.client;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.neueda.leap.trading.account.Account;
import com.neueda.leap.trading.account.AccountRepository;
import com.neueda.leap.trading.account.AccountResponse;
import com.neueda.leap.trading.cash.CashBalanceRepository;
import com.neueda.leap.trading.cash.CashBalanceResponse;
import com.neueda.leap.trading.position.PositionRepository;
import com.neueda.leap.trading.position.PositionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ClientPortfolioService {

    private final CurrentClient currentClient;
    private final AccountRepository accountRepository;
    private final CashBalanceRepository cashBalanceRepository;
    private final PositionRepository positionRepository;

    public ClientPortfolioService(
            CurrentClient currentClient,
            AccountRepository accountRepository,
            CashBalanceRepository cashBalanceRepository,
            PositionRepository positionRepository) {
        this.currentClient = currentClient;
        this.accountRepository = accountRepository;
        this.cashBalanceRepository = cashBalanceRepository;
        this.positionRepository = positionRepository;
    }

    public List<AccountResponse> accounts() {
        return ownedAccounts().stream().map(AccountResponse::from).toList();
    }

    public List<CashBalanceResponse> cash() {
        Set<UUID> accountIds = ownedAccountIds();
        if (accountIds.isEmpty()) return List.of();
        return cashBalanceRepository.findByAccountIdInOrderByAccountIdAscCurrencyAsc(accountIds)
                .stream().map(CashBalanceResponse::from).toList();
    }

    public List<PositionResponse> positions() {
        Set<UUID> accountIds = ownedAccountIds();
        if (accountIds.isEmpty()) return List.of();
        return positionRepository.findByAccountIdInOrderByAccountIdAscInstrumentIdAsc(accountIds)
                .stream().map(PositionResponse::from).toList();
    }

    private List<Account> ownedAccounts() {
        return accountRepository.findByClientIdOrderByAccountNumber(currentClient.clientId());
    }

    private Set<UUID> ownedAccountIds() {
        return ownedAccounts().stream().map(Account::getAccountId).collect(Collectors.toSet());
    }
}
