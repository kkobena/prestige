CREATE TABLE `historique_importation` (
	`id` VARCHAR(40) NOT NULL,
`createdAt` DATETIME NOT NULL,
	`mvtdate` DATE NOT NULL,
	`user` VARCHAR(40) NOT NULL,
	`montant_achat` INT(11) NOT NULL,
	`montant_vente` INT(11) NOT NULL,
	`nbre_ligne` INT(11) NOT NULL,
	`detail` LONGTEXT NULL DEFAULT NULL COLLATE 'utf8mb4_bin',
	PRIMARY KEY (`id`)
	
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
