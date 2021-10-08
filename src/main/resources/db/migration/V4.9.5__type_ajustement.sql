CREATE TABLE IF NOT EXISTS `motif_ajustement` (
	`id` int(3) NOT NULL AUTO_INCREMENT,
	`libelle` VARCHAR(250) NOT NULL,
	PRIMARY KEY (`id`),
UNIQUE INDEX `idx_motif_ajustement` (`libelle`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
ALTER TABLE  t_ajustement_detail ADD COLUMN IF NOT EXISTS motif_ajustement_id int (3);
ALTER TABLE t_ajustement_detail ADD CONSTRAINT `ajust_motif_id_fk` FOREIGN KEY (`motif_ajustement_id`) REFERENCES `motif_ajustement` (`id`);
INSERT IGNORE INTO `motif_ajustement`(id,libelle) VALUES(1,'Reappro rayon'),(2,'Reappro réserve'),(3,'Correction stock');