ALTER TABLE t_preenregistrement_detail
    ADD INDEX IF NOT EXISTS `idx_tpd_ca_famille_date_vente` (`dt_CREATED`, `lg_PREENREGISTREMENT_ID`, `lg_FAMILLE_ID`);

ALTER TABLE t_preenregistrement
    ADD INDEX IF NOT EXISTS `idx_tp_ca_statut_cancel_price` (`str_STATUT`, `b_IS_CANCEL`, `int_PRICE`, `lg_PREENREGISTREMENT_ID`);

ALTER TABLE t_famille
    ADD INDEX IF NOT EXISTS `idx_tf_famillearticle` (`lg_FAMILLEARTICLE_ID`);

ALTER TABLE t_famillearticle
    ADD INDEX IF NOT EXISTS `idx_tfa_code_libelle` (`str_CODE_FAMILLE`, `str_LIBELLE`);