-- event_publication was unintentionally created in the catalog schema (unqualified
-- CREATE TABLE picked up Flyway's search_path). Hibernate validates it in the
-- default/public schema, so move it there. Indexes move with the table.
DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.tables
            WHERE table_schema = 'catalog' AND table_name = 'event_publication'
        ) THEN
            ALTER TABLE catalog.event_publication SET SCHEMA public;
        END IF;
    END $$;