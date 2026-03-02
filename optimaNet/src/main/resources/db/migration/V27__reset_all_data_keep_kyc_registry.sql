-- One-time reset to clear all runtime data again.
-- Keeps Flyway history and KYC registry seed records.
DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT tablename
        FROM pg_tables
        WHERE schemaname = 'public'
          AND tablename NOT IN ('flyway_schema_history', 'v2_kyc_registry')
    LOOP
        EXECUTE format(
            'TRUNCATE TABLE %I.%I RESTART IDENTITY CASCADE',
            'public',
            r.tablename
        );
    END LOOP;
END $$;
