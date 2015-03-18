-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema sxmt
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema sxmt
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `sxmt` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `sxmt` ;
-- -----------------------------------------------------
-- Table `sxmt`.`stations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sxmt`.`stations` (
  `stationId` BIGINT(20) NOT NULL,
  `stationName` VARCHAR(45) NULL,
  `stationHandle` VARCHAR(45) NULL,
  `stationThumbnail` VARCHAR(150) NULL,
  `stationBackdrop` VARCHAR(150) NULL,
  PRIMARY KEY (`stationId`),
  UNIQUE INDEX `userId_UNIQUE` (`stationId` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `sxmt`.`tweets`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sxmt`.`tweets` (
  `tweetId` BIGINT(20) NOT NULL,
  `stationId` BIGINT(20) NULL,
  `twitterText` VARCHAR(140) NULL,
  `songName` VARCHAR(100) NULL,
  `artist` VARCHAR(100) NULL,
  `origination` DATETIME NULL,
  `jsonBlob` BLOB NULL,
  PRIMARY KEY (`tweetId`),
  INDEX `userId_idx` (`stationId` ASC),
  INDEX `origination_idx` (`origination` DESC),
  UNIQUE INDEX `tweetId_UNIQUE` (`tweetId` ASC),
  CONSTRAINT `stationId`
    FOREIGN KEY (`stationId`)
    REFERENCES `sxmt`.`stations` (`stationId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `sxmt`.`transformers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sxmt`.`transformers` (
  `stationId` BIGINT(20) NOT NULL,
  `regex` VARCHAR(500) NULL,
  `startTime` DATE NULL,
  `endTime` DATE NULL,
  INDEX `userId_idx` (`stationId` ASC),
  CONSTRAINT `stationId2`
    FOREIGN KEY (`stationId`)
    REFERENCES `sxmt`.`stations` (`stationId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `sxmt`.`videos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sxmt`.`videos` (
  `tweetId` BIGINT(20) NOT NULL,
  `videoTitle` VARCHAR(100) NULL,
  `videoId` VARCHAR(45) NULL,
  `channelName` VARCHAR(45) NULL,
  `videoThumbnail` VARCHAR(150) NULL,
  `videoType` ENUM('SAFE','NORMAL') NULL,
  PRIMARY KEY (`tweetId`),
  UNIQUE INDEX `tweetId_UNIQUE` (`tweetId` ASC),
  CONSTRAINT `tweetId`
    FOREIGN KEY (`tweetId`)
    REFERENCES `sxmt`.`tweets` (`tweetId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

DROP USER 'sxmt';
FLUSH PRIVILEGES;
CREATE USER 'sxmt' IDENTIFIED BY 'sxmt';

GRANT ALL ON `sxmt`.* TO 'sxmt';

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
