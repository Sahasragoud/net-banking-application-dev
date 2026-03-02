-- Destructive reset requested for a fresh test cycle.
-- Keeps Flyway history and KYC registry reference data.
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
