-- Reconstruct average-cost basis for positions that existed before V8.
-- Replay filled trades chronologically. BUY adds actual fill cost; SELL removes
-- the average cost of the quantity sold at that point in the history.

WITH RECURSIVE trade_history AS (
    SELECT
        o.account_id,
        o.instrument_id,
        o.order_id,
        o.side,
        f.quantity,
        f.price,
        f.filled_at,
        ROW_NUMBER() OVER (
            PARTITION BY o.account_id, o.instrument_id
            ORDER BY f.filled_at, f.fill_id
        ) AS rn
    FROM trading.orders o
    JOIN trading.fills f ON f.order_id = o.order_id
    WHERE o.status = 'FILLED'
),
replay AS (
    SELECT
        th.account_id,
        th.instrument_id,
        th.rn,
        CASE WHEN th.side = 'BUY' THEN th.quantity ELSE -th.quantity END AS running_quantity,
        CASE WHEN th.side = 'BUY' THEN th.quantity * th.price ELSE 0::numeric END AS running_cost_basis
    FROM trade_history th
    WHERE th.rn = 1

    UNION ALL

    SELECT
        th.account_id,
        th.instrument_id,
        th.rn,
        CASE
            WHEN th.side = 'BUY' THEN r.running_quantity + th.quantity
            ELSE r.running_quantity - th.quantity
        END,
        CASE
            WHEN th.side = 'BUY' THEN r.running_cost_basis + (th.quantity * th.price)
            WHEN r.running_quantity = th.quantity THEN 0::numeric
            ELSE GREATEST(
                0::numeric,
                r.running_cost_basis -
                    ((r.running_cost_basis / NULLIF(r.running_quantity, 0)) * th.quantity)
            )
        END
    FROM replay r
    JOIN trade_history th
      ON th.account_id = r.account_id
     AND th.instrument_id = r.instrument_id
     AND th.rn = r.rn + 1
),
latest AS (
    SELECT DISTINCT ON (account_id, instrument_id)
        account_id,
        instrument_id,
        running_quantity,
        running_cost_basis
    FROM replay
    ORDER BY account_id, instrument_id, rn DESC
)
UPDATE trading.positions p
SET cost_basis = ROUND(l.running_cost_basis, 8)
FROM latest l
WHERE p.account_id = l.account_id
  AND p.instrument_id = l.instrument_id
  AND p.quantity = l.running_quantity;
