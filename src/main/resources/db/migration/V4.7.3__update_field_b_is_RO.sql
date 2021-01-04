UPDATE  t_compte_client_tiers_payant d SET d.b_IS_RO =FALSE WHERE d.b_IS_RO IS NULL;
ALTER TABLE t_preenregistrement_detail MODIFY COLUMN `int_UG` INT(11)  NULL DEFAULT '0';
