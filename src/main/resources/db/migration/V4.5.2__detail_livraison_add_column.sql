ALTER TABLE t_bon_livraison_detail  ADD COLUMN prixTarif INT(10) NOT NULL DEFAULT '0';
ALTER TABLE t_bon_livraison_detail  ADD COLUMN prixUni INT(10) NOT NULL DEFAULT '0';
ALTER TABLE t_rupture_history ADD COLUMN  grossisteId VARCHAR(40) NULL;