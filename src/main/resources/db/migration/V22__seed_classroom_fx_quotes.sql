-- SIMULATED CLASSROOM DATA ONLY. Not live market prices.
-- Illustrative USD quote units per one unit of the foreign base currency.
-- Do not update historical quotes when refreshing this migration.
WITH fixture(symbol, base_ccy, description, bid, ask) AS (
  VALUES
    ('EUR/USD', 'EUR', 'Euro / US Dollar (classroom)', 1.08000000::numeric, 1.08100000::numeric),
    ('CAD/USD', 'CAD', 'Canadian Dollar / US Dollar (classroom)', 0.73000000::numeric, 0.73100000::numeric),
    ('AUD/USD', 'AUD', 'Australian Dollar / US Dollar (classroom)', 0.66000000::numeric, 0.66100000::numeric)
),
inserted AS (
  INSERT INTO trading.instruments
    (instrument_id, symbol, instrument_type, exchange, base_currency, quote_currency, name, tradable)
  SELECT gen_random_uuid(), symbol, 'FX', NULL, base_ccy, 'USD', description, false
  FROM fixture
  ON CONFLICT DO NOTHING
  RETURNING instrument_id, symbol
)
INSERT INTO trading.market_quotes
  (quote_id, instrument_id, bid_price, ask_price, source, quoted_at)
SELECT gen_random_uuid(), i.instrument_id, f.bid, f.ask, 'CLASSROOM_SIMULATED_FX', CURRENT_TIMESTAMP
FROM inserted i JOIN fixture f ON f.symbol = i.symbol;
