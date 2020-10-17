ALTER TABLE t_famille  ADD COLUMN is_scheduled  TINYINT(1) NULL DEFAULT '0';

CREATE TABLE IF NOT EXISTS `medecin` (
	`id` VARCHAR(255) NOT NULL,
	`num_ordre` VARCHAR(100) NOT NULL,
	`nom` VARCHAR(255) NOT NULL,
`commentaire` VARCHAR(255)  NULL,
created_at datetime not null,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `MEDECIN_ch0c4o3olc65u3yap006nxq0i` (`num_ordre`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;
ALTER TABLE t_preenregistrement  ADD COLUMN medecin_id  VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8_general_ci';
ALTER TABLE t_preenregistrement ADD CONSTRAINT `FKMEDECIN_ch0c4o3olc65u3yap006nxq0i` FOREIGN KEY (`medecin_id`) REFERENCES `medecin` (`id`);