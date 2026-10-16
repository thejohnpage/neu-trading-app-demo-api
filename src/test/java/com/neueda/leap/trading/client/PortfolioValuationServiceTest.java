package com.neueda.leap.trading.client;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.neueda.leap.trading.account.Account;
import com.neueda.leap.trading.account.AccountRepository;
import com.neueda.leap.trading.cash.CashBalanceResponse;
import com.neueda.leap.trading.cash.CashWalletService;
import com.neueda.leap.trading.cash.FxRateResponse;
import com.neueda.leap.trading.position.PositionResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PortfolioValuationServiceTest {
    private final CurrentClient client=mock(CurrentClient.class);
    private final AccountRepository accounts=mock(AccountRepository.class);
    private final ClientPortfolioService portfolio=mock(ClientPortfolioService.class);
    private final CashWalletService wallet=mock(CashWalletService.class);
    private final PortfolioValuationService service=new PortfolioValuationService(client,accounts,portfolio,wallet);
    private final UUID accountId=UUID.randomUUID();

    @Test void consolidatesCashAndPositionsUsingExactDecimalArithmetic() {
        when(portfolio.cash()).thenReturn(List.of(
            new CashBalanceResponse(accountId,"USD",new BigDecimal("100.10"),Instant.now()),
            new CashBalanceResponse(accountId,"GBP",new BigDecimal("50.20"),Instant.now())));
        when(portfolio.positions()).thenReturn(List.of(
            new PositionResponse(accountId,UUID.randomUUID(),"ABC","EQUITY","GBP",
                BigDecimal.ONE,BigDecimal.TEN,new BigDecimal("20.00"),new BigDecimal("12.00"),
                new BigDecimal("8.00"),BigDecimal.ZERO,Instant.now())));
        when(wallet.rate("GBP","USD")).thenReturn(
            new FxRateResponse("GBP","USD",new BigDecimal("1.25"),"TEST",Instant.now()));
        var result=service.value(null,"USD");
        assertEquals(0,result.totalCash().compareTo(new BigDecimal("162.85")));
        assertEquals(0,result.totalMarketValue().compareTo(new BigDecimal("25")));
        assertEquals(0,result.totalCostBasis().compareTo(new BigDecimal("15")));
        assertEquals(0,result.unrealizedProfitLoss().compareTo(new BigDecimal("10")));
        assertEquals(0,result.totalEquity().compareTo(new BigDecimal("187.85")));
        assertEquals(0,result.cash().get(1).nativeBalance().compareTo(new BigDecimal("50.20")));
    }

    @Test void rejectsUnownedAccountBeforeValuation() {
        UUID clientId=UUID.randomUUID();
        when(client.clientId()).thenReturn(clientId);
        when(accounts.findByAccountIdAndClientId(accountId,clientId)).thenReturn(Optional.empty());
        assertThrows(com.neueda.leap.trading.api.ResourceNotFoundException.class,
            () -> service.value(accountId,"USD"));
        verifyNoInteractions(portfolio,wallet);
    }

    @Test void rejectsUnsupportedReportingCurrency() {
        assertThrows(IllegalArgumentException.class,()->service.value(null,"XYZ"));
        verifyNoInteractions(portfolio,wallet);
    }
}
