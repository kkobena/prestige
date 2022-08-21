/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.PharmaMLItemDTO;
import dal.Rupture;
import dal.TFactureDetail;
import dal.TFamille;
import dal.TFamilleGrossiste;
import dal.TGrossiste;
import dal.TOrder;
import dal.TOrderDetail;
import dal.TUser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.OrderService;
import rest.service.PharmaMlService;
import toolkits.utils.jdom;
import util.DateConverter;
import util.PharmaMlUtils;
import static util.PharmaMlUtils.NOMBRE_LIGNE_CLAIRE;
import static util.PharmaMlUtils.NOMBRE_LIGNE_CODE;
import static util.PharmaMlUtils.TYPE_CODIFICATION_CIP;
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
    Comparator<File> fileComparator = Comparator.comparing(File::lastModified);
    @EJB
    OrderService orderService;

    public EntityManager getEntityManager() {
        return em;
    }

    private List<TOrderDetail> findByOrder(TOrder order) {
        TypedQuery<TOrderDetail> q = getEntityManager().createNamedQuery("TOrderDetail.findByLgORDERID", TOrderDetail.class);
        q.setParameter("lgORDERID", order);
        return q.getResultList();
    }

    private TFamilleGrossiste findByGrossisteAndFamille(String idProduit, String grossisteId) {
        try {
            TypedQuery<TFamilleGrossiste> q = getEntityManager().createQuery("SELECT o FROM TFamilleGrossiste o WHERE o.lgFAMILLEID.lgFAMILLEID=?1 AND o.lgGROSSISTEID.lgGROSSISTEID=?2 AND o.strSTATUT='enable' ", TFamilleGrossiste.class);
            q.setParameter(1, idProduit);
            q.setParameter(2, grossisteId);
            q.setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {
//            e.printStackTrace(System.err);
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
     * @return Le code de commande forme a partir de la reference de la commande
     * sans le separeut <<_>>
     */
    @Override
    public JSONObject envoiPharmaCommande(String commandeId, LocalDate dateLivraisonSouhaitee, int typeCommande, String typeCommandeExecptionel, String commentaire) {
        JSONObject json = new JSONObject();
        TOrder order = getEntityManager().find(TOrder.class, commandeId);
        TGrossiste tg = order.getLgGROSSISTEID();
        List<PharmaMLItemDTO> itemDTO = new ArrayList<>();
        findByOrder(order).stream().forEach(d -> {
            TFamille tf = d.getLgFAMILLEID();
            PharmaMLItemDTO o = new PharmaMLItemDTO(d, tf, findByGrossisteAndFamille(tf.getLgFAMILLEID(), tg.getLgGROSSISTEID()), false, false, false, PharmaMlUtils.TYPE_CODIFICATION_CIP);
            itemDTO.add(o);
        });
        try {
            buildCommande(message_url + order.getStrREFORDER(), tg.getIdRepartiteur(), PharmaMlUtils.TYPE_TRAVAIL_COMMANDE,
                    dateLivraisonSouhaitee, typeCommande, typeCommandeExecptionel, itemDTO, commentaire, order.getStrREFORDER().replace("_", ""));
            return json.put("success", true);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return json.put("success", true);
        }

    }

  
    public void buildCommande(String pathPharmaMl, String idRepartiteur, String typeTravail, LocalDate dateLivraisonSouhaitee, int typeCommande, String codeCommande, List<PharmaMLItemDTO> itemDTO, String commentaire, String commandeRef) throws IOException {
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
            PharmaMLItemDTO o = new PharmaMLItemDTO(d, tf, findByGrossisteAndFamille(tf.getLgFAMILLEID(), tg.getLgGROSSISTEID()), false, false, false, PharmaMlUtils.TYPE_CODIFICATION_CIP);
            itemDTO.add(o);
        });
        try {
            buildInfoProduits(message_url + order.getStrREFORDER(), tg.getIdRepartiteur(), RandomStringUtils.randomNumeric(PharmaMlUtils.DEF_COUNT),
                    itemDTO);
            return json.put("success", true);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return json.put("success", true);
        }

    }

    private void buildInfoProduits(String pathPharmaMl, String idRepartiteur, String codeRequete, List<PharmaMLItemDTO> itemDTO) throws IOException {
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
//https://www.ntppool.org/en/
    }

    File findCommandeResponse(String filePath) {
        File messageDir = new File(filePath);
        File[] responses = messageDir.listFiles();
        if (responses.length == 0) {
            return null;
        }
        List<File> files = Stream.of(responses).sorted(fileComparator.reversed()).filter(x -> DateConverter.convertLongToLacalDate(x.lastModified()).isEqual(LocalDate.now())).collect(Collectors.toList());
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
            e.printStackTrace(System.err);
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
                prixAchatHtaxe = Integer.valueOf(phaht);
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
            e.printStackTrace(System.err);
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
            return new JSONObject().put("success", true).put("nbreproduit", prisEncompte.size())
                    .put("nbrerupture", rupture.size());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new JSONObject().put("success", false);
        }

    }

    public void test() {
        try {
            String[] id = {"10662031032481273098", "10662033106163354419"};
            for (int i = 0; i < id.length; i++) {
                String string = id[i];
                TOrder order = orderService.findByRef("", string);
                Rupture rupture = orderService.creerRupture(order);
                findByOrder(order).forEach(x -> {
                    TOrderDetail o = orderService.findByCipAndOrderId(x.getLgFAMILLEID().getIntCIP(), string);
                    orderService.creerRuptureItem(rupture, o.getLgFAMILLEID(), o.getIntNUMBER());
//                getEntityManager().remove(o);
                });
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public JSONObject renvoiPharmaCommande(String ruptureId, String grossiste, LocalDate dateLivraisonSouhaitee, int typeCommande, String typeCommandeExecptionel, String commentaire) {
        JSONObject json = new JSONObject();
        Rupture order = getEntityManager().find(Rupture.class, ruptureId);
        TGrossiste envoiGr = getEntityManager().find(TGrossiste.class, grossiste);
        order.setGrossiste(envoiGr);
        this.getEntityManager().merge(order);
//        TGrossiste tg = order.getGrossiste();
        List<PharmaMLItemDTO> itemDTO = new ArrayList<>();
        orderService.ruptureDetaisDtoByRupture(ruptureId).forEach(d -> {
            TFamille tf = d.getProduit();
            PharmaMLItemDTO o = new PharmaMLItemDTO(d, tf, orderService.findOrCreateFamilleGrossiste(tf, envoiGr), false, false, false, PharmaMlUtils.TYPE_CODIFICATION_CIP);
            itemDTO.add(o);
        });
        try {
            buildCommande(message_url + order.getReference(), envoiGr.getIdRepartiteur(), PharmaMlUtils.TYPE_TRAVAIL_COMMANDE,
                    dateLivraisonSouhaitee, typeCommande, typeCommandeExecptionel, itemDTO, commentaire, order.getReference().replace("_", ""));
            return json.put("success", true);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return json.put("success", true);
        }
    }

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
            return new JSONObject().put("success", true).put("nbreproduit", prisEncompte.size())
                    .put("nbrerupture", rupture.size());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new JSONObject().put("success", false);
        }

    }

    void orderItemsFromPharmaMLItemDTO(List<PharmaMLItemDTO> items, TOrder order, String ruptureId) {
        TGrossiste grossiste = order.getLgGROSSISTEID();
        Collection<TOrderDetail> orderDetails = new ArrayList<>(items.size());
        items.forEach(x -> {
            TFamilleGrossiste o = orderService.finFamilleGrossisteByFamilleCipAndIdGrossiste(x.getCip(), grossiste.getLgGROSSISTEID());
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
                this.getEntityManager().refresh(orderService.ruptureDetaisByRuptureAndProduitId(ruptureId, o.getLgFAMILLEID().getLgFAMILLEID()));
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });

    }

}
