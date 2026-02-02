-- 1. Add column (nullable)
ALTER TABLE user_identities
ADD COLUMN hashed_aadhaar_number VARCHAR(64);

-- 2. (OPTIONAL) Copy existing encrypted value if it is already a hash
UPDATE user_identities
SET hashed_aadhaar_number = encrypted_aadhaar_number
WHERE hashed_aadhaar_number IS NULL
  AND encrypted_aadhaar_number IS NOT NULL;
