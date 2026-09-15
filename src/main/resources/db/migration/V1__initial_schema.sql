CREATE SCHEMA IF NOT EXISTS identity;
CREATE SCHEMA IF NOT EXISTS trading;
CREATE SCHEMA IF NOT EXISTS audit;
CREATE SCHEMA IF NOT EXISTS reporting;

CREATE TABLE identity.clients (
    client_id       UUID PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    client_segment  VARCHAR(50),
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE identity.users (
    user_id         UUID PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255),
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE identity.roles (
    role_id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role_name       VARCHAR(50) NOT NULL UNIQUE,
    description     VARCHAR(255)
);

CREATE TABLE identity.user_roles (
    user_id UUID NOT NULL REFERENCES identity.users(user_id),
    role_id BIGINT NOT NULL REFERENCES identity.roles(role_id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE identity.sessions (
    session_id      UUID PRIMARY KEY,
    subject_type    VARCHAR(10) NOT NULL CHECK (subject_type IN ('CLIENT', 'USER')),
    subject_id      UUID NOT NULL,
    refresh_token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at      TIMESTAMPTZ NOT NULL,
    revoked_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE trading.accounts (
    account_id      UUID PRIMARY KEY,
    client_id       UUID NOT NULL REFERENCES identity.clients(client_id),
    account_number  VARCHAR(30) NOT NULL UNIQUE,
    base_currency   VARCHAR(10) NOT NULL DEFAULT 'USD',
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                    CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CLOSED')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_accounts_client ON trading.accounts(client_id);

CREATE TABLE trading.instruments (
    instrument_id       UUID PRIMARY KEY,
    symbol              VARCHAR(30) NOT NULL,
    instrument_type     VARCHAR(20) NOT NULL
                        CHECK (instrument_type IN ('EQUITY', 'FX', 'CRYPTO')),
    exchange            VARCHAR(30),
    base_currency       VARCHAR(10),
    quote_currency      VARCHAR(10) NOT NULL,
    name                VARCHAR(255) NOT NULL,
    tradable            BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uq_instrument_market
    ON trading.instruments(symbol, COALESCE(exchange, ''));

CREATE TABLE trading.market_quotes (
    quote_id            UUID PRIMARY KEY,
    instrument_id       UUID NOT NULL REFERENCES trading.instruments(instrument_id),
    bid_price           NUMERIC(20,8) NOT NULL CHECK (bid_price >= 0),
    ask_price           NUMERIC(20,8) NOT NULL CHECK (ask_price >= 0),
    source              VARCHAR(100) NOT NULL,
    quoted_at           TIMESTAMPTZ NOT NULL,
    received_at         TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (ask_price >= bid_price)
);

CREATE INDEX idx_market_quotes_instrument_time
    ON trading.market_quotes(instrument_id, quoted_at DESC);

CREATE TABLE trading.orders (
    order_id            UUID PRIMARY KEY,
    account_id          UUID NOT NULL REFERENCES trading.accounts(account_id),
    instrument_id       UUID NOT NULL REFERENCES trading.instruments(instrument_id),
    side                VARCHAR(4) NOT NULL CHECK (side IN ('BUY', 'SELL')),
    quantity            NUMERIC(20,8) NOT NULL CHECK (quantity > 0),
    status              VARCHAR(20) NOT NULL
                        CHECK (status IN ('SUBMITTED', 'ACCEPTED', 'FILLED', 'REJECTED')),
    rejection_reason    VARCHAR(500),
    submitted_at        TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accepted_at         TIMESTAMPTZ,
    completed_at        TIMESTAMPTZ,
    version             BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_orders_account_time
    ON trading.orders(account_id, submitted_at DESC);
CREATE INDEX idx_orders_status ON trading.orders(status);

CREATE TABLE trading.order_events (
    order_event_id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id            UUID NOT NULL REFERENCES trading.orders(order_id),
    event_type          VARCHAR(30) NOT NULL
                        CHECK (event_type IN ('SUBMITTED', 'ACCEPTED', 'PRICED', 'FILLED', 'REJECTED')),
    event_time          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    details             JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX idx_order_events_order_time
    ON trading.order_events(order_id, event_time, order_event_id);

CREATE TABLE audit.pricing_decisions (
    pricing_decision_id UUID PRIMARY KEY,
    order_id            UUID NOT NULL REFERENCES trading.orders(order_id),
    instrument_id       UUID NOT NULL REFERENCES trading.instruments(instrument_id),
    bid_price           NUMERIC(20,8) NOT NULL,
    ask_price           NUMERIC(20,8) NOT NULL,
    execution_price     NUMERIC(20,8) NOT NULL,
    quote_source        VARCHAR(100) NOT NULL,
    quote_timestamp     TIMESTAMPTZ NOT NULL,
    priced_at           TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pricing_order ON audit.pricing_decisions(order_id);

CREATE TABLE trading.fills (
    fill_id             UUID PRIMARY KEY,
    order_id            UUID NOT NULL REFERENCES trading.orders(order_id),
    quantity            NUMERIC(20,8) NOT NULL CHECK (quantity > 0),
    price               NUMERIC(20,8) NOT NULL CHECK (price >= 0),
    filled_at           TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_fills_order ON trading.fills(order_id, filled_at);

CREATE TABLE trading.positions (
    account_id          UUID NOT NULL REFERENCES trading.accounts(account_id),
    instrument_id       UUID NOT NULL REFERENCES trading.instruments(instrument_id),
    quantity            NUMERIC(20,8) NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version             BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (account_id, instrument_id)
);

CREATE TABLE trading.cash_balances (
    account_id          UUID NOT NULL REFERENCES trading.accounts(account_id),
    currency            VARCHAR(10) NOT NULL,
    balance             NUMERIC(20,8) NOT NULL DEFAULT 0 CHECK (balance >= 0),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version             BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (account_id, currency)
);

CREATE TABLE trading.cash_transactions (
    cash_transaction_id UUID PRIMARY KEY,
    account_id          UUID NOT NULL REFERENCES trading.accounts(account_id),
    order_id            UUID REFERENCES trading.orders(order_id),
    currency            VARCHAR(10) NOT NULL,
    amount              NUMERIC(20,8) NOT NULL,
    transaction_type    VARCHAR(30) NOT NULL
                        CHECK (transaction_type IN ('TRADE_BUY', 'TRADE_SELL', 'DEMO_FUNDING', 'ADJUSTMENT')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cash_transactions_account_time
    ON trading.cash_transactions(account_id, created_at DESC);

-- Transactional outbox: accepted order intent and its integration event are
-- committed together. A publisher can retry safely after process failure.
CREATE TABLE trading.outbox_events (
    event_id            UUID PRIMARY KEY,
    aggregate_type      VARCHAR(50) NOT NULL,
    aggregate_id        UUID NOT NULL,
    event_type          VARCHAR(100) NOT NULL,
    payload             JSONB NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at        TIMESTAMPTZ
);

CREATE INDEX idx_outbox_unpublished
    ON trading.outbox_events(created_at)
    WHERE published_at IS NULL;

-- Reporting is deliberately a read model, not the authoritative trading record.
CREATE TABLE reporting.trade_activity (
    fill_id             UUID PRIMARY KEY,
    client_id           UUID NOT NULL,
    client_segment      VARCHAR(50),
    account_id          UUID NOT NULL,
    order_id            UUID NOT NULL,
    instrument_id       UUID NOT NULL,
    symbol              VARCHAR(30) NOT NULL,
    instrument_type     VARCHAR(20) NOT NULL,
    side                VARCHAR(4) NOT NULL,
    quantity            NUMERIC(20,8) NOT NULL,
    price               NUMERIC(20,8) NOT NULL,
    notional            NUMERIC(28,8) NOT NULL,
    filled_at           TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_reporting_trade_time ON reporting.trade_activity(filled_at DESC);
CREATE INDEX idx_reporting_trade_instrument ON reporting.trade_activity(instrument_id, filled_at DESC);
CREATE INDEX idx_reporting_trade_segment ON reporting.trade_activity(client_segment, filled_at DESC);

-- Seed internal roles used by the Admin Dashboard API.
INSERT INTO identity.roles (role_name, description) VALUES
    ('ADMIN', 'Administers internal users and platform configuration'),
    ('TRADING_OPERATIONS', 'Reviews trading activity and order lifecycle'),
    ('RISK', 'Risk oversight'),
    ('COMPLIANCE', 'Compliance and audit access'),
    ('FINANCE', 'Financial reporting access'),
    ('ANALYST', 'Business reporting and analytics');
