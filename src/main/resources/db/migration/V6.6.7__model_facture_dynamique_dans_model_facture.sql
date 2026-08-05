-- Les modeles de facture dynamiques deviennent des lignes de t_model_facture, la table des
-- modeles deja utilisee par la fiche tiers payant (champ "Code.Edit.Bordereau").
--
-- Objectif : UN SEUL endroit d'affectation d'un modele a un tiers payant (la fiche tiers
-- payant), au lieu d'un second champ dedie aux modeles dynamiques. La table
-- model_facture_dynamique reste la definition des colonnes du modele ; c'est t_model_facture
-- qui porte desormais le lien vers cette definition.
--
-- Script rejouable : chaque etape est gardee (information_schema / NOT EXISTS).

-- 1) Lien de t_model_facture vers la definition dynamique
SET @existe := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_model_facture'
       AND COLUMN_NAME = 'model_facture_dynamique_id');
SET @sql := IF(@existe = 0,
    'ALTER TABLE `t_model_facture` ADD COLUMN `model_facture_dynamique_id` INT NULL',
    'DO 0');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2) Une ligne t_model_facture pour chaque modele dynamique qui n'en a pas encore
--    (identifiant numerique = max existant + 1, valeur unique 'DYN<id>' car la fiche tiers
--    payant resout le modele par str_VALUE).
--    nomFichier et nomFichierRemiseTierspayant sont declarees NOT NULL sans valeur par defaut
--    (V4.7.7) : elles designent un fichier Jasper, inutile pour un modele dynamique dont la
--    mise en page est construite par le moteur. On les renseigne donc a chaine vide.
INSERT INTO t_model_facture (lg_MODEL_FACTURE_ID, str_VALUE, str_DESCRIPTION, str_STATUT,
                             dt_CREATED, dt_UPDATED, model_facture_dynamique_id,
                             nomFichier, nomFichierRemiseTierspayant)
SELECT CAST(
           (SELECT COALESCE(MAX(CAST(m2.lg_MODEL_FACTURE_ID AS UNSIGNED)), 0)
              FROM t_model_facture m2) + d.id AS CHAR),
       CONCAT('DYN', d.id),
       CONCAT('Modele dynamique : ', d.str_NOM),
       'enable', NOW(), NOW(), d.id,
       '', ''
  FROM model_facture_dynamique d
 WHERE NOT EXISTS (SELECT 1 FROM t_model_facture m
                    WHERE m.model_facture_dynamique_id = d.id);

-- 3) Report des affectations existantes : les tiers payants qui pointaient vers un modele
--    dynamique via l'ancienne colonne pointent desormais vers la ligne t_model_facture creee.
SET @colTp := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_tiers_payant'
       AND COLUMN_NAME = 'model_facture_dynamique_id');
SET @sql := IF(@colTp > 0,
    'UPDATE t_tiers_payant tp
        JOIN t_model_facture m ON m.model_facture_dynamique_id = tp.model_facture_dynamique_id
         SET tp.lg_MODEL_FACTURE_ID = m.lg_MODEL_FACTURE_ID
       WHERE tp.model_facture_dynamique_id IS NOT NULL',
    'DO 0');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 4) L'ancienne colonne de t_tiers_payant n'a plus de raison d'etre
SET @sql := IF(@colTp > 0,
    'ALTER TABLE `t_tiers_payant` DROP COLUMN `model_facture_dynamique_id`',
    'DO 0');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
