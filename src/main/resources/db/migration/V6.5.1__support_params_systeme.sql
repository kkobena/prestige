-- =====================================================================
-- Centre de Support - Masquage des parametres techniques
-- ---------------------------------------------------------------------
-- Les parametres de configuration du support / supervision sont des
-- reglages techniques. On les bascule en str_TYPE = 'SYSTEME' pour qu'ils
-- ne soient plus visibles dans l'ecran "Parametres" (visibles uniquement
-- en base). Les valeurs ne sont PAS modifiees, seul le type de visibilite
-- change : aucun impact fonctionnel, aucune regression.
--
-- Rappel visibilite (webservices/sm_user/parameter/ws_data_all.jsp) :
--   - str_TYPE 'CUSTOMER' : visible par tous
--   - str_TYPE 'ADMIN'    : visible par les profils de type ADMIN
--   - str_TYPE 'SYSTEME'  : non liste dans l'ecran (base uniquement)
-- =====================================================================

UPDATE t_parameters
   SET str_TYPE = 'SYSTEME',
       dt_UPDATED = NOW()
 WHERE str_KEY IN (
        'SUPPORT_EMAIL',
        'SUPPORT_STORAGE_DIR',
        'SUPPORT_RETENTION_INFO',
        'SUPPORT_RETENTION_WARN',
        'SUPPORT_RETENTION_ERROR',
        'SUPPORT_RETENTION_FATAL',
        'SUPPORT_NOTIFY_ENABLED',
        'SUPPORT_AUTO_TICKET_ENABLED',
        'SUPPORT_AUTO_TICKET_SEUIL',
        'SUPPORT_WATCHDOG_ENABLED',
        'SUPPORT_SERVER_LOG_PATH',
        'SUPPORT_MONITOR_ENABLED',
        'SUPPORT_MEM_THRESHOLD_PCT',
        'SUPPORT_DISK_MIN_MB',
        'SUPPORT_SLOWLOG_ENABLED',
        'SUPPORT_SLOW_MS'
   )
   AND str_TYPE <> 'SYSTEME';
