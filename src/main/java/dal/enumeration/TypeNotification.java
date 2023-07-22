/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal.enumeration;

/**
 *
 * @author koben
 */
public enum TypeNotification {
    DECONDITIONNEMENT("Deconditionnement de produit"),
    MODIFICATION_PRIX_VENTE_PRODUIT("Modification prix de vente de produit"),
    ANNULATION_DE_VENTE("Annulation de vente"), SUPPRESION_DE_FACTURE("Suppression de facture"),
    DESACTIVATION_DE_PRODUIT("Desactivation de produit"), SUPPRESSION_DE_PRODUIT("Suppression de produit"),
    ACTIVATION_DE_PRODUIT("Activation de produit"), AJUSTEMENT_DE_PRODUIT("Ajustement de produit"),
    VALIDATION_DE_CAISSE("Validation de caisse"), ANNULATION_DE_CAISSE("Annulation de caisse"),
    MVT_DE_CAISSE("Mouvement de caisse"), ENTREE_EN_STOCK("Entree en stock de BL"),
    CLOTURE_DE_CAISSE("Cloture de caisse"), ANNULATION_CLOTURE_DE_CAISSE("Annulation de cloture de caisse"),
    AVOIR_PRODUIT("Reception avoir"), QUANTITE_UG("Entrée quantité UG"), RETOUR_FOURNISSEUR("Retour fournisseur"),
    MODIFICATION_INFO_PRODUIT_COMMANDE("Modification info produit à la commande"),;

    private final String value;

    public String getValue() {
        return value;
    }

    private TypeNotification(String value) {
        this.value = value;
    }
}
