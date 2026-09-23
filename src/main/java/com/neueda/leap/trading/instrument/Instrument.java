package com.neueda.leap.trading.instrument;
import java.time.Instant; import java.util.UUID;
/** Tradable instrument domain object persisted by MyBatis. */
public class Instrument {
 private UUID instrumentId; private String symbol; private String instrumentType; private String exchange; private String baseCurrency; private String quoteCurrency; private String name; private boolean tradable; private Instant createdAt;
 public Instrument(){}
 public UUID getInstrumentId(){return instrumentId;} public void setInstrumentId(UUID v){instrumentId=v;} public String getSymbol(){return symbol;} public void setSymbol(String v){symbol=v;}
 public String getInstrumentType(){return instrumentType;} public void setInstrumentType(String v){instrumentType=v;} public String getExchange(){return exchange;} public void setExchange(String v){exchange=v;}
 public String getBaseCurrency(){return baseCurrency;} public void setBaseCurrency(String v){baseCurrency=v;} public String getQuoteCurrency(){return quoteCurrency;} public void setQuoteCurrency(String v){quoteCurrency=v;}
 public String getName(){return name;} public void setName(String v){name=v;} public boolean isTradable(){return tradable;} public void setTradable(boolean v){tradable=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
}
