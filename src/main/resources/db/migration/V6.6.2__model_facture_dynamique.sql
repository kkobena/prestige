-- =====================================================================
-- Createur de modeles de facture : modeles dynamiques definis par
-- l'utilisateur (colonnes a afficher, libelles, ordre, tri), independants
-- des modeles Jasper historiques (t_model_facture) qui restent inchanges.
-- Un tiers payant rattache a un modele dynamique voit sa facture editee
-- par le nouveau moteur ; les autres gardent le circuit Jasper existant.
-- =====================================================================

CREATE TABLE IF NOT EXISTS `model_facture_dynamique` (
    `id`              INT(11)      NOT NULL AUTO_INCREMENT,
    `str_NOM`         VARCHAR(100) NOT NULL,
    `str_DESCRIPTION` VARCHAR(255) NULL,
    `str_MODE_TRI`    VARCHAR(30)  NOT NULL DEFAULT 'TIERS_PAYANT',
    `str_STATUT`      VARCHAR(20)  NOT NULL DEFAULT 'enable',
    `dt_CREATED`      DATETIME     NULL,
    `dt_UPDATED`      DATETIME     NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `model_facture_dynamique_colonne` (
    `id`          INT(11)      NOT NULL AUTO_INCREMENT,
    `model_id`    INT(11)      NOT NULL,
    `str_CHAMP`   VARCHAR(50)  NOT NULL,
    `str_LIBELLE` VARCHAR(100) NOT NULL,
    `int_ORDRE`   INT(11)      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_mfd_colonne_model` (`model_id`),
    CONSTRAINT `fk_mfd_colonne_model` FOREIGN KEY (`model_id`)
        REFERENCES `model_facture_dynamique` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

-- Rattachement optionnel d'un tiers payant a un modele dynamique
-- (NULL = circuit Jasper historique inchange).
ALTER TABLE `t_tiers_payant`
    ADD COLUMN IF NOT EXISTS `model_facture_dynamique_id` INT(11) NULL DEFAULT NULL;
