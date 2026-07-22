-- =====================================================================
-- Centre de Support - Veilleur de coherence : passage en mode observation
-- ---------------------------------------------------------------------
-- Retour d'experience du premier deploiement :
--   * Un controle en erreur condamnait la transaction partagee et faisait
--     "cascader" l'erreur sur les controles suivants. Corrige cote code :
--     chaque controle s'execute desormais dans SA PROPRE transaction
--     (REQUIRES_NEW), l'erreur d'un controle n'impacte plus les autres.
--   * COMMANDE_SANS_DETAIL remonte ~500 cas ; en mode "ticket auto" cela
--     creait un ticket de synthese massif.
--
-- Par prudence, on bascule TOUS les controles d'orphelins (en-tete sans
-- ligne) en mode OBSERVATION (dry_run = 1) : ils sont journalises dans
-- Diagnostic & bugs SANS creer de ticket automatique. On calibre d'abord
-- les volumes reels (kit docs/centre-support/calibration_coherence.sql),
-- puis on repasse en mode ticket, controle par controle, une fois valides :
--   UPDATE t_support_controle SET dry_run = 0 WHERE code = '<CODE>';
-- =====================================================================

UPDATE t_support_controle SET dry_run = 1, modified_at = NOW()
 WHERE code IN (
    'BL_SANS_DETAIL',
    'FACTURE_SANS_DETAIL',
    'SUGGESTION_SANS_DETAIL',
    'COMMANDE_SANS_DETAIL',
    'DOSSIER_REGLEMENT_SANS_DETAIL'
 );
