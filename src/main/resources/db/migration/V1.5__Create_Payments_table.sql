CREATE TABLE IF NOT EXISTS `Payments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `amount` DECIMAL(10,2) NOT NULL,
  `paymentDate` DATETIME NOT NULL,
  `transactionReference` VARCHAR(100),
  `paymentMethod` VARCHAR(50),
  `user_bundle_id` BIGINT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_bundle_id`) REFERENCES `UserBundles`(`id`)
);