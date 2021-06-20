CREATE TABLE IF NOT EXISTS `stock_daily_value` (
	`id` INT(11) NOT NULL,
	`valeur_achat` BIGINT(20) NOT NULL,
	`valeur_vente` BIGINT(20) NOT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;


INSERT IGNORE INTO stock_daily_value (id,valeur_achat,valeur_vente) SELECT s.id,SUM(s.prixPaf*s.qty) AS VALURACHAT,
SUM(s.prixUni*s.qty) AS VALURVENTE 
 FROM t_stock_snapshot s WHERE s.magasin='1' GROUP BY s.id;