-- V3 seeded 25 MSFT shares directly into the position projection without an
-- acquisition trade. Give that fixture an explicit documented demo acquisition
-- price so cost basis remains meaningful after the later 5-share SELL.
--
-- The V2 MSFT fixture quote was 510.00 bid / 510.25 ask. Treat the seeded
-- holding as having been acquired at the fixture ask price (510.25).
--
-- Original seeded basis: 25 * 510.25 = 12,756.25
-- Recorded SELL:          5 shares
-- Average-cost basis remaining after SELL:
--                         20 * 510.25 = 10,205.00

UPDATE trading.positions
SET cost_basis = 10205.00000000
WHERE account_id = '20000000-0000-0000-0000-000000000001'
  AND instrument_id = '40000000-0000-0000-0000-000000000002'
  AND quantity = 20.00000000
  AND cost_basis = 0;
