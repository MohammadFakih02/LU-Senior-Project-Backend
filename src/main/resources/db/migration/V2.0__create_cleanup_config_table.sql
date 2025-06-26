CREATE TABLE app_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(255) NOT NULL UNIQUE,
    config_value VARCHAR(255),
    last_modified_by VARCHAR(255),
    last_modified_at DATETIME
);

INSERT INTO app_config (config_key, config_value) VALUES
    ('payment.retention.days', '60'),
    ('payment.overdue.enabled', 'true'),
     ('payment.recurring.enabled', 'true');