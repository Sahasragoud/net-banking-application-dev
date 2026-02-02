CREATE TABLE  IF NOT EXISTS user_identities (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,
    full_name VARCHAR(255) NOT NULL,

    encrypted_aadhaar_number TEXT NOT NULL,
    masked_aadhaar_number VARCHAR(20) NOT NULL,
    mobile_number VARCHAR(10) NOT NULL,

    email VARCHAR(255) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,

    verification_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_user_identities_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_user_identities_user
        UNIQUE (user_id),

    CONSTRAINT uk_user_identities_encrypted_aadhaar
        UNIQUE (encrypted_aadhaar_number),

    CONSTRAINT uk_user_identities_mobile
        UNIQUE (mobile_number)
);
