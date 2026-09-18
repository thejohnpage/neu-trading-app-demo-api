DROP INDEX IF EXISTS identity.idx_users_client;
ALTER TABLE identity.users DROP COLUMN IF EXISTS client_id;
ALTER TABLE identity.clients ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255);
