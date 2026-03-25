-- Add UNIQUE constraint on external_id to prevent duplicate Strava/Garmin syncs
-- Safe: uses IF NOT EXISTS pattern via DO block

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'uq_running_activities_external_id'
    ) THEN
        -- Remove any existing duplicates first (keep the oldest)
        DELETE FROM activity_splits
        WHERE activity_id IN (
            SELECT a.id FROM running_activities a
            JOIN running_activities b ON a.external_id = b.external_id AND a.id > b.id
            WHERE a.external_id IS NOT NULL
        );

        DELETE FROM running_activities a
        USING running_activities b
        WHERE a.external_id IS NOT NULL
          AND a.external_id = b.external_id
          AND a.id > b.id;

        ALTER TABLE running_activities
            ADD CONSTRAINT uq_running_activities_external_id UNIQUE (external_id);
    END IF;
END
$$;
