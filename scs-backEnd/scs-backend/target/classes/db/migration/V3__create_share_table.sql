USE `scs_db`;

CREATE TABLE IF NOT EXISTS `file_shares`
(
    `share_id` INT AUTO_INCREMENT PRIMARY KEY,
    `file_id` INT NOT NULL,
    `sender` INT NOT NULL,
    `receiver` INT NOT NULL,
    `key` TEXT NOT NULL,
    `IV` TEXT NOT NULL,
    `expiry` TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  COLLATE = latin1_swedish_ci;