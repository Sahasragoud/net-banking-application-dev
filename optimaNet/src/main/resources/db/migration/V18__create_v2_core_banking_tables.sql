CREATE TABLE v2_customers (
    id BIGSERIAL PRIMARY KEY,
    customer_code VARCHAR(32) UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    mobile_number VARCHAR(15) NOT NULL UNIQUE,
    date_of_birth DATE NOT NULL,
    customer_status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE v2_kyc_cases (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL UNIQUE,
    kyc_status VARCHAR(32) NOT NULL,
    aadhaar_number VARCHAR(32),
    pan_number VARCHAR(20),
    address_line VARCHAR(255),
    city VARCHAR(80),
    state VARCHAR(80),
    postal_code VARCHAR(12),
    country VARCHAR(80),
    rejection_reason VARCHAR(255),
    submitted_at TIMESTAMP,
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_v2_kyc_customer FOREIGN KEY (customer_id) REFERENCES v2_customers(id)
);

CREATE TABLE v2_savings_accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(32) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    account_status VARCHAR(32) NOT NULL,
    available_balance NUMERIC(19, 2) NOT NULL,
    interest_rate NUMERIC(5, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_v2_savings_customer FOREIGN KEY (customer_id) REFERENCES v2_customers(id)
);

CREATE TABLE v2_savings_transactions (
    id BIGSERIAL PRIMARY KEY,
    savings_account_id BIGINT NOT NULL,
    txn_type VARCHAR(32) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    balance_after_txn NUMERIC(19, 2) NOT NULL,
    remarks VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_v2_txn_account FOREIGN KEY (savings_account_id) REFERENCES v2_savings_accounts(id)
);

CREATE INDEX idx_v2_customers_status ON v2_customers(customer_status);
CREATE INDEX idx_v2_kyc_status ON v2_kyc_cases(kyc_status);
CREATE INDEX idx_v2_savings_customer_id ON v2_savings_accounts(customer_id);
CREATE INDEX idx_v2_txn_account_id ON v2_savings_transactions(savings_account_id);
