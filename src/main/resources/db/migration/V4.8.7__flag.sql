CREATE TABLE IF NOT EXISTS `flag` (
	`id` VARCHAR(16) NOT NULL,
	`montant` INT(11) NOT NULL,
	`date_start` INT(8) NOT NULL,
`date_end` INT(8) NOT NULL,
	PRIMARY KEY (`id`),
UNIQUE INDEX `date_start_flag` (`date_start`),
UNIQUE INDEX `date_end_flag` (`date_end`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
ALTER TABLE  mvttransaction ADD COLUMN IF NOT EXISTS flag_id VARCHAR (16);
ALTER TABLE  mvttransaction ADD COLUMN IF NOT EXISTS flag BIT(1) NULL DEFAULT  0;
ALTER TABLE mvttransaction ADD	CONSTRAINT `flag_fk` FOREIGN KEY (`flag_id`) REFERENCES `flag` (`ID`);