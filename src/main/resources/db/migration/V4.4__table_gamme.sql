ALTER TABLE t_facture ADD COLUMN  `template` BIT(1) NULL DEFAULT FALSE;
CREATE TABLE IF NOT EXISTS `gamme_produit` (
	`id` VARCHAR(255) NOT NULL,
	`code` VARCHAR(255) NOT NULL,
	`libelle` VARCHAR(255) NOT NULL,
	`status` VARCHAR(255) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UK_gxcny2jtftoegad78u3jirvoj` (`code`),
	UNIQUE INDEX `UK_1r9jeo0jvdg5pjhyvl2gnf2do` (`libelle`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
ALTER TABLE t_famille ADD COLUMN `gamme_id` VARCHAR(255) NULL DEFAULT NULL;
CREATE TABLE IF NOT EXISTS `laboratoire` (
	`id` VARCHAR(255) NOT NULL,
	`libelle` VARCHAR(255) NOT NULL,
	`status` VARCHAR(255) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UK_ch0c4o3olc65u3yap006nxq0i` (`libelle`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

ALTER TABLE t_famille ADD COLUMN `laboratoire_id` VARCHAR(255) NULL DEFAULT NULL;

