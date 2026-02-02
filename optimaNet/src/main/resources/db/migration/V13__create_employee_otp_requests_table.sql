CREATE TABLE  IF NOT EXISTS employee_otp_requests (
    id BIGSERIAL PRIMARY KEY,

    employee_id BIGINT NOT NULL,

    otp_hash TEXT NOT NULL,

    purpose VARCHAR(30) NOT NULL,

    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,

    attempt_count INTEGER NOT NULL DEFAULT 0,
    max_attempts INTEGER NOT NULL DEFAULT 3,

    resend_count INTEGER NOT NULL DEFAULT 0,
    blocked_until TIMESTAMP,

    CONSTRAINT fk_otp_requests_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
        ON DELETE CASCADE
);
