
CREATE TABLE IF NOT EXISTS employee_auth_tokens (
    id BIGSERIAL PRIMARY KEY,

    employee_id BIGINT NOT NULL,
    device_id BIGINT NOT NULL,


    refresh_token_hash TEXT NOT NULL,

    issued_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP ,

    revoked_reason VARCHAR(30),

    CONSTRAINT fk_auth_tokens_user
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_auth_tokens_device
        FOREIGN KEY (device_id)
        REFERENCES employee_devices(id)
        ON DELETE CASCADE
);

