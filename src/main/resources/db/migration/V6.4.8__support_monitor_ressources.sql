-- =====================================================================
-- Centre de Support - Surveillance proactive des ressources
-- ---------------------------------------------------------------------
-- Un moniteur verifie toutes les ~5 min la memoire JVM et l'espace disque
-- et cree une alerte AVANT le blocage (OutOfMemory / disque plein). Au plus
-- une alerte par ressource et par jour (anti-bruit).
--
-- Parametres :
--   SUPPORT_MONITOR_ENABLED  : 1=actif, 0=inactif.
--   SUPPORT_MEM_THRESHOLD_PCT: seuil d'alerte memoire JVM (% utilise), def. 85.
--                              (alerte si depasse de facon soutenue)
--   SUPPORT_DISK_MIN_MB      : espace disque libre minimal (Mo), def. 1024.
-- =====================================================================

INSERT IGNORE INTO t_parameters (`str_KEY`, `str_VALUE`, `str_DESCRIPTION`, `str_TYPE`, `str_STATUT`, `str_IS_EN_KRYPTED`, `str_SECTION_KEY`, `dt_CREATED`, `dt_UPDATED`)
    VALUES ('SUPPORT_MONITOR_ENABLED', '1', 'Surveillance proactive des ressources (1=actif, 0=inactif)', 'CUSTOMER', 'enable', NULL, NULL, NOW(), NULL);

INSERT IGNORE INTO t_parameters (`str_KEY`, `str_VALUE`, `str_DESCRIPTION`, `str_TYPE`, `str_STATUT`, `str_IS_EN_KRYPTED`, `str_SECTION_KEY`, `dt_CREATED`, `dt_UPDATED`)
    VALUES ('SUPPORT_MEM_THRESHOLD_PCT', '85', 'Seuil d alerte memoire JVM en pourcentage utilise', 'CUSTOMER', 'enable', NULL, NULL, NOW(), NULL);

INSERT IGNORE INTO t_parameters (`str_KEY`, `str_VALUE`, `str_DESCRIPTION`, `str_TYPE`, `str_STATUT`, `str_IS_EN_KRYPTED`, `str_SECTION_KEY`, `dt_CREATED`, `dt_UPDATED`)
    VALUES ('SUPPORT_DISK_MIN_MB', '1024', 'Espace disque libre minimal en Mo avant alerte', 'CUSTOMER', 'enable', NULL, NULL, NOW(), NULL);
