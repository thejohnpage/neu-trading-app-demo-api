package com.neueda.leap.trading.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import com.neueda.leap.trading.account.*;
import com.neueda.leap.trading.api.ResourceNotFoundException;
import com.neueda.leap.trading.client.CurrentClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderQueryServiceTest {
    private CurrentClient client;
    private AccountRepository accounts;
    private OrderRepository orders;
    private OrderEventRepository events;
    private OrderQueryService service;
    private UUID clientId;
    private UUID accountId;

    @BeforeEach void setup() {
        client=mock(CurrentClient.class);
        accounts=mock(AccountRepository.class);
        orders=mock(OrderRepository.class);
        events=mock(OrderEventRepository.class);
        service=new OrderQueryService(client,accounts,orders,events);
        clientId=UUID.randomUUID();
        accountId=UUID.randomUUID();
        when(client.clientId()).thenReturn(clientId);
    }

    @Test void noOwnedAccountsReturnsNoOrdersWithoutQueryingOrders() {
        when(accounts.findByClientIdOrderByAccountNumber(clientId)).thenReturn(List.of());
        assertTrue(service.findAll().isEmpty());
        verifyNoInteractions(orders);
    }

    @Test void listOrdersIsRestrictedToOwnedAccountIds() {
        Account a=new Account();
        a.setAccountId(accountId);
        when(accounts.findByClientIdOrderByAccountNumber(clientId)).thenReturn(List.of(a));
        when(orders.findByAccountIds(Set.of(accountId))).thenReturn(List.of());
        assertTrue(service.findAll().isEmpty());
        verify(orders).findByAccountIds(Set.of(accountId));
    }

    @Test void cannotReadOrderOutsideOwnedAccounts() {
        UUID orderId=UUID.randomUUID();
        when(accounts.findByClientIdOrderByAccountNumber(clientId)).thenReturn(List.of());
        when(orders.findOwned(orderId,Set.of())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,()->service.findOne(orderId));
        verify(orders).findOwned(orderId,Set.of());
    }

    @Test void cannotReadLifecycleOfUnownedOrder() {
        UUID orderId=UUID.randomUUID();
        when(accounts.findByClientIdOrderByAccountNumber(clientId)).thenReturn(List.of());
        assertThrows(ResourceNotFoundException.class,()->service.lifecycle(orderId));
        verifyNoInteractions(events);
    }
}
