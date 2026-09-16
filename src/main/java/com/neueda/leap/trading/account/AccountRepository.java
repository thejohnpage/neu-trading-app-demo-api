package com.neueda.leap.trading.account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByClientIdOrderByAccountNumber(UUID clientId);
    Optional<Account> findByAccountIdAndClientId(UUID accountId, UUID clientId);
}
