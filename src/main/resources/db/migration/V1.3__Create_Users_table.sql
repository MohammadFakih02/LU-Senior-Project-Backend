CREATE TABLE IF NOT EXISTS `Users` (
  `UserID` INT NOT NULL AUTO_INCREMENT,
  `FirstName` VARCHAR(45) NOT NULL,
  `LastName` VARCHAR(45) NOT NULL,
  `Email` VARCHAR(60) NULL,
  `LandLine` VARCHAR(45) NULL,
  `Phone` VARCHAR(45) NOT NULL,
  `Consumption` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `Bill` DECIMAL(10,2) NOT NULL,
  `SubscriptionDate` DATE NOT NULL,
  `Status` ENUM('active', 'inactive', 'suspended') NOT NULL DEFAULT 'active',
  `BundleID` INT NOT NULL,
  `LocationID` INT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`UserID`, `LocationID`),
  CONSTRAINT `fk_Users_Bundles`
    FOREIGN KEY (`BundleID`)
    REFERENCES `Bundles` (`BundleID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Users_Locations1`
    FOREIGN KEY (`LocationID`)
    REFERENCES `Locations` (`LocationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;

CREATE UNIQUE INDEX `Phone_UNIQUE` ON `Users` (`Phone` ASC);
CREATE INDEX `fk_Users_Bundles_idx` ON `Users` (`BundleID` ASC);
CREATE UNIQUE INDEX `Email_UNIQUE` ON `Users` (`Email` ASC);
CREATE INDEX `fk_Users_Locations1_idx` ON `Users` (`LocationID` ASC);