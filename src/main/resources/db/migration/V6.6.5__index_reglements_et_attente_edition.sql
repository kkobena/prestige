-- Index de lecture pour la "Liste des reglements" (api/v1/reglement-facture/list)
-- et la "Liste des dossiers en attente d'edition" (api/v1/dossiers-attente-edition/list).
--
-- Ces deux ecrans utilisent deja >= / <= pour les dates ; la lenteur observee vient de
-- colonnes filtrees SANS index :
--   1. t_reglement.str_REF_RESSOURCE : la liste des reglements recherche le reglement
--      de chaque dossier par cette colonne (une recherche PAR LIGNE affichee) ; sans
--      index, chaque recherche parcourt toute la table des reglements.
--   2. t_dossier_reglement.dt_CREATED : filtre de periode de la liste des reglements.
--   3. t_preenregistrement.dt_CREATED : filtre de periode des dossiers en attente
--      d'edition (l'index V6.6.4 porte sur dt_UPDATED, pas sur dt_CREATED).
--   4. t_preenregistrement_compte_client_tiers_payent.str_STATUT_FACTURE : la liste des
--      dossiers en attente ne garde que les bons NON factures ; cet index permet de
--      partir directement de ce petit sous-ensemble au lieu de parcourir toute la table.
--
-- Aucune donnee modifiee, aucun resultat change : un index ne fait qu'accelerer une lecture.
-- Creation en ALGORITHM=INPLACE, LOCK=NONE : les ecritures restent possibles pendant l'operation.
-- Chaque bloc verifie qu'aucun index ne commence deja par la meme colonne, quel que soit son
-- nom : le script est rejouable et n'echoue pas sur une base ou l'index existe deja.

SET @existe := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_reglement'
       AND SEQ_IN_INDEX = 1 AND COLUMN_NAME = 'str_REF_RESSOURCE');
SET @sql := IF(@existe = 0,
    'ALTER TABLE `t_reglement` ADD INDEX `ix_reglement_ref_ressource` (`str_REF_RESSOURCE`), ALGORITHM=INPLACE, LOCK=NONE',
    'DO 0');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @existe := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_dossier_reglement'
       AND SEQ_IN_INDEX = 1 AND COLUMN_NAME = 'dt_CREATED');
SET @sql := IF(@existe = 0,
    'ALTER TABLE `t_dossier_reglement` ADD INDEX `ix_dossier_reglement_dt_created` (`dt_CREATED`), ALGORITHM=INPLACE, LOCK=NONE',
    'DO 0');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @existe := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_preenregistrement'
       AND SEQ_IN_INDEX = 1 AND COLUMN_NAME = 'dt_CREATED');
SET @sql := IF(@existe = 0,
    'ALTER TABLE `t_preenregistrement` ADD INDEX `ix_preenreg_dt_created` (`dt_CREATED`), ALGORITHM=INPLACE, LOCK=NONE',
    'DO 0');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @existe := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_preenregistrement_compte_client_tiers_payent'
       AND SEQ_IN_INDEX = 1 AND COLUMN_NAME = 'str_STATUT_FACTURE');
SET @sql := IF(@existe = 0,
    'ALTER TABLE `t_preenregistrement_compte_client_tiers_payent` ADD INDEX `ix_pcctp_statut_facture` (`str_STATUT_FACTURE`), ALGORITHM=INPLACE, LOCK=NONE',
    'DO 0');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
