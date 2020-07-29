
	INSERT IGNORE  INTO t_sous_menu (`lg_SOUS_MENU_ID`, `str_VALUE`, `str_IMAGE_CSS`, `str_DESCRIPTION`, `str_COMPOSANT`, `lg_MENU_ID`, `int_PRIORITY`, `str_URL`, `str_Status`, `P_KEY`, `dt_CREATED`, `dt_UPDATED`, `icon_CLASS`) 
	VALUES ('12052020', 'Groupe grossistes', NULL, 'Groupe grossistes', 'groupegrossistes', '10', 6, NULL, 'enable', 'P_SM_GROUPE_GROSSISTES', NULL, NULL, '');

INSERT IGNORE  INTO t_privilege (`lg_PRIVELEGE_ID`, `str_NAME`, `str_TYPE`, `str_DESCRIPTION`, `lg_PRIVELEGE_ID_DEP`, `dt_CREATED`, `lg_CREATED_BY`, `dt_UPDATED`, `lg_UPDATED_BY`, `str_STATUT`) 
	VALUES ('12052020', 'P_SM_GROUPE_GROSSISTES', 'CUSTOMER', 'Groupe grossistes', NULL, '2016-03-01 10:10:01.0', NULL, NULL, NULL, 'enable');


	INSERT IGNORE  INTO t_sous_menu (`lg_SOUS_MENU_ID`, `str_VALUE`, `str_IMAGE_CSS`, `str_DESCRIPTION`, `str_COMPOSANT`, `lg_MENU_ID`, `int_PRIORITY`, `str_URL`, `str_Status`, `P_KEY`, `dt_CREATED`, `dt_UPDATED`, `icon_CLASS`) 
	VALUES ('121052020', 'Gamme produits', NULL, 'Gamme produits', 'gammeproduits', '10', 7, NULL, 'enable', 'P_SM_GAMME_PRODUITS', NULL, NULL, '');

INSERT IGNORE  INTO t_privilege (`lg_PRIVELEGE_ID`, `str_NAME`, `str_TYPE`, `str_DESCRIPTION`, `lg_PRIVELEGE_ID_DEP`, `dt_CREATED`, `lg_CREATED_BY`, `dt_UPDATED`, `lg_UPDATED_BY`, `str_STATUT`) 
	VALUES ('121052020', 'P_SM_GAMME_PRODUITS', 'CUSTOMER', 'Gamme produits', NULL, '2016-03-01 10:10:01.0', NULL, NULL, NULL, 'enable');
	
		INSERT  IGNORE  INTO t_sous_menu (`lg_SOUS_MENU_ID`, `str_VALUE`, `str_IMAGE_CSS`, `str_DESCRIPTION`, `str_COMPOSANT`, `lg_MENU_ID`, `int_PRIORITY`, `str_URL`, `str_Status`, `P_KEY`, `dt_CREATED`, `dt_UPDATED`, `icon_CLASS`) 
	VALUES ('120052020', 'Laboratoire produits', NULL, 'Laboratoire produits', 'laboratoireproduits', '10', 8, NULL, 'enable', 'P_SM_LABORATOIRE_PRODUITS', NULL, NULL, '');

INSERT IGNORE  INTO t_privilege (`lg_PRIVELEGE_ID`, `str_NAME`, `str_TYPE`, `str_DESCRIPTION`, `lg_PRIVELEGE_ID_DEP`, `dt_CREATED`, `lg_CREATED_BY`, `dt_UPDATED`, `lg_UPDATED_BY`, `str_STATUT`) 
	VALUES ('120052020', 'P_SM_LABORATOIRE_PRODUITS', 'CUSTOMER', 'Laboratoire produits', NULL, '2016-03-01 10:10:01.0', NULL, NULL, NULL, 'enable');

	INSERT  IGNORE  INTO t_sous_menu (`lg_SOUS_MENU_ID`, `str_VALUE`, `str_IMAGE_CSS`, `str_DESCRIPTION`, `str_COMPOSANT`, `lg_MENU_ID`, `int_PRIORITY`, `str_URL`, `str_Status`, `P_KEY`, `dt_CREATED`, `dt_UPDATED`, `icon_CLASS`) 
	VALUES ('131052020', 'Statistiques produits par laboratoire', NULL, 'Statistiques produits par laboratoire', 'statislaboratoireproduits', '3', 8, NULL, 'enable', 'P_SM_LABORATOIRE_PRODUITS_STAT', NULL, NULL, '');

INSERT IGNORE  INTO t_privilege (`lg_PRIVELEGE_ID`, `str_NAME`, `str_TYPE`, `str_DESCRIPTION`, `lg_PRIVELEGE_ID_DEP`, `dt_CREATED`, `lg_CREATED_BY`, `dt_UPDATED`, `lg_UPDATED_BY`, `str_STATUT`) 
	VALUES ('131052020', 'P_SM_LABORATOIRE_PRODUITS_STAT', 'CUSTOMER', 'Statistiques produits par laboratoire', NULL, '2016-03-01 10:10:01.0', NULL, NULL, NULL, 'enable');

	INSERT  IGNORE  INTO t_sous_menu (`lg_SOUS_MENU_ID`, `str_VALUE`, `str_IMAGE_CSS`, `str_DESCRIPTION`, `str_COMPOSANT`, `lg_MENU_ID`, `int_PRIORITY`, `str_URL`, `str_Status`, `P_KEY`, `dt_CREATED`, `dt_UPDATED`, `icon_CLASS`) 
	VALUES ('13052020', 'Statistiques produits par gamme', NULL, 'Statistiques produits par gamme', 'statisgammeproduits', '3', 8, NULL, 'enable', 'P_SM_GAMME_PRODUITS_STAT', NULL, NULL, '');

INSERT IGNORE  INTO t_privilege (`lg_PRIVELEGE_ID`, `str_NAME`, `str_TYPE`, `str_DESCRIPTION`, `lg_PRIVELEGE_ID_DEP`, `dt_CREATED`, `lg_CREATED_BY`, `dt_UPDATED`, `lg_UPDATED_BY`, `str_STATUT`) 
	VALUES ('13052020', 'P_SM_GAMME_PRODUITS_STAT', 'CUSTOMER', 'Statistiques produits par gamme', NULL, '2016-03-01 10:10:01.0', NULL, NULL, NULL, 'enable');

