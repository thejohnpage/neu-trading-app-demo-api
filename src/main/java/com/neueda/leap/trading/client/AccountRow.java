package com.neueda.leap.trading.client;
import java.util.UUID;
/** MyBatis result object for a client account. */
public class AccountRow {
 private UUID accountId; private String accountNumber; private String baseCurrency; private String status;
 public AccountRow(){}
 public UUID getAccountId(){return accountId;} public void setAccountId(UUID v){accountId=v;}
 public String getAccountNumber(){return accountNumber;} public void setAccountNumber(String v){accountNumber=v;}
 public String getBaseCurrency(){return baseCurrency;} public void setBaseCurrency(String v){baseCurrency=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
