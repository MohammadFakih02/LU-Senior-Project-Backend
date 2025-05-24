INSERT INTO app_config (config_key, config_value, last_modified_at)
SELECT 'security.admin.username', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM app_config WHERE config_key = 'security.admin.username');

INSERT INTO app_config (config_key, config_value, last_modified_at)
SELECT 'security.admin.password', 'YOUR_PRE_HASHED_BCRYPT_PASSWORD_FOR_ADMIN123', NOW()
WHERE NOT EXISTS (SELECT 1 FROM app_config WHERE config_key = 'security.admin.password');