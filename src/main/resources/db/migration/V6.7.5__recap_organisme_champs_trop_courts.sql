-- Recapitulatif par compte d'organisme : "Data too long for column 'CODEORGANISME'".
--
-- CAUSE, etablie a partir de la definition de la procedure et des donnees :
--   la procedure declare  DECLARE CODEORGANISME VARCHAR(100)  et une table temporaire
--   f_CODEORGANISME VARCHAR(100), alors que la colonne source t_tiers_payant.str_CODE_ORGANISME
--   contient jusqu'a 118 caracteres en base. Le serveur tourne en sql_mode STRICT_TRANS_TABLES :
--   MySQL refuse l'affectation au lieu de la tronquer, et leve l'erreur 1406 des le FETCH.
--   Un SEUL organisme trop long suffit a faire echouer TOUT l'ecran, pour toutes les periodes.
--
-- CORRECTIF : porter a 255 les variables et les colonnes de la table temporaire qui recoivent du
--   texte, ainsi que les deux parametres de recherche (search_value recevait deja le texte saisi
--   a l'ecran suivi de '%', et aurait leve la meme erreur au-dela de 99 caracteres).
--   Aucune donnee n'est modifiee et aucune valeur n'est tronquee : seules les contenants
--   grandissent. La requete, l'ordre des colonnes et le tri sont repris a l'identique.
--
-- A NOTER : la fiche d'au moins un tiers payant contient un libelle visiblement saisi par erreur
--   (un texte recopie deux fois dans le meme champ). Cette migration rend l'ecran a nouveau
--   utilisable, elle ne corrige pas ce libelle : il reste a reprendre sur la fiche de l'organisme.

DELIMITER @@

DROP PROCEDURE IF EXISTS proc_recaptulatif_organisme @@

CREATE PROCEDURE proc_recaptulatif_organisme
(
        IN `dt_start` DATE,
        IN `dt_end` DATE,
        IN `search_value` VARCHAR(255),
        IN `lgTIERSPAYANT` VARCHAR(100)
)
BEGIN
DECLARE MONTANT NUMERIC(15);
DECLARE FULLNAME VARCHAR(255);
DECLARE LIBELLETYPE VARCHAR(255);
DECLARE CODEORGANISME VARCHAR(255);
DECLARE CODECOMPTABLE VARCHAR(255);
DECLARE NUMERODECOMPTE VARCHAR(255);

DECLARE done INT DEFAULT 0;
DECLARE curbl CURSOR FOR
SELECT
t.`str_FULLNAME`,SUM(d.`dbl_AMOUNT`),p.`str_LIBELLE_TYPE_TIERS_PAYANT` ,t.`str_CODE_ORGANISME`,
t.`str_CODE_COMPTABLE`,t.`int_NUMERO_DECOMPTE`
 FROM t_dossier_reglement d,t_tiers_payant t,
t_type_tiers_payant p WHERE  t.`lg_TIERS_PAYANT_ID`=d.`str_ORGANISME_ID`
 AND p.`lg_TYPE_TIERS_PAYANT_ID`=t.`lg_TYPE_TIERS_PAYANT_ID`
AND DATE(d.`dt_CREATED`)>=DATE(`dt_start`) AND DATE(d.`dt_CREATED`) <=DATE(`dt_end`)
AND t.`lg_TIERS_PAYANT_ID` LIKE `lgTIERSPAYANT`
AND (t.`str_FULLNAME` LIKE `search_value` OR p.`str_LIBELLE_TYPE_TIERS_PAYANT` LIKE `search_value`)
GROUP BY t.`str_FULLNAME`
;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=1;
DROP  TEMPORARY TABLE IF EXISTS emp_rapport_recap_table;
 CREATE TEMPORARY TABLE IF NOT EXISTS emp_rapport_recap_table
(
f_FULLNAME VARCHAR(255),
f_LIBELLETYPE VARCHAR(255),
f_CODEORGANISME VARCHAR(255),
f_CODECOMPTABLE VARCHAR(255),
f_NUMERODECOMPTE VARCHAR(255),
f_MONTANT NUMERIC (15) DEFAULT 0,
f_CREDIT NUMERIC (15) DEFAULT 0,
f_SOLDE NUMERIC (15) DEFAULT 0
);
OPEN curbl;
bl_loop:LOOP
FETCH curbl INTO FULLNAME, MONTANT,LIBELLETYPE,CODEORGANISME,CODECOMPTABLE,NUMERODECOMPTE;
IF done=1 THEN
 LEAVE bl_loop;
 END IF;
 INSERT INTO emp_rapport_recap_table (f_FULLNAME,f_LIBELLETYPE,f_CODEORGANISME,f_CODECOMPTABLE,f_NUMERODECOMPTE,f_MONTANT)
 VALUES (FULLNAME,LIBELLETYPE,CODEORGANISME,CODECOMPTABLE,NUMERODECOMPTE,MONTANT);

END LOOP bl_loop;
 CLOSE curbl;
 CALL proc_recaptulatif_credit(`dt_start`,`dt_end`,`search_value`,`lgTIERSPAYANT`);
CALL proc_recaptulatif_solde(`search_value`,`lgTIERSPAYANT`);
SELECT * FROM emp_rapport_recap_table ORDER BY f_MONTANT DESC,f_LIBELLETYPE ;
END @@

DELIMITER ;
