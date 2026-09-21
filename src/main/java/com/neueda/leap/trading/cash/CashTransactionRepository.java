package com.neueda.leap.trading.cash;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashTransactionRepository extends JpaRepository<CashTransaction,UUID> {
 List<CashTransaction> findByAccountIdOrderByCreatedAtDesc(UUID accountId);
}
