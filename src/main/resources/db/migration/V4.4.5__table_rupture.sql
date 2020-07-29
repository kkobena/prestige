DROP TABLE IF EXISTS rupture_detail;
DROP TABLE IF EXISTS rupture;
CREATE TABLE IF NOT EXISTS `rupture` (
	`id` VARCHAR(40) NOT NULL,
`reference` VARCHAR(70) NOT NULL,
	`statut` VARCHAR(20) NOT NULL,
	`dtCreated` date,
`dtUpdated` date,
`grossisteId` VARCHAR(40),
	PRIMARY KEY (`id`)
)
ENGINE=InnoDB
;
CREATE TABLE IF NOT EXISTS `rupture_detail` (
	`id` VARCHAR(40) NOT NULL,
	`qty` int(8) NOT NULL,
`prixAchat` int(10) NOT NULL,
`prixVente` int(10) NOT NULL,
	`produitId` VARCHAR(40) NOT NULL,
`ruptureId` VARCHAR(40) NOT NULL,
	PRIMARY KEY (`id`)
)
ENGINE=InnoDB
;