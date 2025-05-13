-- Add deleted column to Bundles table
ALTER TABLE `Bundles`
ADD COLUMN `deleted` BOOLEAN NOT NULL DEFAULT FALSE;

-- Add deleted column to Users table
ALTER TABLE `Users`
ADD COLUMN `deleted` BOOLEAN NOT NULL DEFAULT FALSE;

-- Add deleted column to UserBundles table
ALTER TABLE `UserBundles`
ADD COLUMN `deleted` BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE Payments
ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

