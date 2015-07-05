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
  PRIMARY KEY (`stationId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `sxmt`.`artists`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sxmt`.`artists` (
  `artistId` INT NOT NULL,
  `artistName` VARCHAR(100) NULL,
  `echoNestId` VARCHAR(45) NULL,
  PRIMARY KEY (`artistId`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `sxmt`.`videos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sxmt`.`videos` (
  `videoId` VARCHAR(45) NOT NULL,
  `videoTitle` VARCHAR(100) NULL,
  `channelName` VARCHAR(45) NULL,
  `videoThumbnail` VARCHAR(150) NULL,
  `videoType` ENUM('SAFE','NORMAL') NULL,
  PRIMARY KEY (`videoId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `sxmt`.`tweets`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sxmt`.`tweets` (
  `tweetId` BIGINT(20) NOT NULL,
  `stationId` BIGINT(20) NULL,
  `twitterText` VARCHAR(140) NULL,
  `songName` VARCHAR(100) NULL,
  `origination` DATETIME NULL,
  `jsonBlob` LONGTEXT NULL,
  `artist` VARCHAR(100) NULL,
  `artistId` INT NULL,
  `stationSongIndex` BIGINT(20) NULL,
  `videoId` VARCHAR(45) NULL,
  PRIMARY KEY (`tweetId`),
  INDEX `userId_idx` (`stationId` ASC),
  INDEX `origination_idx` (`origination` DESC),
  INDEX `artistid_idx` (`artistId` ASC),
  INDEX `videoId_idx` (`videoId` ASC),
  CONSTRAINT `stationId`
    FOREIGN KEY (`stationId`)
    REFERENCES `sxmt`.`stations` (`stationId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `artistId`
    FOREIGN KEY (`artistId`)
    REFERENCES `sxmt`.`artists` (`artistId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `videoId`
    FOREIGN KEY (`videoId`)
    REFERENCES `sxmt`.`videos` (`videoId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `sxmt`.`genres`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sxmt`.`genres` (
  `genreId` INT NOT NULL AUTO_INCREMENT,
  `genre` VARCHAR(40) NULL,
  PRIMARY KEY (`genreId`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `sxmt`.`artistGenres`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sxmt`.`artistGenres` (
  `artistGenresId` INT NOT NULL AUTO_INCREMENT,
  `artistId` INT NULL,
  `genreId` INT NULL,
  PRIMARY KEY (`artistGenresId`),
  INDEX `artistId_idx` (`artistId` ASC),
  INDEX `genreId_idx` (`genreId` ASC),
  CONSTRAINT `artistId2`
    FOREIGN KEY (`artistId`)
    REFERENCES `sxmt`.`artists` (`artistId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `genreId`
    FOREIGN KEY (`genreId`)
    REFERENCES `sxmt`.`genres` (`genreId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `sxmt`;

DELIMITER $$
USE `sxmt`$$
CREATE DEFINER = CURRENT_USER TRIGGER `sxmt`.`tweets_BEFORE_INSERT` BEFORE INSERT ON `tweets` FOR EACH ROW
BEGIN
	SET NEW.stationSongIndex = (
		SELECT COALESCE(MAX(stationSongIndex),0)+1
        FROM tweets
        WHERE stationId = NEW.stationId
    );
END
$$


DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
