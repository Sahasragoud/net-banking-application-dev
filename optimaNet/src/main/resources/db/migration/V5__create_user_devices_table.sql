
CREATE TABLE  IF NOT EXISTS user_devices (

    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,
    device_id VARCHAR(255) NOT NULL,


    device_fingerprint VARCHAR(255),
    device_type  VARCHAR(30),
    device_model  VARCHAR(30),
    os_version  VARCHAR(50),
    app_version VARCHAR(50),

    is_blocked BOOLEAN NOT NULL DEFAULT FALSE,
    blocked_reason VARCHAR(255),

    is_trusted BOOLEAN NOT NULL DEFAULT FALSE,

    first_seen_ip  VARCHAR(30),
    last_seen_ip  VARCHAR(30),

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_user_devices_user
         FOREIGN KEY (user_id)
         REFERENCES users(id)
         ON DELETE CASCADE,

    CONSTRAINT uk_user_device_fingerprint
         UNIQUE (user_id, device_fingerprint)
);
