DELIMITER @@
DROP PROCEDURE IF EXISTS proc_updatemvtrans_mvts @@
CREATE PROCEDURE proc_updatemvtrans_mvts
()
BEGIN 

DECLARE dtUPDATED DATETIME;
DECLARE lgUSERID VARCHAR(40);
DECLARE strREF VARCHAR(40);
DECLARE lgTYPEREGLEMENTID VARCHAR(40);
DECLARE typeMvt VARCHAR(10);
DECLARE lgPREENREGISTREMENTID VARCHAR(40);
DECLARE intPRICE NUMERIC(10);
DECLARE categorie INT(1) ;
DECLARE typetransac INT(1);
DECLARE done INT DEFAULT 0;
DECLARE curbl CURSOR FOR 
SELECT m.lg_MVT_CAISSE_ID,m.lg_TYPE_MVT_CAISSE_ID,m.lg_USER_ID,m.int_AMOUNT,m.dt_CREATED,m.str_REF_TICKET,t.lg_TYPE_REGLEMENT_ID FROM t_mvt_caisse m,t_mode_reglement r,t_type_reglement t WHERE 
  m.lg_MODE_REGLEMENT_ID=r.lg_MODE_REGLEMENT_ID AND r.lg_TYPE_REGLEMENT_ID=t.lg_TYPE_REGLEMENT_ID AND m.lg_TYPE_MVT_CAISSE_ID <> '3' AND m.lg_TYPE_MVT_CAISSE_ID <> '8' AND m.lg_TYPE_MVT_CAISSE_ID <> '9';
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=1;
OPEN curbl;
bl_loop:LOOP
FETCH curbl INTO lgPREENREGISTREMENTID,typeMvt,lgUSERID,intPRICE,dtUPDATED,strREF,lgTYPEREGLEMENTID;
IF done=1 THEN 
 LEAVE bl_loop;
 END IF;
IF typeMvt ='1' || typeMvt='4' THEN 
SET typetransac=4;
SET categorie=0;
ELSE
SET typetransac=3;
SET categorie=1;
END IF;

INSERT IGNORE INTO mvttransaction (uuid, `createdAt`, `MONTANT`, `MONTANTCREDIT`, `MONTANTREGLE`, `MONTANTRESTANT`, mvtdate, caisse, `typeTransaction`, `grossisteId`, `lg_EMPLACEMENT_ID`, `typeReglementId`, `typeMvtCaisseId`, `lg_USER_ID`, `montantRemise`, `montantNet`, `MONTANTVERSE`, pkey, categorie, `avoidAmount`, `checked`, `montantPaye`, `montantTva`, marge, reference, organisme) 
	VALUES (UUID(), dtUPDATED, intPRICE, 0, intPRICE, 0, dtUPDATED, lgUSERID, typetransac, NULL, '1', lgTYPEREGLEMENTID, typeMvt, lgUSERID, 0, intPRICE, 0, lgPREENREGISTREMENTID, categorie, 0, true, intPRICE, 0, 0, strREF, NULL);

END LOOP bl_loop;
 CLOSE curbl;

END @@ 
DELIMITER ; 
