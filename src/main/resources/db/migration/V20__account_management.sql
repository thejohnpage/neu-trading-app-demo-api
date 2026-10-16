ALTER TABLE trading.accounts
  ADD COLUMN account_name VARCHAR(100),
  ADD COLUMN account_type VARCHAR(30) NOT NULL DEFAULT 'INDIVIDUAL';

UPDATE trading.accounts SET account_name = account_number WHERE account_name IS NULL;
ALTER TABLE trading.accounts ALTER COLUMN account_name SET NOT NULL;
ALTER TABLE trading.accounts ADD CONSTRAINT ck_account_type
  CHECK (account_type IN ('INDIVIDUAL'));
