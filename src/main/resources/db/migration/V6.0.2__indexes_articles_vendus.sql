ALTER TABLE t_preenregistrement
    ADD INDEX IF NOT EXISTS idx_preenregistrement_articles_vendus (str_STATUT, b_IS_CANCEL, dt_UPDATED, int_PRICE, lg_USER_ID) USING BTREE;

ALTER TABLE t_preenregistrement_detail
    ADD INDEX IF NOT EXISTS idx_preenregistrement_detail_vente_famille (lg_PREENREGISTREMENT_ID, lg_FAMILLE_ID) USING BTREE;

ALTER TABLE t_famille_stock
    ADD INDEX IF NOT EXISTS idx_famille_stock_famille_emplacement (lg_FAMILLE_ID, lg_EMPLACEMENT_ID) USING BTREE;