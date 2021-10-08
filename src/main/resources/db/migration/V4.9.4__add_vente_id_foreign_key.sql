ALTER TABLE mvttransaction DROP CONSTRAINT IF EXISTS mvt_vent_id_foreign_key;

ALTER TABLE  mvttransaction  ADD CONSTRAINT  `mvt_vent_id_foreign_key` FOREIGN KEY (`vente_id`) REFERENCES `t_preenregistrement` (`lg_PREENREGISTREMENT_ID`);