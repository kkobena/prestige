	INSERT  IGNORE  INTO t_sous_menu (`lg_SOUS_MENU_ID`, `str_VALUE`, `str_IMAGE_CSS`, `str_DESCRIPTION`, `str_COMPOSANT`, `lg_MENU_ID`, `int_PRIORITY`, `str_URL`, `str_Status`, `P_KEY`, `dt_CREATED`, `dt_UPDATED`, `icon_CLASS`) 
	VALUES ('13112020', 'Exporter ventes', NULL, 'Exporter ventes', 'exportdepotvents', '9', 15, NULL, 'enable', 'P_SM_EXPORT_VENTES', NULL, NULL, '');
INSERT IGNORE  INTO t_privilege (`lg_PRIVELEGE_ID`, `str_NAME`, `str_TYPE`, `str_DESCRIPTION`, `lg_PRIVELEGE_ID_DEP`, `dt_CREATED`, `lg_CREATED_BY`, `dt_UPDATED`, `lg_UPDATED_BY`, `str_STATUT`) 
	VALUES ('13112020', 'P_SM_EXPORT_VENTES', 'CUSTOMER', 'Exporter ventes', NULL, '2020-11-13 10:10:01.0', NULL, NULL, NULL, 'enable');

	INSERT  IGNORE  INTO t_sous_menu (`lg_SOUS_MENU_ID`, `str_VALUE`, `str_IMAGE_CSS`, `str_DESCRIPTION`, `str_COMPOSANT`, `lg_MENU_ID`, `int_PRIORITY`, `str_URL`, `str_Status`, `P_KEY`, `dt_CREATED`, `dt_UPDATED`, `icon_CLASS`) 
	VALUES ('12112020', 'Importe ventes', NULL, 'Importer ventes', 'impordepotvents', '9', 14, NULL, 'enable', 'P_SM_IMPORT_VENTES', NULL, NULL, '');
INSERT IGNORE  INTO t_privilege (`lg_PRIVELEGE_ID`, `str_NAME`, `str_TYPE`, `str_DESCRIPTION`, `lg_PRIVELEGE_ID_DEP`, `dt_CREATED`, `lg_CREATED_BY`, `dt_UPDATED`, `lg_UPDATED_BY`, `str_STATUT`) 
	VALUES ('12112020', 'P_SM_IMPORT_VENTES', 'CUSTOMER', 'Importer ventes', NULL, '2020-11-13 10:10:01.0', NULL, NULL, NULL, 'enable');

