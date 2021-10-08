CREATE TABLE IF NOT EXISTS `licence` (
	`id` VARCHAR(200) NOT NULL,
	`date_start` DATE NOT NULL,
`date_end` DATE NOT NULL,
	PRIMARY KEY (`id`),
UNIQUE INDEX `date_start_licence` (`date_start`),
UNIQUE INDEX `date_end_licence` (`date_end`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
