ALTER TABLE t_famille ADD CONSTRAINT `FKm9t4vnhaxp6colb0896x9humg` FOREIGN KEY (`gamme_id`) REFERENCES `gamme_produit` (`id`);

ALTER TABLE t_famille ADD CONSTRAINT `FKs6egbrcb35ecmnmclm1tly2qo` FOREIGN KEY (`laboratoire_id`) REFERENCES `laboratoire` (`id`);