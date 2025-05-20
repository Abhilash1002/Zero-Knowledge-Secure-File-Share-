USE `scs_db`;

CREATE TABLE IF NOT EXISTS `public_files`
(
    `file_id` INT AUTO_INCREMENT PRIMARY KEY,
    `private_file_id` INT NOT NULL,
    `owner_id` INT NOT NULL,
    `file_name` VARCHAR(255) NOT NULL,
    `file_data` LONGBLOB 
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  COLLATE = latin1_swedish_ci;