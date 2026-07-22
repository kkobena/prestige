-- =====================================================================
-- Centre de Support - Occurrences detaillees + sous-menu Maintenance
-- ---------------------------------------------------------------------
-- 1) t_application_event_occurrence : dates/heures individuelles des
--    occurrences d'un evenement (niveaux ERROR et FATAL uniquement,
--    plafond applicatif de 100 lignes par evenement), avec pour chaque
--    occurrence QUI l'a constatee (utilisateur + IP + nom du poste, colonne
--    constate_par). Les WARN/INFO restent dedupliques sans historique.
-- 2) Sous-menu "Maintenance" du Centre de Support : actions de vidage
--    (etiquettes, suggestions, commandes en cours...), reserve aux
--    profils Administrateur / Super Administrateur (+ role systeme '00').
-- =====================================================================

CREATE TABLE IF NOT EXISTS `t_application_event_occurrence` (
    `id` VARCHAR(50) NOT NULL,
    `event_id` VARCHAR(50) NOT NULL,
    `seen_at` DATETIME NOT NULL,
    `constate_par` VARCHAR(255) NULL,
    PRIMARY KEY (`id`),
    KEY `idx_event_occurrence_event` (`event_id`),
    KEY `idx_event_occurrence_seen` (`seen_at`)
) COLLATE='utf8_general_ci' ENGINE=InnoDB;

-- 2) Sous-menu Maintenance (menu Centre de Support 20260710)
INSERT IGNORE INTO t_sous_menu (`lg_SOUS_MENU_ID`, `str_VALUE`, `str_IMAGE_CSS`, `str_DESCRIPTION`, `str_COMPOSANT`, `lg_MENU_ID`, `int_PRIORITY`, `str_URL`, `str_Status`, `P_KEY`, `dt_CREATED`, `dt_UPDATED`, `icon_CLASS`)
    VALUES ('202607106', 'Maintenance', NULL, 'Maintenance', 'supportmaintenance', '20260710', 6, NULL, 'enable', 'P_SM_SUPPORT_MAINTENANCE', NOW(), NULL, '');

INSERT IGNORE INTO t_privilege (`lg_PRIVELEGE_ID`, `str_NAME`, `str_TYPE`, `str_DESCRIPTION`, `lg_PRIVELEGE_ID_DEP`, `dt_CREATED`, `lg_CREATED_BY`, `dt_UPDATED`, `lg_UPDATED_BY`, `str_STATUT`)
    VALUES ('202607106', 'P_SM_SUPPORT_MAINTENANCE', 'CUSTOMER', 'CENTRE DE SUPPORT - MAINTENANCE', NULL, NOW(), NULL, NULL, NULL, 'enable');

-- Visibilite : role systeme '00'
INSERT INTO t_role_privelege (`lg_ROLE_PRIVILEGE`, `lg_ROLE_ID`, `lg_PRIVILEGE_ID`, `dt_CREATED`, `dt_UPDATED`)
SELECT LEFT(UUID(), 40), '00', p.lg_PRIVELEGE_ID, NOW(), NOW()
FROM t_privilege p
WHERE p.str_NAME = 'P_SM_SUPPORT_MAINTENANCE'
  AND NOT EXISTS (
      SELECT 1 FROM t_role_privelege rp
       WHERE rp.lg_ROLE_ID = '00' AND rp.lg_PRIVILEGE_ID = p.lg_PRIVELEGE_ID
  );

-- Visibilite : profils Administrateur / Super Administrateur (detection par prefixe de nom)
INSERT INTO t_role_privelege (`lg_ROLE_PRIVILEGE`, `lg_ROLE_ID`, `lg_PRIVILEGE_ID`, `dt_CREATED`, `dt_UPDATED`)
SELECT LEFT(UUID(), 40), r.lg_ROLE_ID, p.lg_PRIVELEGE_ID, NOW(), NOW()
FROM t_role r
JOIN t_privilege p ON p.str_NAME = 'P_SM_SUPPORT_MAINTENANCE'
WHERE r.str_STATUT = 'enable'
  AND (
        r.str_NAME LIKE 'Administrateur%'
        OR r.str_NAME LIKE 'Super Administrateur%'
        OR r.str_DESIGNATION LIKE 'Administrateur%'
        OR r.str_DESIGNATION LIKE 'Super Administrateur%'
      )
  AND NOT EXISTS (
      SELECT 1 FROM t_role_privelege rp
       WHERE rp.lg_ROLE_ID = r.lg_ROLE_ID AND rp.lg_PRIVILEGE_ID = p.lg_PRIVELEGE_ID
  );
