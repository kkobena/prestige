-- =====================================================================
-- Centre de Support - Surveillance des jobs planifies
-- ---------------------------------------------------------------------
-- t_support_job : catalogue parametrable de "controles de fraicheur". Pour
-- chaque job, une requete SELECT renvoie la DATE/HEURE de son dernier
-- passage (via son artefact, ou via t_support_job_run pour les jobs du
-- support qui s'auto-declarent). Si ce dernier passage est plus vieux que
-- max_age_minutes (ou introuvable), le moniteur cree une alerte "job en
-- retard".
--
-- t_support_job_run : dernier passage auto-declare par les jobs du support.
-- =====================================================================

CREATE TABLE IF NOT EXISTS `t_support_job` (
    `id` VARCHAR(50) NOT NULL,
    `code` VARCHAR(60) NOT NULL,
    `libelle` VARCHAR(255) NOT NULL,
    `requete_sql` TEXT NOT NULL,
    `max_age_minutes` INT NOT NULL DEFAULT 1800,
    `actif` TINYINT(1) NOT NULL DEFAULT 1,
    `ordre` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_support_job_code` (`code`)
) COLLATE='utf8_general_ci' ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `t_support_job_run` (
    `code` VARCHAR(60) NOT NULL,
    `last_run_at` DATETIME NOT NULL,
    `hostname` VARCHAR(255) NULL,
    PRIMARY KEY (`code`)
) COLLATE='utf8_general_ci' ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Jobs surveilles (dernier passage via artefact ou auto-declaration)
-- max_age_minutes = 1800 (30 h) : tolere le cycle quotidien, alerte si un jour est saute.
-- ---------------------------------------------------------------------
INSERT IGNORE INTO t_support_job (id, code, libelle, requete_sql, max_age_minutes, actif, ordre)
VALUES ('sjob-01', 'VALORISATION_QUOTIDIENNE', 'Valorisation quotidienne du stock (00:05)',
'SELECT STR_TO_DATE(CAST(MAX(id) AS CHAR), ''%Y%m%d'') FROM stock_daily_value', 1800, 1, 1);

INSERT IGNORE INTO t_support_job (id, code, libelle, requete_sql, max_age_minutes, actif, ordre)
VALUES ('sjob-02', 'SNAPSHOT_STOCK', 'Snapshot de stock quotidien',
'SELECT MAX(id) FROM t_stock_snapshot', 1800, 1, 2);

INSERT IGNORE INTO t_support_job (id, code, libelle, requete_sql, max_age_minutes, actif, ordre)
VALUES ('sjob-03', 'PURGE_SUPPORT', 'Purge quotidienne du Centre de Support (03:45)',
'SELECT last_run_at FROM t_support_job_run WHERE code = ''PURGE_SUPPORT''', 1800, 1, 3);

INSERT IGNORE INTO t_support_job (id, code, libelle, requete_sql, max_age_minutes, actif, ordre)
VALUES ('sjob-04', 'COHERENCE_SUPPORT', 'Veilleur de coherence quotidien (04:15)',
'SELECT last_run_at FROM t_support_job_run WHERE code = ''COHERENCE_SUPPORT''', 1800, 1, 4);

-- ---------------------------------------------------------------------
-- Peremption des lots : detecte indirectement que le job de peremption n'a
-- pas tourne (lots perimes encore en stock et non marques). Controle de
-- coherence classique (dry_run), en complement du moniteur de jobs.
-- ---------------------------------------------------------------------
INSERT IGNORE INTO t_support_controle (id, created_at, modified_at, status, code, libelle, module, dry_run, actif, ordre, requete_sql)
VALUES ('sctrl-19', NOW(), NOW(), 'ENABLE', 'LOT_PERIME_NON_MARQUE', 'Lot perime en stock non marque (job de peremption ?)', 'STOCK', 1, 1, 19,
'SELECT l.lg_LOT_ID AS id, CONCAT(''Lot '', COALESCE(l.int_NUM_LOT,''?''), '' perime le '', COALESCE(DATE(l.dt_PEREMPTION),''?''), '' - reste '', l.current_stock) AS ctx FROM t_lot l WHERE l.dt_PEREMPTION IS NOT NULL AND l.dt_PEREMPTION < CURDATE() AND l.current_stock > 0 AND l.str_STATUT NOT IN (''perime'',''is_Closed'')');
