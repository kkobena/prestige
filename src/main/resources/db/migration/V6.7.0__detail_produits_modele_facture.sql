-- Option "detailler les produits de chaque bon" du createur de modeles de facture :
-- sous la ligne du bon, les lignes de vente (produits) sont affichees avec leurs propres colonnes.
--
-- b_DETAILLER_PRODUITS vaut 0 par defaut : les modeles deja crees gardent UNE ligne par bon,
-- exactement comme aujourd'hui.
-- str_NIVEAU distingue, dans la meme table de colonnes, celles du bon ('BON', valeur par defaut
-- pour tout l'existant) de celles du produit ('PRODUIT').
--
-- Bloc garde et rejouable (aucun echec si les colonnes existent deja).

SET @existe := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'model_facture_dynamique'
       AND COLUMN_NAME = 'b_DETAILLER_PRODUITS');
SET @sql := IF(@existe = 0,
    'ALTER TABLE `model_facture_dynamique` ADD COLUMN `b_DETAILLER_PRODUITS` TINYINT(1) NOT NULL DEFAULT 0',
    'DO 0');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @existe := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'model_facture_dynamique_colonne'
       AND COLUMN_NAME = 'str_NIVEAU');
SET @sql := IF(@existe = 0,
    'ALTER TABLE `model_facture_dynamique_colonne` ADD COLUMN `str_NIVEAU` VARCHAR(10) NOT NULL DEFAULT ''BON''',
    'DO 0');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Securite : toute colonne existante sans niveau renseigne est une colonne de bon.
UPDATE `model_facture_dynamique_colonne` SET `str_NIVEAU` = 'BON'
 WHERE `str_NIVEAU` IS NULL OR `str_NIVEAU` = '';
