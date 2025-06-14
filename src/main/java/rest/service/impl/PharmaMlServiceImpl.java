/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.PharmaMLItemDTO;
import dal.Rupture;
import dal.TFamille;
import dal.TFamilleGrossiste;
import dal.TGrossiste;
import dal.TOfficine;
import dal.TOrder;
import dal.TOrderDetail;
import dal.TUser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import rest.service.OrderService;
import rest.service.PharmaMlService;
import rest.service.pharmaMl.Commande;
import rest.service.pharmaMl.Corps;
import rest.service.pharmaMl.CsrpEnveloppe;
import rest.service.pharmaMl.Entete;
import rest.service.pharmaMl.LigneN;
import rest.service.pharmaMl.MessageCorps;
import rest.service.pharmaMl.MessageEntete;
import rest.service.pharmaMl.MessageOfficine;
import rest.service.pharmaMl.Normale;
import rest.service.pharmaMl.OfficinePartenaire;
import rest.service.pharmaMl.Partenaire;
import toolkits.utils.jdom;
import util.AppParameters;
import util.Constant;
import util.DateConverter;
import util.PharmaMlUtils;
import static util.PharmaMlUtils.NATURE_PARTENAIRE_VALUE_OF;
import static util.PharmaMlUtils.NOMBRE_LIGNE_CLAIRE;
import static util.PharmaMlUtils.NOMBRE_LIGNE_CODE;
import static util.PharmaMlUtils.TYPE_CODIFICATION_CIP;
import static util.PharmaMlUtils.TYPE_CODIFICATION_CIP39;
import static util.PharmaMlUtils.TYPE_CODIFICATION_EAN13;
import static util.PharmaMlUtils.TYPE_CODIFICATION_LIBELLE_PRODUIT;
import static util.PharmaMlUtils.buildBody;
import static util.PharmaMlUtils.buildCommandeLine;
import static util.PharmaMlUtils.buildComment;
import static util.PharmaMlUtils.buildDateLivraisonLine;
import static util.PharmaMlUtils.buildRepartiteurLine;
import static util.PharmaMlUtils.buildTypeTravailLine;
import static util.PharmaMlUtils.finCommande;

/**
 *
 * @author kkoffi
 */
@Stateless
public class PharmaMlServiceImpl implements PharmaMlService {

    private static final Logger LOG = Logger.getLogger(PharmaMlServiceImpl.class.getName());
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    private final String message_url = jdom.pharmaml_message;
    private final String reponse_url = jdom.pharmaml_reponse;
    private final Comparator<File> fileComparator = Comparator.comparing(File::lastModified);
    @EJB
    private OrderService orderService;
    private final AppParameters ap = AppParameters.getInstance();

    public EntityManager getEntityManager() {
        return em;
    }

    private List<TOrderDetail> findByOrder(TOrder order) {
        TypedQuery<TOrderDetail> q = getEntityManager().createNamedQuery("TOrderDetail.findByLgORDERID",
                TOrderDetail.class);
        q.setParameter("lgORDERID", order);
        return q.getResultList();
    }

    private TFamilleGrossiste findByGrossisteAndFamille(String idProduit, String grossisteId) {
        try {
            TypedQuery<TFamilleGrossiste> q = getEntityManager().createQuery(
                    "SELECT o FROM TFamilleGrossiste o WHERE o.lgFAMILLEID.lgFAMILLEID=?1 AND o.lgGROSSISTEID.lgGROSSISTEID=?2 AND o.strSTATUT='enable' ",
                    TFamilleGrossiste.class);
            q.setParameter(1, idProduit);
            q.setParameter(2, grossisteId);
            q.setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {
            // LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }

    /**
     *
     * @param commandeId
     * @param dateLivraisonSouhaitee
     * @param typeCommande
     * @param typeCommandeExecptionel
     * @param commentaire
     *
     * @return Le code de commande forme a partir de la reference de la commande sans le separeut <<_>>
     */
    @Override
    public JSONObject envoiPharmaCommande(String commandeId, LocalDate dateLivraisonSouhaitee, int typeCommande,
            String typeCommandeExecptionel, String commentaire) {
        JSONObject json = new JSONObject();
        TOrder order = getEntityManager().find(TOrder.class, commandeId);
        TGrossiste tg = order.getLgGROSSISTEID();
        List<PharmaMLItemDTO> itemDTO = new ArrayList<>();
        findByOrder(order).stream().forEach(d -> {
            TFamille tf = d.getLgFAMILLEID();
            PharmaMLItemDTO o = new PharmaMLItemDTO(d, tf,
                    findByGrossisteAndFamille(tf.getLgFAMILLEID(), tg.getLgGROSSISTEID()), false, false, false,
                    PharmaMlUtils.TYPE_CODIFICATION_CIP);
            itemDTO.add(o);
        });
        try {
            buildCommande(message_url + order.getStrREFORDER(), tg.getIdRepartiteur(),
                    PharmaMlUtils.TYPE_TRAVAIL_COMMANDE, dateLivraisonSouhaitee, typeCommande, typeCommandeExecptionel,
                    itemDTO, commentaire, order.getStrREFORDER().replace("_", ""));
            return json.put("success", true);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
            return json.put("success", true);
        }

    }

    public void buildCommande(String pathPharmaMl, String idRepartiteur, String typeTravail,
            LocalDate dateLivraisonSouhaitee, int typeCommande, String codeCommande, List<PharmaMLItemDTO> itemDTO,
            String commentaire, String commandeRef) throws IOException {
        NOMBRE_LIGNE_CODE = 0;
        NOMBRE_LIGNE_CLAIRE = 0;
        FileWriter fileWriter = new FileWriter(pathPharmaMl);
        try (PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(buildRepartiteurLine(idRepartiteur));
            printWriter.println(buildTypeTravailLine(PharmaMlUtils.TYPE_TRAVAIL_COMMANDE));
            printWriter.println(buildCommandeLine(typeCommande, codeCommande, commandeRef));
            printWriter.println(buildDateLivraisonLine(dateLivraisonSouhaitee));
            buildBody(itemDTO).forEach(w -> printWriter.println(w));
            if (!StringUtils.isEmpty(commentaire)) {
                printWriter.println(buildComment(commentaire));
            }
            printWriter.println(buildComment(null));
            printWriter.println(finCommande());
        }

    }

    @Override
    public JSONObject envoiPharmaInfosProduit(String commandeId) {
        JSONObject json = new JSONObject();
        TOrder order = getEntityManager().find(TOrder.class, commandeId);
        TGrossiste tg = order.getLgGROSSISTEID();
        List<PharmaMLItemDTO> itemDTO = new ArrayList<>();
        findByOrder(order).stream().forEach(d -> {
            TFamille tf = d.getLgFAMILLEID();
            PharmaMLItemDTO o = new PharmaMLItemDTO(d, tf,
                    findByGrossisteAndFamille(tf.getLgFAMILLEID(), tg.getLgGROSSISTEID()), false, false, false,
                    PharmaMlUtils.TYPE_CODIFICATION_CIP);
            itemDTO.add(o);
        });
        try {
            buildInfoProduits(message_url + order.getStrREFORDER(), tg.getIdRepartiteur(),
                    RandomStringUtils.randomNumeric(PharmaMlUtils.DEF_COUNT), itemDTO);
            return json.put("success", true);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
            return json.put("success", true);
        }

    }

    private void buildInfoProduits(String pathPharmaMl, String idRepartiteur, String codeRequete,
            List<PharmaMLItemDTO> itemDTO) throws IOException {
        NOMBRE_LIGNE_CODE = 0;
        NOMBRE_LIGNE_CLAIRE = 0;
        FileWriter fileWriter = new FileWriter(pathPharmaMl);
        try (PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(buildRepartiteurLine(idRepartiteur));
            printWriter.println(buildTypeTravailLine(PharmaMlUtils.TYPE_TRAVAIL_INFOS_PRODUITS));
            printWriter.println(PharmaMlUtils.buildReferenceDemandeInfosProduit(codeRequete));
            PharmaMlUtils.buildBodyInfosProduit(itemDTO).forEach(w -> printWriter.println(w));
            printWriter.println(finCommande());
        }
    }

    private void processInfoProduit() {
        // https://www.ntppool.org/en/
    }

    File findCommandeResponse(String filePath) {
        File messageDir = new File(filePath);
        File[] responses = messageDir.listFiles();
        if (responses.length == 0) {
            return null;
        }
        List<File> files = Stream.of(responses).sorted(fileComparator.reversed())
                .filter(x -> DateConverter.convertLongToLacalDate(x.lastModified()).isEqual(LocalDate.now()))
                .collect(Collectors.toList());
        if (files.isEmpty()) {
            return null;
        }
        File finalFile = files.get(0);
        return finalFile;
    }

    private Pair<List<PharmaMLItemDTO>, List<PharmaMLItemDTO>> processCommande(File finalFile, String __refCommande) {
        List<PharmaMLItemDTO> prisEncompte = new ArrayList<>();
        List<PharmaMLItemDTO> ruptures = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(finalFile.getAbsolutePath()));
            String isCommande = lines.get(1);
            if (!isCommande.substring(1, 2).contentEquals(PharmaMlUtils.TYPE_TRAVAIL_COMMANDE)) {
                return Pair.of(Collections.emptyList(), Collections.emptyList());
            }
            String refCommande = lines.get(2);
            if (refCommande.substring(1, __refCommande.length()).equals(__refCommande.replace("_", ""))) {
                lines.subList(2, lines.size()).stream().forEach(x -> {
                    if (x.startsWith(PharmaMlUtils.RECEPTION_PRODUIT)) {
                        PharmaMLItemDTO o = gererLigneCommande(x);
                        if (o.isLivre()) {
                            prisEncompte.add(o);
                        } else {
                            ruptures.add(o);
                        }
                    }
                });
            }

        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return Pair.of(prisEncompte, ruptures);
    }

    private PharmaMLItemDTO gererLigneCommande(String x) {
        PharmaMLItemDTO tO = new PharmaMLItemDTO();
        int qty = Integer.valueOf(x.substring(1, 5));
        int lastIndex = x.lastIndexOf(PharmaMlUtils.R);
        int lastIndexSeparateurP = x.lastIndexOf(PharmaMlUtils.P);
        String typeCodif = x.substring(5, 7).trim();
        String codeRetour = x.substring(lastIndex + 1, lastIndex + 5).trim();
        int _codeRetour = 0, prixAchatHtaxe = 0;
        if (!StringUtils.isEmpty(codeRetour)) {
            _codeRetour = Integer.valueOf(codeRetour);
            tO.setLivre(false);
        }
        if (lastIndexSeparateurP > 0) {
            String phaht = x.substring(lastIndexSeparateurP + 1, x.length()).trim();
            if (!StringUtils.isEmpty(phaht)) {
                prixAchatHtaxe = Integer.parseInt(phaht);
            }
        }
        tO.setCodeRetour(_codeRetour);
        tO.setQuantite(qty);
        tO.setTypeCodification(typeCodif);
        tO.setPrixAchat(prixAchatHtaxe);
        switch (typeCodif) {
        case TYPE_CODIFICATION_CIP:
            tO.setCip(x.substring(7, lastIndex).trim());
            break;
        case TYPE_CODIFICATION_EAN13:
            tO.setEan(x.substring(7, lastIndex).trim());
            break;
        case TYPE_CODIFICATION_LIBELLE_PRODUIT:
            tO.setLibelle(x.substring(7, lastIndex).trim());
            break;
        default:
            break;

        }
        return tO;
    }

    void removeItemsFromOrder(List<PharmaMLItemDTO> items, TOrder order) {
        try {
            final String orderId = order.getLgORDERID();
            Rupture rupture = orderService.creerRupture(order);
            items.forEach(x -> {
                TOrderDetail o = orderService.findByCipAndOrderId(x.getCip(), orderId);
                orderService.creerRuptureItem(rupture, o.getLgFAMILLEID(), o.getIntNUMBER());
                getEntityManager().remove(o);
            });
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }

    }

    Integer orderItemsFromPharmaMLItemDTO(List<PharmaMLItemDTO> items, final String orderId) {
        LongAdder adder = new LongAdder();
        items.forEach(x -> {
            TOrderDetail o = orderService.findByCipAndOrderId(x.getCip(), orderId);
            o.setIntNUMBER(x.getQuantite());
            if (x.getPrixAchat() > 0) {
                o.setPrixAchat(x.getPrixAchat());
            } else {
                o.setPrixAchat(o.getIntPAFDETAIL());
            }
            o.setPrixUnitaire(o.getIntPRICEDETAIL());
            o.setIntPRICE(o.getIntNUMBER() * o.getPrixAchat());
            adder.add(o.getIntPRICE());
            o.setIntORERSTATUS((short) 3);
            o.setStrSTATUT(DateConverter.PASSE);
            getEntityManager().merge(o);
        });
        return adder.intValue();
    }

    @Override
    public JSONObject lignesCommandeRetour(String commandeRef, String orderId) {
        try {
            TOrder order = orderService.findByRef(commandeRef, orderId);
            if (order.getStrSTATUT().equalsIgnoreCase(DateConverter.PASSE)) {
                return new JSONObject().put("success", true).put("nbreproduit", "Commande dejà traitée")
                        .put("nbrerupture", 0);
            }
            File finalFile = findCommandeResponse(reponse_url);
            if (finalFile == null) {
                return new JSONObject().put("success", false).put("status", "responseNotFound");
            }
            Pair<List<PharmaMLItemDTO>, List<PharmaMLItemDTO>> p = processCommande(finalFile, order.getStrREFORDER());
            List<PharmaMLItemDTO> prisEncompte = p.getLeft();
            List<PharmaMLItemDTO> rupture = p.getRight();
            if (!rupture.isEmpty()) {
                removeItemsFromOrder(rupture, order);
            }
            if (!prisEncompte.isEmpty()) {
                Integer montantCommande = orderItemsFromPharmaMLItemDTO(prisEncompte, order.getLgORDERID());
                order.setDtUPDATED(new Date());
                order.setIntPRICE(montantCommande);
                order.setStrSTATUT(DateConverter.PASSE);
                getEntityManager().merge(order);
            } else {
                if (!rupture.isEmpty()) {
                    getEntityManager().remove(order);
                }
            }
            return new JSONObject().put("success", true).put("nbreproduit", prisEncompte.size()).put("nbrerupture",
                    rupture.size());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new JSONObject().put("success", false);
        }

    }

    @Override
    public JSONObject renvoiPharmaCommande(String ruptureId, String grossiste, LocalDate dateLivraisonSouhaitee,
            int typeCommande, String typeCommandeExecptionel, String commentaire) {
        JSONObject json = new JSONObject();
        Rupture order = getEntityManager().find(Rupture.class, ruptureId);
        TGrossiste envoiGr = getEntityManager().find(TGrossiste.class, grossiste);
        order.setGrossiste(envoiGr);
        this.getEntityManager().merge(order);
        // TGrossiste tg = order.getGrossiste();
        List<PharmaMLItemDTO> itemDTO = new ArrayList<>();
        orderService.ruptureDetaisDtoByRupture(ruptureId).forEach(d -> {
            TFamille tf = d.getProduit();
            PharmaMLItemDTO o = new PharmaMLItemDTO(d, tf, orderService.findOrCreateFamilleGrossiste(tf, envoiGr),
                    false, false, false, PharmaMlUtils.TYPE_CODIFICATION_CIP);
            itemDTO.add(o);
        });
        try {
            buildCommande(message_url + order.getReference(), envoiGr.getIdRepartiteur(),
                    PharmaMlUtils.TYPE_TRAVAIL_COMMANDE, dateLivraisonSouhaitee, typeCommande, typeCommandeExecptionel,
                    itemDTO, commentaire, order.getReference().replace("_", ""));
            return json.put("success", true);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
            return json.put("success", true);
        }
    }

    @Override
    public JSONObject reponseRupture(String ruptureId, TUser u) {
        try {
            Rupture rup = getEntityManager().find(Rupture.class, ruptureId);
            File finalFile = findCommandeResponse(reponse_url);
            if (finalFile == null) {
                return new JSONObject().put("success", false).put("status", "responseNotFound");
            }
            Pair<List<PharmaMLItemDTO>, List<PharmaMLItemDTO>> p = processCommande(finalFile, rup.getReference());
            List<PharmaMLItemDTO> prisEncompte = p.getLeft();
            List<PharmaMLItemDTO> rupture = p.getRight();

            if (!prisEncompte.isEmpty()) {
                TGrossiste grossiste = rup.getGrossiste();
                TOrder order = orderService.createOrder(grossiste, u);
                orderItemsFromPharmaMLItemDTO(prisEncompte, order, rup.getId());
                getEntityManager().persist(order);
            }

            if (rupture.isEmpty()) {
                this.getEntityManager().remove(rup);
            }
            return new JSONObject().put("success", true).put("nbreproduit", prisEncompte.size()).put("nbrerupture",
                    rupture.size());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new JSONObject().put("success", false);
        }

    }

    @Override
    public JSONObject envoiCommande(String commandeId, LocalDate dateLivraisonSouhaitee, int typeCommande,
            String typeCommandeExecptionel, String commentaire) {
        TOrder commande = em.find(TOrder.class, commandeId);

        processommandeXml(commande);
        return new JSONObject().put("success", true);
    }

    private void orderItemsFromPharmaMLItemDTO(List<PharmaMLItemDTO> items, TOrder order, String ruptureId) {
        TGrossiste grossiste = order.getLgGROSSISTEID();
        Collection<TOrderDetail> orderDetails = new ArrayList<>(items.size());
        items.forEach(x -> {
            TFamilleGrossiste o = orderService.finFamilleGrossisteByFamilleCipAndIdGrossiste(x.getCip(),
                    grossiste.getLgGROSSISTEID());
            TOrderDetail OTOrderDetail = new TOrderDetail(RandomStringUtils.randomAlphabetic(20));
            OTOrderDetail.setLgORDERID(order);
            OTOrderDetail.setIntNUMBER(x.getQuantite());
            OTOrderDetail.setIntQTEREPGROSSISTE(x.getQuantite());
            OTOrderDetail.setIntQTEMANQUANT(x.getQuantite());
            OTOrderDetail.setLgFAMILLEID(o.getLgFAMILLEID());
            OTOrderDetail.setLgGROSSISTEID(grossiste);
            OTOrderDetail.setStrSTATUT(DateConverter.PASSE);
            OTOrderDetail.setDtCREATED(new Date());
            OTOrderDetail.setDtUPDATED(new Date());
            OTOrderDetail.setIntPAFDETAIL(o.getIntPAF());
            OTOrderDetail.setIntPRICEDETAIL(o.getIntPRICE());
            if (x.getPrixAchat() > 0) {
                OTOrderDetail.setIntPAFDETAIL(x.getPrixAchat());
            }
            OTOrderDetail.setIntPRICE(OTOrderDetail.getIntPAFDETAIL() * x.getQuantite());
            OTOrderDetail.setIntORERSTATUS((short) 3);
            order.setIntPRICE(order.getIntPRICE() + OTOrderDetail.getIntPRICE());
            orderDetails.add(OTOrderDetail);
            getEntityManager().persist(o);
            try {
                this.getEntityManager().refresh(orderService.ruptureDetaisByRuptureAndProduitId(ruptureId,
                        o.getLgFAMILLEID().getLgFAMILLEID()));
            } catch (Exception e) {
                LOG.log(Level.SEVERE, null, e);
            }
        });

    }

    private void processommandeXml(TOrder o) {
        try {
            TGrossiste grossiste = o.getLgGROSSISTEID();
            if (StringUtils.isEmpty(grossiste.getStrURLPHARMAML())) {
                return;
            }
            TOfficine of = em.find(TOfficine.class, Constant.OFFICINE);
            CsrpEnveloppe payLoad = buildPayload(o, grossiste, of, null);

            JAXBContext requestContext = JAXBContext.newInstance(CsrpEnveloppe.class);
            Marshaller marshaller = requestContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            /// marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            marshaller.marshal(payLoad, sw);
            // save file
            String fileName = o.getStrREFORDER() + "_"
                    + StringUtils.replace(grossiste.getStrLIBELLE(), StringUtils.SPACE, StringUtils.EMPTY);
            createSaveXmlFile(marshaller, payLoad, ap.pharmaMlDir, "C", fileName);
            HttpClient client = getHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(grossiste.getStrURLPHARMAML()))
                    .header("Content-Type", "text/xml; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(sw.toString())).build();

            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int httpCode = httpResponse.statusCode();
            String xmlResponse = httpResponse.body();
            System.err.println(xmlResponse);
            if (httpCode == 200) {
                saveResonse(xmlResponse, ap.pharmaMlDir, fileName);
            }
            System.err.println(" " + httpResponse);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }

    }

    private HttpClient getHttpClient() {
        return HttpClient.newHttpClient();
    }

    private CsrpEnveloppe buildPayload(TOrder order, TGrossiste grossiste, TOfficine of, String commentiare) {

        CsrpEnveloppe ce = new CsrpEnveloppe();
        ce.setUsage(PharmaMlUtils.USAGE_VALUE);
        ce.setVersionProtocole(PharmaMlUtils.VERSION_PROTOCLE_VALUE);
        ce.setVersionLogiciel(PharmaMlUtils.VERSION_LOGICIEL_VALUE);
        ce.setIdLogiciel(PharmaMlUtils.ID_LOGICIEL_VALUE);
        ce.setEntete(buildEntete(grossiste, of));
        ce.setCorps(buildCorps(order, commentiare));
        ce.setNatureAction(PharmaMlUtils.NATURE_ACTION_REQ_EMISSION);
        return ce;

    }

    private String getDate() {
        // return "2025-06-14T20:50:25";
        // return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss"));
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private Entete buildEntete(TGrossiste grossiste, TOfficine of) {
        Entete e = new Entete();
        e.setDate(getDate());
        e.setRefMessage(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        e.setEmetteur(buildEmetteur(grossiste, of));
        e.setRecepteur(buildRecepteur(grossiste));
        return e;

    }

    private Partenaire buildEmetteur(TGrossiste grossiste, TOfficine of) {
        String code = StringUtils.isNotEmpty(grossiste.getStrOFFICINEID()) ? grossiste.getStrOFFICINEID()
                : PharmaMlUtils.CODE_VALUE;
        Partenaire p = new Partenaire();
        p.setNature(PharmaMlUtils.NATURE_PARTENAIRE_VALUE_OF);
        p.setCode(code);
        p.setAdresse(of.getStrNOMCOMPLET());
        p.setId(grossiste.getIdRepartiteur());
        return p;
    }

    private Partenaire buildRecepteur(TGrossiste grossiste) {

        Partenaire p = new Partenaire();
        p.setNature(PharmaMlUtils.NATURE_PARTENAIRE_VALUE_RE);
        p.setCode(grossiste.getStrCODERECEPTEURPHARMA());
        p.setAdresse(grossiste.getStrLIBELLE());

        p.setId(grossiste.getStrIDRECEPTEURPHARMA());
        return p;
    }

    private MessageEntete buildMessageEntete(TGrossiste grossiste) {
        MessageEntete me = new MessageEntete();
        me.setEmetteur(buildOfficineEmetteur(grossiste));
        me.setDestinataire(buildOfficineDestinataire(grossiste));
        me.setDate(getDate());
        return me;
    }

    private OfficinePartenaire buildOfficineDestinataire(TGrossiste grossiste) {
        OfficinePartenaire op = new OfficinePartenaire();
        op.setIdSociete(grossiste.getIdRepartiteur());
        op.setCodeSociete(grossiste.getStrCODERECEPTEURPHARMA());
        op.setNaturePartenaire(PharmaMlUtils.NATURE_PARTENAIRE_VALUE_RE);
        return op;
    }

    private OfficinePartenaire buildOfficineEmetteur(TGrossiste grossiste) {
        OfficinePartenaire op = new OfficinePartenaire();
        op.setIdClient(grossiste.getStrIDRECEPTEURPHARMA());
        op.setNaturePartenaire(NATURE_PARTENAIRE_VALUE_OF);
        return op;
    }

    private MessageCorps buildMessageCorps(TOrder order, String commentaire, String idGrossiste) {
        MessageCorps mc = new MessageCorps();
        mc.setCommande(buildCommande(order, commentaire, idGrossiste));
        return mc;
    }

    private Commande buildCommande(TOrder order, String commentaire, String idGrossiste) {
        Commande c = new Commande();
        c.setDateLivraison(LocalDate.now().plusDays(1).toString());
        if (StringUtils.isNotEmpty(commentaire)) {
            c.setCommentaireGeneral(commentaire);
        } else {
            c.setCommentaireGeneral(order.getStrREFORDER());
        }

        c.setRefCdeClient(order.getStrREFORDER());
        c.setNormale(buildNormale(order, idGrossiste));
        return c;
    }

    private List<LigneN> buildCommandeLigne(TOrder order, String grossisteId) {
        AtomicInteger count = new AtomicInteger(1);
        return new ArrayList<>(order.getTOrderDetailCollection()).stream().map(item -> {
            LigneN ligne = new LigneN();
            TFamille famille = item.getLgFAMILLEID();
            TFamilleGrossiste familleGrossiste = orderService
                    .finFamilleGrossisteByFamilleCipAndIdGrossiste(famille.getIntCIP(), grossisteId);

            String numLigne = StringUtils.leftPad(count.getAndIncrement() + "", 4, '0');
            String quantite = StringUtils.leftPad(item.getIntNUMBER() + "", 4, '0');
            String cip = null;
            if (familleGrossiste != null && StringUtils.isEmpty(familleGrossiste.getStrCODEARTICLE())) {
                cip = familleGrossiste.getStrCODEARTICLE();
            }
            if (StringUtils.isEmpty(cip)) {
                cip = item.getLgFAMILLEID().getIntCIP();
            }
            ligne.setCodeProduit(cip);
            ligne.setQuantite(quantite);
            ligne.setNumLigne(numLigne);
            ligne.setTypeCodification(TYPE_CODIFICATION_CIP39);
            return ligne;

        }).collect(Collectors.toList());
    }

    private Normale buildNormale(TOrder order, String grossisteId) {
        Normale n = new Normale();
        n.setLignes(buildCommandeLigne(order, grossisteId));
        return n;
    }

    private Corps buildCorps(TOrder order, String commentaire) {

        Corps c = new Corps();
        c.setMessageOfficine(buildMessageOfficine(order, commentaire));

        return c;
    }

    private MessageOfficine buildMessageOfficine(TOrder order, String commentaire) {
        TGrossiste grossiste = order.getLgGROSSISTEID();
        MessageOfficine messageOfficine = new MessageOfficine();
        messageOfficine.setEntete(buildMessageEntete(grossiste));
        messageOfficine.setCorps(buildMessageCorps(order, commentaire, grossiste.getLgGROSSISTEID()));
        return messageOfficine;
    }

    private void createSaveXmlFile(Marshaller marshaller, Object objectToSave, String fileDir, String prefix,
            String fileName) throws IOException, JAXBException {
        Path path = Paths.get(fileDir + File.separator + prefix.toUpperCase() + "_" + fileName + ".xml");
        try (OutputStream os = Files.newOutputStream(path)) {
            marshaller.marshal(objectToSave, os);
        }

    }

    private void saveResonse(String response, String fileDir, String fileName) {
        try {
            Path path = Paths.get(fileDir + File.separator + "R_" + fileName + ".xml");
            Files.write(path, response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "saveResonse", ex);
        }
    }
}
