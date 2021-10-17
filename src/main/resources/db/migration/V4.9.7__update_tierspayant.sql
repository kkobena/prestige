
ALTER TABLE  t_tiers_payant ADD COLUMN IF NOT EXISTS account INT(11) NULL DEFAULT 0;
ALTER TABLE  t_tiers_payant ADD COLUMN IF NOT EXISTS to_be_exclude BOOLEAN NULL DEFAULT FALSE;
	INSERT  IGNORE  INTO t_sous_menu (`lg_SOUS_MENU_ID`, `str_VALUE`, `str_IMAGE_CSS`, `str_DESCRIPTION`, `str_COMPOSANT`, `lg_MENU_ID`, `int_PRIORITY`, `str_URL`, `str_Status`, `P_KEY`, `dt_CREATED`, `dt_UPDATED`, `icon_CLASS`) 
	VALUES ('15102021', 'Tiers-paynts exclus', NULL, 'Tiers-paynts exclus', 'tpexclus', '53251827585053722655', 9, NULL, 'enable', 'P_SM_TP_EXCLUS', NULL, NULL, '');
INSERT IGNORE  INTO t_privilege (`lg_PRIVELEGE_ID`, `str_NAME`, `str_TYPE`, `str_DESCRIPTION`, `lg_PRIVELEGE_ID_DEP`, `dt_CREATED`, `lg_CREATED_BY`, `dt_UPDATED`, `lg_UPDATED_BY`, `str_STATUT`) 
	VALUES ('15102021', 'P_SM_TP_EXCLUS', 'CUSTOMER', 'Tiers-paynts exclus', NULL, '2020-09-27 10:10:01.0', NULL, NULL, NULL, 'enable');


