CREATE TABLE portfolio (
    id UUID PRIMARY KEY,
    client_name VARCHAR(255) NOT NULL,
    risk_profile VARCHAR(50),
    cash_balance DECIMAL(19, 4) NOT NULL DEFAULT 0.0
);

CREATE TABLE holding (
    id UUID PRIMARY KEY,
    portfolio_id UUID NOT NULL REFERENCES portfolio(id),
    symbol VARCHAR(50) NOT NULL,
    quantity DECIMAL(19, 4) NOT NULL DEFAULT 0.0,
    average_cost DECIMAL(19, 4) NOT NULL DEFAULT 0.0,
    CONSTRAINT uq_portfolio_symbol UNIQUE(portfolio_id, symbol)
);

CREATE TABLE transaction (
    id UUID PRIMARY KEY,
    portfolio_id UUID NOT NULL REFERENCES portfolio(id),
    type VARCHAR(20) NOT NULL,
    symbol VARCHAR(50) NOT NULL,
    quantity DECIMAL(19, 4) NOT NULL,
    price DECIMAL(19, 4) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

CREATE TABLE dividend (
    id UUID PRIMARY KEY,
    portfolio_id UUID NOT NULL REFERENCES portfolio(id),
    symbol VARCHAR(50) NOT NULL,
    per_share_amount DECIMAL(19, 4) NOT NULL,
    total_amount DECIMAL(19, 4) NOT NULL,
    record_date DATE NOT NULL
);
