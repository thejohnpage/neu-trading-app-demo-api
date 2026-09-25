package com.neueda.leap.trading.instrument;
import java.util.*; import org.apache.ibatis.annotations.*;
/** MyBatis mapper for instruments. */
@Mapper public interface InstrumentRepository {
 String COLS="instrument_id instrumentId,symbol,instrument_type instrumentType,exchange,base_currency baseCurrency,quote_currency quoteCurrency,name,tradable,created_at createdAt";
 @Select("SELECT "+COLS+" FROM trading.instruments ORDER BY symbol") List<Instrument> findAllByOrderBySymbolAsc();
 @Select("SELECT "+COLS+" FROM trading.instruments WHERE lower(symbol)=lower(#{symbol}) ORDER BY exchange NULLS FIRST LIMIT 1") Optional<Instrument> findFirstBySymbolIgnoreCaseOrderByExchangeAsc(String symbol);
}
