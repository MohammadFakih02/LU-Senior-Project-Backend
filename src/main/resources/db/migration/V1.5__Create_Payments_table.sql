CREATE TABLE IF NOT EXISTS `Payments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `amount` DECIMAL(10,2) NOT NULL,
  `payment_date` DATETIME,
  `due_date` DATETIME,
  `payment_method` VARCHAR(50),
  `user_bundle_id` BIGINT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_bundle_id`) REFERENCES `UserBundles`(`id`)
);