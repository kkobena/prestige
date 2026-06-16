-- Tracabilite des avoirs client (avoir en cours / avoir cloture)
-- Les flags existants (b_IS_AVOIR, int_AVOIR) gardent leur comportement :
-- ils representent l'etat COURANT d'un avoir et sont remis a zero a la cloture.
-- Les colonnes ci-dessous conservent l'HISTORIQUE de l'avoir afin de pouvoir
-- distinguer un avoir cloture d'une vente normale, et de retrouver les produits
-- et quantites qui etaient en avoir.

-- En-tete de vente : marqueur d'historique + date de cloture de l'avoir
ALTER TABLE t_preenregistrement
    ADD COLUMN b_HAS_AVOIR BIT(1) NOT NULL DEFAULT 0,
    ADD COLUMN dt_CLOTURE_AVOIR DATETIME NULL;

-- Detail de vente : quantite initialement mise en avoir (jamais remise a zero)
ALTER TABLE t_preenregistrement_detail
    ADD COLUMN int_AVOIR_INITIAL INT NOT NULL DEFAULT 0;

-- Initialisation : les avoirs actuellement ouverts deviennent historises
UPDATE t_preenregistrement SET b_HAS_AVOIR = 1 WHERE b_IS_AVOIR = 1;
UPDATE t_preenregistrement_detail SET int_AVOIR_INITIAL = int_AVOIR WHERE int_AVOIR > 0;

-- Index pour le filtre de l'onglet "Avoir cloture" (filtre sur la date de cloture)
CREATE INDEX idx_preenr_cloture_avoir ON t_preenregistrement (b_HAS_AVOIR, dt_CLOTURE_AVOIR);
