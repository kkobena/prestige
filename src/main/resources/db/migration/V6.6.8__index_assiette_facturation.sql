-- Index de lecture pour la recherche des bons a facturer (ecran Generer facture).
--
-- La requete part des bons NON FACTURES de la periode, puis remonte aux tiers payants.
-- Un index sur le seul statut de facturation (V6.6.5) ne suffit pas : la base doit
-- ensuite verifier le statut du bon et rejoindre la vente. Cet index composite permet
-- de filtrer les deux statuts d'un coup et de suivre directement la jointure.
--
-- Aucune donnee modifiee : un index n'accelere qu'une lecture.
-- INPLACE / LOCK=NONE : les ventes restent possibles pendant la creation.
-- Bloc garde et rejouable (aucun echec si l'index existe deja).

SET @existe := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 't_preenregistrement_compte_client_tiers_payent'
       AND INDEX_NAME = 'ix_pcctp_statuts_preenreg');
SET @sql := IF(@existe = 0,
    'ALTER TABLE `t_preenregistrement_compte_client_tiers_payent`
        ADD INDEX `ix_pcctp_statuts_preenreg` (`str_STATUT_FACTURE`, `str_STATUT`, `lg_PREENREGISTREMENT_ID`),
        ALGORITHM=INPLACE, LOCK=NONE',
    'DO 0');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
