CREATE TABLE IF NOT EXISTS `notification` (
	`id` VARCHAR(50) NOT NULL ,
	`created_at` DATETIME NOT NULL,
	`modfied_at` DATETIME NOT NULL,
`user_id` VARCHAR (50)  NULL DEFAULT NULL,
`client_id` VARCHAR (50)  NULL  DEFAULT NULL,
`message` VARCHAR (3000) NOT NULL,
`user_to` VARCHAR (50)  NULL  DEFAULT NULL,
`statut` VARCHAR (50) NOT NULL ,
`canal` VARCHAR (50) NOT NULL ,
`type_notification` VARCHAR (255) NOT NULL ,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;