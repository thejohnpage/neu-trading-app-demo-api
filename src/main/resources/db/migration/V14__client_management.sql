CREATE TABLE identity.client_segments (
 segment_code VARCHAR(50) PRIMARY KEY,
 display_name VARCHAR(100) NOT NULL,
 description VARCHAR(255),
 active BOOLEAN NOT NULL DEFAULT TRUE
);
INSERT INTO identity.client_segments(segment_code,display_name,description) VALUES
 ('RETAIL','Retail','Default individual client classification'),
 ('AFFLUENT','Affluent','Internal affluent client classification'),
 ('INSTITUTIONAL','Institutional','Institutional client classification')
ON CONFLICT DO NOTHING;
UPDATE identity.clients SET client_segment='RETAIL' WHERE client_segment IS NULL OR btrim(client_segment)='';
ALTER TABLE identity.clients ADD CONSTRAINT fk_clients_segment FOREIGN KEY(client_segment) REFERENCES identity.client_segments(segment_code);
INSERT INTO identity.capabilities(capability_name,description) VALUES ('CLIENT_MANAGEMENT','View and maintain client profiles and classifications') ON CONFLICT DO NOTHING;
INSERT INTO identity.role_capabilities(role_id,capability_id)
SELECT r.role_id,c.capability_id FROM identity.roles r JOIN identity.capabilities c ON c.capability_name='CLIENT_MANAGEMENT'
WHERE r.role_name='SUPER_ADMIN' ON CONFLICT DO NOTHING;
