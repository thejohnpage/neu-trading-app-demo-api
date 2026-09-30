ALTER TABLE trading.cash_transactions
    DROP CONSTRAINT IF EXISTS cash_transactions_transaction_type_check;

ALTER TABLE trading.cash_transactions
    ADD CONSTRAINT cash_transactions_transaction_type_check
    CHECK (transaction_type IN (
        'TRADE_BUY', 'TRADE_SELL', 'DEMO_FUNDING', 'ADJUSTMENT',
        'DEPOSIT', 'WITHDRAWAL', 'FX_DEBIT', 'FX_CREDIT'
    ));
