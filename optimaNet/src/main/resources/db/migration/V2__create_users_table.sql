CREATE TABLE  IF NOT EXISTS  users (
    id BIGSERIAL PRIMARY KEY,

    customer_id VARCHAR(50) UNIQUE,
    user_status VARCHAR(50) NOT NULL,
    is_blocked BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    role VARCHAR(20) NOT NULL
);
