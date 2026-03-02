-- Destructive reset for transactional/application data.
-- Keeps Flyway history and local KYC registry reference data intact.
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
