UPDATE  t_preenregistrement_detail d SET d.int_UG =0 WHERE  d.int_UG  IS NULL;
UPDATE  t_famille_stock s SET s.int_UG =0 WHERE s.int_UG  IS NULL;
ALTER TABLE hmvtproduit DROP COLUMN version;
ALTER TABLE hmvtproduit ADD COLUMN ug int(4) NOT NULL DEFAULT '0';

ALTER TABLE mvttransaction ADD COLUMN margeug int(8) NOT NULL DEFAULT '0';
ALTER TABLE mvttransaction ADD COLUMN montantttcug int(8) NOT NULL DEFAULT '0';
ALTER TABLE mvttransaction ADD COLUMN montantnetug int(8) NOT NULL DEFAULT '0';
ALTER TABLE mvttransaction ADD COLUMN montanttvaug int(8) NOT NULL DEFAULT '0';

ALTER TABLE t_preenregistrement ADD COLUMN margeug int(8) NOT NULL DEFAULT '0';
ALTER TABLE t_preenregistrement ADD COLUMN montantttcug int(8) NOT NULL DEFAULT '0';
ALTER TABLE t_preenregistrement ADD COLUMN montantnetug int(8) NOT NULL DEFAULT '0';
ALTER TABLE t_preenregistrement ADD COLUMN montanttvaug int(8) NOT NULL DEFAULT '0';

ALTER TABLE t_preenregistrement_detail ADD COLUMN montanttvaug int(8) NOT NULL DEFAULT '0';
