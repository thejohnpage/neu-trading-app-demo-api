-- One fill per order in the reference execution engine. This database-level
-- invariant makes Kafka redelivery safe in addition to the application check.
CREATE UNIQUE INDEX uq_fills_order ON trading.fills(order_id);
