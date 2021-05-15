DELIMITER @@
DROP PROCEDURE IF EXISTS proc_migrer_regl_tiers_payant_mvtransaction @@
CREATE PROCEDURE proc_migrer_regl_tiers_payant_mvtransaction
()
BEGIN 

DECLARE dtUPDATED DATETIME;
DECLARE lgUSERID VARCHAR(40);
DECLARE ORGANISMEID VARCHAR(40);
DECLARE strREF VARCHAR(40);
DECLARE lgTYPEVENTEID VARCHAR(40);
DECLARE lgTYPEREGLEMENTID VARCHAR(40);
DECLARE lgPREENREGISTREMENTID VARCHAR(40);
DECLARE EMPLACEMENT VARCHAR(40);
DECLARE intPRICE NUMERIC(10);
DECLARE done INT DEFAULT 0;
DECLARE curbl CURSOR FOR 
SELECT distinct d.lg_DOSSIER_REGLEMENT_ID,tr.lg_TYPE_REGLEMENT_ID,d.dbl_AMOUNT,d.str_ORGANISME_ID,d.dt_CREATED,d.lg_USER_ID,mv.str_REF_TICKET FROM t_dossier_reglement d,t_reglement r, t_mode_reglement m,t_type_reglement tr, t_type_mvt_caisse tm, t_mvt_caisse mv

where d.lg_DOSSIER_REGLEMENT_ID=r.str_REF_RESSOURCE AND r.lg_MODE_REGLEMENT_ID=m.lg_MODE_REGLEMENT_ID AND tr.lg_TYPE_REGLEMENT_ID=m.lg_TYPE_REGLEMENT_ID
AND tm.lg_TYPE_MVT_CAISSE_ID=mv.lg_TYPE_MVT_CAISSE_ID AND m.lg_MODE_REGLEMENT_ID=mv.lg_MODE_REGLEMENT_ID 
AND tm.lg_TYPE_MVT_CAISSE_ID='3' AND d.lg_DOSSIER_REGLEMENT_ID NOT IN (SELECT h.pkey FROM mvttransaction h);
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=1;
OPEN curbl;
bl_loop:LOOP
FETCH curbl INTO lgPREENREGISTREMENTID,lgTYPEREGLEMENTID,intPRICE,ORGANISMEID,dtUPDATED,lgUSERID,strREF;
IF done=1 THEN 
 LEAVE bl_loop;
 END IF;
INSERT IGNORE INTO mvttransaction (uuid, `avoidAmount`, categorie, `checked`, `createdAt`, marge, montant, `montantCredit`, `montantNet`, `montantPaye`, `montantRegle`, `montantRemise`, `montantRestant`, `montantTva`, `montantVerse`, mvtdate, pkey, reference, `typeTransaction`, caisse, `grossisteId`, `lg_EMPLACEMENT_ID`, `typeReglementId`, `typeMvtCaisseId`, `lg_USER_ID`, organisme, `montantAcc`, margeug, montantttcug, montantnetug, montanttvaug) 
	VALUES (UUID(), 0, 1, true, dtUPDATED, 0, intPRICE, 0, intPRICE, intPRICE, intPRICE, 0, 0, 0, intPRICE, dtUPDATED, lgPREENREGISTREMENTID, strREF, 3, lgUSERID, NULL, '1', lgTYPEREGLEMENTID, '3', lgUSERID, NULL, 0, 0, 0, 0, 0);
END LOOP bl_loop;
 CLOSE curbl;

END @@ 
DELIMITER ; 
 CALL proc_migrer_regl_tiers_payant_mvtransaction();
