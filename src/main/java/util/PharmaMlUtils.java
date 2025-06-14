/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import commonTasks.dto.PharmaMLItemDTO;
import dal.TFamilleGrossiste;
import dal.TGrossiste;
import dal.TOrder;
import dal.TOrderDetail;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author kkoffi
 */
public final class PharmaMlUtils {

    public static final int DEF_COUNT = 6;
    public static final String PATTERN_DATE_LIVRAISON_SOUHAITE = "yyyy-MM-dd";
    /*
     * Ligne repartiteur suivi du l'identifiant
     */
    public static final String R = "R";
    /*
     * la lettre P indique le type de travail
     */
    public static final String P = "P";
    /*
     * Type de commande
     */
    public static final String TYPE_TRAVAIL_COMMANDE = "C";
    /*
     * Date de livraison souhaitee
     */
    public static final String L = "L";
    /*
     * Ligne de produits
     */
    public static final String E = "E";
    /*
     * Commentaire
     */
    public static final String W = "W";
    /*
     * Fin de la commande
     */
    public static final String Z = "Z";
    /*
     * Commande normale
     */
    public static final int COMMANDE_NORMALE = 0;

    /*
     * Commande package
     */
    public static final int COMMANDE_PACKAGE = 1;

    /*
     * Commande speciale
     */
    public static final int COMMANDE_SPECIALE = 2;

    public static final String OUI = "O";
    public static final String NON = "N";
    public static final String SEPARATEUR_COMMANDE_SP_PAC = "C";
    /*
     * Type de codification CIP
     */
    public static final String TYPE_CODIFICATION_CIP = "C1";
    public static final String TYPE_CODIFICATION_EAN13 = "C2";
    public static final String TYPE_CODIFICATION_LIBELLE_PRODUIT = "C3";
    public static int NOMBRE_LIGNE_CODE = 0;
    public static int NOMBRE_LIGNE_CLAIRE = 0;
    public static final String TYPE_TRAVAIL_INFOS_PRODUITS = "Q";
    public static final String RECEPTION_PRODUIT = "K";
    /**
     * les codes retour commande
     */
    public static final int PRODUIT_INCONNU = 1;
    public static final int PRODUIT_PAS_EN_STOCK = 2;
    public static final int PRODUIT_NE_SE_FAIT_PLUS = 3;
    public static final int PRODUIT_MANQUE_FABRIQUANT = 4;
    public static final int PRODUIT_MANQUE_RAYON = 5;
    public static final int PRODUIT_RETIRE = 6;
    public static final int PRODUIT_NON_AUTORISE = 7;
    public static final String NATURE_ACTION_LIBELLE = "Nature_Action";
    public static final String NATURE_ACTION_RE = "RE";
    public static final String CSRP_ENVELOPPE_LIBELLE = "CSRP_ENVELOPPE";
    public static final String CSRP_ENVELOPPE_VALUE = "urn:x-csrp:fr.csrp.protocole:enveloppe";
    public static final String XMLNS = "xmlns";
    public static final String VERSION_PROTOCLE_LIBELLE = "Version_Protocole";
    public static final String VERSION_PROTOCLE_VALUE = "1.0.0.0";

    public static final String ID_LOGICIEL_LIBELLE = "Id_Logiciel";
    public static final String ID_LOGICIEL_VALUE = "Prestige";

    public static final String VERSION_LOGICIEL_LIBELLE = "Id_Logiciel";
    public static final String VERSION_LOGICIEL_VALUE = "2.0.0";
    public static final String USAGE_LIBELLE = "Usage";
    public static final String USAGE_VALUE = "P";
    public static final String ENTETE = "ENTETE";
    public static final String EMETTEUR = "EMETTEUR";
    public static final String RECEPTEUR = "RECEPTEUR";
    public static final String REF_MESSAGE = "REF_MESSAGE";// LA VALEUR CORRESPOND 0 LA DATE DU JOUR AU FORMAT
    // YYYYMMDDSSMMSS
    public static final String DATE_LIBELLE = "DATE";
    public static final String CORPS_LIBELLE = "CORPS";
    public static final String MESSAGE_OFFICINE_LIBELLE = "MESSAGE_OFFICINE";
    public static final String MESSAGE_OFFICINE_VALUE = "urn:x-csrp:fr.csrp.protocole:message";
    public static final String DESTINATAIRE = "DESTINATAIRE";

    public static final String ID_CLIENT_LIBELLE = "Id_Client";
    public static final String NATURE_PARTENAIRE_LIBELLE = "Nature_Partenaire";
    public static final String NATURE_PARTENAIRE_VALUE_OF = "OF";
    public static final String NATURE_PARTENAIRE_VALUE_RE = "RE";
    public static final String CODE_SOCIETE_LIBELLE = "Code_Societe";
    public static final String ID_SOCIETE_LIBELLE = "Id_Societe";
    public static final String COMMANDE_LIBELLE = "COMMANDE";
    public static final String REF_CLE_CLIENT_LIBELLE = "Ref_Cde_Client";
    public static final String COMMENTAIRE_GENERALE_LIBELLE = "Commentaire_General";
    public static final String DATE_LIVRAISON_LIBELLE = "Date_livraison";
    public static final String NORMALE_LIBELLE = "NORMALE";
    public static final String LIGNE_N_LIBELLE = "LIGNE_N";
    public static final String NUM_LIGNE_LIBELLE = "Num_Ligne";
    public static final String TYPE_CODIFICATION_LIBELLE = "Type_Codification";
    public static final String TYPE_CODIFICATION_CIP39 = "CIP39";
    public static final String CODE_PRODUIT_LIBELLE = "Code_Produit";
    public static final String QUANTITE_LIBELLE = "Quantite";
    public static final String XML_ENTETE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public static final String CHEVRON_OUVRANT = "<";
    public static final String CHEVRON_FERMANT = ">";
    public static final String SLASH = "/";
    public static final String BALISE_FERMANTE = "/>";
    public static final String EQUALS = "=";
    public static final String SPACE = " ";
    public static final String NATURE_LIBELLE = "Nature";
    public static final String CODE_LIBELLE = "Code";
    public static final String CODE_VALUE = "00";
    public static final String ID_LIBELLE = "Id";
    public static final String ADRESSE_LIBELLE = "Adresse";
    public static final String NATURE_ACTION_REQ_EMISSION = "REQ_EMISSION";

    public static String buildRepartiteurLine(String idRepartiteur) {
        return R + idRepartiteur;
    }

    public static String buildComment(String commentaire) {
        if (StringUtils.isEmpty(commentaire)) {
            return W + StringUtils.rightPad("COMMENTAIRE GENERAL", 100, StringUtils.SPACE);
        }
        return W + StringUtils.rightPad(commentaire, 256, StringUtils.SPACE);
    }

    public static String finCommande() {
        return Z + StringUtils.leftPad(NOMBRE_LIGNE_CODE + "", 4, '0')
                + StringUtils.leftPad(NOMBRE_LIGNE_CLAIRE + "", 4, '0');

    }

    public static String buildCommandeLine(final int typeCommande, final String codeCommande, String commandeId) {

        /*
         * switch (typeCommande) { case COMMANDE_PACKAGE: case COMMANDE_SPECIALE: return TYPE_TRAVAIL_COMMANDE +
         * StringUtils.leftPad(typeCommande + "", 3, "0") + R +
         * StringUtils.rightPad(RandomStringUtils.randomNumeric(DEF_COUNT), 20, StringUtils.SPACE) +
         * SEPARATEUR_COMMANDE_SP_PAC + StringUtils.rightPad(codeCommande, 15, StringUtils.SPACE); default: // return
         * TYPE_TRAVAIL_COMMANDE + StringUtils.leftPad(typeCommande + "", 3, "0") + R +
         * StringUtils.rightPad(RandomStringUtils.randomNumeric(DEF_COUNT), 20, StringUtils.SPACE); return
         * TYPE_TRAVAIL_COMMANDE + StringUtils.leftPad(typeCommande + "", 3, "0") + R + commandeId; }
         */
        return TYPE_TRAVAIL_COMMANDE + StringUtils.leftPad(typeCommande + "", 3, "0") + R + commandeId;
    }

    public static List<String> buildBody(List<PharmaMLItemDTO> itemDTO) {
        LongAdder adder = new LongAdder();
        adder.increment();
        List<String> strings = new ArrayList<>();
        itemDTO.forEach(d -> {
            strings.add(buildBodyElement(d, adder.intValue()));
            adder.increment();

        });
        return strings;

    }

    public static List<String> buildBodyInfosProduit(List<PharmaMLItemDTO> itemDTO) {
        LongAdder adder = new LongAdder();
        adder.increment();
        List<String> strings = new ArrayList<>();
        if (itemDTO.size() <= 50) {
            itemDTO.forEach(d -> {
                strings.add(buildBodyElementInfoProduit(d, adder.intValue()));
                adder.increment();
            });
        } else {
            itemDTO.subList(0, 50).forEach(d -> {
                strings.add(buildBodyElementInfoProduit(d, adder.intValue()));
                adder.increment();

            });
        }
        return strings;

    }

    public static String buildBodyElement(PharmaMLItemDTO e, int index) {
        String ligne = E + StringUtils.leftPad(e.getQuantite() + "", 4, '0');
        String code;
        switch (e.getTypeCodification()) {
        case TYPE_CODIFICATION_EAN13:
            code = TYPE_CODIFICATION_EAN13 + e.getEan();
            NOMBRE_LIGNE_CODE++;
            break;
        case TYPE_CODIFICATION_LIBELLE_PRODUIT:
            code = TYPE_CODIFICATION_LIBELLE_PRODUIT + e.getLibelle();
            NOMBRE_LIGNE_CLAIRE++;
            break;
        default:
            code = TYPE_CODIFICATION_CIP + e.getCip();
            NOMBRE_LIGNE_CODE++;
            break;
        }
        return ligne + StringUtils.rightPad(code, 52, StringUtils.SPACE) + returnOorN(e.isLivraisonPartielle())
                + returnOorN(e.isReliquats()) + returnOorN(e.isLivraisonEquivalente())
                + StringUtils.leftPad(index + "", 4, '0');
    }

    public static String buildTypeTravailLine(String typeTravail) {
        if (StringUtils.isEmpty(typeTravail)) {
            return P + TYPE_TRAVAIL_COMMANDE;
        }
        return P + typeTravail;
    }

    public static String buildDateLivraisonLine(LocalDate dateLivraisonSouhaitee) {
        return L + dateLivraisonSouhaitee.format(DateTimeFormatter.ofPattern(PATTERN_DATE_LIVRAISON_SOUHAITE));

    }

    public static String returnOorN(boolean r) {
        return r ? "O" : "N";
    }

    public static String buildReferenceDemandeInfosProduit(String codeRequete) {
        return TYPE_TRAVAIL_INFOS_PRODUITS + codeRequete;
    }

    public static String buildBodyElementInfoProduit(PharmaMLItemDTO e, int index) {
        String ligne = E + StringUtils.leftPad(e.getQuantite() + "", 4, '0');
        String code;
        switch (e.getTypeCodification()) {
        case TYPE_CODIFICATION_EAN13:
            code = TYPE_CODIFICATION_EAN13 + e.getEan();
            NOMBRE_LIGNE_CODE++;
            break;
        case TYPE_CODIFICATION_LIBELLE_PRODUIT:
            code = TYPE_CODIFICATION_LIBELLE_PRODUIT + e.getLibelle();
            NOMBRE_LIGNE_CLAIRE++;
            break;
        default:
            code = TYPE_CODIFICATION_CIP + e.getCip();
            NOMBRE_LIGNE_CODE++;
            break;
        }
        return ligne + StringUtils.rightPad(code, 52, StringUtils.SPACE) + StringUtils.leftPad(index + "", 4, '0');
    }

    public static StringBuilder buildCommande(TOrder commande, StringBuilder itemsBuilder) {
        // <COMMANDE Ref_Cde_Client="0549711409" Commentaire_General="0549711409" Date_livraison="2023-09-07">
        // <CORPS>
        // <MESSAGE_OFFICINE xmlns="urn:x-csrp:fr.csrp.protocole:message">
        TGrossiste grossiste = commande.getLgGROSSISTEID();
        StringBuilder sb = new StringBuilder(XML_ENTETE);
        // <CSRP_ENVELOPPE xmlns="urn:x-csrp:fr.csrp.protocole:enveloppe" Nature_Action="REQ_EMISSION"
        // Version_Protocole="1.0.0.0" Id_Logiciel="AZME" Version_Logiciel="17.0.10.0" Usage="P">
        sb.append(CHEVRON_OUVRANT).append(CSRP_ENVELOPPE_LIBELLE).append(SPACE).append(XMLNS).append(EQUALS)
                .append(CSRP_ENVELOPPE_VALUE).append(SPACE).append(NATURE_ACTION_LIBELLE).append(EQUALS)
                .append(NATURE_ACTION_REQ_EMISSION).append(SPACE).append(VERSION_PROTOCLE_LIBELLE).append(EQUALS)
                .append(VERSION_PROTOCLE_VALUE).append(SPACE).append(ID_LOGICIEL_LIBELLE).append(EQUALS)
                .append(ID_LOGICIEL_VALUE).append(SPACE).append(VERSION_LOGICIEL_LIBELLE).append(EQUALS)
                .append(VERSION_LOGICIEL_VALUE).append(SPACE).append(USAGE_LIBELLE).append(EQUALS).append(USAGE_VALUE)
                .append(CHEVRON_FERMANT);

        buildEntete(sb, grossiste);

        sb.append(CHEVRON_OUVRANT).append(CORPS_LIBELLE).append(CHEVRON_FERMANT).append(CHEVRON_OUVRANT)
                .append(MESSAGE_OFFICINE_LIBELLE).append(SPACE).append(XMLNS).append(EQUALS)
                .append(MESSAGE_OFFICINE_VALUE).append(CHEVRON_FERMANT);
        buildEnteteOffice(sb, grossiste);
        sb.append(CHEVRON_OUVRANT).append(CORPS_LIBELLE).append(CHEVRON_FERMANT).append(CHEVRON_OUVRANT)
                .append(COMMANDE_LIBELLE).append(CHEVRON_FERMANT).append(REF_CLE_CLIENT_LIBELLE).append(EQUALS)
                .append(commande.getStrREFORDER()).append(SPACE).append(COMMENTAIRE_GENERALE_LIBELLE).append(EQUALS)
                .append(commande.getStrREFORDER()).append(SPACE).append(DATE_LIVRAISON_LIBELLE).append(EQUALS)
                .append(LocalDate.now()).append(CHEVRON_FERMANT).append(CHEVRON_OUVRANT).append(NORMALE_LIBELLE)
                .append(CHEVRON_FERMANT).append(itemsBuilder) // ajouter les lignes de la commande

                // fermer NORMALE TAG
                .append(CHEVRON_OUVRANT).append(SLASH).append(NORMALE_LIBELLE).append(CHEVRON_FERMANT)
                // fermer COMMANDE TAG
                .append(CHEVRON_OUVRANT).append(SLASH).append(COMMANDE_LIBELLE).append(CHEVRON_FERMANT)
                // fermer CORPS TAG
                .append(CHEVRON_OUVRANT).append(SLASH).append(CORPS_LIBELLE).append(CHEVRON_FERMANT)
                // fermer MESSAGE_OFFICINE TAG
                .append(CHEVRON_OUVRANT).append(SLASH).append(MESSAGE_OFFICINE_LIBELLE).append(CHEVRON_FERMANT)
                // fermer CORPS TAG
                .append(CHEVRON_OUVRANT).append(SLASH).append(CORPS_LIBELLE).append(CHEVRON_FERMANT)
                // fermer CSRP_ENVELOPPE TAG
                .append(CHEVRON_OUVRANT).append(SLASH).append(CSRP_ENVELOPPE_LIBELLE).append(CHEVRON_FERMANT);

        return sb;

    }

    private StringBuilder appendValue(StringBuilder sb, String value) {
        return sb.append("\"").append(value).append("\"");
    }

    public static void buildEntete(StringBuilder sb, TGrossiste grossiste) {
        /*
         * <ENTETE> <EMETTEUR Nature="OF" Code="00" Id="002174230" Adresse="PHARMACIE K"/> <RECEPTEUR Nature="RE"
         * Code="427" Id="42700" Adresse="COPHARMED VRIDI"/> <REF_MESSAGE>20240907201755000</REF_MESSAGE>
         * <DATE>2024-09-07T20:17:55</DATE> </ENTETE>
         *
         *
         * str_URL_PHARMAML:http://pharma-ml.ubipharm-cotedivoire.com/LABOREX/
         *
         *
         *
         */
        sb.append(CHEVRON_OUVRANT).append(ENTETE).append(CHEVRON_FERMANT).append(CHEVRON_OUVRANT).append(EMETTEUR)
                .append(SPACE).append(NATURE_LIBELLE).append(EQUALS).append(NATURE_PARTENAIRE_VALUE_OF).append(SPACE)
                .append(CODE_LIBELLE).append(EQUALS)
                .append(StringUtils.isNotEmpty(grossiste.getStrOFFICINEID()) ? grossiste.getStrOFFICINEID()
                        : CODE_VALUE)
                .append(SPACE).append(ID_LIBELLE).append(EQUALS).append(grossiste.getStrIDRECEPTEURPHARMA())
                .append(SPACE).append(ADRESSE_LIBELLE).append(BALISE_FERMANTE);

        sb.append(CHEVRON_OUVRANT).append(RECEPTEUR).append(SPACE).append(NATURE_LIBELLE).append(EQUALS)
                .append(NATURE_PARTENAIRE_VALUE_RE).append(SPACE).append(CODE_LIBELLE).append(EQUALS)
                .append(grossiste.getStrCODERECEPTEURPHARMA()).append(SPACE).append(ID_LIBELLE).append(EQUALS)
                .append(grossiste.getStrIDRECEPTEURPHARMA()).append(SPACE).append(ADRESSE_LIBELLE)
                .append(grossiste.getStrLIBELLE()).append(BALISE_FERMANTE);
        sb.append(CHEVRON_OUVRANT).append(REF_MESSAGE).append(CHEVRON_FERMANT)
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                .append(CHEVRON_OUVRANT).append(SLASH).append(REF_MESSAGE).append(CHEVRON_FERMANT);

        sb.append(CHEVRON_OUVRANT).append(DATE_LIBELLE).append(CHEVRON_FERMANT).append(LocalDateTime.now())
                .append(CHEVRON_OUVRANT).append(SLASH).append(DATE_LIBELLE).append(CHEVRON_FERMANT)
                .append(CHEVRON_OUVRANT).append(SLASH).append(ENTETE).append(CHEVRON_FERMANT);
    }

    public static void buildEnteteOffice(StringBuilder sb, TGrossiste grossiste) {
        /*
         * <ENTETE> <EMETTEUR Id_Client="00214230" Nature_Partenaire="OF"/> <DESTINATAIRE Code_Societe="427"
         * Id_Societe="42700" Nature_Partenaire="RE"/> <DATE>2024-09-07T20:17:55</DATE> </ENTETE>
         *
         *
         * str_URL_PHARMAML:http://pharma-ml.ubipharm-cotedivoire.com/LABOREX/
         *
         *
         *
         */
        sb.append(CHEVRON_OUVRANT).append(ENTETE).append(CHEVRON_FERMANT).append(CHEVRON_OUVRANT).append(EMETTEUR)
                .append(SPACE).append(ID_CLIENT_LIBELLE).append(EQUALS).append(grossiste.getStrIDRECEPTEURPHARMA())
                .append(SPACE).append(NATURE_PARTENAIRE_LIBELLE).append(EQUALS).append(NATURE_PARTENAIRE_VALUE_OF)
                .append(BALISE_FERMANTE);

        sb.append(CHEVRON_OUVRANT).append(DESTINATAIRE).append(SPACE).append(CODE_SOCIETE_LIBELLE).append(EQUALS)
                .append(grossiste.getStrCODERECEPTEURPHARMA()).append(SPACE).append(ID_SOCIETE_LIBELLE).append(EQUALS)
                .append(grossiste.getStrIDRECEPTEURPHARMA()).append(SPACE).append(NATURE_PARTENAIRE_LIBELLE)
                .append(EQUALS).append(NATURE_PARTENAIRE_VALUE_RE).append(BALISE_FERMANTE);

        sb.append(CHEVRON_OUVRANT).append(DATE_LIBELLE).append(CHEVRON_FERMANT).append(LocalDateTime.now())
                .append(CHEVRON_OUVRANT).append(SLASH).append(DATE_LIBELLE).append(CHEVRON_FERMANT)
                .append(CHEVRON_OUVRANT).append(SLASH).append(ENTETE).append(CHEVRON_FERMANT);
    }

    public static void buildLigneCommande(StringBuilder sb, TOrderDetail item, TFamilleGrossiste familleGrossiste,
            int index) {
        String numLigne = StringUtils.leftPad(index + "", 4, '0');
        String quantite = StringUtils.leftPad(item.getIntNUMBER() + "", 4, '0');
        String cip = null;
        if (familleGrossiste != null && StringUtils.isEmpty(familleGrossiste.getStrCODEARTICLE())) {
            cip = familleGrossiste.getStrCODEARTICLE();
        }
        if (StringUtils.isEmpty(cip)) {
            cip = item.getLgFAMILLEID().getIntCIP();
        }

        // <LIGNE_N Num_Ligne="0001" Type_Codification="CIP39" Code_Produit="3600901" Quantite="0010" Equivalent="false"
        // Partielle="false" Reliquat="false"/>
        sb.append(CHEVRON_OUVRANT).append(LIGNE_N_LIBELLE).append(SPACE).append(NUM_LIGNE_LIBELLE).append(EQUALS)
                .append(numLigne).append(SPACE).append(TYPE_CODIFICATION_LIBELLE).append(EQUALS)
                .append(TYPE_CODIFICATION_CIP39).append(SPACE).append(CODE_PRODUIT_LIBELLE).append(EQUALS).append(cip)
                .append(SPACE).append(QUANTITE_LIBELLE).append(EQUALS).append(quantite).append(BALISE_FERMANTE);
    }
}
