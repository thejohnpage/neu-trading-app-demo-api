package com.neueda.leap.trading.cash;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CashBalanceRepository extends JpaRepository<CashBalance, CashBalanceId> {
    List<CashBalance> findByAccountIdInOrderByAccountIdAscCurrencyAsc(Collection<UUID> accountIds);
}
