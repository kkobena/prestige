ALTER TABLE t_retour_fournisseur_detail ADD COLUMN bonLivraisonDetail_id VARCHAR(50);
ALTER TABLE `t_retour_fournisseur_detail` CHANGE COLUMN `lg_RETOUR_FRS_ID` `lg_RETOUR_FRS_ID` VARCHAR(40) NOT NULL ;
