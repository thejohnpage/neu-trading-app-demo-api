package com.neueda.leap.trading.account;

import java.util.Optional;
import java.util.UUID;
import com.neueda.leap.trading.client.CurrentClient;
import com.neueda.leap.trading.api.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountManagementServiceTest {
    private final CurrentClient client = mock(CurrentClient.class);
    private final AccountRepository accounts = mock(AccountRepository.class);
    private final AccountManagementMapper mapper = mock(AccountManagementMapper.class);
    private final UUID clientId = UUID.randomUUID();
    private final UUID accountId = UUID.randomUUID();
    private final AccountManagementService service = new AccountManagementService(client, accounts, mapper);

    @BeforeEach void setup() { when(client.clientId()).thenReturn(clientId); }

    @Test void createsGbpAccountWithGbpCash() {
        when(mapper.create(any(), eq(clientId), anyString(), eq("UK Trading"), eq("GBP"))).thenReturn(1);
        when(mapper.initialCash(any(), eq("GBP"))).thenReturn(1);
        AccountResponse created = service.create(new CreateAccountRequest("UK Trading", "INDIVIDUAL", "gbp"));
        assertEquals("GBP", created.baseCurrency());
        assertEquals("UK Trading", created.accountName());
        assertEquals("ACTIVE", created.status());
        verify(mapper).initialCash(created.accountId(), "GBP");
    }

    @Test void rejectsUnsupportedCurrencyWithoutWrites() {
        assertThrows(IllegalArgumentException.class, () -> service.create(new CreateAccountRequest("Test", "INDIVIDUAL", "XYZ")));
        verifyNoInteractions(mapper);
    }

    @Test void rejectsUnsupportedAccountType() {
        assertThrows(IllegalArgumentException.class, () -> service.create(new CreateAccountRequest("Test", "JOINT", "USD")));
        verifyNoInteractions(mapper);
    }

    @Test void rejectsForeignAccount() {
        when(accounts.findByAccountIdAndClientId(accountId, clientId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.close(accountId));
        verifyNoInteractions(mapper);
    }

    @Test void refusesClosureWithBalancesOrOpenOrders() {
        Account account = account("ACTIVE");
        when(accounts.findByAccountIdAndClientId(accountId, clientId)).thenReturn(Optional.of(account));
        when(mapper.canClose(accountId)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> service.close(accountId));
        verify(mapper, never()).close(any(), any());
    }

    @Test void closesEmptyAccountWithoutDeletingIt() {
        Account account = account("ACTIVE");
        when(accounts.findByAccountIdAndClientId(accountId, clientId)).thenReturn(Optional.of(account));
        when(mapper.canClose(accountId)).thenReturn(true);
        when(mapper.close(accountId, clientId)).thenReturn(1);
        Account closed = account("CLOSED");
        when(accounts.findByAccountIdAndClientId(accountId, clientId))
            .thenReturn(Optional.of(account), Optional.of(closed));
        assertEquals("CLOSED", service.close(accountId).status());
        verify(mapper).close(accountId, clientId);
    }

    private Account account(String status) {
        Account a = new Account();
        a.setAccountId(accountId);
        a.setClientId(clientId);
        a.setAccountNumber("ACC-12345678");
        a.setAccountName("Primary");
        a.setAccountType("INDIVIDUAL");
        a.setBaseCurrency("USD");
        a.setStatus(status);
        return a;
    }
}
