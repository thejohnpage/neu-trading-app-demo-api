package com.neueda.leap.trading.marketdata;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
/** Latest market quote projection loaded by MyBatis. */
public class MarketQuote {
 private UUID quoteId; private UUID instrumentId; private String symbol; private String quoteCurrency; private BigDecimal bidPrice; private BigDecimal askPrice; private String source; private Instant quotedAt; private Instant receivedAt;
 public MarketQuote(){}
 public UUID getQuoteId(){return quoteId;} public void setQuoteId(UUID v){quoteId=v;} public UUID getInstrumentId(){return instrumentId;} public void setInstrumentId(UUID v){instrumentId=v;}
 public String getSymbol(){return symbol;} public void setSymbol(String v){symbol=v;} public String getQuoteCurrency(){return quoteCurrency;} public void setQuoteCurrency(String v){quoteCurrency=v;}
 public BigDecimal getBidPrice(){return bidPrice;} public void setBidPrice(BigDecimal v){bidPrice=v;} public BigDecimal getAskPrice(){return askPrice;} public void setAskPrice(BigDecimal v){askPrice=v;}
 public String getSource(){return source;} public void setSource(String v){source=v;} public Instant getQuotedAt(){return quotedAt;} public void setQuotedAt(Instant v){quotedAt=v;} public Instant getReceivedAt(){return receivedAt;} public void setReceivedAt(Instant v){receivedAt=v;}
}
