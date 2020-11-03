INSERT IGNORE  INTO t_type_reglement (`lg_TYPE_REGLEMENT_ID`, `str_NAME`, `str_DESCRIPTION`, `str_FLAG`, `str_STATUT`, `dt_CREATED`, `dt_UPDATED`) 
	VALUES ('7', 'ORANGE', 'ORANGE', '0', 'enable', '2016-04-29 14:38:58.0', NULL);
INSERT IGNORE  INTO t_type_reglement (`lg_TYPE_REGLEMENT_ID`, `str_NAME`, `str_DESCRIPTION`, `str_FLAG`, `str_STATUT`, `dt_CREATED`, `dt_UPDATED`) 
	VALUES ('8', 'MOOV', 'MOOV', '0', 'enable', '2016-04-29 14:38:58.0', NULL);
INSERT IGNORE  INTO t_type_reglement (`lg_TYPE_REGLEMENT_ID`, `str_NAME`, `str_DESCRIPTION`, `str_FLAG`, `str_STATUT`, `dt_CREATED`, `dt_UPDATED`) 
	VALUES ('9', 'MTN', 'MTN', '0', 'enable', '2016-04-29 14:38:58.0', NULL);
INSERT IGNORE  INTO t_mode_reglement (`lg_MODE_REGLEMENT_ID`, `str_NAME`, `str_DESCRIPTION`, `lg_TYPE_REGLEMENT_ID`, `dt_CREATED`, `dt_UPDATED`, `str_STATUT`) 
	VALUES ('9', 'MTN', 'MTN', '9', '2016-04-29 14:46:25.0', '2016-04-29 14:46:25.0', 'enable');
INSERT IGNORE  INTO t_mode_reglement (`lg_MODE_REGLEMENT_ID`, `str_NAME`, `str_DESCRIPTION`, `lg_TYPE_REGLEMENT_ID`, `dt_CREATED`, `dt_UPDATED`, `str_STATUT`) 
VALUES ('8', 'MOOV', 'MOOV', '8', '2016-04-29 14:46:25.0', '2016-04-29 14:46:25.0', 'enable');
INSERT IGNORE  INTO t_mode_reglement (`lg_MODE_REGLEMENT_ID`, `str_NAME`, `str_DESCRIPTION`, `lg_TYPE_REGLEMENT_ID`, `dt_CREATED`, `dt_UPDATED`, `str_STATUT`) 
	VALUES ('10', 'ORANGE', 'ORANGE', '7', '2016-04-29 14:46:25.0', '2016-04-29 14:46:25.0', 'enable');	
ALTER TABLE t_client add column email varchar(100) ;