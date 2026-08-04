-- Index de lecture pour la "Liste des bons par organismes" (api/v1/facture-subro/list).
--
-- Les deux requetes de cet ecran (liste + totaux) filtrent t_preenregistrement sur
-- p.dt_UPDATED BETWEEN ?1 AND ?2 : sans index sur dt_UPDATED, la table des ventes est
-- parcourue entierement a chaque affichage, ce qui explique le temps d'attente observe.
--
-- Aucune donnee modifiee, aucun resultat change : un index ne fait qu'accelerer une lecture.
-- Creation en ALGORITHM=INPLACE, LOCK=NONE : les ecritures restent possibles pendant l'operation.
-- Le bloc verifie qu'aucun index ne commence deja par la meme colonne, quel que soit son nom :
-- le script est rejouable et n'echoue pas sur une base ou l'index a deja ete pose a la main.

SET @existe := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_preenregistrement'
       AND SEQ_IN_INDEX = 1 AND COLUMN_NAME = 'dt_UPDATED');
SET @sql := IF(@existe = 0,
    'ALTER TABLE `t_preenregistrement` ADD INDEX `ix_preenreg_dt_updated` (`dt_UPDATED`, `str_STATUT`), ALGORITHM=INPLACE, LOCK=NONE',
    'DO 0');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
