package rest.service.fne;

import com.google.common.util.concurrent.AtomicDouble;
import dal.FneTiersPayantInvoice;
import dal.TFacture;
import dal.TOfficine;
import dal.TTiersPayant;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;
import rest.service.exception.FneExeception;
import util.AppParameters;
import util.Constant;
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

    // =========================
    // ASSURANCE (B2B) - EXISTANT
    // =========================
    @Override
    public void createInvoice(String idFacture) throws FneExeception {
        try {
            createInvoice(em.find(TFacture.class, idFacture));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            throw new FneExeception(e.getLocalizedMessage());
        }
    }

    private void createInvoice(TFacture facture) throws FneExeception {
        TOfficine officine = getOfficine();
        Client client = getHttpClient();

        // ✅ build + règles B2B (NCC obligatoire)
        FneInvoice invoice = buildFromFacture(facture, officine);
        invoice.setTemplate("B2B");

        JSONObject payload = buildPayload(invoice);

        WebTarget myResource = client.target(sp.fneUrl);
        Response response = myResource.request().header("Authorization", "Bearer ".concat(sp.fnePkey))
                .post(Entity.entity(payload.toString(), MediaType.APPLICATION_JSON_TYPE));

        FneResponse fneResponse = response.readEntity(FneResponse.class);
        LOG.log(Level.INFO, "response --- {0}", fneResponse);
        saveResponse(fneResponse, facture);
    }

    // =========================
    // CARNET (B2C) - NOUVEAU
    // =========================
    @Override
    public void createInvoiceItems(String idFacture) throws FneExeception {
        try {
            createInvoiceItems(em.find(TFacture.class, idFacture));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            throw new FneExeception(e.getLocalizedMessage());
        }
    }

    private void createInvoiceItems(TFacture facture) throws FneExeception {
        TOfficine officine = getOfficine();
        Client client = getHttpClient();

        // ✅ build + règles B2C (pas de NCC)
        FneInvoice invoice = buildFromFactureItems(facture, officine);
        invoice.setTemplate("B2C");

        JSONObject payload = buildPayload(invoice);

        WebTarget myResource = client.target(sp.fneUrl);
        Response response = myResource.request().header("Authorization", "Bearer ".concat(sp.fnePkey))
                .post(Entity.entity(payload.toString(), MediaType.APPLICATION_JSON_TYPE));

        FneResponse fneResponse = response.readEntity(FneResponse.class);
        LOG.log(Level.INFO, "response --- {0}", fneResponse);
        saveResponse(fneResponse, facture);
    }

    // =========================
    // PAYLOAD RULES (B2B/B2C)
    // =========================
    private JSONObject buildPayload(FneInvoice invoice) {

        JSONObject payload = new JSONObject(invoice);

        String template = payload.optString("template", "B2B").trim();
        if (template.isEmpty())
            template = "B2B";
        payload.put("template", template);

        String ncc = payload.optString("clientNcc", "").trim();

        if ("B2C".equalsIgnoreCase(template)) {

            // ✅ B2C: NCC facultatif -> on l'envoie seulement s'il est renseigné
            if (ncc.isEmpty()) {
                payload.remove("clientNcc");
            } else {
                payload.put("clientNcc", ncc); // normalise (trim)
            }

            String name = payload.optString("clientCompanyName", "").trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("clientCompanyName obligatoire pour une facture B2C (carnet)");
            }

        } else if ("B2B".equalsIgnoreCase(template)) {

            // ✅ B2B: NCC obligatoire
            if (ncc.isEmpty()) {
                throw new IllegalArgumentException("clientNcc obligatoire pour une facture B2B (assurance)");
            }
            payload.put("clientNcc", ncc); // normalise (trim)
        }

        return payload;
    }

    private TOfficine getOfficine() {
        return em.find(TOfficine.class, Constant.OFFICINE);
    }

    // =========================
    // BUILDERS
    // =========================
    private FneInvoice buildFromFacture(TFacture facture, TOfficine officine) {

        TTiersPayant tTiersPayant = facture.getTiersPayant();
        FneInvoice fneInvoice = new FneInvoice();

        fneInvoice.setEstablishment(officine.getStrNOMCOMPLET());
        fneInvoice.setClientCompanyName(tTiersPayant.getStrFULLNAME());
        fneInvoice.setClientEmail(tTiersPayant.getStrMAIL());
        fneInvoice.setClientPhone(tTiersPayant.getStrTELEPHONE());
        fneInvoice.setPointOfSale(sp.fnepointOfSale);

        // ✅ Assurance/B2B : NCC requis -> buildPayload valide
        fneInvoice.setClientNcc(tTiersPayant.getStrCOMPTECONTRIBUABLE());

        List<FneInvoiceItem> fneInvoiceItems = buildFromProduitCodeTva(facture);
        fneInvoice.setItems(fneInvoiceItems);

        return fneInvoice;
    }

    private FneInvoice buildFromFactureItems(TFacture facture, TOfficine officine) {

        TTiersPayant tTiersPayant = facture.getTiersPayant();
        FneInvoice fneInvoice = new FneInvoice();

        fneInvoice.setEstablishment(officine.getStrNOMCOMPLET());
        fneInvoice.setPointOfSale(sp.fnepointOfSale);

        // ✅ Carnet/B2C : identification = nom carnet/tiers payant
        fneInvoice.setClientCompanyName(tTiersPayant.getStrFULLNAME());
        fneInvoice.setClientEmail(tTiersPayant.getStrMAIL());
        fneInvoice.setClientPhone(tTiersPayant.getStrTELEPHONE());

        // 🚫 Pas de NCC en B2C (buildPayload supprime au cas où)
        String ncc = tTiersPayant.getStrCOMPTECONTRIBUABLE();
        if (ncc != null && !ncc.trim().isEmpty()) {
            fneInvoice.setClientNcc(ncc.trim());
        }
        fneInvoice.setItems(buildFromProduitsAvecTva(facture));

        return fneInvoice;
    }

    private Client getHttpClient() {
        return ClientBuilder.newClient();
    }

    private void saveResponse(FneResponse fneResponse, TFacture facture) {
        FneTiersPayantInvoice fne = new FneTiersPayantInvoice();
        fne.setFacture(facture);
        fne.getResponses().add(fneResponse);
        em.persist(fne);
    }

    // =========================
    // ASSURANCE: AGRÉGATION PAR TVA (EXISTANT)
    // =========================
    private List<Item> getFactureMonatantByTva(String factureId, String tiersPayantId) {
        String sqlQuery = "SELECT d.valeurTva AS codeTva ,SUM(d.int_PRICE) AS montantTTCByCodeTva,cp.int_PERCENT AS taux FROM t_facture_detail fd JOIN t_preenregistrement_detail d ON d.lg_PREENREGISTREMENT_ID=fd.P_KEY JOIN t_preenregistrement_compte_client_tiers_payent cp JOIN t_compte_client_tiers_payant cpt ON cpt.lg_COMPTE_CLIENT_TIERS_PAYANT_ID=cp.lg_COMPTE_CLIENT_TIERS_PAYANT_ID "
                + " JOIN t_tiers_payant tp ON tp.lg_TIERS_PAYANT_ID=cpt.lg_TIERS_PAYANT_ID ON cp.lg_PREENREGISTREMENT_ID=fd.P_KEY  WHERE fd.lg_FACTURE_ID=?1 AND tp.lg_TIERS_PAYANT_ID=?2 GROUP  BY d.valeurTva,cp.int_PERCENT ORDER BY d.valeurTva";
        Query query = em.createNativeQuery(sqlQuery, Tuple.class).setParameter(1, factureId).setParameter(2,
                tiersPayantId);

        List<Tuple> list = query.getResultList();
        return list.stream().map(this::buildFromTuple).collect(Collectors.toList());
    }

    private Item buildFromTuple(Tuple tuple) {
        return new Item(tuple.get("codeTva", Integer.class),
                tuple.get("montantTTCByCodeTva", BigDecimal.class).intValue(),
                arrondiTauxCouverture(tuple.get("taux", Integer.class)));
    }

    private List<FneInvoiceItem> buildFromProduitCodeTva(TFacture facture) {
        List<Item> itemsByCodeTvaAndByTaux = getFactureMonatantByTva(facture.getLgFACTUREID(),
                facture.getTiersPayant().getLgTIERSPAYANTID());

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
            double montantHt = calculHt(totalTtc, codeTva);
            double partAssurence = BigDecimal.valueOf(montantHt).multiply(BigDecimal.valueOf(tauxAssure / 100.f))
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
            montantAtomicHt.addAndGet(partAssurence);
        });

        return montantAtomicHt.get();
    }

    public int arrondiTauxCouverture(int taux) {
        int arrondi = Math.round(taux / 5f) * 5;
        return Math.min(100, arrondi);
    }

    // =========================
    // CARNET: DÉTAIL PRODUITS + TVA (NOUVEAU)
    // =========================
    private static class ProduitItem {
        private final String cip;
        private final String description;
        private final int priceTtcUnit;
        private final int quantity;
        private final int tvaValue;

        ProduitItem(String cip, String description, int priceTtcUnit, int quantity, int tvaValue) {
            this.cip = cip;
            this.description = description;
            this.priceTtcUnit = priceTtcUnit;
            this.quantity = quantity;
            this.tvaValue = tvaValue;
        }
    }

    private List<ProduitItem> getFactureItems(String factureId) {

        String sql = "SELECT f.int_CIP AS cip, f.str_DESCRIPTION AS description, f.int_PRICE AS price, "
                + "       pd.int_QUANTITY AS quantity, c.int_VALUE AS tvaValue " + "FROM t_facture fa "
                + "JOIN t_facture_detail fd ON fa.lg_FACTURE_ID = fd.lg_FACTURE_ID "
                + "JOIN t_preenregistrement_detail pd ON fd.P_KEY = pd.lg_PREENREGISTREMENT_ID "
                + "JOIN t_famille f ON f.lg_FAMILLE_ID = pd.lg_FAMILLE_ID "
                + "JOIN t_code_tva c ON f.lg_CODE_TVA_ID = c.lg_CODE_TVA_ID " + "WHERE fa.lg_FACTURE_ID = ?1";

        Query q = em.createNativeQuery(sql, Tuple.class).setParameter(1, factureId);
        List<Tuple> rows = q.getResultList();

        return rows.stream()
                .map(t -> new ProduitItem(String.valueOf(t.get("cip")), t.get("description", String.class),
                        ((Number) t.get("price")).intValue(), ((Number) t.get("quantity")).intValue(),
                        ((Number) t.get("tvaValue")).intValue()))
                .collect(Collectors.toList());
    }

    private List<FneInvoiceItem> buildFromProduitsAvecTva(TFacture facture) {

        List<ProduitItem> produits = getFactureItems(facture.getLgFACTUREID());
        List<FneInvoiceItem> items = new ArrayList<>(produits.size());

        for (ProduitItem p : produits) {
            FneInvoiceItem it = new FneInvoiceItem();
            it.setReference(p.cip);
            it.setDescription(p.description);
            it.setQuantity(p.quantity);
            double ht = calculHt(p.priceTtcUnit, p.tvaValue); // HT unitaire
            it.setAmount(round2(ht));
            it.setTaxes(new String[] { TaxeEnum.getByValue(p.tvaValue).name() });
            items.add(it);
        }

        return items;
    }

    private double round2(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    // =========================
    // INNER TYPES
    // =========================
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

    private double calculHt(int ttc, int tva) {
        return (ttc) * 1.0 / (1 + (tva / 100.f));
    }
}
