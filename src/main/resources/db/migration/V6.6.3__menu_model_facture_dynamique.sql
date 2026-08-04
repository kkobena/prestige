-- =====================================================================
-- Menu "Createur de modeles de facture" (modeles dynamiques), a cote de
-- l'ecran "Modele facture" existant (composant 'modelfacture') qui reste
-- inchange. str_COMPOSANT = xtype ExtJS charge au clic = 'modelfacturedynamique'.
-- Se reconnecter apres execution : les privileges sont recharges a la
-- connexion (USER_LIST_PRIVILEGE).
-- =====================================================================

-- 1) Privilege du nouveau menu
INSERT IGNORE INTO t_privilege
    (`lg_PRIVELEGE_ID`, `str_NAME`, `str_TYPE`, `str_DESCRIPTION`, `lg_PRIVELEGE_ID_DEP`,
     `dt_CREATED`, `lg_CREATED_BY`, `dt_UPDATED`, `lg_UPDATED_BY`, `str_STATUT`)
VALUES
    ('20260802', 'P_SM_MODEL_FACTURE_DYNAMIQUE', 'CUSTOMER', 'Createur de modeles de facture',
     NULL, NOW(), NULL, NULL, NULL, 'enable');

-- 2) Sous-menu, rattache au meme menu que l'ecran "Modele facture"
INSERT IGNORE INTO t_sous_menu
    (`lg_SOUS_MENU_ID`, `str_VALUE`, `str_IMAGE_CSS`, `str_DESCRIPTION`, `str_COMPOSANT`,
     `lg_MENU_ID`, `int_PRIORITY`, `str_URL`, `str_Status`, `P_KEY`, `dt_CREATED`, `dt_UPDATED`, `icon_CLASS`)
SELECT '20260802', 'Createur de modeles de facture', NULL,
       'Createur de modeles de facture', 'modelfacturedynamique',
       m.lg_MENU_ID, 99, NULL, 'enable', 'P_SM_MODEL_FACTURE_DYNAMIQUE', NOW(), NULL, ''
FROM (
    SELECT sm.lg_MENU_ID
    FROM t_sous_menu sm
    WHERE sm.str_COMPOSANT = 'modelfacture'
    LIMIT 1
) m;

-- 3) Visibilite : memes roles que ceux qui ont deja acces a l'ecran "Modele facture"
INSERT INTO t_role_privelege
    (`lg_ROLE_PRIVILEGE`, `lg_ROLE_ID`, `lg_PRIVILEGE_ID`, `dt_CREATED`, `dt_UPDATED`)
SELECT DISTINCT LEFT(UUID(), 40), rp.lg_ROLE_ID, '20260802', NOW(), NOW()
FROM t_role_privelege rp
JOIN t_privilege p  ON rp.lg_PRIVILEGE_ID = p.lg_PRIVELEGE_ID
JOIN t_sous_menu sm ON sm.P_KEY = p.str_NAME
WHERE sm.str_COMPOSANT = 'modelfacture'
  AND rp.lg_ROLE_ID NOT IN (
      SELECT rp2.lg_ROLE_ID FROM t_role_privelege rp2 WHERE rp2.lg_PRIVILEGE_ID = '20260802'
  );
