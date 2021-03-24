DELIMITER @@
DROP PROCEDURE IF EXISTS proc_ligne_achat_transaction @@
CREATE PROCEDURE proc_ligne_achat_transaction()

BEGIN 

DECLARE dtUPDATED DATETIME;
DECLARE lgUSERID VARCHAR(40);
DECLARE lgGROSSISTEID VARCHAR(40);
DECLARE strREFLIVRAISON VARCHAR(40);
DECLARE lgBONLIVRAISONID VARCHAR(40);
DECLARE intMHT NUMERIC(10);
DECLARE intTVA NUMERIC(10);
DECLARE intHTTC NUMERIC(10);
DECLARE done INT DEFAULT 0;
DECLARE curbl CURSOR FOR 
SELECT b.str_REF_LIVRAISON, b.lg_USER_ID, b.lg_BON_LIVRAISON_ID ,b.dt_UPDATED  ,o.lg_GROSSISTE_ID,
b.int_MHT,b.int_TVA,b.int_HTTC
 FROM  t_bon_livraison  b,t_order o where b.str_STATUT='is_closed' 
AND b.lg_ORDER_ID=o.lg_ORDER_ID and b.lg_BON_LIVRAISON_ID  NOT IN (SELECT m.pkey FROM mvttransaction  m );
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=1;
OPEN curbl;
bl_loop:LOOP
FETCH curbl INTO strREFLIVRAISON,lgUSERID,lgBONLIVRAISONID,dtUPDATED,lgGROSSISTEID,intMHT,intTVA,intHTTC;
IF done=1 THEN 
 LEAVE bl_loop;
 END IF;

INSERT INTO `mvttransaction` (`uuid`, `avoidAmount`, `categorie`, `checked`, `createdAt`, `marge`, `montant`, `montantCredit`, `montantNet`, `montantPaye`, `montantRegle`, `montantRemise`, `montantRestant`, `montantTva`, `montantVerse`, `mvtdate`, `pkey`, `reference`, `typeTransaction`, `caisse`, `grossisteId`, `lg_EMPLACEMENT_ID`, `typeReglementId`, `typeMvtCaisseId`, `lg_USER_ID`, `montantAcc`, `margeug`, `montantttcug`, `montantnetug`, `montanttvaug`) 
	VALUES (UUID(), 0, 1, b'1', dtUPDATED, 0, intHTTC, 0, intMHT, 0, 0, 0, 0, intTVA, 0, dtUPDATED, lgBONLIVRAISONID, strREFLIVRAISON, 2, lgUSERID, lgGROSSISTEID, '1', NULL, NULL, lgUSERID, 0, 0, 0, 0, 0);

END LOOP bl_loop;
 CLOSE curbl;

END @@ 
DELIMITER ; 
CALL proc_ligne_achat_transaction();