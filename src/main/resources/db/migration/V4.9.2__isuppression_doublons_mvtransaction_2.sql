DELIMITER @@
DROP PROCEDURE IF EXISTS suppresion_doublons_transacton_2mvts @@
CREATE PROCEDURE suppresion_doublons_transacton_2mvts()
BEGIN 

DECLARE ID VARCHAR(100);
DECLARE done INT DEFAULT 0;
DECLARE curbl CURSOR FOR 

SELECT d.ID FROM doublon_mvtransaction_table2 d;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=1;
OPEN curbl;
bl_loop:LOOP
FETCH curbl INTO ID;
IF done=1 THEN 
 LEAVE bl_loop;
 END IF;
DELETE FROM  mvttransaction  WHERE uuid=ID ;
END LOOP bl_loop;
 CLOSE curbl;

END @@ 
DELIMITER ; 


CALL suppresion_doublons_transacton_2mvts();
ALTER TABLE mvttransaction DROP INDEX IF EXISTS `pkey_unq`;
ALTER TABLE mvttransaction ADD 	UNIQUE INDEX `pkey_unq` (`pkey`);