

CREATE TABLE IF NOT EXISTS  employees(

    id BIGSERIAL PRIMARY KEY,

    employee_code VARCHAR(25) UNIQUE NOT NULL,

    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mobile_number VARCHAR(10) UNIQUE NOT NULL,

    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT chk_employee_role
    CHECK (role IN ('KYC_OFFICER', 'SUPERVISOR', 'ADMIN')),

    CONSTRAINT chk_employee_status
    CHECK (status IN ('ACTIVE', 'SUSPENDED', 'TERMINATED', 'INACTIVE'))

);

