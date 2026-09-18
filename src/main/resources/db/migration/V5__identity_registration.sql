ALTER TABLE identity.users
    ADD COLUMN client_id UUID REFERENCES identity.clients(client_id);

CREATE INDEX idx_users_client ON identity.users(client_id);

INSERT INTO identity.roles (role_name, description) VALUES
    ('SUPER_ADMIN', 'Full user and role administration'),
    ('ADMIN_OPERATIONS', 'Operational order and lifecycle administration'),
    ('ADMIN_REPORTING', 'Warehouse reporting and analytics')
ON CONFLICT (role_name) DO NOTHING;
