CREATE UNIQUE INDEX idx_bundles_name_active
ON bundles (name, deleted);