UPDATE  t_preenregistrement_detail d SET d.int_UG =0 WHERE  d.int_UG  IS NULL;
UPDATE  t_famille_stock s SET s.int_UG =0 WHERE s.int_UG  IS NULL;
ALTER TABLE hmvtproduit DROP COLUMN IF EXISTS version;
ALTER TABLE hmvtproduit ADD COLUMN IF NOT EXISTS ug int(4) NOT NULL DEFAULT '0';

ALTER TABLE mvttransaction ADD COLUMN IF NOT EXISTS margeug int(8) NOT NULL DEFAULT '0';
ALTER TABLE mvttransaction ADD COLUMN IF NOT EXISTS montantttcug int(8) NOT NULL DEFAULT '0';
ALTER TABLE mvttransaction ADD COLUMN IF NOT EXISTS montantnetug int(8) NOT NULL DEFAULT '0';
ALTER TABLE mvttransaction ADD COLUMN IF NOT EXISTS montanttvaug int(8) NOT NULL DEFAULT '0';

ALTER TABLE t_preenregistrement ADD COLUMN IF NOT EXISTS margeug int(8) NOT NULL DEFAULT '0';
ALTER TABLE t_preenregistrement ADD COLUMN IF NOT EXISTS montantttcug int(8) NOT NULL DEFAULT '0';
ALTER TABLE t_preenregistrement ADD COLUMN IF NOT EXISTS montantnetug int(8) NOT NULL DEFAULT '0';
ALTER TABLE t_preenregistrement ADD COLUMN IF NOT EXISTS montanttvaug int(8) NOT NULL DEFAULT '0';
ALTER TABLE t_preenregistrement_detail ADD COLUMN IF NOT EXISTS montanttvaug int(8) NOT NULL DEFAULT '0';
