
-- 1. Enforce NOT NULL (data already backfilled in application)
ALTER TABLE user_identities
ALTER COLUMN hashed_aadhaar_number SET NOT NULL;

-- 2. Enforce uniqueness to prevent duplicate Aadhaar registrations
CREATE UNIQUE INDEX ux_user_identities_hashed_aadhaar
ON user_identities (hashed_aadhaar_number);
