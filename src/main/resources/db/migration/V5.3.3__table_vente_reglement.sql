CREATE TABLE `vente_reglement` (
	`id` VARCHAR(50) NOT NULL,
	`flag_id` VARCHAR(255) NULL DEFAULT NULL,
	`flaged_amount` INT(11) NOT NULL,
	`montant` INT(11) NOT NULL,
	`montant_attentu` INT(11) NOT NULL,
	`mvtDate` DATETIME NOT NULL,
	`vente_id` VARCHAR(40) NOT NULL,
	`type_regelement` VARCHAR(40) NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `indexflag_idVenteReglement` (`flag_id`),
	INDEX `FKk4o86jltu2s1xwnuoau226f61` (`vente_id`),
	INDEX `FKs7aqbjxvu4ur8oqq1dbl8b62h` (`type_regelement`),
	CONSTRAINT `FKk4o86jltu2s1xwnuoau226f61` FOREIGN KEY (`vente_id`) REFERENCES `t_preenregistrement` (`lg_PREENREGISTREMENT_ID`),
	CONSTRAINT `FKs7aqbjxvu4ur8oqq1dbl8b62h` FOREIGN KEY (`type_regelement`) REFERENCES `t_type_reglement` (`lg_TYPE_REGLEMENT_ID`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
