ALTER TABLE transactions ADD COLUMN completed BOOLEAN NOT NULL DEFAULT FALSE;

-- Set completed=TRUE for all existing transactions dated today or in the past
UPDATE transactions 
SET completed = TRUE 
WHERE created_at IS NULL OR DATE(created_at) <= CURRENT_DATE;
