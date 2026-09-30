ALTER TABLE identity.roles ADD COLUMN IF NOT EXISTS system_role BOOLEAN NOT NULL DEFAULT FALSE;
CREATE TABLE identity.capabilities (
 capability_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 capability_name VARCHAR(80) NOT NULL UNIQUE,
 description VARCHAR(255) NOT NULL
);
CREATE TABLE identity.role_capabilities (
 role_id BIGINT NOT NULL REFERENCES identity.roles(role_id) ON DELETE CASCADE,
 capability_id BIGINT NOT NULL REFERENCES identity.capabilities(capability_id) ON DELETE CASCADE,
 PRIMARY KEY(role_id,capability_id)
);
INSERT INTO identity.capabilities(capability_name,description) VALUES
 ('USER_MANAGEMENT','Create, edit, enable and disable administrative users'),
 ('ROLE_MANAGEMENT','Create roles and configure role capabilities'),
 ('ORDER_OPERATIONS','Inspect orders, lifecycle events and pricing decisions'),
 ('AUDIT_VIEW','View the administrative audit trail'),
 ('REPORTING','View warehouse reports and analytics')
ON CONFLICT(capability_name) DO NOTHING;
UPDATE identity.roles SET system_role=true WHERE role_name='SUPER_ADMIN';
INSERT INTO identity.role_capabilities(role_id,capability_id)
SELECT r.role_id,c.capability_id FROM identity.roles r CROSS JOIN identity.capabilities c
WHERE r.role_name='SUPER_ADMIN' ON CONFLICT DO NOTHING;
INSERT INTO identity.role_capabilities(role_id,capability_id)
SELECT r.role_id,c.capability_id FROM identity.roles r JOIN identity.capabilities c ON
 (r.role_name IN ('ADMIN_OPERATIONS','TRADING_OPERATIONS') AND c.capability_name='ORDER_OPERATIONS') OR
 (r.role_name IN ('RISK','COMPLIANCE') AND c.capability_name IN ('ORDER_OPERATIONS','AUDIT_VIEW')) OR
 (r.role_name='ADMIN_OPERATIONS' AND c.capability_name='AUDIT_VIEW') OR
 (r.role_name IN ('ADMIN_REPORTING','ANALYST','FINANCE') AND c.capability_name='REPORTING')
ON CONFLICT DO NOTHING;
