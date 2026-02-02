
CREATE TABLE  IF NOT EXISTS  auth_tokens (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,
    device_id BIGINT NOT NULL,


    refresh_token_hash TEXT NOT NULL,
    token_type VARCHAR(30) NOT NULL,

    issued_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP ,

    revoked_reason VARCHAR(30) NOT NULL,

    CONSTRAINT fk_auth_tokens_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_auth_tokens_device
        FOREIGN KEY (device_id)
        REFERENCES user_devices(id)
        ON DELETE CASCADE
);

