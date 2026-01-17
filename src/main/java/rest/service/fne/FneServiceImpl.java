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
        JSONObject payload = new JSONObject(buildFromFacture(facture, officine));

        WebTarget myResource = client.target(sp.fneUrl);
        Response response = myResource.request().header("Authorization", "Bearer ".concat(sp.fnePkey))
                .post(Entity.entity(payload.toString(), MediaType.APPLICATION_JSON_TYPE));
        // String fneResponse = response.readEntity(String.class);

        FneResponse fneResponse = response.readEntity(FneResponse.class);
        LOG.log(Level.INFO, "response --- {0}", fneResponse);
        saveResponse(fneResponse, facture);

    }

    private TOfficine getOfficine() {
        return em.find(TOfficine.class, Constant.OFFICINE);
    }

    private FneInvoice buildFromFacture(TFacture facture, TOfficine officine) {

        TTiersPayant tTiersPayant = facture.getTiersPayant();
        FneInvoice fneInvoice = new FneInvoice();
        fneInvoice.setEstablishment(officine.getStrNOMCOMPLET());
        fneInvoice.setClientCompanyName(tTiersPayant.getStrFULLNAME());
        fneInvoice.setClientEmail(tTiersPayant.getStrMAIL());
        fneInvoice.setClientPhone(tTiersPayant.getStrTELEPHONE());
        fneInvoice.setPointOfSale(sp.fnepointOfSale);
        fneInvoice.setClientNcc(tTiersPayant.getStrCOMPTECONTRIBUABLE());
        List<FneInvoiceItem> fneInvoiceItems = buildFromProduitCodeTva(facture);
        fneInvoice.setItems(fneInvoiceItems);

        // Pour des logs de tests
        // int montantTotalFne = fneInvoiceItems.stream().mapToInt(FneInvoiceItem::getAmount).sum();
        // LOG.info(String.format("montantHt fne: %d monantFacture: %s", montantTotalFne, facture.getDblMONTANTCMDE() +
        // ""));
        // facture.getTFactureDetailCollection().forEach(t -> fneInvoice.getItems().add(buildFrom(t)));//Flatten by code
        // tva
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

    private List<Item> getFactureMonatantByTva(String factureId) {
        Query query = em.createNativeQuery(
                "SELECT d.valeurTva AS codeTva ,SUM(d.int_PRICE) AS montantTTCByCodeTva,cp.int_PERCENT AS taux FROM t_facture_detail fd JOIN t_preenregistrement_detail d ON d.lg_PREENREGISTREMENT_ID=fd.P_KEY JOIN t_preenregistrement_compte_client_tiers_payent cp ON cp.lg_PREENREGISTREMENT_ID=fd.P_KEY  WHERE fd.lg_FACTURE_ID=?1  GROUP  BY d.valeurTva,cp.int_PERCENT",
                Tuple.class).setParameter(1, factureId);
        List<Tuple> list = query.getResultList();
        return list.stream().map(t -> buildFromTuple(t)).collect(Collectors.toList());

    }

    private Item buildFromTuple(Tuple tuple) {
        return new Item(tuple.get("codeTva", Integer.class),
                tuple.get("montantTTCByCodeTva", BigDecimal.class).intValue(), tuple.get("taux", Integer.class));
    }

    private List<FneInvoiceItem> buildFromProduitCodeTva(TFacture facture) {
        List<Item> itemsByCodeTvaAndByTaux = getFactureMonatantByTva(facture.getLgFACTUREID());

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

    // 1428351F
    private class Item {

        private final int codeTva;
        private final int montantTtc;
        private final int taux;
        // private final int remise;

        public int getCodeTva() {
            return codeTva;
        }

        public int getMontantTtc() {
            return montantTtc;
        }

        public Item(int codeTva, int montantTtc, int taux/* , int remise */) {
            this.codeTva = codeTva;
            this.montantTtc = montantTtc;
            this.taux = taux;
            // this.remise=remise;
        }

        /*
         * public int getRemise() { return remise; }
         */
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
