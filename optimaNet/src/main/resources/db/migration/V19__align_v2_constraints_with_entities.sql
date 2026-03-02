-- Align v2 schema constraints with JPA entity definitions.
-- Backfill customer_code before making it NOT NULL.
UPDATE v2_customers
SET customer_code = 'TMP-' || EXTRACT(YEAR FROM CURRENT_DATE)::text || '-' || LPAD(id::text, 6, '0')
WHERE customer_code IS NULL OR BTRIM(customer_code) = '';

ALTER TABLE v2_customers
    ALTER COLUMN customer_code SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'chk_v2_customers_status'
    ) THEN
        ALTER TABLE v2_customers
            ADD CONSTRAINT chk_v2_customers_status
            CHECK (customer_status IN ('PENDING_KYC', 'ACTIVE', 'BLOCKED', 'REJECTED'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'chk_v2_kyc_status'
    ) THEN
        ALTER TABLE v2_kyc_cases
            ADD CONSTRAINT chk_v2_kyc_status
            CHECK (kyc_status IN ('NOT_STARTED', 'SUBMITTED', 'APPROVED', 'REJECTED'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'chk_v2_savings_account_status'
    ) THEN
        ALTER TABLE v2_savings_accounts
            ADD CONSTRAINT chk_v2_savings_account_status
            CHECK (account_status IN ('ACTIVE', 'FROZEN', 'CLOSED'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'chk_v2_savings_txn_type'
    ) THEN
        ALTER TABLE v2_savings_transactions
            ADD CONSTRAINT chk_v2_savings_txn_type
            CHECK (txn_type IN ('DEPOSIT', 'WITHDRAWAL'));
    END IF;
END $$;
