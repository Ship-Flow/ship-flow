CREATE SCHEMA IF NOT EXISTS orders;

CREATE TABLE IF NOT EXISTS orders.processed_saga_events (
    event_id        VARCHAR(36) PRIMARY KEY,
    event_type      VARCHAR(100) NOT NULL,
    processed_at    TIMESTAMP(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders.p_orders (
    id              UUID PRIMARY KEY,
    orderer_id      UUID NOT NULL,
    product_id      UUID NOT NULL,
    shipment_id     UUID,
    supplier_company_id UUID,
    receiver_company_id UUID,
    departure_hub_id    UUID,
    arrival_hub_id      UUID,
    value           INT,
    status          VARCHAR(50) NOT NULL,
    cancel_reason   VARCHAR,
    request_deadline TIMESTAMP(6),
    request_note    VARCHAR,
    created_by      UUID,
    created_at      TIMESTAMP(6),
    updated_by      UUID,
    updated_at      TIMESTAMP(6),
    deleted_by      UUID,
    deleted_at      TIMESTAMP(6)
);

CREATE TABLE IF NOT EXISTS orders.p_order_read_models (
    order_id            UUID PRIMARY KEY,
    order_status        VARCHAR(50),
    shipment_id         UUID,
    shipment_status     VARCHAR,
    supplier_company_id UUID,
    supplier_company_name VARCHAR,
    receiver_company_id UUID,
    receiver_company_name VARCHAR,
    orderer_id          UUID,
    orderer_name        VARCHAR,
    product_id          UUID,
    product_name        VARCHAR,
    quantity            INT,
    departure_hub_id    UUID,
    departure_hub_name  VARCHAR,
    arrival_hub_id      UUID,
    arrival_hub_name    VARCHAR,
    request_deadline    TIMESTAMP(6),
    request_note        VARCHAR,
    cancel_reason       VARCHAR,
    created_at          TIMESTAMP(6),
    created_by          UUID,
    updated_at          TIMESTAMP(6),
    updated_by          UUID,
    deleted_at          TIMESTAMP(6),
    deleted_by          UUID
);
