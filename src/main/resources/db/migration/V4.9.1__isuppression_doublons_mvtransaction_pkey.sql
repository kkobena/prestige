CREATE OR REPLACE TABLE DOUBLON_MVTRANSACTION AS SELECT m.pkey,COUNT(m.pkey) AS nb,m.`createdAt` FROM mvttransaction m GROUP BY m.pkey HAVING  COUNT(m.pkey)>1; 
CREATE OR REPLACE TABLE `doublon_mvtransaction_table2` (
	`ID` VARCHAR(100) NOT NULL,
	`createdAt` DATETIME NOT NULL,
	`pkey` VARCHAR(100) NOT NULL
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

DELIMITER @@
DROP PROCEDURE IF EXISTS suppresion_doublons_transacton_mvts @@
CREATE PROCEDURE suppresion_doublons_transacton_mvts()
BEGIN 

DECLARE pkey VARCHAR(100);
DECLARE NB int(10);
DECLARE createdAt DATETIME;
DECLARE mvtuuid VARCHAR(100);
DECLARE done INT DEFAULT 0;
DECLARE curbl CURSOR FOR 

SELECT d.pkey,d.nb,d.createdAt FROM DOUBLON_MVTRANSACTION d;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=1;
OPEN curbl;
bl_loop:LOOP
FETCH curbl INTO pkey,NB,createdAt;
IF done=1 THEN 
 LEAVE bl_loop;
 END IF;
INSERT INTO doublon_mvtransaction_table2(ID,createdAt,pkey)  SELECT m.uuid,m.createdAt,m.pkey from  mvttransaction m WHERE m.pkey=pkey LIMIT NB OFFSET 1;

END LOOP bl_loop;
 CLOSE curbl;

END @@ 
DELIMITER ; 

call suppresion_doublons_transacton_mvts();

