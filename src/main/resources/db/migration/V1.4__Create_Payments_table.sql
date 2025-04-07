CREATE TABLE IF NOT EXISTS `Payments` (
  `PaymentID` INT NOT NULL AUTO_INCREMENT,
  `Amount` DECIMAL(10,2) NOT NULL,
  `PaymentDate` DATE NULL,
  `Status` ENUM('complete', 'pending', 'failed') NOT NULL,
  `Method` VARCHAR(50) NULL,
  `UserID` INT NOT NULL,
  `DueDate` Date Null,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`PaymentID`, `UserID`),
  CONSTRAINT `fk_Payments_Users1`
    FOREIGN KEY (`UserID`)
    REFERENCES `Users` (`UserID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;

CREATE INDEX `fk_Payments_Users1_idx` ON `Payments` (`UserID` ASC);