CREATE TABLE IF NOT EXISTS  otp_requests (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    otp_hash TEXT NOT NULL,

    purpose VARCHAR(30) NOT NULL,

    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,

    attempt_count INTEGER NOT NULL DEFAULT 0,
    max_attempts INTEGER NOT NULL DEFAULT 3,

    resend_count INTEGER NOT NULL DEFAULT 0,
    blocked_until TIMESTAMP,

    CONSTRAINT fk_otp_requests_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);
