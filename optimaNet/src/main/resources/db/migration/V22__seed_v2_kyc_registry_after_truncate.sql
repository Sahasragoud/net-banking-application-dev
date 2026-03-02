-- Re-seed local KYC registry after destructive reset migrations.
INSERT INTO v2_kyc_registry (document_type, document_number, holder_name, active)
VALUES
    ('PAN', 'ABCDE1234F', 'RAVI KUMAR', TRUE),
    ('AADHAAR', '123456781234', 'RAVI KUMAR', TRUE),
    ('VOTER_ID', 'ABC1234567', 'RAVI KUMAR', TRUE),
    ('PAN', 'FGHIJ5678K', 'PRIYA SHARMA', TRUE),
    ('AADHAAR', '234567892345', 'PRIYA SHARMA', TRUE),
    ('VOTER_ID', 'DEF7654321', 'PRIYA SHARMA', TRUE)
ON CONFLICT (document_type, document_number) DO UPDATE
SET holder_name = EXCLUDED.holder_name,
    active = EXCLUDED.active,
    updated_at = NOW();
