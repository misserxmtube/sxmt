-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`users` (
  `userId` BIGINT(20) NOT NULL,
  `userName` VARCHAR(45) NULL,
  `userHandle` VARCHAR(45) NULL,
  PRIMARY KEY (`userId`),
  UNIQUE INDEX `userId_UNIQUE` (`userId` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`tweets`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`tweets` (
  `tweetId` BIGINT(20) NOT NULL,
  `userId` BIGINT(20) NULL,
  `twitterText` VARCHAR(140) NULL,
  `songName` VARCHAR(100) NULL,
  `artist` VARCHAR(100) NULL,
  `origination` DATETIME NULL,
  `jsonBlob` BLOB NULL,
  PRIMARY KEY (`tweetId`),
  INDEX `userId_idx` (`userId` ASC),
  INDEX `origination_idx` (`origination` DESC),
  UNIQUE INDEX `tweetId_UNIQUE` (`tweetId` ASC),
  CONSTRAINT `userId`
    FOREIGN KEY (`userId`)
    REFERENCES `mydb`.`users` (`userId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`transformers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`transformers` (
  `userId` BIGINT(20) NOT NULL,
  `regex` VARCHAR(500) NULL,
  `startTime` DATE NULL,
  `endTime` DATE NULL,
  INDEX `userId_idx` (`userId` ASC),
  CONSTRAINT `userId2`
    FOREIGN KEY (`userId`)
    REFERENCES `mydb`.`users` (`userId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`videos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`videos` (
  `tweetId` BIGINT(20) NOT NULL,
  `videoTitle` VARCHAR(100) NULL,
  `description` VARCHAR(500) NULL,
  `videoId` VARCHAR(45) NULL,
  `channelName` VARCHAR(45) NULL,
  PRIMARY KEY (`tweetId`),
  UNIQUE INDEX `tweetId_UNIQUE` (`tweetId` ASC),
  CONSTRAINT `tweetId`
    FOREIGN KEY (`tweetId`)
    REFERENCES `mydb`.`tweets` (`tweetId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE USER 'sxmt' IDENTIFIED BY 'sxmt';

GRANT ALL ON `mydb`.* TO 'sxmt';

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
