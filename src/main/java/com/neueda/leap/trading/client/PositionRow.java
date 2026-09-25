package com.neueda.leap.trading.client;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
/** MyBatis result object used to calculate client portfolio valuation. */
public class PositionRow {
 private UUID accountId; private UUID instrumentId; private String symbol; private String instrumentType; private String currency;
 private BigDecimal quantity; private BigDecimal costBasis; private BigDecimal currentPrice; private Instant updatedAt;
 public PositionRow(){}
 public UUID getAccountId(){return accountId;} public void setAccountId(UUID v){accountId=v;}
 public UUID getInstrumentId(){return instrumentId;} public void setInstrumentId(UUID v){instrumentId=v;}
 public String getSymbol(){return symbol;} public void setSymbol(String v){symbol=v;}
 public String getInstrumentType(){return instrumentType;} public void setInstrumentType(String v){instrumentType=v;}
 public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;}
 public BigDecimal getQuantity(){return quantity;} public void setQuantity(BigDecimal v){quantity=v;}
 public BigDecimal getCostBasis(){return costBasis;} public void setCostBasis(BigDecimal v){costBasis=v;}
 public BigDecimal getCurrentPrice(){return currentPrice;} public void setCurrentPrice(BigDecimal v){currentPrice=v;}
 public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
