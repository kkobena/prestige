DELIMITER @@
DROP PROCEDURE IF EXISTS proc_updateaccount_mvt @@
 CREATE PROCEDURE proc_updateaccount_mvt
(IN dtStart DATE, IN dtEnd DATE)
BEGIN 
DECLARE idMvt VARCHAR(40);
DECLARE montant NUMERIC(10);
DECLARE done INT DEFAULT 0;
DECLARE curbl CURSOR FOR 

select g.uuid,g.montantNet from mvttransaction g 
where g.montantAcc =0 AND g.typeTransaction=0  AND g.typeReglementId='1' AND g.montantRemise=0
AND g.mvtdate BETWEEN dtStart and dtEnd;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=1;
OPEN curbl;
bl_loop:LOOP
FETCH curbl INTO idMvt,montant;
IF done=1 THEN 
 LEAVE bl_loop;
 END IF;
UPDATE mvttransaction set montantAcc=montantNet WHERE uuid=idMvt;
END LOOP bl_loop;
 CLOSE curbl;

END @@ 
DELIMITER ; 

INSERT IGNORE INTO t_parameters (`str_KEY`, `str_VALUE`, `str_DESCRIPTION`, `str_TYPE`, `str_STATUT`, `str_IS_EN_KRYPTED`, `str_SECTION_KEY`, `dt_CREATED`, `dt_UPDATED`) 
	VALUES ('KEY_NOMBRE_TICKETS_VNO', '0', 'NOMBRE DE TICKETS VENTE A CREDIT', 'CUSTOMER', 'enable', NULL, NULL, NULL, NULL);
CALL proc_updateaccount_mvt('2020-01-01','2025-01-01');