/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileupload;

import dal.TFamille;
import dal.TFamilleGrossiste;
import dal.TFamilleGrossiste_;
import dal.TFamille_;
import dal.TGrossiste;
import dal.TOrder;
import dal.TOrderDetail;
import dal.TParameters;
import dal.TUser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.transaction.UserTransaction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import toolkits.parameters.commonparameter;
import toolkits.utils.date;
import toolkits.utils.jdom;
import toolkits.utils.logger;
import util.DateConverter;

/**
 *
 * @author KKOFFI
 */
@WebServlet(name = "FileFormaManager", urlPatterns = { "/commande" })
@MultipartConfig(fileSizeThreshold = 5242880, maxFileSize = 20971520L, maxRequestSize = 41943040L)
public class FileFormaManager extends HttpServlet {

    DateFormat df = new SimpleDateFormat("dd_MM_YYYY_HH_mm_ss");
    private JsonBuilderFactory factory;
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @Inject
    private UserTransaction userTransaction;
    private List<OrderItem> items;

    private enum Format {
        LABOREX, COPHARMED, TEDIS, DPCI, CIP_QTE, CIP_QTE_CIP_QTER_PA
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String lg_GROSSISTE_ID = "";

        if (request.getParameter("lg_GROSSISTE_ID") != null) {
            lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
        }

        String modeBL = request.getParameter("modeBL");
        items = new ArrayList<>();

        factory = Json.createBuilderFactory(null);
        JsonObjectBuilder json = factory.createObjectBuilder();
        Part part = request.getPart("fichier");
        String fileName = part.getSubmittedFileName();
        String extension = fileName.substring(fileName.indexOf(".") + 1, fileName.length());
        HttpSession session = request.getSession();
        TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

        JSONObject _json = new JSONObject();
        //
        try (PrintWriter out = response.getWriter()) {
            switch (extension) {
            case "csv":
                _json = bulkInsertFormatCSV(part, lg_GROSSISTE_ID, em, Format.valueOf(modeBL), OTUser);

                break;

            case "txt":
                _json = bulkInsertTxt(part, lg_GROSSISTE_ID, em, Format.valueOf(modeBL), OTUser);
                break;
            default:

                break;
            }
            if (!items.isEmpty()) {

                json.add("toBe", true);
                String finalFile = fichierReponse(request.getServletContext(),
                        fileName.substring(0, fileName.indexOf('.') - 1), Format.valueOf(modeBL), true, items);
                jdom.InitRessource();
                json.add("success", "<span style='color:blue;font-weight:800;'>" + _json.getInt("count") + "/"
                        + _json.getInt("ligne")
                        + "\n</span> produits mis à jour <a href=\"../VericationCommande?fileName=" + finalFile
                        + " \" style=\"color:red !important;\">Cliquer sur le lien pour télécharger les produits non pris en compte</a>");
            } else {
                json.add("toBe", false);
                json.add("success", "<span style='color:blue;font-weight:800;'>" + _json.getInt("count") + "/"
                        + _json.getInt("ligne") + "</span> produits mis à jour");
            }
            json.add("statut", 1);

            out.println(json.build());
        } catch (Exception ex) {
            Logger.getLogger(FileFormaManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String buildCommandeRef(Date ODate, EntityManager em) throws JSONException {
        TParameters OTParameters = em.find(TParameters.class, "KEY_LAST_ORDER_COMMAND_NUMBER");
        TParameters OTParameters_KEY_SIZE_ORDER_NUMBER = em.find(TParameters.class, "KEY_SIZE_ORDER_NUMBER");

        String jsondata = OTParameters.getStrVALUE();
        int int_last_code = 0;
        int_last_code = int_last_code + 1;

        try {
            JSONArray jsonArray = new JSONArray(jsondata);
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            int_last_code = Integer.parseInt(jsonObject.getString("int_last_code"));
            Date dt_last_date = date.stringToDate(jsonObject.getString("str_last_date"), date.formatterMysqlShort2);

            String str_lasd = date.DateToString(dt_last_date, date.formatterMysqlShort2);
            String str_actd = date.DateToString(ODate, date.formatterMysqlShort2);

            if (!str_lasd.equals(str_actd)) {
                int_last_code = 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // KEY_SIZE_ORDER_NUMBER
        Calendar now = Calendar.getInstance();

        int mois = now.get(Calendar.MONTH) + 1;
        int jour = now.get(Calendar.DAY_OF_MONTH);
        String mois_tostring = "";

        int intsize = ((int_last_code + 1) + "").length();
        int intsize_tobuild = Integer.parseInt(OTParameters_KEY_SIZE_ORDER_NUMBER.getStrVALUE());
        String str_last_code = "";
        for (int i = 0; i < (intsize_tobuild - intsize); i++) {
            str_last_code = str_last_code + "0";
        }

        str_last_code = str_last_code + (int_last_code + 1) + "";

        // String str_code = jour + "" + mois + "" + this.getKey().getYear(ODate) + "_" + str_last_code;
        if (mois < 10) {
            mois_tostring = "0" + mois;
        } else {
            mois_tostring = String.valueOf(mois);
        }
        String str_code = jour + "" + mois_tostring + "" + DateConverter.convertDateToLocalDate(ODate).getYear() + "_"
                + str_last_code;
        JSONObject json = new JSONObject();
        JSONArray arrayObj = new JSONArray();
        json.put("int_last_code", str_last_code);
        json.put("str_last_date", date.DateToString(ODate, date.formatterMysqlShort2));
        arrayObj.put(json);
        String jsonData = arrayObj.toString();

        OTParameters.setStrVALUE(jsonData);
        new logger().OCategory.info(jsonData);
        new logger().OCategory.info(str_code);
        return str_code;
    }

    public TOrder createOrder(TGrossiste OTGrossiste, String str_STATUT, EntityManager em, TUser OTUser) {
        TOrder OTOrder = null;
        try {

            OTOrder = new TOrder();

            OTOrder.setLgORDERID(RandomStringUtils.randomAlphanumeric(20));
            OTOrder.setLgUSERID(OTUser);

            try {

                if (OTGrossiste != null) {
                    OTOrder.setLgGROSSISTEID(OTGrossiste);
                    OTOrder.setStrREFORDER(this.buildCommandeRef(new Date(), em));
                }
            } catch (Exception e) {
            }

            OTOrder.setStrSTATUT(str_STATUT);
            OTOrder.setDtCREATED(new Date());
            OTOrder.setDtUPDATED(new Date());
            em.persist(OTOrder);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return OTOrder;

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    enum ArticleHeader {
        LOCATION, GROSSISTE, DESCRIPTION, CIP, AEN, QTE, PU, PA
    }

    private JSONObject bulkInsertTxt(Part part, String lgGROSSISTE, EntityManager em, Format mode, TUser OTUser)
            throws Exception {
        int count = 0;
        int i = 0;
        JSONObject json = new JSONObject();

        TGrossiste grossiste = em.find(TGrossiste.class, lgGROSSISTE);
        userTransaction.begin();
        TOrder order = createOrder(grossiste, commonparameter.statut_is_Process, em, OTUser);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split("\t");
                int ligne = createTOrderDetailVIACSV(em, grossiste, order, row[1], Integer.valueOf(row[3]),
                        Integer.valueOf(row[2]), Integer.valueOf(row[5]), 0);
                i += ligne;
                if (ligne == 0) {
                    items.add(new OrderItem(row[1], Integer.valueOf(row[3]), row[1], Integer.valueOf(row[3]),
                            Double.valueOf(row[5])));
                }
                if ((count % 20) == 0) {
                    em.flush();
                    em.clear();
                }
                count++;
            }
            userTransaction.commit();
            json.put("count", i);
            json.put("ligne", count);
        } catch (IOException e) {
            throw new Exception(e);
        }

        return json;
    }

    private JSONObject bulkInsertFormatCSV(Part part, String lgGROSSISTE, EntityManager em, Format mode, TUser OTUser)
            throws Exception {
        int count = 0;
        int i = 0;
        JSONObject json = new JSONObject();
        try {
            TGrossiste grossiste = em.find(TGrossiste.class, lgGROSSISTE);
            userTransaction.begin();
            TOrder order = createOrder(grossiste, commonparameter.statut_is_Process, em, OTUser);
            CSVParser parser = new CSVParser(new InputStreamReader(part.getInputStream()),
                    CSVFormat.EXCEL.withDelimiter(';'));
            switch (mode) {
            case LABOREX:

                for (CSVRecord cSVRecord : parser) {
                    if (count > 0) {
                        int ligne = createTOrderDetailVIACSV(em, grossiste, order, cSVRecord.get(2),
                                Integer.valueOf(cSVRecord.get(5)), Double.valueOf(cSVRecord.get(6)).intValue(),
                                Double.valueOf(cSVRecord.get(7)).intValue(), 0);
                        if (ligne == 0) {
                            // printer.printRecord("N° Facture", "N° ligne", "CIP/EAN13", "Libellé du produit", "Qté
                            // commandée","Qté livrée","Prix de cession","Prix public","N° commande","Tva");
                            items.add(new OrderItem(cSVRecord.get(0), Integer.valueOf(cSVRecord.get(1)),
                                    cSVRecord.get(2), cSVRecord.get(3), Integer.valueOf(cSVRecord.get(4)),
                                    Integer.valueOf(cSVRecord.get(5)), Double.valueOf(cSVRecord.get(6)),
                                    Double.valueOf(cSVRecord.get(7)), cSVRecord.get(8),
                                    Double.valueOf(cSVRecord.get(9))));
                        }
                        i += ligne;
                    }
                    if ((count % 20) == 0) {
                        em.flush();
                        em.clear();

                    }
                    count++;
                }
                userTransaction.commit();
                json.put("count", i);
                json.put("ligne", count - 1);
                break;
            case COPHARMED:

                for (CSVRecord cSVRecord : parser) {
                    if (count > 0) {
                        int ligne = createTOrderDetailVIACSV(em, grossiste, order, cSVRecord.get(4),
                                Integer.valueOf(cSVRecord.get(9)), Double.valueOf(cSVRecord.get(11)).intValue(),
                                Integer.valueOf(cSVRecord.get(13)), Integer.valueOf(cSVRecord.get(10)));
                        i += ligne;
                        if (ligne == 0) {
                            OrderItem orderItem = new OrderItem().dateBl(cSVRecord.get(0))
                                    .ug(Integer.valueOf(cSVRecord.get(10))).facture(cSVRecord.get(1))
                                    .cip(cSVRecord.get(4)).libelle(cSVRecord.get(6))
                                    .cmdeL(Integer.valueOf(cSVRecord.get(9))).ligne(Integer.valueOf(cSVRecord.get(2)))
                                    .prixUn(Integer.valueOf(cSVRecord.get(13))).cmde(Integer.valueOf(cSVRecord.get(8)))
                                    .prixAchat(Double.valueOf(cSVRecord.get(11)).intValue());
                            items.add(orderItem);
                        }

                    }
                    if ((count % 20) == 0) {
                        em.flush();
                        em.clear();

                    }
                    count++;
                }
                userTransaction.commit();
                json.put("count", i);
                json.put("ligne", count - 1);
                break;
            case TEDIS:

                for (CSVRecord cSVRecord : parser) {

                    if (count > 0) {

                    }
                    int ligne = createTOrderDetailVIACSV(em, grossiste, order, cSVRecord.get(0),
                            Integer.valueOf(cSVRecord.get(3)), Integer.valueOf(cSVRecord.get(4)), 0, 0);
                    i += ligne;
                    if (ligne == 0) {
                        items.add(new OrderItem(cSVRecord.get(0), Integer.valueOf(cSVRecord.get(1)), cSVRecord.get(0),
                                Integer.valueOf(cSVRecord.get(3)), Double.valueOf(cSVRecord.get(4))));
                    }

                    if ((count % 20) == 0) {
                        em.flush();
                        em.clear();

                    }
                    count++;
                }
                userTransaction.commit();
                json.put("count", i);
                json.put("ligne", count);
                break;
            case DPCI:

                for (CSVRecord cSVRecord : parser) {
                    int ligne = createTOrderDetailVIACSV(em, grossiste, order, cSVRecord.get(2),
                            Integer.valueOf(cSVRecord.get(6)), Double.valueOf(cSVRecord.get(3)).intValue(),
                            Double.valueOf(cSVRecord.get(4)).intValue(), 0);
                    i += ligne;
                    if (ligne == 0) {
                        OrderItem orderItem = new OrderItem().refBl(cSVRecord.get(8)).cip(cSVRecord.get(2))
                                .libelle(cSVRecord.get(1)).tva(Double.valueOf(cSVRecord.get(5)))
                                .cmdeL(Integer.valueOf(cSVRecord.get(6))).ligne(Integer.valueOf(cSVRecord.get(0)))
                                .prixUn(Double.valueOf(cSVRecord.get(4)).intValue())
                                .cmde(Integer.valueOf(cSVRecord.get(7)))// c'est valeur n'est pas explique a cette
                                                                        // date19/12/2020
                                .prixAchat(Double.valueOf(cSVRecord.get(3)).intValue());
                        items.add(orderItem);
                    }
                    if ((count % 20) == 0) {
                        em.flush();
                        em.clear();
                    }
                    count++;
                }
                userTransaction.commit();
                json.put("count", i);
                json.put("ligne", count);
                break;
            case CIP_QTE:
                int isFirstLigne = Integer.MAX_VALUE;
                for (CSVRecord cSVRecord : parser) {
                    isFirstLigne = skipFirstLigne(cSVRecord, count);
                    if (isFirstLigne < 0) {
                        continue;
                    }

                    int qty = Integer.parseInt(cSVRecord.get(1));
                    int ligne = createTOrderDetailVIACSV(grossiste, order, cSVRecord.get(0), qty, null);
                    i += ligne;
                    if (ligne == 0) {

                        items.add(new OrderItem().cip(cSVRecord.get(0)).cmde(qty));
                    }

                    if ((count % 20) == 0) {
                        em.flush();
                        em.clear();

                    }
                    count++;
                }
                userTransaction.commit();
                json.put("count", i);
                json.put("ligne", isFirstLigne < 0 ? (count - 1) : count);
                break;

            case CIP_QTE_CIP_QTER_PA:
                int firstLigne = Integer.MAX_VALUE;
                for (CSVRecord cSVRecord : parser) {
                    firstLigne = skipFirstLigne(cSVRecord, count);
                    if (firstLigne < 0) {
                        continue;
                    }
                    int qty = Integer.parseInt(cSVRecord.get(3));
                    int prixAchat = Integer.parseInt(cSVRecord.get(4));
                    int ligne = createTOrderDetailVIACSV(grossiste, order, cSVRecord.get(0), qty, prixAchat);
                    i += ligne;
                    if (ligne == 0) {

                        items.add(new OrderItem().cip(cSVRecord.get(0)).cmde(Integer.parseInt(cSVRecord.get(1)))
                                .cmdeL(qty).prixAchat(prixAchat));
                    }

                    if ((count % 20) == 0) {
                        em.flush();
                        em.clear();

                    }
                    count++;
                }
                userTransaction.commit();
                json.put("count", i);
                json.put("ligne", firstLigne < 0 ? (count - 1) : count);
                break;
            default:
                break;
            }

        } catch (IOException ex) {
            throw new Exception(ex);
        }

        return json;
    }

    public TOrderDetail findFamilleInTOrderDetail(String lg_ORDER_ID, String lg_FAMILLE_ID, EntityManager em) {
        TOrderDetail OTOrderDetail = null;
        try {

            Query qry = em.createQuery(
                    "SELECT t FROM TOrderDetail t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgORDERID.lgORDERID = ?2 ")
                    .setParameter(1, lg_FAMILLE_ID).setParameter(2, lg_ORDER_ID);
            if (qry.getResultList().size() > 0) {
                OTOrderDetail = (TOrderDetail) qry.getSingleResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // this.buildErrorTraceMessage(e.getMessage());
        }
        return OTOrderDetail;
    }

    public int createTOrderDetailVIACSV(EntityManager em, TGrossiste OTGrossiste, TOrder lg_ORDER_ID,
            String lg_famille_id, int qty, int int_PAF_DETAIL, int pu, int ug) {
        TOrderDetail OTOrderDetail;
        TFamille OTFamille = null;
        TFamilleGrossiste OTFamilleGrossiste = null;
        try {
            try {
                OTFamilleGrossiste = getTFamilleGrossiste(lg_famille_id.trim(), em);
                if (OTFamilleGrossiste != null) {
                    OTFamille = OTFamilleGrossiste.getLgFAMILLEID();
                }
            } catch (Exception e) {

            }
            if (OTFamille != null) {
                OTOrderDetail = findFamilleInTOrderDetail(lg_ORDER_ID.getLgORDERID(), OTFamille.getLgFAMILLEID(), em);
                if (OTOrderDetail == null) {
                    OTOrderDetail = new TOrderDetail();
                    OTOrderDetail.setLgORDERDETAILID(RandomStringUtils.randomAlphanumeric(20));
                    OTOrderDetail.setLgORDERID(lg_ORDER_ID);
                    OTOrderDetail.setLgFAMILLEID(OTFamille);
                    OTOrderDetail.setLgGROSSISTEID(OTGrossiste);
                    OTOrderDetail.setIntNUMBER(qty);
                    OTOrderDetail.setIntQTEREPGROSSISTE(qty);
                    OTOrderDetail.setIntQTEMANQUANT(qty);
                    OTOrderDetail.setIntPRICE(qty * int_PAF_DETAIL);
                    OTOrderDetail.setIntPRICEDETAIL((pu == 0 ? OTFamilleGrossiste.getIntPRICE() : pu));
                    OTOrderDetail.setIntPAFDETAIL(int_PAF_DETAIL);
                    OTOrderDetail.setPrixAchat(OTOrderDetail.getIntPAFDETAIL());
                    // OTOrderDetail.setPrixUnitaire(OTOrderDetail.getIntPRICEDETAIL());
                    OTOrderDetail.setStrSTATUT(commonparameter.statut_is_Process);
                    OTOrderDetail.setDtCREATED(new Date());
                    OTOrderDetail.setUg(ug);
                    em.persist(OTOrderDetail);
                } else {
                    OTOrderDetail.setUg(OTOrderDetail.getUg() + ug);
                    OTOrderDetail.setIntNUMBER(OTOrderDetail.getIntNUMBER() + qty);
                    OTOrderDetail.setIntQTEMANQUANT(OTOrderDetail.getIntNUMBER());
                    OTOrderDetail.setIntPRICE(OTOrderDetail.getIntNUMBER() * int_PAF_DETAIL);
                    OTOrderDetail.setIntQTEREPGROSSISTE(OTOrderDetail.getIntNUMBER());
                    OTOrderDetail.setDtUPDATED(new Date());
                    em.merge(OTOrderDetail);

                }
                return 1;

            } else {

                return 0;
            }
        } catch (NoResultException e) {

            new logger().OCategory.info("impossible de creer OTOrderDetail   " + e.toString());
            return 0;
        }
    }

    public TFamilleGrossiste getTFamilleGrossiste(String lg_FAMILLE_ID, EntityManager em) {

        TFamilleGrossiste familleGrossiste = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TFamilleGrossiste> cq = cb.createQuery(TFamilleGrossiste.class);
            Root<TFamilleGrossiste> root = cq.from(TFamilleGrossiste.class);
            Join<TFamilleGrossiste, TFamille> j = root.join("lgFAMILLEID", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            criteria = cb.and(criteria,
                    cb.or(cb.like(j.get("intCIP"), lg_FAMILLE_ID + "%"),
                            cb.like(j.get("intEAN13"), lg_FAMILLE_ID + "%"),
                            cb.like(root.get("strCODEARTICLE"), lg_FAMILLE_ID + "%")));
            criteria = cb.and(criteria, cb.equal(j.get("boolDECONDITIONNE"), Short.valueOf("0")));
            criteria = cb.and(criteria, cb.equal(root.get(TFamilleGrossiste_.strSTATUT), "enable"));
            criteria = cb.and(criteria, cb.equal(j.get(TFamille_.strSTATUT), "enable"));
            cq.where(criteria);
            TypedQuery<TFamilleGrossiste> q = em.createQuery(cq);
            q.setFirstResult(0).setMaxResults(1);
            familleGrossiste = q.getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return familleGrossiste;
    }

    private String fichierReponse(ServletContext context, String fileName, Format mode, boolean isTxt,
            List<OrderItem> list) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(context.getRealPath("WEB-INF") + File.separator + fileName + ".csv"),
                StandardCharsets.UTF_8)) {
            CSVPrinter printer = CSVFormat.EXCEL.withDelimiter(';').print(writer);

            switch (mode) {
            case LABOREX:

                printer.printRecord("N° Facture", "N° ligne", "CIP/EAN13", "Libellé du produit", "Qté commandée",
                        "Qté livrée", "Prix de cession", "Prix public", "N° commande", "Tva");
                for (OrderItem item : list) {
                    printer.printRecord(item.getFacture(), item.getLigne(), item.getCip(), item.getLibelle(),
                            item.getCmde(), item.getCmdeL(), item.getMontant(), item.getPrixUn(), item.getRefBl(),
                            item.getTva());

                }
                break;
            case COPHARMED:
                // Date Code Cip Description Laboratoire Quantite Demandee Quantitee livree Unite Gratuite Prix de
                // Cession Hors Taxe Taux Taxe Prix public Prix TTC
                printer.printRecord("Date", "Numero Facture", "Numero Ligne", "Code Interne", "Code CIP",
                        "Code CIP Alternatif", "Description", "Laboratoire", "Quantité demandée", "Quantitee livree",
                        "Unite Gratuite", "Prix de Cession Hors Taxe", "Taux Taxe", "Prix public", "Prix TTC");
                for (OrderItem item : list) {
                    printer.printRecord(item.getDateBl(), item.getFacture(), item.getLigne(), "", item.getCip(), "",
                            item.getLibelle(), "", +item.getCmde(), item.getCmdeL(), item.getUg(), item.getPrixAchat(),
                            "", item.getPrixUn(), "");

                }
                break;
            case TEDIS:
                for (OrderItem item : list) {

                    printer.printRecord(item.getCip(), item.getCmde(), item.getCip(), item.getCmdeL(),
                            item.getMontant().intValue());

                }
                break;
            case DPCI:
                for (OrderItem item : list) {
                    printer.printRecord(item.getLigne(), item.getLibelle(), item.getCip(), item.getPrixAchat(),
                            item.getPrixUn(), item.getTva(), item.getCmdeL(), item.getCmde(), item.getRefBl());

                }
                break;
            case CIP_QTE:
                for (OrderItem item : list) {
                    printer.printRecord(item.getCip(), item.getCmde());

                }
                break;
            case CIP_QTE_CIP_QTER_PA:
                for (OrderItem item : list) {
                    printer.printRecord(item.getCip(), item.getCmde(), item.getCip(), item.getCmdeL(),
                            item.getPrixAchat());

                }
                break;
            default:
                break;
            }

            printer.flush();
            jdom.InitRessource();
            String dirDisk = jdom.path_commande + fileName + ".csv";
            Files.copy(Paths.get(context.getRealPath("WEB-INF") + File.separator + fileName + ".csv"),
                    Paths.get(dirDisk), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException ex) {
            Logger.getLogger(FileFormaManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fileName;
    }

    public int createTOrderDetailVIACSV(TGrossiste OTGrossiste, TOrder lg_ORDER_ID, String codeCip, int qty,
            Integer prixAchat) {
        TOrderDetail OTOrderDetail;
        TFamille OTFamille = null;
        TFamilleGrossiste OTFamilleGrossiste;
        try {
            try {
                OTFamilleGrossiste = getTFamilleGrossiste(codeCip.trim(), em);
                if (OTFamilleGrossiste != null) {
                    OTFamille = OTFamilleGrossiste.getLgFAMILLEID();
                }
            } catch (Exception e) {

            }
            if (OTFamille == null) {
                OTFamille = findByCodeCipOrEan(codeCip.trim());
            }
            if (OTFamille != null) {
                OTOrderDetail = findFamilleInTOrderDetail(lg_ORDER_ID.getLgORDERID(), OTFamille.getLgFAMILLEID(), em);
                if (OTOrderDetail == null) {
                    OTOrderDetail = new TOrderDetail();
                    OTOrderDetail.setLgORDERDETAILID(RandomStringUtils.randomAlphanumeric(20));
                    OTOrderDetail.setLgORDERID(lg_ORDER_ID);
                    OTOrderDetail.setLgFAMILLEID(OTFamille);
                    OTOrderDetail.setLgGROSSISTEID(OTGrossiste);
                    OTOrderDetail.setIntNUMBER(qty);
                    OTOrderDetail.setIntQTEREPGROSSISTE(qty);
                    OTOrderDetail.setIntQTEMANQUANT(qty);
                    OTOrderDetail.setIntPRICEDETAIL(OTFamille.getIntPRICE());
                    OTOrderDetail.setIntPAFDETAIL(prixAchat == null ? OTFamille.getIntPAF() : prixAchat);
                    OTOrderDetail.setPrixAchat(OTOrderDetail.getIntPAFDETAIL());
                    // OTOrderDetail.setPrixUnitaire(OTOrderDetail.getIntPRICEDETAIL());
                    OTOrderDetail.setIntPRICE(qty * OTOrderDetail.getIntPAFDETAIL());
                    OTOrderDetail.setStrSTATUT(DateConverter.STATUT_PROCESS);
                    OTOrderDetail.setDtCREATED(new Date());
                    OTOrderDetail.setDtUPDATED(OTOrderDetail.getDtCREATED());
                    OTOrderDetail.setUg(0);
                    em.persist(OTOrderDetail);
                } else {

                    OTOrderDetail.setIntNUMBER(OTOrderDetail.getIntNUMBER() + qty);
                    OTOrderDetail.setIntQTEMANQUANT(OTOrderDetail.getIntNUMBER());
                    OTOrderDetail.setIntPRICE(OTOrderDetail.getIntNUMBER() * OTOrderDetail.getIntPAFDETAIL());
                    OTOrderDetail.setIntQTEREPGROSSISTE(OTOrderDetail.getIntNUMBER());
                    OTOrderDetail.setDtUPDATED(new Date());
                    em.merge(OTOrderDetail);

                }
                return 1;

            } else {

                return 0;
            }
        } catch (NoResultException e) {

            new logger().OCategory.info("impossible de creer OTOrderDetail   " + e.toString());
            return 0;
        }
    }

    private TFamille findByCodeCipOrEan(String codeCipOrCodeEan) {
        try {
            TypedQuery<TFamille> query = em.createQuery(
                    "SELECT o FROM TFamille o WHERE o.intCIP LIKE ?1 OR o.intEAN13 LIKE ?1", TFamille.class);
            query.setParameter(1, codeCipOrCodeEan + "%");
            query.setMaxResults(1);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private int skipFirstLigne(CSVRecord cSVRecord, int index) {
        if (index < 1) {
            try {
                return Integer.valueOf(cSVRecord.get(1));
            } catch (Exception e) {
                return -1;
            }

        }
        return 0;
    }
}
