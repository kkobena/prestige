ALTER TABLE `sms_token` MODIFY COLUMN `access_token` VARCHAR(2000) NULL DEFAULT NULL COLLATE 'utf8_general_ci';