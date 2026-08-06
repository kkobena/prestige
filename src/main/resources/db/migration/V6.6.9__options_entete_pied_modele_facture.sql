-- Options de presentation du modele de facture dynamique : afficher ou non l'en-tete
-- (nom de l'officine, bloc tiers payant, numero de facture) et le pied de page (numerotation).
--
-- Valeur par defaut 1 : les modeles deja crees gardent exactement la presentation actuelle.
-- Bloc garde et rejouable (aucun echec si les colonnes existent deja).

SET @existe := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'model_facture_dynamique'
       AND COLUMN_NAME = 'b_AFFICHER_ENTETE');
SET @sql := IF(@existe = 0,
    'ALTER TABLE `model_facture_dynamique` ADD COLUMN `b_AFFICHER_ENTETE` TINYINT(1) NOT NULL DEFAULT 1',
    'DO 0');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @existe := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'model_facture_dynamique'
       AND COLUMN_NAME = 'b_AFFICHER_PIED_PAGE');
SET @sql := IF(@existe = 0,
    'ALTER TABLE `model_facture_dynamique` ADD COLUMN `b_AFFICHER_PIED_PAGE` TINYINT(1) NOT NULL DEFAULT 1',
    'DO 0');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
