CREATE TABLE  IF NOT EXISTS kyc_documents (

    id BIGSERIAL PRIMARY KEY,

    kyc_application_id BIGINT NOT NULL,

    document_type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,

    file_path TEXT NOT NULL,

    rejection_reason VARCHAR(500),

    uploaded_at TIMESTAMP NOT NULL,
    reviewed_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_kyc_documents_application
        FOREIGN KEY (kyc_application_id)
        REFERENCES kyc_applications(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_kyc_document_type
        CHECK (document_type IN (
            'AADHAAR_FRONT',
            'AADHAAR_BACK',
            'PASSPORT_PHOTO',
            'PAN_CARD',
            'DIGITAL_SIGN'
        )),

    CONSTRAINT chk_kyc_document_status
        CHECK (status IN (
            'UPLOADED',
            'UNDER_REVIEW',
            'APPROVED',
            'REJECTED'
        ))
);

-- Performance & access patterns
CREATE INDEX IF NOT EXISTS idx_kyc_docs_application
    ON kyc_documents(kyc_application_id);

CREATE INDEX IF NOT EXISTS idx_kyc_docs_application_type
    ON kyc_documents(kyc_application_id, document_type);

CREATE INDEX IF NOT EXISTS idx_kyc_docs_status
    ON kyc_documents(status);

