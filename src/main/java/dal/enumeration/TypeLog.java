/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal.enumeration;

/**
 *
 * @author Kobena
 */
public enum TypeLog {
    DECONDITIONNEMENT("Deconditionnement de produit"),
    MODIFICATION_PRIX_VENTE_PRODUIT("Modification prix de vente de produit"),
    ANNULATION_DE_VENTE("Annulation de vente"), SUPPRESION_DE_FACTURE("Suppression de facture"),
    DESACTIVATION_DE_PRODUIT("Désactivation de produit"), SUPPRESSION_DE_PRODUIT("Suppression de produit"),
    ACTIVATION_DE_PRODUIT("Activation de produit"), AJUSTEMENT_DE_PRODUIT("Ajustement de produit"),
    VALIDATION_DE_CAISSE("Validation de caisse"), ANNULATION_DE_CAISSE("Annulation de caisse"),
    MVT_DE_CAISSE("Mouvement de caisse"), ENTREE_EN_STOCK("Entrée en stock de BL"),
    ATTRIBUTION_DE_FOND_DE_CAISSE("Attribution de fond de caisse"),
    MODIFICATION_DATE_SYSTEME("Modification de la date système"), IVENTAIRE("Inventaire du stock"),
    GENERATION_DE_FACTURE("Génération de facture"),
    MODIFICATION_INFO_PRODUIT_COMMANDE("Modification info produit à la commande"), QUANTITE_UG("Entrée quantité UG"),
    MODIFICATION_INFO_VENTE("Modification de vente"), RETOUR_FOURNISSEUR("Retour fournisseur"),
    SAISIS_PERIMES("Saisis de périmés"), VALIDATION_DE_FOND_DE_CAISSE("Validation de fond de caisse "),
    AUTHENTIFICATION("Authentification"), DECONNECTION("Deconnection"),;

    private final String value;

    public String getValue() {
        return value;
    }

    private TypeLog(String value) {
        this.value = value;
    }

}
