
INSERT IGNORE INTO t_menu (`lg_MENU_ID`, `str_VALUE`, `str_IMAGE_CSS`, `str_DESCRIPTION`, `int_PRIORITY`, `str_Status`, `P_KEY`, `lg_MODULE_ID`, `dt_CREATED`, `dt_UPDATED`, `icon_CLASS`) 
	VALUES ('23052020', 'MENU PHARMACIEN', NULL, 'MENU PHARMACIEN', 3, 'enable', 'P_M_MENU_PHARMACIEN', '1', NULL, NULL, '');
	
	INSERT IGNORE INTO t_privilege (`lg_PRIVELEGE_ID`, `str_NAME`, `str_TYPE`, `str_DESCRIPTION`, `lg_PRIVELEGE_ID_DEP`, `dt_CREATED`, `lg_CREATED_BY`, `dt_UPDATED`, `lg_UPDATED_BY`, `str_STATUT`) 
	VALUES ('23052020', 'P_M_MENU_PHARMACIEN', 'CUSTOMER', 'MENU PHARMACIEN', NULL, '2016-03-01 10:10:01.0', NULL, NULL, NULL, 'enable');
	
	INSERT IGNORE  INTO t_sous_menu (`lg_SOUS_MENU_ID`, `str_VALUE`, `str_IMAGE_CSS`, `str_DESCRIPTION`, `str_COMPOSANT`, `lg_MENU_ID`, `int_PRIORITY`, `str_URL`, `str_Status`, `P_KEY`, `dt_CREATED`, `dt_UPDATED`, `icon_CLASS`) 
	VALUES ('20200627', 'Articles en sur-stock', NULL, 'Articles en sur-stock', 'SurStock', '23052020', 0, NULL, 'enable', 'P_SM_SUR_STOCK', NULL, NULL, '');

INSERT IGNORE  INTO t_privilege (`lg_PRIVELEGE_ID`, `str_NAME`, `str_TYPE`, `str_DESCRIPTION`, `lg_PRIVELEGE_ID_DEP`, `dt_CREATED`, `lg_CREATED_BY`, `dt_UPDATED`, `lg_UPDATED_BY`, `str_STATUT`) 
	VALUES ('20200627', 'P_SM_SUR_STOCK', 'CUSTOMER', 'Articles en sur-stock', NULL, '2016-03-01 10:10:01.0', NULL, NULL, NULL, 'enable');
