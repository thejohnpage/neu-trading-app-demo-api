package com.neueda.leap.trading.client;

import com.neueda.leap.trading.identity.IdentityMapper;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountCreationServiceTest {
    @Test void opensAccountForAuthenticatedClientAndInitializesCash() {
        CurrentClient client = mock(CurrentClient.class);
        ClientPortfolioMapper portfolio = mock(ClientPortfolioMapper.class);
        IdentityMapper identity = mock(IdentityMapper.class);
        UUID clientId = UUID.randomUUID();
        when(client.clientId()).thenReturn(clientId);
        var result = new ClientPortfolioService(client, portfolio, identity).createAccount();
        assertEquals("ACTIVE", result.status());
        assertEquals("USD", result.baseCurrency());
        assertTrue(result.accountNumber().startsWith("ACC-"));
        var sequence = inOrder(identity);
        sequence.verify(identity).insertAccount(result.accountId(), clientId, result.accountNumber());
        sequence.verify(identity).insertInitialCash(result.accountId());
        verifyNoMoreInteractions(identity);
    }
}
