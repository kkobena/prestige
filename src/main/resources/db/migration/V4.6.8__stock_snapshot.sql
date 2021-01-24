 CREATE TABLE IF NOT EXISTS `stock_snapshot` (
	`id` VARCHAR(40) NOT NULL,
	`produit_id` VARCHAR(40) NOT NULL,
	`stock_journalier` LONGTEXT NULL DEFAULT NULL COLLATE 'utf8mb4_bin',
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_stock_snapshot_t_famille` FOREIGN KEY (`produit_id`) REFERENCES `t_famille` (`lg_FAMILLE_ID`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

