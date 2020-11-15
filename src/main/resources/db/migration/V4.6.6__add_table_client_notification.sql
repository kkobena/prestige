CREATE TABLE IF NOT EXISTS `notification_client` (
	`id` VARCHAR(50) NOT NULL ,
`notification_id` VARCHAR (50)  NOT NULL,
`client_id` VARCHAR (50) NOT NULL,

	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

ALTER TABLE notification  DROP COLUMN `client_id`;