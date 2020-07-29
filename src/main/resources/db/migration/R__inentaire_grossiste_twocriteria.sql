DELIMITER @@
DROP PROCEDURE IF EXISTS proc_inentaire_grossiste_twocriteria @@
CREATE PROCEDURE proc_inentaire_grossiste_twocriteria
(
        IN `FIRSTCRITERIA` VARCHAR(50),
        IN `SECONDCRITERIA` VARCHAR(50),
        IN `LG_EMPLACEMENT_ID` VARCHAR(50),
        IN `lg_TYPE_STOCK_ID` VARCHAR(50),
        IN `lg_INVENTAIRE_ID` VARCHAR(50)
    )
BEGIN 
DECLARE LGFAMILLEID VARCHAR(50);
DECLARE STOCK NUMERIC(12);
DECLARE lgFAMILLESTOCKID VARCHAR(50);
DECLARE NOMBRE NUMERIC(12);
DECLARE done INT DEFAULT 0;

DECLARE curbl CURSOR FOR 
SELECT 
  `t_famille`.`lg_FAMILLE_ID`,
  `t_famille_stock`.`int_NUMBER_AVAILABLE`,
`t_famille_stock`.`lg_FAMILLE_STOCK_ID`
FROM
  `t_famille`
  INNER JOIN `t_famille_stock` ON (`t_famille`.`lg_FAMILLE_ID` = `t_famille_stock`.`lg_FAMILLE_ID`)
 
  INNER JOIN `t_grossiste` ON (`t_famille`.`lg_GROSSISTE_ID` = `t_grossiste`.`lg_GROSSISTE_ID`)
WHERE
  `t_famille_stock`.`lg_EMPLACEMENT_ID` =`LG_EMPLACEMENT_ID` AND 

  `t_famille`.`str_STATUT` = 'enable'  AND `t_grossiste`.`str_CODE`>=`FIRSTCRITERIA` AND `t_grossiste`.`str_CODE`<=`SECONDCRITERIA`
GROUP BY
  t_famille.lg_FAMILLE_ID;
  

DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=1;
SET NOMBRE=0;
OPEN curbl;
bl_loop:LOOP
FETCH curbl INTO LGFAMILLEID,STOCK,lgFAMILLESTOCKID;
IF done=1 THEN 
 LEAVE bl_loop;
 END IF;
INSERT INTO t_inventaire_famille 
(`lg_INVENTAIRE_ID`,`lg_FAMILLE_ID`,`int_NUMBER`,`int_NUMBER_INIT`,`str_STATUT`,`dt_CREATED`,`bool_INVENTAIRE`,`lg_FAMILLE_STOCK_ID`)
VALUES(`lg_INVENTAIRE_ID`,LGFAMILLEID,STOCK,STOCK,'enable',NOW(),1,lgFAMILLESTOCKID);

SET NOMBRE=NOMBRE+1;
END LOOP bl_loop;
 CLOSE curbl;
COMMIT;
SELECT  NOMBRE;

END @@ 
DELIMITER ; 
