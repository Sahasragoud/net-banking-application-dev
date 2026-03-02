ALTER TABLE v2_customers
    ADD COLUMN IF NOT EXISTS mpin_hash VARCHAR(64);

ALTER TABLE v2_customers
    DROP CONSTRAINT IF EXISTS chk_v2_customers_status;

ALTER TABLE v2_customers
    ADD CONSTRAINT chk_v2_customers_status
    CHECK (customer_status IN ('PENDING_KYC', 'LITE', 'ACTIVE', 'BLOCKED', 'REJECTED'));
