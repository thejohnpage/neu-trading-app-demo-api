package com.neueda.leap.trading.reporting;

import java.util.List; import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier; import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.stereotype.Service;
@Service
public class ReportingService {
 private final JdbcTemplate dw;
 public ReportingService(@Qualifier("warehouseJdbcTemplate") JdbcTemplate dw){this.dw=dw;}
 public Map<String,Object> summary(){return dw.queryForMap("""
  SELECT COUNT(*) AS trade_count, COALESCE(SUM(quantity),0) AS total_quantity,
         COALESCE(SUM(notional),0) AS total_notional,
         COUNT(DISTINCT symbol) AS instruments_traded,
         COUNT(DISTINCT client_id) AS active_clients,
         MAX(loaded_at) AS warehouse_last_loaded_at
  FROM dw.trade_activity
  """);}
 public List<Map<String,Object>> activity(){return dw.queryForList("""
  SELECT order_id, client_segment, account_number, symbol, instrument_type, side,
         quantity, execution_price, notional, currency, filled_at, loaded_at
  FROM dw.trade_activity ORDER BY filled_at DESC LIMIT 500
  """);}
 public List<Map<String,Object>> instruments(){return dw.queryForList("""
  SELECT symbol, instrument_type, COUNT(*) AS trade_count, SUM(quantity) AS total_quantity,
         SUM(notional) AS total_notional, AVG(execution_price) AS average_execution_price
  FROM dw.trade_activity GROUP BY symbol,instrument_type ORDER BY total_notional DESC
  """);}
 public List<Map<String,Object>> clientSegments(){return dw.queryForList("""
  SELECT COALESCE(client_segment,'UNSPECIFIED') AS client_segment, COUNT(*) AS trade_count,
         COUNT(DISTINCT client_id) AS clients, SUM(quantity) AS total_quantity, SUM(notional) AS total_notional
  FROM dw.trade_activity GROUP BY client_segment ORDER BY total_notional DESC
  """);}
 public List<Map<String,Object>> volume(){return dw.queryForList("""
  SELECT trade_date, COUNT(*) AS trade_count, SUM(quantity) AS total_quantity, SUM(notional) AS total_notional
  FROM dw.trade_activity GROUP BY trade_date ORDER BY trade_date DESC
  """);}
}
