ALTER TABLE v2_kyc_cases
    ADD COLUMN IF NOT EXISTS voter_id_number VARCHAR(20);

CREATE TABLE IF NOT EXISTS v2_kyc_registry (
    id BIGSERIAL PRIMARY KEY,
    document_type VARCHAR(20) NOT NULL,
    document_number VARCHAR(32) NOT NULL,
    holder_name VARCHAR(120) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_v2_kyc_registry_document UNIQUE (document_type, document_number),
    CONSTRAINT chk_v2_kyc_registry_document_type CHECK (document_type IN ('PAN', 'AADHAAR', 'VOTER_ID'))
);

CREATE INDEX IF NOT EXISTS idx_v2_kyc_registry_document_lookup
    ON v2_kyc_registry(document_type, document_number)
    WHERE active = TRUE;

INSERT INTO v2_kyc_registry (document_type, document_number, holder_name, active)
VALUES
    ('PAN', 'ABCDE1234F', 'RAVI KUMAR', TRUE),
    ('AADHAAR', '123456781234', 'RAVI KUMAR', TRUE),
    ('VOTER_ID', 'ABC1234567', 'RAVI KUMAR', TRUE),
    ('PAN', 'FGHIJ5678K', 'PRIYA SHARMA', TRUE),
    ('AADHAAR', '234567892345', 'PRIYA SHARMA', TRUE),
    ('VOTER_ID', 'DEF7654321', 'PRIYA SHARMA', TRUE)
ON CONFLICT (document_type, document_number) DO NOTHING;
