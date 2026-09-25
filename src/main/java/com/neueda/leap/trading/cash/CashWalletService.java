package com.neueda.leap.trading.cash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.neueda.leap.trading.account.Account;
import com.neueda.leap.trading.account.AccountRepository;
import com.neueda.leap.trading.api.ResourceNotFoundException;
import com.neueda.leap.trading.client.CurrentClient;
import com.neueda.leap.trading.instrument.Instrument;
import com.neueda.leap.trading.instrument.InstrumentRepository;
import com.neueda.leap.trading.marketdata.MarketDataService;
import com.neueda.leap.trading.marketdata.QuoteResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CashWalletService {
 private final CurrentClient currentClient;
 private final AccountRepository accounts;
 private final CashBalanceRepository balances;
 private final CashTransactionRepository transactions;
 private final MarketDataService marketData;
 private final InstrumentRepository instruments;

 public CashWalletService(CurrentClient currentClient,AccountRepository accounts,CashBalanceRepository balances,
                          CashTransactionRepository transactions,MarketDataService marketData,InstrumentRepository instruments){
  this.currentClient=currentClient;this.accounts=accounts;this.balances=balances;this.transactions=transactions;
  this.marketData=marketData;this.instruments=instruments;
 }

 @Transactional
 public CashBalanceResponse deposit(CashMovementRequest request){
  validateMovement(request); Account account=ownedActiveAccount(request.accountId());
  String currency=currency(request.currency()); Instant now=Instant.now();
  CashBalance balance=balance(account.getAccountId(),currency,now);
  balance.apply(request.amount(),now); if(balances.update(balance)!=1) throw new IllegalStateException("Concurrent cash update");
  transactions.insert(CashTransaction.movement(account.getAccountId(),currency,request.amount(),"DEPOSIT",now));
  return CashBalanceResponse.from(balance);
 }

 @Transactional
 public CashBalanceResponse withdraw(CashMovementRequest request){
  validateMovement(request); Account account=ownedActiveAccount(request.accountId());
  String currency=currency(request.currency()); Instant now=Instant.now();
  CashBalance balance=balances.find(account.getAccountId(),currency)
      .orElseThrow(() -> new IllegalArgumentException("No cash balance in "+currency));
  if(balance.getBalance().compareTo(request.amount())<0)throw new IllegalArgumentException("Insufficient cash in "+currency);
  balance.apply(request.amount().negate(),now); if(balances.update(balance)!=1) throw new IllegalStateException("Concurrent cash update");
  transactions.insert(CashTransaction.movement(account.getAccountId(),currency,request.amount().negate(),"WITHDRAWAL",now));
  return CashBalanceResponse.from(balance);
 }

 @Transactional(readOnly=true)
 public FxRateResponse rate(String from,String to){
  String source=currency(from),target=currency(to);
  if(source.equals(target))return new FxRateResponse(source,target,BigDecimal.ONE,"IDENTITY",Instant.now());

  Instrument direct=instruments.findFirstBySymbolIgnoreCaseOrderByExchangeAsc(source+"/"+target).orElse(null);
  if(direct!=null){
   QuoteResponse q=marketData.getCurrentQuoteByInstrumentId(direct.getInstrumentId());
   return new FxRateResponse(source,target,q.bid(),q.source(),q.quotedAt());
  }

  Instrument inverse=instruments.findFirstBySymbolIgnoreCaseOrderByExchangeAsc(target+"/"+source).orElse(null);
  if(inverse==null)throw new ResourceNotFoundException("No FX market available for "+source+"/"+target);

  QuoteResponse q=marketData.getCurrentQuoteByInstrumentId(inverse.getInstrumentId());
  BigDecimal inverseRate=BigDecimal.ONE.divide(q.ask(),12,RoundingMode.HALF_UP);
  return new FxRateResponse(source,target,inverseRate,q.source(),q.quotedAt());
 }

 @Transactional
 public CashConversionResponse convert(CashConversionRequest request){
  if(request==null||request.accountId()==null||request.amount()==null||request.amount().signum()<=0)
   throw new IllegalArgumentException("Account and positive amount are required");
  Account account=ownedActiveAccount(request.accountId());
  String from=currency(request.fromCurrency()),to=currency(request.toCurrency());
  if(from.equals(to))throw new IllegalArgumentException("Source and target currencies must differ");
  FxRateResponse fx=rate(from,to); Instant now=Instant.now();
  CashBalance source=balances.find(account.getAccountId(),from)
      .orElseThrow(() -> new IllegalArgumentException("No cash balance in "+from));
  if(source.getBalance().compareTo(request.amount())<0)throw new IllegalArgumentException("Insufficient cash in "+from);
  BigDecimal credited=request.amount().multiply(fx.rate()).setScale(8,RoundingMode.HALF_UP);
  CashBalance target=balance(account.getAccountId(),to,now);
  source.apply(request.amount().negate(),now); target.apply(credited,now); if(balances.update(source)!=1||balances.update(target)!=1) throw new IllegalStateException("Concurrent cash update");
  transactions.insert(CashTransaction.movement(account.getAccountId(),from,request.amount().negate(),"FX_DEBIT",now));
  transactions.insert(CashTransaction.movement(account.getAccountId(),to,credited,"FX_CREDIT",now));
  return new CashConversionResponse(from,to,request.amount(),credited,fx.rate(),fx.source());
 }

 @Transactional(readOnly=true)
 public List<CashTransactionResponse> transactions(UUID accountId){
  ownedActiveAccount(accountId);
  return transactions.findByAccountIdOrderByCreatedAtDesc(accountId).stream().map(CashTransactionResponse::from).toList();
 }

 private CashBalance balance(UUID accountId,String currency,Instant now){
  return balances.find(accountId,currency)
      .orElseGet(() -> createBalance(accountId,currency,now));
 }

 private CashBalance createBalance(UUID accountId,String currency,Instant now){CashBalance b=CashBalance.open(accountId,currency,now);balances.insert(b);return b;}

 private Account ownedActiveAccount(UUID accountId){
  Account a=accounts.findByAccountIdAndClientId(accountId,currentClient.clientId())
      .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
  if(!"ACTIVE".equals(a.getStatus()))throw new IllegalArgumentException("Account is not active");
  return a;
 }

 private void validateMovement(CashMovementRequest request){
  if(request==null||request.accountId()==null||request.amount()==null||request.amount().signum()<=0)
   throw new IllegalArgumentException("Account and positive amount are required");
  currency(request.currency());
 }

 private String currency(String value){
  if(value==null||value.isBlank())throw new IllegalArgumentException("Currency is required");
  String c=value.trim().toUpperCase(Locale.ROOT);
  if(!c.matches("[A-Z]{3,10}"))throw new IllegalArgumentException("Invalid currency");
  return c;
 }
}
