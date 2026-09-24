CREATE TABLE identity.client_segments (
 segment_code VARCHAR(50) PRIMARY KEY,
 display_name VARCHAR(100) NOT NULL,
 description VARCHAR(255),
 active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Preserve every legacy segment value before adding referential integrity.
-- The BRS does not define a closed segment taxonomy, so existing classifications
-- must not be silently rewritten merely to satisfy the new foreign key.
INSERT INTO identity.client_segments(segment_code,display_name,description)
SELECT DISTINCT
       upper(btrim(client_segment)),
       initcap(replace(lower(btrim(client_segment)),'_',' ')),
       'Legacy client classification'
FROM identity.clients
WHERE client_segment IS NOT NULL
  AND btrim(client_segment) <> ''
ON CONFLICT (segment_code) DO NOTHING;

INSERT INTO identity.client_segments(segment_code,display_name,description) VALUES
 ('RETAIL','Retail','Default individual client classification'),
 ('AFFLUENT','Affluent','Internal affluent client classification'),
 ('INSTITUTIONAL','Institutional','Institutional client classification')
ON CONFLICT (segment_code) DO NOTHING;

-- New/self-registered clients default to RETAIL. Normalize existing values to
-- the canonical segment code that was inserted above.
UPDATE identity.clients
SET client_segment = CASE
  WHEN client_segment IS NULL OR btrim(client_segment)='' THEN 'RETAIL'
  ELSE upper(btrim(client_segment))
END;

ALTER TABLE identity.clients
 ADD CONSTRAINT fk_clients_segment
 FOREIGN KEY(client_segment) REFERENCES identity.client_segments(segment_code);

INSERT INTO identity.capabilities(capability_name,description)
VALUES ('CLIENT_MANAGEMENT','View and maintain client profiles and classifications')
ON CONFLICT (capability_name) DO NOTHING;

INSERT INTO identity.role_capabilities(role_id,capability_id)
SELECT r.role_id,c.capability_id
FROM identity.roles r
JOIN identity.capabilities c ON c.capability_name='CLIENT_MANAGEMENT'
WHERE r.role_name='SUPER_ADMIN'
ON CONFLICT DO NOTHING;
