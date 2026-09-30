ALTER TABLE trading.positions
    ADD COLUMN cost_basis NUMERIC(28,8) NOT NULL DEFAULT 0;
