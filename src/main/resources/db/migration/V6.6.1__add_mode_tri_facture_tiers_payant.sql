-- Champ tri de la fiche tiers payant : ordre des lignes lors de la generation de facture
-- (ALPHABETIQUE = nom du client, DATE_BON = date du bon / de l'operation).
ALTER TABLE `t_tiers_payant`
    ADD COLUMN IF NOT EXISTS `str_MODE_TRI_FACTURE` VARCHAR(30) NOT NULL DEFAULT 'ALPHABETIQUE';
