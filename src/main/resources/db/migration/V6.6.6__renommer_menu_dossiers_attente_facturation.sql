-- Renomme le menu "Liste des factures en attente d'edition" en
-- "Liste des dossiers en attente de facturation".
--
-- Le nouveau libelle fait 44 caracteres alors que t_sous_menu.str_VALUE etait un
-- VARCHAR(35) : la colonne est d'abord elargie a 100 (uniquement si elle est encore
-- plus petite - script rejouable, aucune donnee modifiee par l'ALTER).
SET @len := (
    SELECT CHARACTER_MAXIMUM_LENGTH FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_sous_menu' AND COLUMN_NAME = 'str_VALUE');
SET @sql := IF(@len IS NOT NULL AND @len < 60,
    'ALTER TABLE `t_sous_menu` MODIFY `str_VALUE` VARCHAR(100)',
    'DO 0');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Renommage du menu, uniquement sur l'entree pointant vers l'ecran concerne.
-- Rejouable sans effet si le libelle a deja ete corrige.
UPDATE t_sous_menu
   SET str_VALUE = 'Liste des dossiers en attente de facturation',
       str_DESCRIPTION = 'Liste des dossiers en attente de facturation'
 WHERE str_COMPOSANT = 'factureenattenteedition'
   AND (str_VALUE LIKE '%attente%' OR str_DESCRIPTION LIKE '%attente%');
