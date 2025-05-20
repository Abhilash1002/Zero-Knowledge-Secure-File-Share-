USE `scs_db`;

CREATE TABLE IF NOT EXISTS `user_files`
(
    `file_id` INT AUTO_INCREMENT PRIMARY KEY,
    `owner_id` INT NOT NULL,
    `key` TEXT NOT NULL,
    `IV` TEXT NOT NULL,
    `file_name` VARCHAR(255) NOT NULL,
    `file_data` LONGBLOB 
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  COLLATE = latin1_swedish_ci;