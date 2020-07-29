CREATE TABLE IF NOT EXISTS `annulation_recette` (
	`id` VARCHAR(255) NOT NULL,
	`created_at` DATETIME NOT NULL,
	`modified_at` DATETIME NOT NULL,
	`status` VARCHAR(255) NOT NULL,
	`montantPaye` INT(11) NOT NULL,
	`montantRegle` INT(11) NOT NULL,
	`montantTiersPayant` INT(11) NOT NULL,
	`montantVente` INT(11) NOT NULL,
	`mvtDate` DATE NOT NULL,
	`caissier_id` VARCHAR(40) NOT NULL,
	`user_id` VARCHAR(40) NOT NULL,
	`preenregistrement_id` VARCHAR(40) NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FKaqely0ia0jq2acutm6aphwysj` (`preenregistrement_id`),
	CONSTRAINT `FKaqely0ia0jq2acutm6aphwysj` FOREIGN KEY (`preenregistrement_id`) REFERENCES `t_preenregistrement` (`lg_PREENREGISTREMENT_ID`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
