CREATE TABLE audit.audit_events (
    audit_event_id UUID PRIMARY KEY,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actor_type VARCHAR(20) NOT NULL,
    actor_id UUID NOT NULL,
    actor_email VARCHAR(255),
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_id VARCHAR(255),
    outcome VARCHAR(20) NOT NULL CHECK (outcome IN ('SUCCESS','FAILURE')),
    details JSONB NOT NULL DEFAULT '{}'::jsonb
);
CREATE INDEX idx_audit_events_time ON audit.audit_events(occurred_at DESC);
CREATE INDEX idx_audit_events_actor ON audit.audit_events(actor_type,actor_id,occurred_at DESC);
CREATE INDEX idx_audit_events_action ON audit.audit_events(action,occurred_at DESC);
CREATE INDEX idx_audit_events_resource ON audit.audit_events(resource_type,resource_id,occurred_at DESC);
