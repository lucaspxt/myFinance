-- Insert User
INSERT INTO users (id, name) VALUES (1, 'Joe Doe');

-- Insert Bank Accounts
INSERT INTO bank_accounts (id, name, user_id, default_account, archived) VALUES
(1, 'Wallet', 1, true, false),
(2, 'PostFinance', 1, false, false);

-- Insert Categories
INSERT INTO categories (id, name, archived, user_id) VALUES
(1, 'Income', false, 1),
(2, 'Housing', false, 1),
(3, 'Food', false, 1),
(4, 'Transportation', false, 1),
(5, 'Health', false, 1),
(6, 'Utilities', false, 1),
(7, 'Entertainment', false, 1),
(8, 'Shopping', false, 1),
(9, 'Savings & Investments', false, 1);

-- Reset sequences to continue from the last inserted ID
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('bank_accounts_id_seq', (SELECT MAX(id) FROM bank_accounts));
SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories));
