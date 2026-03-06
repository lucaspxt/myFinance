ALTER TABLE categories ADD COLUMN user_id BIGINT REFERENCES users(id);
