-- =====================================================================
-- Centre de Support - Detection des ecrans / requetes lents
-- ---------------------------------------------------------------------
-- Un filtre HTTP mesure le temps de traitement des requetes de donnees
-- (/api/*, /webservices/*, *.jsp) et cree un evenement PERFORMANCE (WARN)
-- pour celles qui depassent un seuil. Les appels au meme endpoint sont
-- dedupliques (occurrences), ce qui pointe l'ecran/traitement a optimiser.
--
-- Parametres :
--   SUPPORT_SLOWLOG_ENABLED : 1=actif, 0=inactif.
--   SUPPORT_SLOW_MS         : seuil en millisecondes (def. 5000 = 5 s).
-- =====================================================================

INSERT IGNORE INTO t_parameters (`str_KEY`, `str_VALUE`, `str_DESCRIPTION`, `str_TYPE`, `str_STATUT`, `str_IS_EN_KRYPTED`, `str_SECTION_KEY`, `dt_CREATED`, `dt_UPDATED`)
    VALUES ('SUPPORT_SLOWLOG_ENABLED', '1', 'Detection des requetes lentes (1=actif, 0=inactif)', 'CUSTOMER', 'enable', NULL, NULL, NOW(), NULL);

INSERT IGNORE INTO t_parameters (`str_KEY`, `str_VALUE`, `str_DESCRIPTION`, `str_TYPE`, `str_STATUT`, `str_IS_EN_KRYPTED`, `str_SECTION_KEY`, `dt_CREATED`, `dt_UPDATED`)
    VALUES ('SUPPORT_SLOW_MS', '5000', 'Seuil (ms) au-dela duquel une requete est consideree lente', 'CUSTOMER', 'enable', NULL, NULL, NOW(), NULL);
