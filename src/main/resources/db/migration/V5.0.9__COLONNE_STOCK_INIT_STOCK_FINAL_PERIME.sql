ALTER TABLE t_warehouse  ADD COLUMN IF NOT EXISTS stock_initial  INT(11) NULL DEFAULT NULL;
ALTER TABLE t_warehouse  ADD COLUMN IF NOT EXISTS stock_final  INT(11) NULL DEFAULT NULL;