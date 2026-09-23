package com.neueda.leap.trading.marketdata;
import java.util.Optional; import java.util.UUID; import org.apache.ibatis.annotations.*;
/** MyBatis mapper for market quotes. */
@Mapper public interface MarketQuoteRepository {
 @Select("""
   SELECT q.quote_id quoteId,
          q.instrument_id instrumentId,
          i.symbol,
          i.quote_currency quoteCurrency,
          q.bid_price bidPrice,
          q.ask_price askPrice,
          q.source,
          q.quoted_at quotedAt,
          q.received_at receivedAt
   FROM trading.market_quotes q
   JOIN trading.instruments i ON i.instrument_id=q.instrument_id
   WHERE q.instrument_id=#{instrumentId}
   ORDER BY q.quoted_at DESC
   LIMIT 1
   """)
 Optional<MarketQuote> findLatest(UUID instrumentId);
}
