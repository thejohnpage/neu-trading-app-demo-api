CREATE TABLE trading.cash_transfers (
    transfer_id UUID PRIMARY KEY,
    client_id UUID NOT NULL REFERENCES identity.clients(client_id),
    from_account_id UUID NOT NULL REFERENCES trading.accounts(account_id),
    to_account_id UUID NOT NULL REFERENCES trading.accounts(account_id),
    from_currency VARCHAR(10) NOT NULL,
    to_currency VARCHAR(10) NOT NULL,
    debited_amount NUMERIC(20,8) NOT NULL CHECK (debited_amount > 0),
    credited_amount NUMERIC(20,8) NOT NULL CHECK (credited_amount > 0),
    exchange_rate NUMERIC(24,12) NOT NULL CHECK (exchange_rate > 0),
    rate_source VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT cash_transfer_distinct_accounts CHECK (from_account_id <> to_account_id)
);

ALTER TABLE trading.cash_transactions
    ADD COLUMN transfer_id UUID REFERENCES trading.cash_transfers(transfer_id);

ALTER TABLE trading.cash_transactions
    DROP CONSTRAINT IF EXISTS cash_transactions_transaction_type_check;
ALTER TABLE trading.cash_transactions
    ADD CONSTRAINT cash_transactions_transaction_type_check
    CHECK (transaction_type IN (
        'TRADE_BUY', 'TRADE_SELL', 'DEMO_FUNDING', 'ADJUSTMENT',
        'DEPOSIT', 'WITHDRAWAL', 'FX_DEBIT', 'FX_CREDIT',
        'TRANSFER_OUT', 'TRANSFER_IN'
    ));

CREATE INDEX idx_cash_transactions_transfer ON trading.cash_transactions(transfer_id)
    WHERE transfer_id IS NOT NULL;
CREATE INDEX idx_cash_transfers_client_time ON trading.cash_transfers(client_id, created_at DESC);
