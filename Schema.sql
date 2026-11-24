CREATE TABLE IF NOT EXISTS customers (
                                         customer_number     TEXT PRIMARY KEY,
                                         user_id             TEXT NOT NULL UNIQUE,
                                         username            TEXT NOT NULL UNIQUE,
                                         password_hash       TEXT NOT NULL,
                                         email               TEXT,
                                         phone_number        TEXT,
                                         address             TEXT,
                                         customer_type       TEXT NOT NULL CHECK (customer_type IN ('INDIVIDUAL', 'COMPANY')),

    first_name          TEXT,
    surname             TEXT,
    national_id         TEXT,
    employed            INTEGER CHECK (employed IN (0, 1)),

    company_name        TEXT,
    registration_number TEXT,
    contact_person      TEXT
    );

CREATE TABLE IF NOT EXISTS staff (
                                     employee_id     TEXT PRIMARY KEY,
                                     user_id         TEXT NOT NULL UNIQUE,
                                     username        TEXT NOT NULL UNIQUE,
                                     password_hash   TEXT NOT NULL,
                                     email           TEXT,
                                     phone_number    TEXT,
                                     address         TEXT,
                                     branch          TEXT,
                                     first_name      TEXT,
                                     surname         TEXT,
                                     role            TEXT NOT NULL CHECK (role IN ('TELLER', 'MANAGER'))
    );

CREATE TABLE IF NOT EXISTS accounts (
                                        account_number   TEXT PRIMARY KEY,
                                        customer_number  TEXT NOT NULL REFERENCES customers(customer_number) ON DELETE CASCADE,
    account_type     TEXT NOT NULL CHECK (account_type IN ('SAVINGS', 'INVESTMENT', 'CHEQUE')),
    branch           TEXT,
    balance          REAL NOT NULL DEFAULT 0 CHECK (balance >= 0),

    interest_rate    REAL,
    min_deposit      REAL,

    employer_name    TEXT,
    employer_address TEXT
    );

CREATE TABLE IF NOT EXISTS transactions (
                                            transaction_id  TEXT PRIMARY KEY,
                                            account_number  TEXT NOT NULL REFERENCES accounts(account_number) ON DELETE CASCADE,
    type            TEXT NOT NULL,
    amount          REAL NOT NULL CHECK (amount > 0),
    reference       TEXT,
    balance_after   REAL NOT NULL,
    timestamp       TEXT NOT NULL
    );

CREATE TABLE IF NOT EXISTS interest_payments (
                                                 payment_id      TEXT PRIMARY KEY,
                                                 account_number  TEXT NOT NULL REFERENCES accounts(account_number) ON DELETE CASCADE,
    amount          REAL NOT NULL,
    payment_date    TEXT,
    status          TEXT NOT NULL CHECK (status IN ('PENDING', 'CALCULATED', 'PAID', 'REJECTED'))
    );