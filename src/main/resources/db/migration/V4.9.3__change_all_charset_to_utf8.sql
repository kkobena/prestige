SET foreign_key_checks = 0;
ALTER TABLE `annulation_recette`  CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `annulation_snapshot` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `gamme_produit` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `groupefournisseur` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `historique_importation` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `hmvtproduit` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `laboratoire` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `medecin` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `mvttransaction` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `notification` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `notification_client` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `rupture` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `rupture_detail` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `stock_snapshot` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `typemvtproduit` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_ajustement` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_ajustement_detail` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_alert_event` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_alert_event_user_fone` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_ayant_droit` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_billetage` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_billetage_details` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_bon_livraison` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_bon_livraison_detail` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_bordereau` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_bordereau_detail` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_caisse` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_calendrier` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_cash_transaction` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_categorie_ayantdroit` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_category_client` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_centre_payeur` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_client` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_code_acte` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_code_gestion` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_code_tva` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_coefficient_ponderation` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_coffre_caisse` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_company` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_compte_client` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_compte_client_tiers_payant` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_contencieux` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_contre_indication` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_dci` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_deconditionnement` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_depenses` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_devise` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE    `t_dossier_facture` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE    `t_dossier_reglement` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_dossier_reglement_detail` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_dossier_tiers_payant` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_emplacement` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_escompte_societe` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_escompte_societe_tranche` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_etat_article` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_etiquette` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_evaluationoffreprix` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_event_log` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_fabriquant` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_facture` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_facture_detail` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_famille` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_famillearticle` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_famille_dci` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_famille_grossiste` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_famille_history` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_famille_stock` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_famille_stockretrocession` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_famille_zonegeo` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_fiche_societe` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_forme_article` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_grille_remise` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_grossiste` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_groupe_factures` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_groupe_famille` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_groupe_tierspayant` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_historypreenregistrement` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_imprimante` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_inboud_message` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_indicateur_reapprovisionnement` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_inventaire` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_inventaire_famille` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_language` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_litige` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_lot` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_medecin` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_medecin_client` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_medecin_specialite` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_menu` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_model_facture` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_mode_reglement` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_module` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_month` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_motif_reglement` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_motif_retour` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_mouvement` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_mouvementprice` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_mouvement_snapshot` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_mvt_caisse` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_nature_reglement` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_nature_vente` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_notification` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_officine` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_optimisation_quantite` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_order` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_order_detail` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_outboud_message` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_parameters` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_preenregistrement` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_preenregistrement_compte_client` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_preenregistrement_compte_client_tiers_payent` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_preenregistrement_detail` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_privilege` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_promotion` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_promotion_history` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_promotion_product` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_quinzaine` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_recettes` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_regime_caisse` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_reglement` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_reglement_bordereau` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_reglement_dossier` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_reglement_transaction` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_remise` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_resume_caisse` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_retourdepot` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_retourdepotdetail` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_retour_fournisseur` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_retour_fournisseur_detail` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_retrocession` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_retrocession_detail` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_risque` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_role` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_role_privelege` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_role_user` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_rupture_history` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_sequencier` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_skin` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_snapshot_famillesell` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_snapshot_preenregistrement_compte_client_tiers_payent` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_snap_shop_daly_recette` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_snap_shop_daly_recette_caisse` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_snap_shop_daly_sortie_famille` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_snap_shop_daly_stat` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_snap_shop_daly_stat_frequentation` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_snap_shop_daly_vente` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_snap_shop_rupture_stock` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_snap_shop_vente_client` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_snap_shop_vente_societe` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_sous_menu` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_specialite` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_stock_snapshot` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_suggestion_order` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_suggestion_order_details` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_tableau` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_taux_marque` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_taux_remboursement` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_taux_rembourssement` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_tiers_payant` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_tranche` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_tranche_horaire` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_transaction` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_transaction_code` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_transaction_type` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_tva` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_typecontencieux` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_typedepot` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_typeetiquette` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_typelitige` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_typesuggestion` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_bordereau` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_client` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_contrat` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_depense` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_facture` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_mvt_caisse` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_passation` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_promo` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_recette` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_regime` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_reglement` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_remise` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_risque` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_societe` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_stock` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_stock_famille` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_tiers_payant` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_type_vente` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_user` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_user_account_snap_shot` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_user_fone` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_user_imprimante` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_ville` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_warehouse` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_warehousedetail` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_workflow_promo` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_workflow_remise_article` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE   `t_zone_geographique` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
SET foreign_key_checks = 1;
