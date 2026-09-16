package com.neueda.leap.trading.account;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts", schema = "trading")
public class Account {

    @Id
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "account_number", nullable = false, length = 30)
    private String accountNumber;

    @Column(name = "base_currency", nullable = false, length = 10)
    private String baseCurrency;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Account() {
    }

    public UUID getAccountId() { return accountId; }
    public UUID getClientId() { return clientId; }
    public String getAccountNumber() { return accountNumber; }
    public String getBaseCurrency() { return baseCurrency; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
