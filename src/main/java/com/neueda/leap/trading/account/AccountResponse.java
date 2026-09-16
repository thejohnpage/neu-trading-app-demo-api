package com.neueda.leap.trading.account;

import java.util.UUID;

public record AccountResponse(
        UUID accountId,
        String accountNumber,
        String baseCurrency,
        String status) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getAccountId(),
                account.getAccountNumber(),
                account.getBaseCurrency(),
                account.getStatus());
    }
}
