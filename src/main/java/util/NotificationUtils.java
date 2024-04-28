/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author koben
 */
public enum NotificationUtils {
    USER("Opérateur", "user"),
    MVT_DATE("Date saisie", "dateMvt"),
    MONTANT("Montant", "montant"),
    TYPE_NAME("Type", "type"),
    MESSAGE("Message", "message"),
    NUM_BL("Num Bon", "numBon"),
    MONTANT_TVA("Montant Tva", "montantTva"),
    MONTANT_TTC("Montant Ttc", "montantTtc"),
    DATE_BON("Date bon", "dateBon"),
    ITEMS("Détail", "detail"),
    ITEM_DESC("Description", "description"),
    ITEM_QTY("Quantité", "quantite"),
    ITEM_QTY_INIT("Qté initiale", "quantiteInit"),
    ITEM_QTY_FINALE("Qté finale", "quantiteFinale"),
    PRIX_INIT("Prix initial", "prixUni"),
    PRIX_FINAL("Prix final", "prixFinal"),
    PRIX_ACHAT_INIT("Prix achat initial", "prixAchatUni"),
    PRIX_ACHA_FINAL("Prix achat final", "prixAchatFinal"),
    DATE("Date", "dateMvt"),
    DATE_INI("Date initiale", "dateMvtIni"),
    ITEM_KEY("Code", "code");

    private final String value;
    private final String id;

    private NotificationUtils(String value, String id) {
        this.value = value;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

}
