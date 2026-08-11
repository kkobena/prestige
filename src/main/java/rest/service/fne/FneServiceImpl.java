package rest.service.fne;

import com.google.common.util.concurrent.AtomicDouble;
import dal.FneInvoiceEntity;
import dal.TCompteClientTiersPayant;
import dal.TFacture;
import dal.TFactureDetail;
import dal.TGroupeFactures;
import dal.TOfficine;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TTiersPayant;
import dal.TTypeTiersPayant;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import rest.service.exception.FneExeception;
import rest.service.impl.Utils;
import util.Constant;
import util.AppParameters;
import util.DateCommonUtils;

/**
 *
 * @author koben
 */
@Stateless
public class FneServiceImpl implements FneService {

    private static final Logger LOG = Logger.getLogger(FneServiceImpl.class.getName());
    final AppParameters sp = AppParameters.getInstance();
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public void createInvoice(String idFacture, TypeInvoice typeInvoice) throws FneExeception {

        try {

            createInvoice(em.find(TFacture.class, idFacture), typeInvoice);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            throw new FneExeception(e.getLocalizedMessage());
        }
    }

    @Override
    public JSONObject createGroupeInvoice(String idGroupeFactures, TypeInvoice typeInvoice) {
        int certifiees = 0, dejaCertifiees = 0;
        // Meme regle que pour les avoirs : un motif par cause, avec les numeros de facture concernes.
        Map<String, List<String>> motifs = new LinkedHashMap<>();
        for (TFacture facture : fetchGroupeFactures(identifiantsDeGroupe(idGroupeFactures))) {
            boolean deja = StringUtils.isNotEmpty(facture.getFneUrl())
                    || StringUtils.isNotEmpty(facture.getFneAvoirReference());
            try {
                createInvoice(facture, typeInvoice);
                certifiees++;
            } catch (Exception e) {
                // comportement historique du groupe : une facture en echec n'empeche pas les autres
                if (deja) {
                    dejaCertifiees++;
                } else {
                    motifs.computeIfAbsent(StringUtils.defaultIfEmpty(e.getMessage(), "Erreur technique"),
                            cle -> new ArrayList<>()).add(StringUtils.defaultString(facture.getStrCODEFACTURE()));
                    LOG.log(Level.SEVERE, "createGroupeInvoice: facture " + facture.getLgFACTUREID(), e);
                }
            }
        }
        int echecs = compterFactures(motifs);
        return new JSONObject().put("certifiees", certifiees).put("dejaCertifiees", dejaCertifiees)
                .put("echecs", echecs).put("message", messageGroupe(certifiees, dejaCertifiees, motifs));
    }

    /**
     * Compte rendu en langage simple de la certification d'une facture de groupe.
     *
     * Meme regle d'ecriture que pour les avoirs : quand RIEN n'a abouti, la CAUSE vient en tete.
     *
     * Visible du paquet pour etre testee sans monter l'EJB.
     */
    static String messageGroupe(int certifiees, int dejaCertifiees, Map<String, List<String>> motifs) {
        int echecs = compterFactures(motifs);
        if (certifiees == 0 && echecs > 0) {
            StringBuilder m = new StringBuilder("Aucune facture certifiée. ").append(detailMotifs(motifs));
            if (dejaCertifiees > 0) {
                m.append(' ').append(dejaCertifiees)
                        .append(" facture(s) portaient déjà une certification et ont été ignorée(s).");
            }
            return m.toString();
        }
        if (certifiees == 0 && dejaCertifiees > 0) {
            return "Facture déjà certifiée : les " + dejaCertifiees
                    + " facture(s) de ce groupe portent déjà une certification FNE valide. Rien n'a été renvoyé.";
        }
        StringBuilder m = new StringBuilder();
        m.append(certifiees).append(" facture(s) certifiée(s)");
        if (dejaCertifiees > 0) {
            m.append(", ").append(dejaCertifiees).append(" déjà certifiée(s) et donc ignorée(s)");
        }
        m.append('.');
        if (echecs > 0) {
            m.append(' ').append(detailMotifs(motifs));
        }
        return m.toString();
    }

    /** Nombre total de factures en echec, tous motifs confondus. */
    private static int compterFactures(Map<String, List<String>> motifs) {
        int total = 0;
        for (List<String> factures : motifs.values()) {
            total += factures.size();
        }
        return total;
    }

    private void createInvoice(TFacture facture, TypeInvoice typeInvoice) throws FneExeception {
        verifierNonDejaCertifiee(facture);
        TOfficine officine = getOfficine();
        Client client = getHttpClient();
        JSONObject payload = new JSONObject(resolveFneInvoice(facture, officine, typeInvoice));

        WebTarget myResource = client.target(sp.fneUrl);
        Response response = myResource.request().header("Authorization", "Bearer ".concat(sp.fnePkey))
                .post(Entity.entity(payload.toString(), MediaType.APPLICATION_JSON_TYPE));

        int status = response.getStatus();
        String raw = response.readEntity(String.class);
        LOG.log(Level.INFO, "FNE sign response ({0}) --- {1}", new Object[] { status, raw });
        FneResponse fneResponse = parseFneResponse(raw);
        if (status < 200 || status >= 300 || Objects.isNull(fneResponse)
                || StringUtils.isEmpty(fneResponse.getToken())) {
            throw new FneExeception(resolveErrorMessage(raw, status));
        }
        saveResponse(fneResponse, facture);
        persistSignResponse(raw, facture);

    }

    /**
     * Refuse une nouvelle certification quand la facture porte deja une certification valide, ou un avoir valide.
     *
     * Une double certification cree un second document officiel pour la meme facture chez l'administration fiscale :
     * elle n'est pas rattrapable depuis le logiciel. Le controle est fait ICI, au plus pres de l'envoi, pour couvrir
     * aussi bien la certification unitaire que celle d'une facture de groupe.
     */
    private void verifierNonDejaCertifiee(TFacture facture) throws FneExeception {
        if (Objects.isNull(facture)) {
            throw new FneExeception("Facture introuvable");
        }
        String numero = StringUtils.defaultString(facture.getStrCODEFACTURE());
        if (StringUtils.isNotEmpty(facture.getFneUrl())) {
            throw new FneExeception("Facture déjà certifiée : la facture n° " + numero
                    + " porte déjà une certification FNE valide. Elle ne peut pas être certifiée une seconde fois.");
        }
        if (StringUtils.isNotEmpty(facture.getFneAvoirReference())) {
            throw new FneExeception(
                    "Avoir déjà certifié : un avoir FNE valide (référence " + facture.getFneAvoirReference()
                            + ") a déjà été émis pour la facture n° " + numero + ". Elle ne peut plus être certifiée.");
        }
    }

    private TOfficine getOfficine() {
        return em.find(TOfficine.class, Constant.OFFICINE);
    }

    private FneInvoice buildCommonFneInvoice(TTiersPayant tTiersPayant, TOfficine officine) {
        FneInvoice fneInvoice = new FneInvoice();
        fneInvoice.setEstablishment(officine.getStrNOMCOMPLET());
        fneInvoice.setClientCompanyName(tTiersPayant.getStrFULLNAME());
        fneInvoice.setClientEmail(tTiersPayant.getStrADRESSE());
        fneInvoice.setClientPhone(tTiersPayant.getStrTELEPHONE());
        fneInvoice.setPointOfSale(sp.fnepointOfSale);
        fneInvoice.setClientSellerName("Gestionnaire/Comptable");
        // on pourra recuperer le nom de l'utilisateur connecter apres pour remplacer
        fneInvoice.setClientNcc(tTiersPayant.getStrCOMPTECONTRIBUABLE());
        return fneInvoice;
    }

    private FneInvoice buildFneInvoiceForCarnet(TFacture facture, TOfficine officine, TTiersPayant tTiersPayant,
            TypeInvoice typeInvoice) {
        FneInvoice fneInvoice = buildCommonFneInvoice(tTiersPayant, officine);
        fneInvoice.setTemplate("B2C");
        List<FneInvoiceItem> fneInvoiceItems = resolveInvoiceItems(facture, tTiersPayant, typeInvoice);
        fneInvoice.setItems(fneInvoiceItems);

        return fneInvoice;
    }

    private List<FneInvoiceItem> resolveInvoiceItems(TFacture facture, TTiersPayant tTiersPayant,
            TypeInvoice typeInvoice) {
        String tiersPayantId = tTiersPayant.getLgTIERSPAYANTID();
        if (Objects.isNull(typeInvoice) || typeInvoice == TypeInvoice.GROUPE_TAUX_TVA) {
            return buildFromProduitCodeTva(facture, tiersPayantId);
        }
        return buildFneInvoiceItemParProduit(facture, tiersPayantId);
    }

    private List<FneInvoiceItem> buildFneInvoiceItemParProduit(TFacture facture, String tiersPayantId) {
        List<VenteDetail> venteDetails = fetchVenteDetail(facture.getLgFACTUREID(), tiersPayantId);

        List<FneInvoiceItem> fneInvoiceItems = new ArrayList<>();
        for (VenteDetail item : venteDetails) {
            FneInvoiceItem invoiceItem = new FneInvoiceItem();
            invoiceItem.setDescription(item.getLibelle());
            invoiceItem.setReference(item.getCodeCip());
            invoiceItem.setQuantity(item.getQuantity());
            invoiceItem.setDiscount(item.getTauxRemise());
            invoiceItem.setTaxes(new String[] { TaxeEnum.getByValue(item.getCodeTva()).name() });
            // invoiceItem.setAmount(Utils.calculHt(item.getMontantTtc(), item.getCodeTva()));
            invoiceItem.setAmount(computeTpAmount(item.getMontantTtc(), item.getCodeTva(), item.getTauxCouverture()));
            fneInvoiceItems.add(invoiceItem);
        }

        return fneInvoiceItems;
    }

    private FneInvoice buildFromFacture(TFacture facture, TOfficine officine, TTiersPayant tTiersPayant,
            TypeInvoice typeInvoice) {

        FneInvoice fneInvoice = buildCommonFneInvoice(tTiersPayant, officine);
        List<FneInvoiceItem> fneInvoiceItems = resolveInvoiceItems(facture, tTiersPayant, typeInvoice);
        fneInvoice.setItems(fneInvoiceItems);
        return fneInvoice;
    }

    private FneInvoice resolveFneInvoice(TFacture facture, TOfficine officine, TypeInvoice typeInvoice) {
        TTiersPayant tTiersPayant = facture.getTiersPayant();
        TTypeTiersPayant tTypeTiersPayant = tTiersPayant.getLgTYPETIERSPAYANTID();
        if (Constant.TYPE_TIERS_PAYANT_CARNET_ID.equals(tTypeTiersPayant.getLgTYPETIERSPAYANTID())) {
            return buildFneInvoiceForCarnet(facture, officine, tTiersPayant, typeInvoice);
        }
        return buildFromFacture(facture, officine, tTiersPayant, typeInvoice);

    }

    private Client getHttpClient() {
        return ClientBuilder.newClient();
    }

    private void saveResponse(FneResponse fneResponse, TFacture facture) {
        if (Objects.nonNull(fneResponse) && StringUtils.isNotEmpty(fneResponse.getToken())) {
            facture.setFneUrl(fneResponse.getToken());
            em.merge(facture);
        }

    }

    /**
     * Lecture tolerante de la reponse FNE via org.json : seuls les champs utiles sont extraits, les champs inconnus
     * sont ignores.
     */
    private FneResponse parseFneResponse(String raw) {
        try {
            JSONObject json = new JSONObject(raw);
            FneResponse fneResponse = new FneResponse();
            fneResponse.setNcc(json.optString("ncc", null));
            fneResponse.setReference(json.optString("reference", null));
            fneResponse.setToken(json.optString("token", null));
            fneResponse.setWarning(json.optBoolean("warning", false));
            return fneResponse;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "parseFneResponse", e);
            return null;
        }
    }

    private String resolveErrorMessage(String raw, int status) {
        try {
            JSONObject json = new JSONObject(raw);
            String message = json.optString("message", null);
            if (StringUtils.isNotEmpty(message)) {
                return "FNE (" + status + ") : " + message;
            }
        } catch (Exception e) {
            // reponse non JSON : message generique ci-dessous
        }
        return "La plateforme FNE a retourne une erreur (code " + status + ")";
    }

    /**
     * Enregistre la reponse complete de certification dans fne_invoice : indispensable pour un futur avoir (l'endpoint
     * /refund exige invoice.id et les ids des lignes). Non bloquant : un echec ici ne remet pas en cause la
     * certification qui vient de reussir.
     */
    private void persistSignResponse(String raw, TFacture facture) {
        try {
            JSONObject json = new JSONObject(raw);
            JSONObject invoice = json.optJSONObject("invoice");
            String fneInvoiceId = Objects.nonNull(invoice) ? invoice.optString("id", null) : null;
            if (StringUtils.isEmpty(fneInvoiceId)) {
                LOG.log(Level.WARNING, "persistSignResponse: pas d''objet invoice dans la reponse FNE, facture {0}",
                        facture.getLgFACTUREID());
                return;
            }
            saveFneInvoiceRecord(fneInvoiceId, FneInvoiceEntity.TYPE_SALE, json.optString("reference", null), raw,
                    facture);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "persistSignResponse: enregistrement non bloquant en echec", e);
        }
    }

    private void saveFneInvoiceRecord(String id, String type, String reference, String raw, TFacture facture) {
        FneInvoiceEntity entity = em.find(FneInvoiceEntity.class, id);
        if (Objects.isNull(entity)) {
            entity = new FneInvoiceEntity();
            entity.setId(id);
        }
        entity.setMvtDate(new Date());
        entity.setType(type);
        entity.setReference(reference);
        entity.setResponse(raw);
        entity.setFacture(facture);
        em.merge(entity);
    }

    @Override
    public JSONObject createAvoirTotal(String idFacture) throws FneExeception {
        FneInvoiceEntity sale = requireSaleRecord(idFacture);
        List<FneAvoirItem> items = extractAvoirItems(sale.getResponse());
        if (items.isEmpty()) {
            throw new FneExeception("Avoir impossible : aucune ligne exploitable dans la certification enregistrée "
                    + "pour la facture n° " + numeroFacture(idFacture) + ". Le rattachement manuel, avec le JSON "
                    + "complet de la facture, permet de la corriger.");
        }
        return doRefund(sale, items);
    }

    @Override
    public JSONObject createGroupeAvoir(String idGroupeFactures) {
        int emis = 0, dejaAvoir = 0, nonCertifiees = 0, echecs = 0;
        // Les motifs sont regroupes PAR CAUSE, avec les numeros de facture concernes : ne garder que
        // le dernier motif masquait les autres quand les factures echouaient pour des raisons
        // differentes, et ne disait pas laquelle avait echoue.
        Map<String, List<String>> motifs = new LinkedHashMap<>();
        for (TFacture facture : fetchGroupeFactures(identifiantsDeGroupe(idGroupeFactures))) {
            // Un avoir deja emis n'est jamais renvoye, et une facture non certifiee n'a rien a
            // avoirer : ces deux cas sont comptes, pas traites comme des echecs.
            if (StringUtils.isNotEmpty(facture.getFneAvoirReference())) {
                dejaAvoir++;
                continue;
            }
            if (StringUtils.isEmpty(facture.getFneUrl())) {
                nonCertifiees++;
                continue;
            }
            try {
                createAvoirTotal(facture.getLgFACTUREID());
                emis++;
            } catch (Exception e) {
                echecs++;
                motifs.computeIfAbsent(StringUtils.defaultIfEmpty(e.getMessage(), "Erreur technique"),
                        cle -> new ArrayList<>()).add(StringUtils.defaultString(facture.getStrCODEFACTURE()));
                LOG.log(Level.SEVERE, "createGroupeAvoir: facture " + facture.getLgFACTUREID(), e);
            }
        }
        return new JSONObject().put("emis", emis).put("dejaAvoir", dejaAvoir).put("nonCertifiees", nonCertifiees)
                .put("echecs", echecs).put("message", messageAvoirGroupe(emis, dejaAvoir, nonCertifiees, motifs));
    }

    /**
     * Numero lisible d'une facture (celui que l'utilisateur voit a l'ecran), pour les messages d'erreur : afficher
     * l'identifiant technique ne lui apprend rien et l'empeche de retrouver la ligne concernee.
     */
    private String numeroFacture(String idFacture) {
        TFacture facture = em.find(TFacture.class, idFacture);
        return Objects.nonNull(facture) ? StringUtils.defaultString(facture.getStrCODEFACTURE()) : "";
    }

    /**
     * Compte rendu en langage simple de l'emission des avoirs d'une facture de groupe.
     *
     * Regle d'ecriture, commune a tous les messages FNE : quand RIEN n'a abouti, le message commence par la CAUSE et
     * dit quoi faire ensuite - et non par un decompte a zero, qui n'apprend rien. Quand une partie a abouti, le
     * decompte vient d'abord, puis le detail de ce qui a ete ignore ou a echoue.
     *
     * Visible du paquet pour etre testee sans monter l'EJB.
     *
     * @param motifs
     *            motif d'echec -> numeros des factures concernees
     */
    static String messageAvoirGroupe(int emis, int dejaAvoir, int nonCertifiees, Map<String, List<String>> motifs) {
        int echecs = compterFactures(motifs);
        // Rien n'a abouti : on met la cause en tete, c'est la seule information utile.
        if (emis == 0 && echecs > 0) {
            return "Aucun avoir émis. " + detailMotifs(motifs) + complementIgnorees(dejaAvoir, nonCertifiees);
        }
        if (emis == 0 && dejaAvoir > 0 && nonCertifiees == 0) {
            return "Avoir déjà émis : les " + dejaAvoir
                    + " facture(s) de ce groupe portent déjà un avoir FNE valide. Rien n'a été renvoyé.";
        }
        if (emis == 0 && dejaAvoir == 0) {
            return "Aucun avoir émis : les " + nonCertifiees
                    + " facture(s) de ce groupe ne sont pas certifiées à la FNE. Certifiez-les d'abord.";
        }
        StringBuilder m = new StringBuilder();
        m.append(emis).append(" avoir(s) émis");
        if (dejaAvoir > 0) {
            m.append(", ").append(dejaAvoir).append(" facture(s) avaient déjà un avoir et ont été ignorée(s)");
        }
        if (nonCertifiees > 0) {
            m.append(", ").append(nonCertifiees).append(" non certifiée(s) et donc ignorée(s)");
        }
        m.append('.');
        if (echecs > 0) {
            m.append(' ').append(detailMotifs(motifs));
        }
        return m.toString();
    }

    /** "Motif (facture n° A, B). Autre motif (facture n° C)." - un motif par cause, jamais tronque au dernier. */
    private static String detailMotifs(Map<String, List<String>> motifs) {
        StringBuilder m = new StringBuilder();
        for (Map.Entry<String, List<String>> motif : motifs.entrySet()) {
            if (m.length() > 0) {
                m.append(' ');
            }
            m.append(StringUtils.removeEnd(motif.getKey().trim(), "."));
            List<String> numeros = motif.getValue().stream().filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            if (!numeros.isEmpty()) {
                m.append(" (facture").append(numeros.size() > 1 ? "s n° " : " n° ").append(String.join(", ", numeros))
                        .append(')');
            }
            m.append('.');
        }
        return m.toString();
    }

    /** Rappel des factures volontairement laissees de cote, ajoute apres la cause d'un echec total. */
    private static String complementIgnorees(int dejaAvoir, int nonCertifiees) {
        StringBuilder m = new StringBuilder();
        if (dejaAvoir > 0) {
            m.append(' ').append(dejaAvoir).append(" facture(s) portaient déjà un avoir et ont été ignorée(s).");
        }
        if (nonCertifiees > 0) {
            m.append(' ').append(nonCertifiees)
                    .append(" facture(s) ne sont pas certifiées à la FNE et ont été ignorée(s).");
        }
        return m.toString();
    }

    @Override
    public JSONObject createAvoirPartiel(String idFacture, List<FneAvoirItem> items) throws FneExeception {
        if (Objects.isNull(items) || items.isEmpty()) {
            throw new FneExeception("Avoir partiel impossible : aucune ligne n'a été sélectionnée. "
                    + "Choisissez au moins une ligne à retourner.");
        }
        for (FneAvoirItem item : items) {
            if (StringUtils.isEmpty(item.getId()) || item.getQuantity() <= 0) {
                throw new FneExeception("Avoir partiel impossible : une ligne est incomplète. Chaque ligne retournée "
                        + "doit porter son identifiant FNE et une quantité supérieure à zéro.");
            }
        }
        FneInvoiceEntity sale = requireSaleRecord(idFacture);
        return doRefund(sale, items);
    }

    /**
     * Retourne l'enregistrement de certification (type SALE) le plus recent de la facture, apres controle qu'aucun
     * avoir n'a deja ete emis.
     */
    private FneInvoiceEntity requireSaleRecord(String idFacture) throws FneExeception {
        TFacture facture = em.find(TFacture.class, idFacture);
        if (Objects.isNull(facture)) {
            throw new FneExeception("Facture introuvable : elle a peut-être été supprimée depuis "
                    + "l'affichage de la liste. Actualisez la liste et recommencez (référence technique " + idFacture
                    + ").");
        }
        String numero = StringUtils.defaultString(facture.getStrCODEFACTURE());
        if (StringUtils.isEmpty(facture.getFneUrl())) {
            throw new FneExeception("Avoir impossible : la facture n° " + numero + " n'est pas certifiée à la FNE. "
                    + "Un avoir ne peut porter que sur une facture certifiée.");
        }
        if (StringUtils.isNotEmpty(facture.getFneAvoirReference())) {
            throw new FneExeception("Avoir déjà émis : un avoir FNE valide (référence " + facture.getFneAvoirReference()
                    + ") existe déjà pour la facture n° " + numero
                    + ". Elle ne peut pas faire l'objet d'un second avoir.");
        }
        // Meme regle que la suppression de facture : pas d'avoir sur une facture ayant fait l'objet d'un reglement
        // (le cas facture reglee + avoir/trop-percu est un chantier ulterieur).
        Long nbReglements = em
                .createQuery("SELECT COUNT(o) FROM TDossierReglement o WHERE o.lgFACTUREID.lgFACTUREID = ?1",
                        Long.class)
                .setParameter(1, idFacture).getSingleResult();
        if (nbReglements > 0) {
            throw new FneExeception("Avoir impossible : la facture n° " + numero
                    + " a déjà fait l'objet d'un règlement. Annulez d'abord le règlement, puis relancez l'avoir.");
        }
        FneInvoiceEntity sale = findLastRecordByType(idFacture, FneInvoiceEntity.TYPE_SALE);
        if (Objects.isNull(sale)) {
            // Facture certifiee avant la gestion des avoirs : recuperation automatique des identifiants FNE a partir
            // du token de fne_url, de facon transparente pour l'utilisateur. En cas d'echec, l'exception porte le
            // message orientant vers le rattachement manuel.
            recupererDepuisToken(idFacture);
            sale = findLastRecordByType(idFacture, FneInvoiceEntity.TYPE_SALE);
        }
        if (Objects.isNull(sale)) {
            // La recuperation automatique ci-dessus n'a pas leve d'exception mais n'a rien enregistre : ne pas
            // reproposer la recuperation automatique, elle vient d'echouer.
            throw new FneExeception("Avoir impossible : les identifiants FNE de la facture n° " + numero
                    + " sont introuvables, cette facture n'a pas été générée avec la version actuelle. "
                    + "Il faut passer par le rattachement manuel, avec le JSON de la facture (espace FNE ou "
                    + "support.fne@dgi.gouv.ci).");
        }
        return sale;
    }

    private FneInvoiceEntity findLastRecordByType(String idFacture, String type) {
        TypedQuery<FneInvoiceEntity> query = em.createQuery(
                "SELECT o FROM FneInvoiceEntity o WHERE o.facture.lgFACTUREID = ?1 AND o.type = ?2 ORDER BY o.mvtDate DESC",
                FneInvoiceEntity.class);
        query.setParameter(1, idFacture).setParameter(2, type).setMaxResults(1);
        return query.getResultList().stream().findFirst().orElse(null);
    }

    /**
     * Extrait les lignes (id FNE + quantite complete) de la reponse de certification enregistree, pour construire un
     * avoir total.
     */
    private List<FneAvoirItem> extractAvoirItems(String rawSaleResponse) {
        List<FneAvoirItem> items = new ArrayList<>();
        try {
            JSONObject invoice = resolveInvoiceObject(new JSONObject(rawSaleResponse));
            if (Objects.isNull(invoice)) {
                return items;
            }
            JSONArray jsonItems = invoice.optJSONArray("items");
            if (Objects.isNull(jsonItems)) {
                return items;
            }
            for (int i = 0; i < jsonItems.length(); i++) {
                JSONObject jsonItem = jsonItems.optJSONObject(i);
                if (Objects.isNull(jsonItem)) {
                    continue;
                }
                String id = jsonItem.optString("id", null);
                int quantity = jsonItem.optInt("quantity", 1);
                if (StringUtils.isNotEmpty(id) && quantity > 0) {
                    items.add(new FneAvoirItem(id, quantity));
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "extractAvoirItems", e);
        }
        return items;
    }

    /**
     * La reponse de certification est de la forme {..., "invoice": {...}} ; un rattachement manuel peut fournir
     * directement l'objet facture {id, items...}.
     */
    private JSONObject resolveInvoiceObject(JSONObject json) {
        JSONObject invoice = json.optJSONObject("invoice");
        if (Objects.nonNull(invoice)) {
            return invoice;
        }
        if (StringUtils.isNotEmpty(json.optString("id", null)) && Objects.nonNull(json.optJSONArray("items"))) {
            return json;
        }
        return null;
    }

    private JSONObject doRefund(FneInvoiceEntity sale, List<FneAvoirItem> items) throws FneExeception {
        TFacture facture = sale.getFacture();
        JSONArray jsonItems = new JSONArray();
        for (FneAvoirItem item : items) {
            jsonItems.put(new JSONObject().put("id", item.getId()).put("quantity", item.getQuantity()));
        }
        JSONObject payload = new JSONObject().put("items", jsonItems);
        String refundUrl = buildRefundUrl(sale.getId());
        LOG.log(Level.INFO, "FNE refund request {0} --- {1}", new Object[] { refundUrl, payload });

        Client client = getHttpClient();
        Response response = client.target(refundUrl).request().header("Authorization", "Bearer ".concat(sp.fnePkey))
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(payload.toString(), MediaType.APPLICATION_JSON_TYPE));
        int status = response.getStatus();
        String raw = response.readEntity(String.class);
        LOG.log(Level.INFO, "FNE refund response ({0}) --- {1}", new Object[] { status, raw });

        FneResponse fneResponse = parseFneResponse(raw);
        if (status < 200 || status >= 300 || Objects.isNull(fneResponse)
                || StringUtils.isEmpty(fneResponse.getReference())) {
            throw new FneExeception(resolveErrorMessage(raw, status));
        }

        facture.setFneAvoirReference(fneResponse.getReference());
        facture.setFneAvoirUrl(fneResponse.getToken());
        em.merge(facture);
        try {
            saveFneInvoiceRecord(resolveAvoirRecordId(fneResponse), FneInvoiceEntity.TYPE_AVOIR,
                    fneResponse.getReference(), raw, facture);
        } catch (Exception e) {
            // trace non bloquante : la reference et l'URL de l'avoir sont deja sur la facture
            LOG.log(Level.SEVERE, "doRefund: enregistrement fne_invoice de l'avoir", e);
        }

        JSONObject result = new JSONObject().put("success", true).put("reference", fneResponse.getReference())
                .put("url", StringUtils.defaultString(fneResponse.getToken()));
        // L'avoir est certifie : annulation locale de la facture (liberation des ventes, statut 'avoir').
        // Un echec local ne doit pas masquer le succes FNE : on le signale sans annuler l'operation.
        try {
            annulerFactureLocalement(facture);
            result.put("annulation", true);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "doRefund: annulation locale apres avoir certifie", e);
            result.put("annulation", false).put("warning",
                    "Avoir certifie a la FNE, mais l'annulation locale de la facture est incomplete. Contactez le support");
        }
        return result;
    }

    /**
     * Annulation locale d'une facture apres certification de son avoir total : memes effets que la suppression de
     * facture (dossiers liberes et refacturables, consommation du compte client reajustee, lien de groupe supprime),
     * mais la facture ET SES LIGNES sont conservees (statut 'avoir', restant a 0) pour la consultation des details, la
     * reedition et le releve du tiers payant. Les balances de creances l'ignorent (elles filtrent sur restant &gt; 0)
     * et les controles 'vente deja facturee' ignorent le statut 'avoir'.
     */
    private void annulerFactureLocalement(TFacture facture) {
        List<TFactureDetail> details = em
                .createQuery("SELECT o FROM TFactureDetail o WHERE o.lgFACTUREID.lgFACTUREID = ?1",
                        TFactureDetail.class)
                .setParameter(1, facture.getLgFACTUREID()).getResultList();
        for (TFactureDetail detail : details) {
            libererDossier(detail.getStrREF());
        }
        em.createQuery("SELECT o FROM TGroupeFactures o WHERE o.lgFACTURESID.lgFACTUREID = ?1", TGroupeFactures.class)
                .setParameter(1, facture.getLgFACTUREID()).getResultList().forEach(em::remove);

        facture.setStrSTATUT(Constant.STATUT_FACTURE_AVOIR);
        facture.setDblMONTANTRESTANT(0d);
        facture.setDtUPDATED(new Date());
        em.merge(facture);
    }

    /**
     * Reproduit la liberation faite par la suppression de facture (updateTPreenregistrementCompteClientTiersPayent +
     * resetComptClient de factureManagement) : le dossier redevient facturable et la consommation du compte client est
     * reajustee.
     */
    private void libererDossier(String strRef) {
        TPreenregistrementCompteClientTiersPayent dossier = em.find(TPreenregistrementCompteClientTiersPayent.class,
                strRef);
        if (Objects.isNull(dossier)) {
            return;
        }
        dossier.setStrSTATUTFACTURE(Constant.STATUT_UNPAID);
        dossier.setDtUPDATED(new Date());
        TCompteClientTiersPayant compte = dossier.getLgCOMPTECLIENTTIERSPAYANTID();
        if (Objects.nonNull(compte)) {
            int reste = Objects.isNull(dossier.getIntPRICERESTE()) ? 0 : dossier.getIntPRICERESTE();
            int conso = (Objects.isNull(compte.getDbCONSOMMATIONMENSUELLE()) ? 0 : compte.getDbCONSOMMATIONMENSUELLE())
                    - reste;
            compte.setDbCONSOMMATIONMENSUELLE(conso);
            int plafond = Objects.isNull(compte.getDbPLAFONDENCOURS()) ? 0 : compte.getDbPLAFONDENCOURS();
            if (conso <= plafond) {
                compte.setBCANBEUSE(true);
                if (conso < 0) {
                    compte.setDbCONSOMMATIONMENSUELLE(0);
                }
            }
            em.merge(compte);
        }
        em.merge(dossier);
    }

    private String resolveAvoirRecordId(FneResponse fneResponse) {
        String tokenUuid = extractTokenUuid(fneResponse.getToken());
        if (StringUtils.isNotEmpty(tokenUuid)) {
            return tokenUuid;
        }
        if (StringUtils.isNotEmpty(fneResponse.getReference())) {
            return fneResponse.getReference();
        }
        return UUID.randomUUID().toString();
    }

    /**
     * L'URL de refund est deduite de l'URL de certification configuree (fneUrl), qui se termine par
     * /external/invoices/sign : POST $base/external/invoices/{id}/refund.
     */
    private String buildRefundUrl(String fneInvoiceId) throws FneExeception {
        String base = StringUtils.trimToEmpty(sp.fneUrl);
        if (StringUtils.isEmpty(base)) {
            throw new FneExeception("Opération FNE impossible : l'adresse de la plateforme FNE n'est pas "
                    + "renseignée dans le paramétrage de l'officine (propriété fneUrl).");
        }
        base = StringUtils.removeEnd(base, "/");
        base = StringUtils.removeEnd(base, "/sign");
        return base + "/" + fneInvoiceId + "/refund";
    }

    @Override
    public JSONObject rattacherFacture(String idFacture, String signResponseJson) throws FneExeception {
        TFacture facture = em.find(TFacture.class, idFacture);
        if (Objects.isNull(facture)) {
            throw new FneExeception("Facture introuvable : elle a peut-être été supprimée depuis "
                    + "l'affichage de la liste. Actualisez la liste et recommencez (référence technique " + idFacture
                    + ").");
        }
        if (StringUtils.isEmpty(signResponseJson)) {
            throw new FneExeception("Rattachement impossible : le JSON de la facture FNE est obligatoire. "
                    + "Récupérez-le depuis l'espace FNE, puis collez-le ici.");
        }
        JSONObject json;
        try {
            json = new JSONObject(signResponseJson);
        } catch (Exception e) {
            throw new FneExeception("Rattachement impossible : le contenu collé n'est pas un JSON valide. "
                    + "Copiez la réponse complète de la FNE, accolade ouvrante comprise.");
        }
        JSONObject invoice = resolveInvoiceObject(json);
        if (Objects.isNull(invoice)) {
            throw new FneExeception("Rattachement impossible : le JSON est incomplet. Il doit contenir l'objet "
                    + "facture FNE avec son identifiant (id) et ses lignes (items).");
        }
        String fneInvoiceId = invoice.optString("id", null);
        JSONArray items = invoice.optJSONArray("items");
        if (StringUtils.isEmpty(fneInvoiceId) || Objects.isNull(items) || items.length() == 0) {
            throw new FneExeception("Rattachement impossible : le JSON est incomplet. Il doit contenir l'objet "
                    + "facture FNE avec son identifiant (id) et ses lignes (items).");
        }
        // L'endpoint public de la page QR renvoie aussi un bloc "company" contenant des donnees sensibles de
        // l'entreprise (dont la cle API) : on ne le stocke jamais en base.
        invoice.remove("company");
        // normalisation vers la forme de la reponse de certification {"reference":..., "invoice": {...}}
        String reference = StringUtils.defaultIfEmpty(json.optString("reference", null),
                invoice.optString("reference", null));
        JSONObject normalise = new JSONObject().put("reference", StringUtils.defaultString(reference)).put("invoice",
                invoice);
        saveFneInvoiceRecord(fneInvoiceId, FneInvoiceEntity.TYPE_SALE, reference, normalise.toString(), facture);

        List<FneAvoirItem> avoirItems = extractAvoirItems(normalise.toString());
        return new JSONObject().put("success", true).put("fneInvoiceId", fneInvoiceId).put("nbItems",
                avoirItems.size());
    }

    @Override
    public JSONObject recupererDepuisToken(String idFacture) throws FneExeception {
        TFacture facture = em.find(TFacture.class, idFacture);
        if (Objects.isNull(facture)) {
            throw new FneExeception("Facture introuvable : elle a peut-être été supprimée depuis "
                    + "l'affichage de la liste. Actualisez la liste et recommencez (référence technique " + idFacture
                    + ").");
        }
        FneInvoiceEntity existant = findLastRecordByType(idFacture, FneInvoiceEntity.TYPE_SALE);
        if (Objects.nonNull(existant)) {
            return new JSONObject().put("success", true).put("fneInvoiceId", existant.getId()).put("message",
                    "Les identifiants FNE de cette facture sont deja enregistres");
        }
        String tokenUuid = extractTokenUuid(facture.getFneUrl());
        if (StringUtils.isEmpty(tokenUuid)) {
            throw new FneExeception("Récupération impossible : aucune URL de vérification FNE n'est enregistrée "
                    + "pour la facture n° " + StringUtils.defaultString(facture.getStrCODEFACTURE())
                    + ". Elle n'a jamais été certifiée.");
        }

        String base = StringUtils.removeEnd(StringUtils.removeEnd(StringUtils.trimToEmpty(sp.fneUrl), "/"), "/sign");
        // L'endpoint public de la page QR ($ws/invoices/qr/{token}) retourne la facture complete avec l'id FNE et les
        // ids des lignes (constate sur l'environnement de test DGI) : c'est la voie principale. Les deux autres URLs
        // restent en secours.
        String wsRoot = StringUtils.removeEnd(base, "/external/invoices");
        List<String> urls = Arrays.asList(wsRoot + "/invoices/qr/" + tokenUuid, base + "/" + tokenUuid,
                facture.getFneUrl());
        Client client = getHttpClient();
        for (String url : urls) {
            try {
                Response response = client.target(url).request().header("Authorization", "Bearer ".concat(sp.fnePkey))
                        .accept(MediaType.APPLICATION_JSON).get();
                int status = response.getStatus();
                String raw = response.readEntity(String.class);
                LOG.log(Level.INFO, "FNE recuperation {0} ({1}) --- {2}", new Object[] { url, status, raw });
                if (status < 200 || status >= 300) {
                    continue;
                }
                // La plateforme repond parfois la PAGE HTML de verification au lieu du JSON de la
                // facture. Tenter de la parser levait un JSONException ("A JSONObject text must begin
                // with '{'") et remplissait le journal d'une trace complete pour un cas normal :
                // on reconnait le cas et on passe simplement a l'URL suivante.
                String debut = StringUtils.stripStart(StringUtils.trimToEmpty(raw), "﻿");
                if (!debut.startsWith("{")) {
                    LOG.log(Level.INFO, "FNE recuperation {0} : reponse non JSON, essai suivant", url);
                    continue;
                }
                JSONObject invoice = resolveInvoiceObject(new JSONObject(raw));
                if (Objects.isNull(invoice) || StringUtils.isEmpty(invoice.optString("id", null))
                        || Objects.isNull(invoice.optJSONArray("items"))) {
                    continue;
                }
                return rattacherFacture(idFacture, raw);
            } catch (FneExeception e) {
                throw e;
            } catch (Exception e) {
                LOG.log(Level.WARNING, "recupererDepuisToken: tentative " + url, e);
            }
        }
        throw new FneExeception("Cette facture n'a pas été générée avec la version actuelle : la récupération "
                + "automatique est donc impossible, la plateforme FNE n'expose pas le détail de la facture pour ce "
                + "token. Il faut passer par le rattachement manuel, avec le JSON de la facture (espace FNE ou "
                + "support.fne@dgi.gouv.ci).");
    }

    @Override
    public JSONObject releveFne(String tiersPayantId, String dtStart, String dtEnd) throws FneExeception {
        if (StringUtils.isEmpty(tiersPayantId)) {
            throw new FneExeception("Selectionnez un tiers payant");
        }
        TTiersPayant tiersPayant = em.find(TTiersPayant.class, tiersPayantId);
        if (Objects.isNull(tiersPayant)) {
            throw new FneExeception("Tiers payant introuvable : " + tiersPayantId);
        }
        Date debut = parseDateOrDefault(dtStart, "2000-01-01", false);
        Date fin = parseDateOrDefault(dtEnd, null, true);

        List<TFacture> factures = em.createQuery(
                "SELECT t FROM TFacture t WHERE t.strCUSTOMER = ?1 AND t.fneUrl IS NOT NULL AND (t.dtCREATED >= ?2 AND t.dtCREATED <= ?3) "
                        + "AND (t.template <> TRUE OR t.template IS NULL) ORDER BY t.dtCREATED",
                TFacture.class).setParameter(1, tiersPayantId).setParameter(2, debut).setParameter(3, fin)
                .getResultList();

        JSONArray rows = new JSONArray();
        long totalFactures = 0;
        long totalAvoirs = 0;
        for (TFacture facture : factures) {
            long montant = Objects.isNull(facture.getDblMONTANTCMDE()) ? 0 : Math.round(facture.getDblMONTANTCMDE());
            FneInvoiceEntity vente = findLastRecordByType(facture.getLgFACTUREID(), FneInvoiceEntity.TYPE_SALE);
            Date dateFacture = Objects.nonNull(facture.getDtDATEFACTURE()) ? facture.getDtDATEFACTURE()
                    : facture.getDtCREATED();
            rows.put(new JSONObject()
                    .put("date", Objects.nonNull(dateFacture) ? DateCommonUtils.format(dateFacture) : "")
                    .put("code", StringUtils.defaultString(facture.getStrCODEFACTURE())).put("type", "FACTURE")
                    .put("reference", Objects.nonNull(vente) ? StringUtils.defaultString(vente.getReference()) : "")
                    .put("montant", montant));
            totalFactures += montant;
            if (StringUtils.isNotEmpty(facture.getFneAvoirReference())) {
                FneInvoiceEntity avoir = findLastRecordByType(facture.getLgFACTUREID(), FneInvoiceEntity.TYPE_AVOIR);
                Date dateAvoir = Objects.nonNull(avoir) ? avoir.getMvtDate() : facture.getDtUPDATED();
                rows.put(new JSONObject()
                        .put("date", Objects.nonNull(dateAvoir) ? DateCommonUtils.format(dateAvoir) : "")
                        .put("code", StringUtils.defaultString(facture.getStrCODEFACTURE())).put("type", "AVOIR")
                        .put("reference", facture.getFneAvoirReference()).put("montant", -montant));
                totalAvoirs += montant;
            }
        }

        TOfficine officine = getOfficine();
        return new JSONObject().put("success", true)
                .put("officine", Objects.nonNull(officine) ? officine.getStrNOMCOMPLET() : "")
                .put("tiersPayant", StringUtils.defaultString(tiersPayant.getStrFULLNAME()))
                .put("periode", DateCommonUtils.format(debut) + " au " + DateCommonUtils.format(fin)).put("rows", rows)
                .put("totalFactures", totalFactures).put("totalAvoirs", totalAvoirs)
                .put("net", totalFactures - totalAvoirs);
    }

    /**
     * Parse une date yyyy-MM-dd des champs de periode ; borne de fin poussee a 23:59:59. Valeur par defaut appliquee si
     * le parametre est vide (fin par defaut : maintenant).
     */
    private Date parseDateOrDefault(String value, String defaut, boolean finDeJournee) {
        String candidat = StringUtils.defaultIfEmpty(StringUtils.trimToEmpty(value), defaut);
        if (StringUtils.isEmpty(candidat)) {
            return new Date();
        }
        try {
            java.time.LocalDate localDate = java.time.LocalDate.parse(candidat);
            java.time.LocalDateTime localDateTime = finDeJournee ? localDate.atTime(23, 59, 59)
                    : localDate.atStartOfDay();
            return Date.from(localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            return new Date();
        }
    }

    /**
     * Extrait l'UUID final d'une URL de verification FNE (http://.../fr/verification/{uuid}).
     */
    private String extractTokenUuid(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        String cleaned = StringUtils.removeEnd(url.trim(), "/");
        int idx = cleaned.lastIndexOf('/');
        return idx >= 0 ? cleaned.substring(idx + 1) : cleaned;
    }

    /**
     * IMPORTANT: Dans une vente complémentaire (assurance + carnet), il existe plusieurs lignes dans
     * t_preenregistrement_compte_client_tiers_payent pour le même préenregistrement. Il faut donc filtrer cp par le
     * tiers payant demandé, sinon le taux peut être pris sur l'autre prise en charge (ex: 70% au lieu de 30%).
     */
    private List<Item> getFactureMonatantByTva(String factureId, String tiersPayantId) {
        String sqlQuery = "SELECT d.valeurTva AS codeTva ,SUM(d.int_PRICE) AS montantTTCByCodeTva,cp.int_PERCENT AS taux "
                + "FROM t_facture_detail fd "
                + "JOIN t_preenregistrement_detail d ON d.lg_PREENREGISTREMENT_ID=fd.P_KEY "
                + "JOIN t_preenregistrement_compte_client_tiers_payent cp ON cp.lg_PREENREGISTREMENT_ID=fd.P_KEY "
                + "JOIN t_compte_client_tiers_payant cpt ON cpt.lg_COMPTE_CLIENT_TIERS_PAYANT_ID=cp.lg_COMPTE_CLIENT_TIERS_PAYANT_ID "
                + "JOIN t_tiers_payant tp ON tp.lg_TIERS_PAYANT_ID=cpt.lg_TIERS_PAYANT_ID "
                + "WHERE fd.lg_FACTURE_ID=?1 AND tp.lg_TIERS_PAYANT_ID=?2 " + "GROUP BY d.valeurTva,cp.int_PERCENT "
                + "ORDER BY d.valeurTva";
        Query query = em.createNativeQuery(sqlQuery, Tuple.class).setParameter(1, factureId).setParameter(2,
                tiersPayantId);
        List<Tuple> list = query.getResultList();
        return list.stream().map(t -> buildFromTuple(t)).collect(Collectors.toList());

    }

    /**
     * IMPORTANT: Correction pour vente complémentaire: filtrer le taux sur le tiers payant demandé via cp -> cpt -> tp,
     * et ne pas s'appuyer sur fact.tiersPayant.
     *
     * Le GROUP BY précédent pouvait "choisir" un taux au hasard (MariaDB/MySQL) lorsqu'il y avait plusieurs lignes cp
     * pour le même préenregistrement.
     */
    private List<VenteDetail> fetchVenteDetail(String factureId, String tiersPayantId) {
        String sqlQuery = "SELECT cp.int_PERCENT AS tauxCouverture,COALESCE(r.dbl_TAUX,0.0)  AS tauxRemise, "
                + "d.int_PRICE_UNITAIR AS montantTtc,d.int_QUANTITY AS quantity,d.valeurTva AS codeTva,"
                + "prod.int_CIP AS codeCip,prod.str_NAME AS libelle " + "FROM t_preenregistrement_detail d "
                + "JOIN t_famille prod ON d.lg_FAMILLE_ID=prod.lg_FAMILLE_ID "
                + "JOIN t_facture_detail fd ON d.lg_PREENREGISTREMENT_ID=fd.P_KEY "
                + "LEFT JOIN t_grille_remise r ON r.lg_GRILLE_REMISE_ID=d.lg_GRILLE_REMISE_ID "
                + "JOIN t_preenregistrement_compte_client_tiers_payent cp ON cp.lg_PREENREGISTREMENT_ID=fd.P_KEY "
                + "JOIN t_compte_client_tiers_payant cpt ON cpt.lg_COMPTE_CLIENT_TIERS_PAYANT_ID=cp.lg_COMPTE_CLIENT_TIERS_PAYANT_ID "
                + "JOIN t_tiers_payant tp ON tp.lg_TIERS_PAYANT_ID=cpt.lg_TIERS_PAYANT_ID "
                + "WHERE fd.lg_FACTURE_ID=?1 AND tp.lg_TIERS_PAYANT_ID=?2";

        Query query = em.createNativeQuery(sqlQuery, Tuple.class).setParameter(1, factureId).setParameter(2,
                tiersPayantId);
        List<Tuple> list = query.getResultList();
        return list.stream().map(t -> buildarnetDetailFromTuple(t)).collect(Collectors.toList());

    }

    /**
     * Identifiants numeriques d'une facture de groupe, transmis sous la forme "12_13_14".
     *
     * Tolerant par construction : une chaine vide, nulle, ou comportant un identifiant non numerique ne doit pas faire
     * echouer l'operation par une exception technique sans message (constate : un parametre vide arrivait jusqu'ici et
     * levait un NullPointerException, l'ecran affichait alors un motif de refus faux).
     */
    static Set<Integer> identifiantsDeGroupe(String idGroupeFactures) {
        Set<Integer> ids = new LinkedHashSet<>();
        if (StringUtils.isEmpty(idGroupeFactures)) {
            return ids;
        }
        for (String morceau : idGroupeFactures.split("_")) {
            String valeur = StringUtils.trimToEmpty(morceau);
            if (valeur.isEmpty()) {
                continue;
            }
            try {
                ids.add(Integer.valueOf(valeur));
            } catch (NumberFormatException e) {
                LOG.log(Level.WARNING, "identifiant de facture de groupe ignore : {0}", valeur);
            }
        }
        return ids;
    }

    private List<TFacture> fetchGroupeFactures(Set<Integer> idGroupeFactures) {
        // Un IN () vide n'est pas du SQL valide : on s'arrete avant la requete.
        if (Objects.isNull(idGroupeFactures) || idGroupeFactures.isEmpty()) {
            return new ArrayList<>();
        }
        TypedQuery<TFacture> typedQuery = em.createQuery(
                "SELECT o FROM  TFacture o WHERE o.lgFACTUREID IN ( SELECT g.lgFACTURESID FROM TGroupeFactures g WHERE g.id IN ?1 ) ",
                TFacture.class);
        typedQuery.setParameter(1, idGroupeFactures);
        return typedQuery.getResultList();

    }

    private Item buildFromTuple(Tuple tuple) {
        return new Item(tuple.get("codeTva", Integer.class),
                tuple.get("montantTTCByCodeTva", BigDecimal.class).intValue(),
                Utils.arrondiTauxCouverture(tuple.get("taux", Integer.class)));
    }

    private VenteDetail buildarnetDetailFromTuple(Tuple tuple) {

        return new VenteDetail(tuple.get("codeTva", Integer.class), tuple.get("montantTtc", Integer.class),
                tuple.get("quantity", Integer.class), tuple.get("libelle", String.class),
                tuple.get("codeCip", String.class), tuple.get("tauxRemise", Double.class),
                tuple.get("tauxCouverture", Integer.class));
    }

    private List<FneInvoiceItem> buildFromProduitCodeTva(TFacture facture, String tiersPayantId) {
        List<Item> itemsByCodeTvaAndByTaux = getFactureMonatantByTva(facture.getLgFACTUREID(), tiersPayantId);

        List<FneInvoiceItem> fneInvoiceItems = new ArrayList<>();
        String codeFacture = facture.getStrCODEFACTURE();
        String description = "FACTURATION DU " + DateCommonUtils.format(facture.getDtDEBUTFACTURE()) + " AU "
                + DateCommonUtils.format(facture.getDtFINFACTURE());
        Map<Integer, List<Item>> codeTvaMap = itemsByCodeTvaAndByTaux.stream()
                .collect(Collectors.groupingBy(Item::getCodeTva));
        codeTvaMap.forEach((codeTva, values) -> {

            FneInvoiceItem invoiceItem = new FneInvoiceItem();
            invoiceItem.setDescription(description);
            invoiceItem.setReference(codeFacture);
            TaxeEnum taxeEnum = TaxeEnum.getByValue(codeTva);
            invoiceItem.setTaxes(new String[] { taxeEnum.name() });
            invoiceItem.setAmount(computeMontantByTvaAndTaux(codeTva, values));

            fneInvoiceItems.add(invoiceItem);

        });

        return fneInvoiceItems;
    }

    /**
     * Problème probable pour des vente avec remise, prix de reference, des facture avec remise forfetaire
     *
     * @param itemsByTva
     *
     * @return
     */
    private double computeMontantByTvaAndTaux(int codeTva, List<Item> itemsByTva) {

        AtomicDouble montantAtomicHt = new AtomicDouble(0);

        Map<Integer, List<Item>> tauxMap = itemsByTva.stream().collect(Collectors.groupingBy(Item::getTaux));
        tauxMap.forEach((tauxAssure, values) -> {
            int totalTtc = values.stream().mapToInt(Item::getMontantTtc).sum();
            montantAtomicHt.addAndGet(computeTpAmount(totalTtc, codeTva, tauxAssure));

        });
        return montantAtomicHt.get();

    }

    private double computeTpAmount(int totalTtc, int codeTva, int tauxAssure) {
        double montantHt = Utils.calculHt(totalTtc, codeTva);
        return BigDecimal.valueOf(montantHt).multiply(BigDecimal.valueOf(tauxAssure / 100.f))
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    // 1428351F
    private class Item {

        private final int codeTva;
        private final int montantTtc;
        private final int taux;

        public int getCodeTva() {
            return codeTva;
        }

        public int getMontantTtc() {
            return montantTtc;
        }

        public Item(int codeTva, int montantTtc, int taux) {
            this.codeTva = codeTva;
            this.montantTtc = montantTtc;
            this.taux = taux;

        }

        public int getTaux() {
            return taux;
        }

        @Override
        public String toString() {
            return "Item{" + "codeTva=" + codeTva + ", montantTtc=" + montantTtc + ", taux=" + taux + '}';
        }

    }

    private class VenteDetail {

        private final int codeTva;
        private final int montantTtc;
        private final int quantity;
        private final String libelle;
        private final String codeCip;
        private final Double tauxRemise;
        private final int tauxCouverture;

        public VenteDetail(int codeTva, int montantTtc, int quantity, String libelle, String codeCip, Double tauxRemise,
                int tauxCouverture) {
            this.codeTva = codeTva;
            this.montantTtc = montantTtc;
            this.quantity = quantity;
            this.libelle = libelle;
            this.codeCip = codeCip;
            this.tauxRemise = tauxRemise;
            this.tauxCouverture = tauxCouverture;
        }

        public String getCodeCip() {
            return codeCip;
        }

        public Double getTauxRemise() {
            return tauxRemise;
        }

        public int getTauxCouverture() {
            return tauxCouverture;
        }

        public int getCodeTva() {
            return codeTva;
        }

        public int getMontantTtc() {
            return montantTtc;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getLibelle() {
            return libelle;
        }

    }
}
