-- =====================================================================
-- Centre de Support - Notification sur incident critique
-- ---------------------------------------------------------------------
-- Envoi d'un e-mail au support (SUPPORT_EMAIL) a chaque creation d'un
-- ticket AUTOMATIQUE (erreur FATAL / seuil d'occurrences atteint /
-- anomalie de coherence). Permet a l'editeur d'etre prevenu sans se
-- connecter. Desactivable via ce parametre.
-- =====================================================================

INSERT IGNORE INTO t_parameters (`str_KEY`, `str_VALUE`, `str_DESCRIPTION`, `str_TYPE`, `str_STATUT`, `str_IS_EN_KRYPTED`, `str_SECTION_KEY`, `dt_CREATED`, `dt_UPDATED`)
    VALUES ('SUPPORT_NOTIFY_ENABLED', '1', 'Notifier le support par e-mail a chaque ticket automatique (1=actif, 0=inactif)', 'CUSTOMER', 'enable', NULL, NULL, NOW(), NULL);
