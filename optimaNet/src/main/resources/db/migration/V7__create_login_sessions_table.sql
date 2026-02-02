CREATE TABLE  IF NOT EXISTS login_sessions (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    ip_address VARCHAR(255) NOT NULL,

    login_at TIMESTAMP NOT NULL,
    last_activity_at TIMESTAMP NOT NULL,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    login_session_status VARCHAR(30) NOT NULL DEFAULT 'INITIATED',

    CONSTRAINT fk_login_sessions_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

