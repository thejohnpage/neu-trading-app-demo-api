package com.neueda.leap.trading.cash;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import com.neueda.leap.trading.account.AccountRepository;
import com.neueda.leap.trading.client.CurrentClient;
import com.neueda.leap.trading.instrument.Instrument;
import com.neueda.leap.trading.instrument.InstrumentRepository;
import com.neueda.leap.trading.marketdata.MarketDataService;
import com.neueda.leap.trading.marketdata.QuoteResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CashWalletCrossFxTest {
    private final CurrentClient client=mock(CurrentClient.class);
    private final AccountRepository accounts=mock(AccountRepository.class);
    private final CashBalanceRepository balances=mock(CashBalanceRepository.class);
    private final CashTransactionRepository transactions=mock(CashTransactionRepository.class);
    private final MarketDataService market=mock(MarketDataService.class);
    private final InstrumentRepository instruments=mock(InstrumentRepository.class);
    private final CashWalletService service=new CashWalletService(client,accounts,balances,transactions,market,instruments);
    private final Instant timestamp=Instant.parse("2026-10-16T12:00:00Z");

    private void quote(String symbol, String bid, String ask) {
        Instrument instrument=new Instrument();
        UUID id=UUID.randomUUID();
        instrument.setInstrumentId(id);
        when(instruments.findFirstBySymbolIgnoreCaseOrderByExchangeAsc(symbol)).thenReturn(Optional.of(instrument));
        QuoteResponse response=new QuoteResponse(id,symbol,"USD",new BigDecimal(bid),new BigDecimal(ask),"CLASSROOM_SIMULATED_FX",timestamp);
        when(market.getCurrentQuoteByInstrumentId(id)).thenReturn(response);
    }

    @Test void directAudUsdUsesBid() {
        quote("AUD/USD","0.66000000","0.66100000");
        var result=service.rate("AUD","USD");
        assertEquals(0,result.rate().compareTo(new BigDecimal("0.66")));
        assertEquals("CLASSROOM_SIMULATED_FX",result.source());
    }

    @Test void inverseUsdAudUsesAsk() {
        quote("AUD/USD","0.66000000","0.66100000");
        var result=service.rate("USD","AUD");
        assertEquals(0,result.rate().compareTo(
            BigDecimal.ONE.divide(new BigDecimal("0.66100000"),12,java.math.RoundingMode.HALF_UP)));
    }

    @Test void gbpAudUsesUsdCrossWithoutFloatingPoint() {
        quote("GBP/USD","1.35000000","1.35100000");
        quote("AUD/USD","0.66000000","0.66100000");
        var result=service.rate("GBP","AUD");
        BigDecimal expected=new BigDecimal("1.35")
            .multiply(BigDecimal.ONE.divide(new BigDecimal("0.661"),12,java.math.RoundingMode.HALF_UP))
            .setScale(12,java.math.RoundingMode.HALF_UP);
        assertEquals(0,result.rate().compareTo(expected));
        assertTrue(result.source().startsWith("CROSS_USD"));
    }
}
