-- Give Joanna an initial holding so the BR-10 position endpoint has a useful
-- pre-trade fixture. Later fills will update this same current-state projection.
INSERT INTO trading.positions
    (account_id, instrument_id, quantity)
VALUES
    ('20000000-0000-0000-0000-000000000001',
     '40000000-0000-0000-0000-000000000002',
     25.00000000);
