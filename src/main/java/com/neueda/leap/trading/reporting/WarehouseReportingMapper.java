package com.neueda.leap.trading.reporting;
import java.util.*; import org.apache.ibatis.annotations.*;
/** MyBatis mapper executed against the reporting warehouse datasource. */
@Mapper public interface WarehouseReportingMapper {
 @Select("SELECT COUNT(*) trade_count,COALESCE(SUM(quantity),0) total_quantity,COALESCE(SUM(notional),0) total_notional,COUNT(DISTINCT symbol) instruments_traded,COUNT(DISTINCT client_id) active_clients,MAX(loaded_at) warehouse_last_loaded_at FROM dw.trade_activity") Map<String,Object> summary();
 @Select("SELECT order_id,client_segment,account_number,symbol,instrument_type,side,quantity,execution_price,notional,currency,filled_at,loaded_at FROM dw.trade_activity ORDER BY filled_at DESC LIMIT 500") List<Map<String,Object>> activity();
 @Select("SELECT symbol,instrument_type,COUNT(*) trade_count,SUM(quantity) total_quantity,SUM(notional) total_notional,AVG(execution_price) average_execution_price FROM dw.trade_activity GROUP BY symbol,instrument_type ORDER BY total_notional DESC") List<Map<String,Object>> instruments();
 @Select("SELECT COALESCE(client_segment,'UNSPECIFIED') client_segment,COUNT(*) trade_count,COUNT(DISTINCT client_id) clients,SUM(quantity) total_quantity,SUM(notional) total_notional FROM dw.trade_activity GROUP BY client_segment ORDER BY total_notional DESC") List<Map<String,Object>> clientSegments();
 @Select("SELECT trade_date,COUNT(*) trade_count,SUM(quantity) total_quantity,SUM(notional) total_notional FROM dw.trade_activity GROUP BY trade_date ORDER BY trade_date DESC") List<Map<String,Object>> volume();
}
