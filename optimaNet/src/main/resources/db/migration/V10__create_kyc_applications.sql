CREATE TABLE  IF NOT EXISTS kyc_applications (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,

    assigned_employee_id BIGINT NULL,

    assigned_at TIMESTAMP NULL,
    decision_reason VARCHAR(500) NULL,
    decided_at TIMESTAMP NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT uq_kyc_applications_user UNIQUE (user_id),

    CONSTRAINT fk_kyc_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_kyc_employee
        FOREIGN KEY (assigned_employee_id)
        REFERENCES employees(id)
        ON DELETE SET NULL
);

-- Indexes for workflow & concurrency
CREATE INDEX IF NOT EXISTS idx_kyc_status
    ON kyc_applications(status);

CREATE INDEX IF NOT EXISTS  idx_kyc_assigned_employee
    ON kyc_applications(assigned_employee_id);

CREATE INDEX IF NOT EXISTS idx_kyc_status_unassigned
    ON kyc_applications(status)
    WHERE assigned_employee_id IS NULL;
