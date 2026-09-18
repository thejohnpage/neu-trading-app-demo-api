package com.neueda.leap.trading.identity;

import java.util.UUID;

public record ClientRegistrationResponse(
        UUID clientId,
        UUID accountId,
        String accountNumber,
        String email) {
}
