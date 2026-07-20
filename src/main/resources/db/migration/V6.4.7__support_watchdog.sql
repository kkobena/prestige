-- =====================================================================
-- Centre de Support - Watchdog crash serveur
-- ---------------------------------------------------------------------
-- Detection post-mortem d'un arret non propre du serveur d'application :
-- un fichier temoin (heartbeat) est rafraichi toutes les ~2 min et
-- supprime a l'arret propre. Si, au demarrage, le fichier est encore
-- present, c'est que le serveur a plante (kill, coupure, OutOfMemory...).
-- On cree alors un incident avec la duree d'indisponibilite estimee et la
-- cause probable (extrait du server.log + eventuel hs_err_pid).
--
-- Parametres :
--   SUPPORT_WATCHDOG_ENABLED : 1=actif, 0=inactif.
--   SUPPORT_SERVER_LOG_PATH  : dossier des logs du serveur (server.log)
--                              pour l'analyse de la cause du crash.
-- =====================================================================

INSERT IGNORE INTO t_parameters (`str_KEY`, `str_VALUE`, `str_DESCRIPTION`, `str_TYPE`, `str_STATUT`, `str_IS_EN_KRYPTED`, `str_SECTION_KEY`, `dt_CREATED`, `dt_UPDATED`)
    VALUES ('SUPPORT_WATCHDOG_ENABLED', '1', 'Detection de crash du serveur d application (1=actif, 0=inactif)', 'CUSTOMER', 'enable', NULL, NULL, NOW(), NULL);

INSERT IGNORE INTO t_parameters (`str_KEY`, `str_VALUE`, `str_DESCRIPTION`, `str_TYPE`, `str_STATUT`, `str_IS_EN_KRYPTED`, `str_SECTION_KEY`, `dt_CREATED`, `dt_UPDATED`)
    VALUES ('SUPPORT_SERVER_LOG_PATH', 'D:\\payara5\\payara5\\glassfish\\domains\\domain1\\logs', 'Dossier des logs du serveur (server.log) pour l analyse post-crash', 'CUSTOMER', 'enable', NULL, NULL, NOW(), NULL);
