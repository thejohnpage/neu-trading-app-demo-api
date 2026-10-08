package com.neueda.leap.trading.cash;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import com.neueda.leap.trading.account.*;
import com.neueda.leap.trading.client.CurrentClient;
import com.neueda.leap.trading.instrument.*;
import com.neueda.leap.trading.marketdata.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
class CashWalletServiceBranchesTest {
 private UUID accountId=UUID.randomUUID(),clientId=UUID.randomUUID();
 private AccountRepository accounts=mock(AccountRepository.class);
 private CashBalanceRepository balances=mock(CashBalanceRepository.class);
 private CashTransactionRepository transactions=mock(CashTransactionRepository.class);
 private InstrumentRepository instruments=mock(InstrumentRepository.class);
 private MarketDataService market=mock(MarketDataService.class);
 private CurrentClient client=mock(CurrentClient.class);
 private CashWalletService service;
 @BeforeEach void setup(){
  service=new CashWalletService(client,accounts,balances,transactions,market,instruments);
  when(client.clientId()).thenReturn(clientId);
  Account a=new Account();a.setAccountId(accountId);a.setStatus("ACTIVE");
  when(accounts.findByAccountIdAndClientId(accountId,clientId)).thenReturn(Optional.of(a));
 }
 @Test void withdrawalSucceedsAndWritesLedgerEntry(){
  CashBalance balance=CashBalance.open(accountId,"USD",Instant.now());
  balance.apply(new BigDecimal("100"),Instant.now());
  when(balances.find(accountId,"USD")).thenReturn(Optional.of(balance));
  when(balances.update(balance)).thenReturn(1);
  var result=service.withdraw(new CashMovementRequest(accountId,"USD",new BigDecimal("30")));
  assertEquals(0,new BigDecimal("70").compareTo(result.balance()));
  verify(transactions).insert(any(CashTransaction.class));
 }
 @Test void depositRejectsOptimisticLockConflict(){
  when(balances.find(accountId,"USD")).thenReturn(Optional.empty());
  assertThrows(IllegalStateException.class,
   ()->service.deposit(new CashMovementRequest(accountId,"USD",BigDecimal.ONE)));
  verify(transactions,never()).insert(any());
 }
 @Test void conversionRequiresSourceFunds(){
  CashBalance balance=CashBalance.open(accountId,"USD",Instant.now());
  when(balances.find(accountId,"USD")).thenReturn(Optional.of(balance));
  Instrument fx=new Instrument();fx.setInstrumentId(UUID.randomUUID());
  when(instruments.findFirstBySymbolIgnoreCaseOrderByExchangeAsc("USD/GBP")).thenReturn(Optional.of(fx));
  when(market.getCurrentQuoteByInstrumentId(fx.getInstrumentId()))
   .thenReturn(new QuoteResponse(fx.getInstrumentId(),"USD/GBP","GBP",new BigDecimal("0.75"),new BigDecimal("0.76"),"TEST",Instant.now()));
  assertThrows(IllegalArgumentException.class,
   ()->service.convert(new CashConversionRequest(accountId,"USD","GBP",BigDecimal.TEN)));
  verify(transactions,never()).insert(any());
 }
 @Test void inverseFxRateUsesAskPrice(){
  Instrument fx=new Instrument();fx.setInstrumentId(UUID.randomUUID());
  when(instruments.findFirstBySymbolIgnoreCaseOrderByExchangeAsc("GBP/USD")).thenReturn(Optional.of(fx));
  when(market.getCurrentQuoteByInstrumentId(fx.getInstrumentId()))
   .thenReturn(new QuoteResponse(fx.getInstrumentId(),"GBP/USD","USD",new BigDecimal("1.24"),new BigDecimal("1.25"),"TEST",Instant.now()));
  var result=service.rate("USD","GBP");
  assertEquals(0,new BigDecimal("0.8").compareTo(result.rate()));
 }
}
