CREATE TABLE IF NOT EXISTS `Reference` (
	`id` VARCHAR(14) NOT NULL ,	
`devis`  BIT(1) NOT NULL, 
`emplacement_id` VARCHAR (50) NOT  NULL ,
`reference` VARCHAR (20) NOT NULL,
`reference_temp` VARCHAR (20) NOT NULL,
`last_int_value` INT (8) NOT NULL DEFAULT '0',
`last_int_tmp_value` INT (8) NOT NULL DEFAULT '0',
UNIQUE INDEX `UK_Refch0c4o3olc65u3yap006nxq0i` (`id` ,`emplacement_id` ,`devis` ),
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;