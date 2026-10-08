package com.neueda.leap.trading.cash;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import com.neueda.leap.trading.account.*;
import com.neueda.leap.trading.client.CurrentClient;
import com.neueda.leap.trading.instrument.InstrumentRepository;
import com.neueda.leap.trading.marketdata.MarketDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CashWalletServiceTest {
    private final UUID clientId = UUID.randomUUID();
    private final UUID accountId = UUID.randomUUID();
    private CurrentClient client;
    private AccountRepository accounts;
    private CashBalanceRepository balances;
    private CashTransactionRepository transactions;
    private MarketDataService marketData;
    private InstrumentRepository instruments;
    private CashWalletService service;

    @BeforeEach void setup() {
        client = mock(CurrentClient.class);
        accounts = mock(AccountRepository.class);
        balances = mock(CashBalanceRepository.class);
        transactions = mock(CashTransactionRepository.class);
        marketData = mock(MarketDataService.class);
        instruments = mock(InstrumentRepository.class);
        service = new CashWalletService(client,accounts,balances,transactions,marketData,instruments);
        when(client.clientId()).thenReturn(clientId);
        Account account = new Account();
        account.setAccountId(accountId);
        account.setStatus("ACTIVE");
        when(accounts.findByAccountIdAndClientId(accountId,clientId)).thenReturn(Optional.of(account));
    }

    @Test void depositCreatesBalanceAndRecordsTransaction() {
        when(balances.find(accountId,"USD")).thenReturn(Optional.empty());
        when(balances.update(any(CashBalance.class))).thenReturn(1);
        CashBalanceResponse result=service.deposit(new CashMovementRequest(accountId," usd ",new BigDecimal("25.00")));
        assertEquals(new BigDecimal("25.00"),result.balance());
        verify(balances).insert(any(CashBalance.class));
        verify(transactions).insert(any(CashTransaction.class));
    }

    @Test void withdrawRejectsInsufficientCashWithoutWriting() {
        CashBalance balance=CashBalance.open(accountId,"USD",java.time.Instant.now());
        balance.apply(new BigDecimal("10"),java.time.Instant.now());
        when(balances.find(accountId,"USD")).thenReturn(Optional.of(balance));
        assertThrows(IllegalArgumentException.class,()->service.withdraw(new CashMovementRequest(accountId,"USD",new BigDecimal("11"))));
        verify(balances,never()).update(any());
        verifyNoInteractions(transactions);
    }

    @Test void invalidMovementAmountRejected() {
        assertThrows(IllegalArgumentException.class,()->service.deposit(new CashMovementRequest(accountId,"USD",BigDecimal.ZERO)));
        assertThrows(IllegalArgumentException.class,()->service.withdraw(new CashMovementRequest(accountId,"USD",new BigDecimal("-1"))));
        verifyNoInteractions(balances,transactions);
    }

    @Test void missingAccountCannotDeposit() {
        assertThrows(com.neueda.leap.trading.api.ResourceNotFoundException.class,
            ()->service.deposit(new CashMovementRequest(UUID.randomUUID(),"USD",BigDecimal.ONE)));
        verifyNoInteractions(balances,transactions);
    }

    @Test void identicalCurrencyRateIsOne() {
        FxRateResponse result=service.rate("usd"," USD ");
        assertEquals(0,BigDecimal.ONE.compareTo(result.rate()));
        verifyNoInteractions(instruments,marketData);
    }

    @Test void missingFxPairRejected() {
        when(instruments.findFirstBySymbolIgnoreCaseOrderByExchangeAsc(anyString())).thenReturn(Optional.empty());
        assertThrows(com.neueda.leap.trading.api.ResourceNotFoundException.class,()->service.rate("USD","GBP"));
    }

    @Test void sameCurrencyConversionRejected() {
        assertThrows(IllegalArgumentException.class,()->service.convert(new CashConversionRequest(accountId,"USD","usd",BigDecimal.ONE)));
        verifyNoInteractions(balances,transactions);
    }

    @Test void invalidCurrencyRejected() {
        assertThrows(IllegalArgumentException.class,()->service.deposit(new CashMovementRequest(accountId,"US1",BigDecimal.ONE)));
        assertThrows(IllegalArgumentException.class,()->service.rate("","GBP"));
    }
}
