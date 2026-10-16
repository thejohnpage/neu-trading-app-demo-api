package com.neueda.leap.trading.cash;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.neueda.leap.trading.account.Account;
import com.neueda.leap.trading.account.AccountRepository;
import com.neueda.leap.trading.client.CurrentClient;
import com.neueda.leap.trading.api.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CashTransferServiceTest {
    private final CurrentClient client=mock(CurrentClient.class);
    private final AccountRepository accounts=mock(AccountRepository.class);
    private final CashTransferMapper mapper=mock(CashTransferMapper.class);
    private final CashWalletService wallet=mock(CashWalletService.class);
    private final CashTransferService service=new CashTransferService(client,accounts,mapper,wallet);
    private final UUID clientId=UUID.randomUUID(),fromId=UUID.randomUUID(),toId=UUID.randomUUID(),transferId=UUID.randomUUID();

    @BeforeEach void setup() {}

    private Account account(UUID id,String currency) {
        Account a=new Account();a.setAccountId(id);a.setBaseCurrency(currency);a.setStatus("ACTIVE");return a;
    }
    private void owned(String source,String target) {
        when(client.clientId()).thenReturn(clientId);
        when(accounts.findByAccountIdAndClientId(fromId,clientId)).thenReturn(Optional.of(account(fromId,source)));
        when(accounts.findByAccountIdAndClientId(toId,clientId)).thenReturn(Optional.of(account(toId,target)));
        when(mapper.lockAccounts(fromId,toId)).thenReturn(List.of(fromId,toId));
    }
    private CashTransferRequest request() {
        return new CashTransferRequest(transferId,fromId,toId,new BigDecimal("100.00"));
    }

    @Test void sameCurrencyTransferDebitsAndCreditsAtomically() {
        owned("USD","USD");
        when(wallet.rate("USD","USD")).thenReturn(new FxRateResponse("USD","USD",BigDecimal.ONE,"IDENTITY",Instant.now()));
        when(mapper.debit(eq(fromId),eq("USD"),eq(new BigDecimal("100.00")),any())).thenReturn(1);
        when(mapper.credit(eq(toId),eq("USD"),eq(new BigDecimal("100.00000000")),any())).thenReturn(1);
        when(mapper.insert(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(1);
        when(mapper.ledger(any(),any(),any(),any(),any(),any(),any())).thenReturn(1);
        var result=service.transfer(request());
        assertEquals(0,result.creditedAmount().compareTo(new BigDecimal("100")));
        assertEquals("USD",result.toCurrency());
        verify(mapper).ledger(any(),eq(fromId),eq(transferId),eq("USD"),eq(new BigDecimal("-100.00")),eq("TRANSFER_OUT"),any());
        verify(mapper).ledger(any(),eq(toId),eq(transferId),eq("USD"),eq(new BigDecimal("100.00000000")),eq("TRANSFER_IN"),any());
    }

    @Test void fxTransferUsesQuotedRate() {
        owned("USD","GBP");
        when(wallet.rate("USD","GBP")).thenReturn(new FxRateResponse("USD","GBP",new BigDecimal("0.800000"),"TEST",Instant.now()));
        when(mapper.debit(eq(fromId),eq("USD"),any(),any())).thenReturn(1);
        when(mapper.credit(eq(toId),eq("GBP"),any(),any())).thenReturn(1);
        when(mapper.insert(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(1);
        when(mapper.ledger(any(),any(),any(),any(),any(),any(),any())).thenReturn(1);
        var result=service.transfer(request());
        assertEquals(0,result.creditedAmount().compareTo(new BigDecimal("80")));
        assertEquals("GBP",result.toCurrency());
    }

    @Test void rejectsInsufficientFundsBeforeAnyCredit() {
        owned("USD","USD");
        when(wallet.rate("USD","USD")).thenReturn(new FxRateResponse("USD","USD",BigDecimal.ONE,"IDENTITY",Instant.now()));
        assertThrows(IllegalArgumentException.class,()->service.transfer(request()));
        verify(mapper,never()).credit(any(),any(),any(),any());
        verify(mapper,never()).insert(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any());
    }

    @Test void retryReturnsPreviousTransferWithoutDebiting() {
        owned("USD","USD");
        var previous=new CashTransferResponse(transferId,fromId,toId,"USD","USD",
            new BigDecimal("100"),new BigDecimal("100"),BigDecimal.ONE,"IDENTITY",Instant.now());
        when(mapper.find(transferId,clientId)).thenReturn(previous);
        assertSame(previous,service.transfer(request()));
        verify(mapper,never()).debit(any(),any(),any(),any());
    }

    @Test void rejectsReusedTransferIdWithChangedAmount() {
        owned("USD","USD");
        var previous=new CashTransferResponse(transferId,fromId,toId,"USD","USD",
            new BigDecimal("200"),new BigDecimal("200"),BigDecimal.ONE,"IDENTITY",Instant.now());
        when(mapper.find(transferId,clientId)).thenReturn(previous);
        assertThrows(IllegalArgumentException.class,()->service.transfer(request()));
    }

    @Test void rejectsForeignDestination() {
        when(client.clientId()).thenReturn(clientId);
        when(accounts.findByAccountIdAndClientId(fromId,clientId)).thenReturn(Optional.of(account(fromId,"USD")));
        when(accounts.findByAccountIdAndClientId(toId,clientId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,()->service.transfer(request()));
        verifyNoInteractions(mapper);
    }

    @Test void rejectsSameAccount() {
        assertThrows(IllegalArgumentException.class,()->service.transfer(
            new CashTransferRequest(transferId,fromId,fromId,BigDecimal.ONE)));
        verifyNoInteractions(mapper);
    }
}
