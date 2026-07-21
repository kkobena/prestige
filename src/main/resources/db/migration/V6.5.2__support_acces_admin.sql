-- =====================================================================
-- Centre de Support - Restriction d'acces aux profils Administrateur
-- ---------------------------------------------------------------------
-- Le menu "Centre de Support" (et ses sous-menus) ne doit etre visible
-- que par les profils Administrateur et Super Administrateur.
--
-- A l'origine (V6.3.9 / V6.4.0) les privileges n'etaient attribues qu'au
-- role systeme '00' (SYSTEM_USER, desactive). On ajoute ici l'attribution
-- aux roles Administrateur et Super Administrateur reels de l'officine.
--
-- Les profils sont identifies par prefixe de nom (str_NAME ou
-- str_DESIGNATION) afin de couvrir les variantes de nommage :
--   "Administrateur", "Administrateur du systeme",
--   "Super Administrateur", "Super Administrateur du systeme"...
--
-- Insertion idempotente (NOT EXISTS) : rejouable sans doublon.
-- La visibilite pour les autres profils reste inchangee (non attribuee).
-- =====================================================================

INSERT INTO t_role_privelege (`lg_ROLE_PRIVILEGE`, `lg_ROLE_ID`, `lg_PRIVILEGE_ID`, `dt_CREATED`, `dt_UPDATED`)
SELECT LEFT(UUID(), 40), r.lg_ROLE_ID, p.lg_PRIVELEGE_ID, NOW(), NOW()
FROM t_role r
JOIN t_privilege p
  ON p.str_NAME IN (
        'P_M_CENTRE_SUPPORT',
        'P_SM_SUPPORT_CONTACT',
        'P_SM_SUPPORT_TICKETS',
        'P_SM_SUPPORT_DIAGNOSTIC',
        'P_SM_SUPPORT_SANTE',
        'P_SM_SUPPORT_HISTORIQUE'
     )
WHERE r.str_STATUT = 'enable'
  AND (
        r.str_NAME LIKE 'Administrateur%'
        OR r.str_NAME LIKE 'Super Administrateur%'
        OR r.str_DESIGNATION LIKE 'Administrateur%'
        OR r.str_DESIGNATION LIKE 'Super Administrateur%'
      )
  AND NOT EXISTS (
        SELECT 1 FROM t_role_privelege rp
         WHERE rp.lg_ROLE_ID = r.lg_ROLE_ID
           AND rp.lg_PRIVILEGE_ID = p.lg_PRIVELEGE_ID
      );
