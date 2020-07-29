CREATE OR REPLACE TABLE `t_stock_snapshot` (
	`id` DATE NOT NULL,
	`prixPaf` INT(11) NULL DEFAULT NULL,
	`magasin` VARCHAR(40) NULL DEFAULT NULL,
	`prixTarif` INT(11) NULL DEFAULT NULL,
	`prixUni` INT(11) NULL DEFAULT NULL,
	`qty` INT(11) NULL DEFAULT NULL,
	`valeurTva` INT(11) NULL DEFAULT NULL,
	`familleId` VARCHAR(40) NOT NULL,
	PRIMARY KEY (`id`,`familleId`,`magasin`),
	INDEX `IDX93ktdkly3egu5san5m6psvuff` (`magasin`),
	INDEX `FKdo0lnera4sl0kyykbikiub714` (`familleId`),
	CONSTRAINT `FKdo0lnera4sl0kyykbikiub714` FOREIGN KEY (`familleId`) REFERENCES `t_famille` (`lg_FAMILLE_ID`)
)
ENGINE=InnoDB
;
