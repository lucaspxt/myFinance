CREATE TABLE IF NOT EXISTS users (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    archived BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS bank_accounts (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    user_id         BIGINT       NOT NULL REFERENCES users (id),
    default_account BOOLEAN      NOT NULL DEFAULT FALSE,
    archived        BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS transactions (
    id              BIGSERIAL        PRIMARY KEY,
    type            VARCHAR(50)      NOT NULL,
    category_id     BIGINT REFERENCES categories (id),
    bank_account_id BIGINT           NOT NULL REFERENCES bank_accounts (id),
    value           DOUBLE PRECISION NOT NULL
);
