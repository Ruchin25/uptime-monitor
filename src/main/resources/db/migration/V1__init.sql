CREATE TABLE monitors (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    type             VARCHAR(16)  NOT NULL,
    target           VARCHAR(255) NOT NULL,
    interval_seconds INTEGER      NOT NULL DEFAULT 60,
    expected_status  INTEGER,
    timeout_ms       INTEGER      NOT NULL DEFAULT 5000,
    enabled          BOOLEAN      NOT NULL DEFAULT TRUE,
    last_checked_at  TIMESTAMPTZ,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE check_results (
    id          BIGSERIAL PRIMARY KEY,
    monitor_id  BIGINT       NOT NULL REFERENCES monitors (id) ON DELETE CASCADE,
    checked_at  TIMESTAMPTZ  NOT NULL,
    success     BOOLEAN      NOT NULL,
    status_code INTEGER,
    latency_ms  BIGINT       NOT NULL,
    message     VARCHAR(500)
);

CREATE INDEX idx_check_results_monitor_time
    ON check_results (monitor_id, checked_at DESC);
