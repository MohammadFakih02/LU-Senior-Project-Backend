CREATE INDEX idx_user_bundles_bundle_id ON UserBundles(bundle_id);
CREATE INDEX idx_user_bundles_user_id ON UserBundles(user_id);
CREATE INDEX idx_payments_user_bundle_id ON Payments(user_bundle_id);