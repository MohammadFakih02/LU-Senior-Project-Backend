CREATE TABLE IF NOT EXISTS `UserBundles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `bundle_id` BIGINT NOT NULL,
  `subscription_date` DATE NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `consumption` DECIMAL(10,2) DEFAULT 0,
  `location_id` BIGINT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `Users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`bundle_id`) REFERENCES `Bundles`(`Bundle_id`) ON DELETE CASCADE,
  FOREIGN KEY (`location_id`) REFERENCES `Locations`(`location_id`) ON DELETE CASCADE,
  UNIQUE KEY `unique_user_bundle` (`user_id`, `bundle_id`)
);