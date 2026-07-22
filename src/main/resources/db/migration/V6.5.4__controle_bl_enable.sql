-- =====================================================================
-- Controle de coherence : bons de livraison bloques en statut 'enable'
-- ---------------------------------------------------------------------
-- Cycle de vie normal d'un BL :
--   1. creation (saisie / import)      -> BL 'enable' ; la COMMANDE passe
--      deja a 'is_Closed' a cette etape (voir creerBonLivraison) ;
--   2. controle / reception            -> BL toujours 'enable' (normal) ;
--   3. cloture (entree en stock)       -> BL 'is_Closed'.
-- Un BL 'enable' est donc NORMAL tant que la reception est en cours.
-- Il devient anormal quand il reste 'enable' au-dela d'un delai raisonnable
-- (reception jamais validee : oubli, ecran plante, cloture echouee,
-- doublon d'import, crash). Seuil retenu : 2 jours.
-- NB : le statut de la commande n'est PAS un critere d'anomalie (elle est
-- cloturee des la creation du BL) ; il est affiche en contexte seulement.
-- Controle en DRY-RUN (detection seule) : alimente le journal du Centre de
-- Support avec la reference du BL, le statut de la commande et la date de
-- creation, sans rien modifier.
-- =====================================================================

INSERT IGNORE INTO t_support_controle (id, created_at, modified_at, status, code, libelle, module, dry_run, actif, ordre, requete_sql)
VALUES ('sctrl-20', NOW(), NOW(), 'ENABLE', 'BL_BLOQUE_ENABLE', 'Bon de livraison en attente de cloture depuis plus de 2 jours', 'COMMANDE', 1, 1, 20,
'SELECT b.lg_BON_LIVRAISON_ID AS id, CONCAT(''BL '', IFNULL(b.str_REF_LIVRAISON, ''?''), '' | commande: '', IFNULL(o.str_STATUT, ''absente''), '' | cree le: '', IFNULL(DATE_FORMAT(b.dt_CREATED, ''%d/%m/%Y %H:%i''), ''?'')) AS ctx FROM t_bon_livraison b LEFT JOIN t_order o ON o.lg_ORDER_ID = b.lg_ORDER_ID WHERE b.str_STATUT = ''enable'' AND b.dt_CREATED < DATE_SUB(NOW(), INTERVAL 2 DAY)');
