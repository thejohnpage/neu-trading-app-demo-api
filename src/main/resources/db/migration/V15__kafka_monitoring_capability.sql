INSERT INTO identity.capabilities(capability_name,description)
VALUES ('KAFKA_MONITORING','View Kafka cluster, topic, consumer offset and lag information')
ON CONFLICT (capability_name) DO NOTHING;

INSERT INTO identity.role_capabilities(role_id,capability_id)
SELECT r.role_id,c.capability_id
FROM identity.roles r
JOIN identity.capabilities c ON c.capability_name='KAFKA_MONITORING'
WHERE r.role_name='SUPER_ADMIN'
ON CONFLICT DO NOTHING;
