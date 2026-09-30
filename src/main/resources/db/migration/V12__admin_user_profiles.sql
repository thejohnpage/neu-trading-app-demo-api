CREATE TABLE identity.user_profiles (
 user_id UUID PRIMARY KEY REFERENCES identity.users(user_id) ON DELETE CASCADE,
 job_title VARCHAR(120), department VARCHAR(120), phone VARCHAR(50),
 timezone VARCHAR(80), notes TEXT,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
