-- =====================================================================
-- Centre de Support - Veilleur de coherence : desactivation du controle
-- tiers-payant (premisse incorrecte).
-- ---------------------------------------------------------------------
-- Le controle TIERS_PAYANT_MAUVAIS_COMPTE supposait que le compte tiers-
-- payant devait appartenir au client de la vente (ou a son ayant-droit).
-- C'est faux : en tiers-payant, le compte appartient a l'ORGANISME
-- (assureur / mutuelle), pas au patient. Le controle produit donc des
-- faux positifs sur toutes les ventes tiers-payant normales.
--
-- On le desactive (actif = 0). Le controle DIFFERE_MAUVAIS_COMPTE (compte
-- credit personnel, qui DOIT appartenir au client / ayant-droit) reste actif.
-- =====================================================================

UPDATE t_support_controle SET actif = 0, modified_at = NOW()
 WHERE code = 'TIERS_PAYANT_MAUVAIS_COMPTE';
