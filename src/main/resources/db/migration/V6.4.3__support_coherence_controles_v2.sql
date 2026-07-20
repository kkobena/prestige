-- =====================================================================
-- Centre de Support - Veilleur de coherence : lot 2 de controles
-- ---------------------------------------------------------------------
-- Nouveaux controles pour les flux CAISSE, STOCK/LOTS, INVENTAIRE et
-- VALORISATION. Tous en LECTURE SEULE (SELECT), colonnes SQL verifiees
-- sur les entites JPA (t_inventaire, t_inventaire_famille, t_lot,
-- t_famille_stock, t_resume_caisse, ligne_resume_caisse, t_stock_snapshot,
-- stock_daily_value).
--
-- Tous ces controles demarrent en dry_run = 1 (rodage : journalises dans
-- Diagnostic & bugs, SANS ticket automatique) le temps de la calibration
-- sur les donnees reelles.
-- Le controle quantitatif STOCK_VS_LOTS (fort risque de faux positifs sur
-- les articles non geres par lots) demarre desactive (actif = 0).
--
-- Pour activer le mode ticket une fois un controle valide :
--   UPDATE t_support_controle SET dry_run = 0 WHERE code = '<CODE>';
-- Pour activer un controle desactive :
--   UPDATE t_support_controle SET actif = 1 WHERE code = '<CODE>';
-- =====================================================================

-- ---------------------------------------------------------------------
-- INVENTAIRE
-- ---------------------------------------------------------------------
-- Inventaire (en-tete) sans aucune ligne de comptage.
INSERT IGNORE INTO t_support_controle (id, created_at, modified_at, status, code, libelle, module, dry_run, actif, ordre, requete_sql)
VALUES ('sctrl-11', NOW(), NOW(), 'ENABLE', 'INVENTAIRE_SANS_LIGNE', 'Inventaire sans ligne de comptage', 'INVENTAIRE', 1, 1, 11,
'SELECT i.lg_INVENTAIRE_ID AS id, i.str_STATUT AS ctx FROM t_inventaire i LEFT JOIN t_inventaire_famille f ON f.lg_INVENTAIRE_ID = i.lg_INVENTAIRE_ID WHERE i.str_STATUT IN (''enable'',''is_Closed'') AND f.lg_INVENTAIRE_FAMILLE_ID IS NULL');

-- Ligne d'inventaire saisie (comptee) mais non rattachee a une ligne de stock.
INSERT IGNORE INTO t_support_controle (id, created_at, modified_at, status, code, libelle, module, dry_run, actif, ordre, requete_sql)
VALUES ('sctrl-12', NOW(), NOW(), 'ENABLE', 'INVENTAIRE_LIGNE_SANS_STOCK', 'Ligne d inventaire comptee sans ligne de stock associee', 'INVENTAIRE', 1, 1, 12,
'SELECT f.lg_INVENTAIRE_FAMILLE_ID AS id, f.lg_INVENTAIRE_ID AS ctx FROM t_inventaire_famille f WHERE f.bool_INVENTAIRE = 1 AND f.lg_FAMILLE_STOCK_ID IS NULL');

-- ---------------------------------------------------------------------
-- VALORISATION / SNAPSHOT (le job StockDailyScheduler tourne chaque jour a 00:05)
-- ---------------------------------------------------------------------
-- Valorisation quotidienne (stock_daily_value) absente pour la veille.
-- Renvoie une ligne de synthese uniquement quand la valeur du jour J-1 manque.
INSERT IGNORE INTO t_support_controle (id, created_at, modified_at, status, code, libelle, module, dry_run, actif, ordre, requete_sql)
VALUES ('sctrl-13', NOW(), NOW(), 'ENABLE', 'VALORISATION_JOUR_MANQUANT', 'Valorisation quotidienne du stock absente pour la veille', 'VALORISATION', 1, 1, 13,
'SELECT CAST(DATE_FORMAT(CURDATE() - INTERVAL 1 DAY, ''%Y%m%d'') AS UNSIGNED) AS id, ''valorisation quotidienne absente pour la veille'' AS ctx WHERE NOT EXISTS (SELECT 1 FROM stock_daily_value v WHERE v.id = CAST(DATE_FORMAT(CURDATE() - INTERVAL 1 DAY, ''%Y%m%d'') AS UNSIGNED))');

-- Snapshot de stock (t_stock_snapshot) absent pour la veille.
INSERT IGNORE INTO t_support_controle (id, created_at, modified_at, status, code, libelle, module, dry_run, actif, ordre, requete_sql)
VALUES ('sctrl-14', NOW(), NOW(), 'ENABLE', 'SNAPSHOT_JOUR_MANQUANT', 'Snapshot de stock absent pour la veille', 'VALORISATION', 1, 1, 14,
'SELECT DATE_FORMAT(CURDATE() - INTERVAL 1 DAY, ''%Y-%m-%d'') AS id, ''snapshot de stock absent pour la veille'' AS ctx WHERE NOT EXISTS (SELECT 1 FROM t_stock_snapshot s WHERE s.id = (CURDATE() - INTERVAL 1 DAY))');

-- ---------------------------------------------------------------------
-- STOCK / LOTS (peremption)
-- ---------------------------------------------------------------------
-- Lot marque perime mais dont le stock restant (current_stock) est encore positif :
-- ce stock perime reste comptabilise comme disponible.
INSERT IGNORE INTO t_support_controle (id, created_at, modified_at, status, code, libelle, module, dry_run, actif, ordre, requete_sql)
VALUES ('sctrl-15', NOW(), NOW(), 'ENABLE', 'LOT_PERIME_AVEC_STOCK', 'Lot perime avec du stock restant positif', 'STOCK', 1, 1, 15,
'SELECT l.lg_LOT_ID AS id, CONCAT(l.int_NUM_LOT, '' / reste '', l.current_stock) AS ctx FROM t_lot l WHERE l.str_STATUT = ''perime'' AND l.current_stock > 0');

-- Lot rattache a un article inexistant (FK famille orpheline ou nulle).
INSERT IGNORE INTO t_support_controle (id, created_at, modified_at, status, code, libelle, module, dry_run, actif, ordre, requete_sql)
VALUES ('sctrl-16', NOW(), NOW(), 'ENABLE', 'LOT_SANS_ARTICLE', 'Lot rattache a un article inexistant', 'STOCK', 1, 1, 16,
'SELECT l.lg_LOT_ID AS id, l.int_NUM_LOT AS ctx FROM t_lot l LEFT JOIN t_famille f ON f.lg_FAMILLE_ID = l.lg_FAMILLE_ID WHERE f.lg_FAMILLE_ID IS NULL');

-- Ecart entre le stock disponible de l'article (emplacement principal) et la
-- somme des quantites restantes de ses lots. DESACTIVE par defaut : tous les
-- articles ne sont pas geres par lots -> fort risque de faux positifs a calibrer.
INSERT IGNORE INTO t_support_controle (id, created_at, modified_at, status, code, libelle, module, dry_run, actif, ordre, requete_sql)
VALUES ('sctrl-17', NOW(), NOW(), 'ENABLE', 'STOCK_VS_LOTS', 'Ecart stock article / somme des lots (a calibrer)', 'STOCK', 1, 0, 17,
'SELECT s.lg_FAMILLE_ID AS id, CONCAT(''stock='', s.int_NUMBER_AVAILABLE, '' / lots='', t.som) AS ctx FROM t_famille_stock s JOIN (SELECT l.lg_FAMILLE_ID AS fid, SUM(l.current_stock) AS som FROM t_lot l WHERE l.current_stock > 0 GROUP BY l.lg_FAMILLE_ID) t ON t.fid = s.lg_FAMILLE_ID WHERE s.lg_EMPLACEMENT_ID = ''1'' AND s.str_STATUT = ''enable'' AND s.int_NUMBER_AVAILABLE <> t.som');

-- ---------------------------------------------------------------------
-- CAISSE
-- (Pas de FK t_recettes/t_reglement -> caisse : rapprochement quantitatif non
-- fiable. On se limite ici a un controle structurel d'orphelin.)
-- ---------------------------------------------------------------------
-- Journee de caisse passee sans aucune ligne de resume (session ouverte mais vide).
INSERT IGNORE INTO t_support_controle (id, created_at, modified_at, status, code, libelle, module, dry_run, actif, ordre, requete_sql)
VALUES ('sctrl-18', NOW(), NOW(), 'ENABLE', 'CAISSE_JOUR_SANS_LIGNE', 'Journee de caisse passee sans aucune ligne de resume', 'CAISSE', 1, 1, 18,
'SELECT r.ld_CAISSE_ID AS id, r.dt_DAY AS ctx FROM t_resume_caisse r LEFT JOIN ligne_resume_caisse lr ON lr.resume_caisse_id = r.ld_CAISSE_ID WHERE r.dt_DAY < CURDATE() AND lr.id IS NULL');
