-- Flyway Migration Script for MariaDB
-- Version: 5.9.3
-- Description: Crée une vue et des procédures stockées pour l'analyse 20/80 des ventes.

-- =================================================================================
-- PARTIE 1: Création de la vue v_ventes_par_produit
-- Cette vue pré-calcule les totaux de ventes par produit, jour et emplacement.
-- Elle inclut maintenant les données nécessaires au calcul de la marge.
-- =================================================================================

CREATE OR REPLACE VIEW v_ventes_par_produit AS
SELECT
    CAST(p.dt_UPDATED AS DATE) AS sale_date,
    u.lg_EMPLACEMENT_ID,
    f.lg_FAMILLE_ID,
    f.int_CIP,
    f.str_NAME AS product_name,
    fa.lg_FAMILLEARTICLE_ID,
    fa.str_LIBELLE AS article_family,
    z.lg_ZONE_GEO_ID,
    z.str_LIBELLEE AS rayon,
    g.lg_GROSSISTE_ID,
    g.str_LIBELLE AS grossiste,
    SUM(pd.int_QUANTITY) AS total_quantity,
    SUM(pd.int_PRICE) AS total_price_ttc,
    SUM(pd.int_PRICE - pd.int_PRICEREMISE - pd.montant_Tva) AS total_price_ht,
    SUM(pd.prix_Achat * pd.int_QUANTITY) AS total_cost_price
FROM
    t_preenregistrement_detail pd
JOIN
    t_preenregistrement p ON pd.lg_PREENREGISTREMENT_ID = p.lg_PREENREGISTREMENT_ID
JOIN
    t_famille f ON pd.lg_FAMILLE_ID = f.lg_FAMILLE_ID
JOIN
    t_user u ON p.lg_USER_ID = u.lg_USER_ID
LEFT JOIN
    t_famillearticle fa ON f.lg_FAMILLEARTICLE_ID = fa.lg_FAMILLEARTICLE_ID
LEFT JOIN
    t_zone_geographique z ON f.lg_ZONE_GEO_ID = z.lg_ZONE_GEO_ID
LEFT JOIN
    t_grossiste g ON f.lg_GROSSISTE_ID = g.lg_GROSSISTE_ID
WHERE
    p.str_STATUT = 'close'
    AND p.b_IS_CANCEL = 0
    AND p.int_PRICE > 0
    AND p.lg_TYPE_VENTE_ID <> (SELECT tv.lg_TYPE_VENTE_ID FROM t_type_vente tv WHERE tv.str_KEY = 'DEPOT_EXTENSION' LIMIT 1)
GROUP BY
    CAST(p.dt_UPDATED AS DATE),
    u.lg_EMPLACEMENT_ID,
    f.lg_FAMILLE_ID,
    f.int_CIP,
    f.str_NAME,
    fa.lg_FAMILLEARTICLE_ID,
    fa.str_LIBELLE,
    z.lg_ZONE_GEO_ID,
    z.str_LIBELLEE,
    g.lg_GROSSISTE_ID,
    g.str_LIBELLE;


-- =================================================================================
-- PARTIE 2: Création des procédures stockées pour l'analyse 20/80
-- La syntaxe est adaptée pour MariaDB.
-- =================================================================================

-- Le délimiteur est changé pour permettre l'utilisation de ';' à l'intérieur des procédures.
DELIMITER $$

-- ---------------------------------------------------------------------------------
-- PROCÉDURE 1: Analyse 20/80 basée sur la QUANTITÉ VENDUE
-- ---------------------------------------------------------------------------------
CREATE PROCEDURE analyse_20_80_par_quantite(
    IN p_dt_start DATE,
    IN p_dt_end DATE,
    IN p_emplacement_id VARCHAR(50),
    IN p_code_famille VARCHAR(50),
    IN p_code_rayon VARCHAR(50),
    IN p_code_grossiste VARCHAR(50)
)
BEGIN
    WITH VentesFiltrees AS (
        SELECT
            v.lg_FAMILLE_ID,
            v.product_name,
            v.article_family,
            v.int_CIP,
            v.lg_GROSSISTE_ID,
            SUM(v.total_quantity) AS agg_total_quantity,
            SUM(v.total_price_ttc) AS agg_total_price_ttc,
            (SUM(v.total_price_ht) - SUM(v.total_cost_price)) as agg_marge
        FROM
            v_ventes_par_produit v
        WHERE
            v.sale_date BETWEEN p_dt_start AND p_dt_end
            AND v.lg_EMPLACEMENT_ID = p_emplacement_id
            AND (p_code_famille IS NULL OR p_code_famille = '' OR v.lg_FAMILLEARTICLE_ID = p_code_famille)
            AND (p_code_rayon IS NULL OR p_code_rayon = '' OR v.lg_ZONE_GEO_ID = p_code_rayon)
            AND (p_code_grossiste IS NULL OR p_code_grossiste = '' OR v.lg_GROSSISTE_ID = p_code_grossiste)
        GROUP BY
            v.lg_FAMILLE_ID, v.product_name, v.article_family, v.int_CIP, v.lg_GROSSISTE_ID
    ),
    AnalyseCumulative AS (
        SELECT
            vf.*,
            SUM(vf.agg_total_quantity) OVER () AS grand_total_quantity,
            SUM(vf.agg_total_quantity) OVER (ORDER BY vf.agg_total_quantity DESC ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS cumulative_quantity
        FROM
            VentesFiltrees vf
    )
    SELECT
        ac.int_CIP,
        ac.product_name,
        ac.agg_total_price_ttc AS total_price_ttc,
        ac.agg_total_quantity AS total_quantity,
        ac.lg_FAMILLE_ID,
        ac.lg_GROSSISTE_ID,
        ac.article_family,
        ac.agg_marge AS marge,
        (SELECT fs.int_NUMBER_AVAILABLE FROM t_famille_stock fs
         WHERE fs.lg_FAMILLE_ID = ac.lg_FAMILLE_ID
         AND fs.lg_EMPLACEMENT_ID = p_emplacement_id
         AND fs.str_STATUT = 'enable'
         LIMIT 1) AS stock_disponible
    FROM
        AnalyseCumulative ac
    WHERE
        ac.cumulative_quantity <= (ac.grand_total_quantity * 0.8)
    ORDER BY
        ac.agg_total_quantity DESC;
END$$

-- ---------------------------------------------------------------------------------
-- PROCÉDURE 2: Analyse 20/80 basée sur le CHIFFRE D'AFFAIRES
-- ---------------------------------------------------------------------------------
CREATE PROCEDURE analyse_20_80_par_ca(
    IN p_dt_start DATE,
    IN p_dt_end DATE,
    IN p_emplacement_id VARCHAR(50),
    IN p_code_famille VARCHAR(50),
    IN p_code_rayon VARCHAR(50),
    IN p_code_grossiste VARCHAR(50)
)
BEGIN
    WITH VentesFiltrees AS (
        SELECT
            v.lg_FAMILLE_ID,
            v.product_name,
            v.article_family,
            v.int_CIP,
            v.lg_GROSSISTE_ID,
            SUM(v.total_quantity) AS agg_total_quantity,
            SUM(v.total_price_ttc) AS agg_total_price_ttc,
            (SUM(v.total_price_ht) - SUM(v.total_cost_price)) as agg_marge
        FROM
            v_ventes_par_produit v
        WHERE
            v.sale_date BETWEEN p_dt_start AND p_dt_end
            AND v.lg_EMPLACEMENT_ID = p_emplacement_id
            AND (p_code_famille IS NULL OR p_code_famille = '' OR v.lg_FAMILLEARTICLE_ID = p_code_famille)
            AND (p_code_rayon IS NULL OR p_code_rayon = '' OR v.lg_ZONE_GEO_ID = p_code_rayon)
            AND (p_code_grossiste IS NULL OR p_code_grossiste = '' OR v.lg_GROSSISTE_ID = p_code_grossiste)
        GROUP BY
            v.lg_FAMILLE_ID, v.product_name, v.article_family, v.int_CIP, v.lg_GROSSISTE_ID
    ),
    AnalyseCumulative AS (
        SELECT
            vf.*,
            SUM(vf.agg_total_price_ttc) OVER () AS grand_total_price,
            SUM(vf.agg_total_price_ttc) OVER (ORDER BY vf.agg_total_price_ttc DESC ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS cumulative_price
        FROM
            VentesFiltrees vf
    )
    SELECT
        ac.int_CIP,
        ac.product_name,
        ac.agg_total_price_ttc AS total_price_ttc,
        ac.agg_total_quantity AS total_quantity,
        ac.lg_FAMILLE_ID,
        ac.lg_GROSSISTE_ID,
        ac.article_family,
        ac.agg_marge AS marge,
        (SELECT fs.int_NUMBER_AVAILABLE FROM t_famille_stock fs
         WHERE fs.lg_FAMILLE_ID = ac.lg_FAMILLE_ID
         AND fs.lg_EMPLACEMENT_ID = p_emplacement_id
         AND fs.str_STATUT = 'enable'
         LIMIT 1) AS stock_disponible
    FROM
        AnalyseCumulative ac
    WHERE
        ac.cumulative_price <= (ac.grand_total_price * 0.8)
    ORDER BY
        ac.agg_total_price_ttc DESC;
END$$

-- Réinitialisation du délimiteur par défaut.
DELIMITER ;
