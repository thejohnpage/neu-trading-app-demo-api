package com.neueda.leap.trading.account;

import java.util.UUID;

public record AccountResponse(
        UUID accountId,
        String accountNumber,
        String baseCurrency,
        String status,
        String accountName,
        String accountType) {

    public AccountResponse(UUID accountId, String accountNumber, String baseCurrency, String status) {
        this(accountId, accountNumber, baseCurrency, status, accountNumber, "INDIVIDUAL");
    }


    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getAccountId(),
                account.getAccountNumber(),
                account.getBaseCurrency(),
                account.getStatus(), account.getAccountName(), account.getAccountType());
    }
}
