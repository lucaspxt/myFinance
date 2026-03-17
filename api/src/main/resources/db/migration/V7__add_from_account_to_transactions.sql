ALTER TABLE transactions ADD COLUMN from_account_id BIGINT REFERENCES bank_accounts (id);
