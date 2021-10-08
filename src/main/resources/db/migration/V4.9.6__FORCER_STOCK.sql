INSERT IGNORE INTO t_parameters (`str_KEY`, `str_VALUE`, `str_DESCRIPTION`, `str_TYPE`, `str_STATUT`, `str_IS_EN_KRYPTED`, `str_SECTION_KEY`, `dt_CREATED`, `dt_UPDATED`) 
	VALUES ('FORCER_STOCK_VENTE', '1', '0-impossible de forcer le stock « voir le gestionnaire » 1-le stock peut être forcé comme dans le fonctionnement actuel  ', 'CUSTOMER', 'enable', NULL, NULL, NULL, NULL);

ALTER TABLE t_preenregistrement ADD COLUMN  IF NOT EXISTS completion_date DATETIME NULL DEFAULT NULL;
UPDATE  t_preenregistrement SET  completion_date=`dt_UPDATED`;