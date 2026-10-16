package com.neueda.leap.trading.account;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import com.neueda.leap.trading.api.ResourceNotFoundException;
import com.neueda.leap.trading.client.CurrentClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountManagementService {
    private static final Set<String> CURRENCIES = Set.of("USD", "GBP", "EUR", "CAD", "AUD");
    private final CurrentClient currentClient;
    private final AccountRepository accounts;
    private final AccountManagementMapper mapper;

    public AccountManagementService(CurrentClient currentClient, AccountRepository accounts, AccountManagementMapper mapper) {
        this.currentClient=currentClient; this.accounts=accounts; this.mapper=mapper;
    }

    public AccountResponse get(UUID id) { return AccountResponse.from(owned(id)); }

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        if (request == null) throw new IllegalArgumentException("Account details required");
        String name = name(request.accountName());
        if (!"INDIVIDUAL".equals(request.accountType())) throw new IllegalArgumentException("Only INDIVIDUAL accounts are supported");
        String currency = request.baseCurrency() == null ? "" : request.baseCurrency().toUpperCase(Locale.ROOT);
        if (!CURRENCIES.contains(currency)) throw new IllegalArgumentException("Unsupported base currency");
        UUID id = UUID.randomUUID();
        String number = "ACC-" + id.toString().substring(0, 8).toUpperCase(Locale.ROOT);
        UUID clientId = currentClient.clientId();
        if (mapper.create(id, clientId, number, name, currency) != 1 || mapper.initialCash(id, currency) != 1)
            throw new IllegalStateException("Account creation failed");
        return new AccountResponse(id, number, currency, "ACTIVE", name, "INDIVIDUAL");
    }

    @Transactional
    public AccountResponse update(UUID id, UpdateAccountRequest request) {
        Account account = owned(id);
        if (!"ACTIVE".equals(account.getStatus())) throw new IllegalArgumentException("Account is not active");
        if (request == null) throw new IllegalArgumentException("Account details required");
        if (mapper.rename(id, currentClient.clientId(), name(request.accountName())) != 1)
            throw new IllegalStateException("Account update failed");
        return get(id);
    }

    @Transactional
    public AccountResponse close(UUID id) {
        Account account = owned(id);
        if (!"ACTIVE".equals(account.getStatus())) throw new IllegalArgumentException("Account is not active");
        if (!mapper.canClose(id)) throw new IllegalArgumentException("Account has cash, holdings, or outstanding orders");
        if (mapper.close(id, currentClient.clientId()) != 1) throw new IllegalStateException("Account closure failed");
        return get(id);
    }

    private Account owned(UUID id) {
        return accounts.findByAccountIdAndClientId(id, currentClient.clientId())
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    private static String name(String value) {
        if (value == null || value.isBlank() || value.trim().length() > 100)
            throw new IllegalArgumentException("Account name must be 1 to 100 characters");
        return value.trim();
    }
}
