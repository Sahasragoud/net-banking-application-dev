CREATE TABLE IF NOT EXISTS v2_customer_profiles (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL UNIQUE,
    occupation VARCHAR(80),
    income_source VARCHAR(80),
    yearly_income VARCHAR(80),
    marital_status VARCHAR(40),
    father_name VARCHAR(120),
    mother_maiden_name VARCHAR(120),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_v2_customer_profile_customer FOREIGN KEY (customer_id) REFERENCES v2_customers(id)
);

CREATE TABLE IF NOT EXISTS v2_nominees (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    depositor_name VARCHAR(120),
    depositor_address VARCHAR(255),
    nominee_name VARCHAR(120) NOT NULL,
    nominee_address VARCHAR(255),
    relationship VARCHAR(80),
    age_years INTEGER NOT NULL,
    guardian_name VARCHAR(120),
    guardian_relationship VARCHAR(80),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_v2_nominee_customer FOREIGN KEY (customer_id) REFERENCES v2_customers(id)
);

CREATE INDEX IF NOT EXISTS idx_v2_nominees_customer_id ON v2_nominees(customer_id);
