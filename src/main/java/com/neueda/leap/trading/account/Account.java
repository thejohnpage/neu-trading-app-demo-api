package com.neueda.leap.trading.account;
import java.time.Instant; import java.util.UUID;
/** Trading account domain object persisted by MyBatis. */
public class Account {
 private UUID accountId; private UUID clientId; private String accountNumber; private String baseCurrency; private String status; private Instant createdAt;
 public Account(){}
 public UUID getAccountId(){return accountId;} public void setAccountId(UUID v){accountId=v;} public UUID getClientId(){return clientId;} public void setClientId(UUID v){clientId=v;}
 public String getAccountNumber(){return accountNumber;} public void setAccountNumber(String v){accountNumber=v;} public String getBaseCurrency(){return baseCurrency;} public void setBaseCurrency(String v){baseCurrency=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
}
