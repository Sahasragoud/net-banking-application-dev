-- WARNING: Destructive migration.
-- Truncates all application tables in public schema, excluding Flyway history.
DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT tablename
        FROM pg_tables
        WHERE schemaname = 'public'
          AND tablename <> 'flyway_schema_history'
    LOOP
        EXECUTE format(
            'TRUNCATE TABLE %I.%I RESTART IDENTITY CASCADE',
            'public',
            r.tablename
        );
    END LOOP;
END $$;
