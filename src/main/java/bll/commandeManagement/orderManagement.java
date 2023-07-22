/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bll.commandeManagement;

import dal.dataManager;
import bll.bllBase;
import bll.common.Parameter;
import bll.configManagement.familleGrossisteManagement;
import bll.configManagement.familleManagement;
import bll.configManagement.grossisteManagement;
import bll.teller.SnapshotManager;
import org.json.JSONObject;
import bll.warehouse.WarehouseManager;
import com.opencsv.CSVReader;
import dal.TBonLivraisonDetail;
import dal.TEvaluationoffreprix;
import dal.TFamille;
import dal.TOrder;
import dal.TOrderDetail;
import dal.TFamilleGrossiste;
import dal.TFamille_;
import dal.TGrossiste;
import dal.TLot;
import dal.TOrderDetail_;
import dal.TOrder_;
import dal.TParameters;
import dal.TRuptureHistory;
import dal.TSnapShopRuptureStock;
import dal.TSuggestionOrder;
import dal.TSuggestionOrderDetails;
import dal.TUser;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.persistence.NoResultException;
import toolkits.parameters.commonparameter;
import toolkits.utils.logger;
import java.util.List;
import toolkits.filesmanagers.FilesType.CsvFiles;
import toolkits.utils.date;
import toolkits.utils.jdom;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Calendar;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import toolkits.filesmanagers.FilesType.CsvFiles_with_Opencvs;
import toolkits.filesmanagers.FilesType.XlsFiles_with_POI;
import util.DateConverter;

/**
 *
 * @author AMIGONE
 */
public class orderManagement extends bllBase {

    Object OtableTOrder = dal.TOrder.class;

    public orderManagement(dataManager OdataManager, TUser OTUser) {
        super.setOTUser(OTUser);
        super.setOdataManager(OdataManager);
        super.checkDatamanager();
    }

    public orderManagement(dataManager OdataManager) {
        super.setOdataManager(OdataManager);
        super.checkDatamanager();
    }

    public void csvv(String chemin, String lg_GROSSISTE_ID) {

        try {

            BufferedReader fichier_source = new BufferedReader(new FileReader(chemin));
            String chaine;
            int i = 1;
            TOrder OTOrder = null;
            TOrderDetail OTOrderDetail = null;
            TFamille OTFamille = null;

            while ((chaine = fichier_source.readLine()) != null) {

                if (OTOrder == null) {
                    OTOrder = this.createOrder(lg_GROSSISTE_ID, commonparameter.statut_is_Process);
                }
                if (i > 0) {

                    String[] tabChaine = chaine.split(";");

                    OTFamille = (TFamille) this.getOdataManager().getEm()
                            .createQuery("SELECT t FROM TFamille t WHERE t.intCIP = ?1 AND t.boolDECONDITIONNE = ?2")
                            .setParameter(1, tabChaine[0]).setParameter(2, 0).getSingleResult();

                    if (OTFamille != null) {
                        OTOrderDetail = this.createOrderDetail(OTOrder.getLgORDERID(), OTFamille.getLgFAMILLEID(),
                                lg_GROSSISTE_ID, Integer.parseInt(tabChaine[1]), OTFamille.getIntPRICE(),
                                OTFamille.getIntPAF());
                    } else {
                        new logger().OCategory.info("OTFAMILLE null ");
                    }

                }
                OTFamille = null;
                i++;
            }
            fichier_source.close();
        } catch (Exception e) {
            System.out.println("Le fichier est introuvable !");
        }
    }

    public String MoneyFormat(int Price) {

        String s = "";

        try {

            // formated = Price + 0.00;
            NumberFormat numberFormat = NumberFormat.getInstance(java.util.Locale.FRENCH);

            s = numberFormat.format(Price);
            new logger().OCategory.info("On a  " + s);
        } catch (Exception e) {

        }

        return s;
    }

    public void basculerProduit(String lg_FAMILLE_ID, String lg_GROSSISTE_ID) {

        // <editor-fold defaultstate="collapsed" desc="basculerPrduit - Envoyer l'article chez un autre grossiste">
        TOrder OTOrder = null;
        TOrderDetail OTOrderDetail = null;
        TGrossiste OTGrossiste = null;
        TFamille OTFamille = null;
        TFamilleGrossiste OTFamilleGrossiste = null;

        try {

            OTGrossiste = this.getOdataManager().getEm().find(TGrossiste.class, lg_GROSSISTE_ID);
            OTFamille = this.getOdataManager().getEm().find(TFamille.class, lg_FAMILLE_ID);

        } catch (Exception e) {

        }
        // </editor-fold>
    }

    public TFamilleGrossiste getTProduct(String lg_FAMILLE_ID, String lg_GROSSISTE_ID) {

        TFamille OTFamille = null;
        TFamilleGrossiste OTFamilleGrossiste = null;
        TGrossiste OTGrossiste = null;

        try {

            OTGrossiste = this.getOdataManager().getEm().find(TGrossiste.class, lg_GROSSISTE_ID);
            new logger().OCategory
                    .info("Id " + OTGrossiste.getLgGROSSISTEID() + " Famille " + OTGrossiste.getStrLIBELLE());

            OTFamille = this.getOdataManager().getEm().find(TFamille.class, lg_FAMILLE_ID);
            new logger().OCategory
                    .info("Id " + OTFamille.getLgFAMILLEID() + " Famille " + OTFamille.getStrDESCRIPTION());

            OTFamilleGrossiste = (TFamilleGrossiste) this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TFamilleGrossiste t WHERE t.lgFAMILLEID.lgFAMILLEID LIKE ?1 AND t.lgGROSSISTEID.lgGROSSISTEID LIKE ?2")
                    .setParameter(1, lg_FAMILLE_ID).setParameter(2, lg_GROSSISTE_ID).getSingleResult();

            new logger().OCategory.info("code article " + OTFamilleGrossiste.getStrCODEARTICLE());

        } catch (Exception e) {

            this.buildErrorTraceMessage(e.getMessage());
        }
        return OTFamilleGrossiste;
    }

    public List<TFamille> getallfamille() {

        List<TFamille> lstTFamille = null;

        TFamilleGrossiste OTFamilleGrossiste = null;

        try {

            lstTFamille = this.getOdataManager().getEm()
                    .createQuery("SELECT t FROM TFamille t WHERE t.strSTATUT LIKE ?1")
                    .setParameter(1, commonparameter.statut_enable).getResultList();
            new logger().OCategory.info("lstTFamille " + lstTFamille.size());

            for (TFamille OTFamille : lstTFamille) {

                OTFamilleGrossiste = new TFamilleGrossiste();

                OTFamilleGrossiste.setLgFAMILLEGROSSISTEID(this.getKey().getComplexId());
                OTFamilleGrossiste.setLgFAMILLEID(OTFamille);
                OTFamilleGrossiste.setLgGROSSISTEID(OTFamille.getLgGROSSISTEID());
                OTFamilleGrossiste.setStrCODEARTICLE(OTFamille.getIntCIP());
                OTFamilleGrossiste.setIntPRICE(OTFamille.getIntPRICE());

                this.persiste(OTFamilleGrossiste);

            }

            new logger().OCategory.info("COOL ");

        } catch (Exception Ex) {

            new logger().OCategory.info("ECHEC  ");

        }

        return lstTFamille;

    }

    public List<TOrder> getOrderOfGrossiste(String lg_GROSSISTE_ID) {

        List<TOrder> lstTOrder = null;

        try {

            lstTOrder = this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TOrder t WHERE t.lgGROSSISTEID.lgGROSSISTEID = ?1 AND (t.strSTATUT LIKE ?2 OR t.strSTATUT LIKE ?3 OR t.strSTATUT LIKE ?4 OR t.strSTATUT LIKE ?5) ORDER BY t.dtCREATED DESC ")
                    .setParameter(1, lg_GROSSISTE_ID).setParameter(2, commonparameter.statut_is_Process)
                    .setParameter(3, commonparameter.orderIsPartial).setParameter(4, commonparameter.orderIsPassed)
                    .setParameter(5, commonparameter.statut_is_Closed).getResultList();
            new logger().OCategory.info("ListTBonLivraison " + lstTOrder.size());
        } catch (Exception Ex) {

        }

        return lstTOrder;

    }

    public void ChangeGrossisteOrder(String SUGG_ORDER, String ID_SUGG_ORDER, String lg_GROSSISTE_ID) {

        try {

            String lg_ORDER_ID = "";
            TOrder OTOrder = null;
            TSuggestionOrder OTSuggestionOrder = null;
            TGrossiste OTGrossiste = null;

            switch (SUGG_ORDER) {

            case "COMMANDE":

                lg_ORDER_ID = ID_SUGG_ORDER;
                OTOrder = this.FindOrder(lg_ORDER_ID);
                if (OTOrder != null) {
                    /*
                     * new logger().OCategory.info("OTOrder ID *** " + OTOrder.getLgORDERID()); new
                     * logger().OCategory.info("OTOrder REF *** " + OTOrder.getStrREFORDER()); new
                     * logger().OCategory.info("ANCIEN GROSSISTE *** " + OTOrder.getLgGROSSISTEID().getStrLIBELLE());
                     */
                    OTGrossiste = this.getOdataManager().getEm().find(TGrossiste.class, lg_GROSSISTE_ID);
                    List<TOrderDetail> lstTOrderDetail = new ArrayList<>();
                    lstTOrderDetail = this.getTOrderDetail("", lg_ORDER_ID, OTOrder.getStrSTATUT());
                    if (OTGrossiste != null) {
                        OTOrder.setLgGROSSISTEID(OTGrossiste);
                        OTOrder.setDtUPDATED(new Date());
                        this.persiste(OTOrder);
                        for (TOrderDetail OTOrderDetail : lstTOrderDetail) {
                            OTOrderDetail.setLgGROSSISTEID(OTGrossiste);
                            OTOrderDetail.setDtUPDATED(new Date());
                            this.persiste(OTOrderDetail);
                        }

                        this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
                        /*
                         * new logger().OCategory.info("OTOrder ID *** " + OTOrder.getLgORDERID()); new
                         * logger().OCategory.info("OTOrder REF *** " + OTOrder.getStrREFORDER()); new
                         * logger().OCategory.info("NEW GROSSISTE *** " + OTOrder.getLgGROSSISTEID().getStrLIBELLE());
                         */

                    }

                }

                break;

            case "SUGGESTION":

                String lg_SUGGESTION_ORDER_ID = ID_SUGG_ORDER;
                OTSuggestionOrder = new suggestionManagement(this.getOdataManager())
                        .findTsuggestionOrder(lg_SUGGESTION_ORDER_ID);
                List<TSuggestionOrderDetails> lstTSuggestionOrderDetails = new ArrayList<TSuggestionOrderDetails>();
                lstTSuggestionOrderDetails = new WarehouseManager(this.getOdataManager())
                        .getTSuggestionOrderDetails(OTSuggestionOrder.getLgSUGGESTIONORDERID());
                if (OTSuggestionOrder != null) {

                    OTGrossiste = this.getOdataManager().getEm().find(TGrossiste.class, lg_GROSSISTE_ID);
                    if (OTGrossiste != null) {
                        OTSuggestionOrder.setLgGROSSISTEID(OTGrossiste);
                        OTSuggestionOrder.setDtUPDATED(new Date());
                        this.persiste(OTSuggestionOrder);

                        for (TSuggestionOrderDetails OTSuggestionOrderDetails : lstTSuggestionOrderDetails) {
                            OTSuggestionOrderDetails.setLgGROSSISTEID(OTGrossiste);
                            OTSuggestionOrderDetails.setDtUPDATED(new Date());
                            this.persiste(OTSuggestionOrderDetails);
                        }

                        this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
                    }

                }

                break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.buildSuccesTraceMessage("Echec de mise a jour du grossiste");
        }

    }

    public String ExportOrderCSV(String lg_ORDER_ID) {

        TOrder OTOrder = null;
        String FILEPATH = "";
        List<TOrderDetail> lstTOrderDetail = new ArrayList<>();

        try {

            OTOrder = this.FindOrder(lg_ORDER_ID);
            new logger().OCategory.info("OTOrder  *** " + OTOrder.getLgORDERID());

            if (OTOrder != null) {

                lstTOrderDetail = this.getTOrderDetail(lg_ORDER_ID, commonparameter.statut_is_Process);

                new logger().OCategory.info("lstTOrderDetail  *** " + lstTOrderDetail.size());

                FILEPATH = this.ExportToCsv(lstTOrderDetail, OTOrder);

            }

        } catch (Exception e) {
            new logger().OCategory.info("Impossible de générer le fichier CSV");
        }

        return FILEPATH;
    }

    public String ExportEtatCommandeByOrderCSV(String lg_ORDER_ID) {

        String FILEPATH = "", title = "";
        bonLivraisonManagement ObonLivraisonManagement = new bonLivraisonManagement(this.getOdataManager());
        List<TBonLivraisonDetail> lstTBonLivraisonDetail = new ArrayList<>();

        try {
            lstTBonLivraisonDetail = ObonLivraisonManagement.getTBonLivraisonDetail(lg_ORDER_ID,
                    commonparameter.statut_is_Closed);
            if (lstTBonLivraisonDetail.size() > 0) {
                title = "LIVRAISON_" + lstTBonLivraisonDetail.get(0).getLgBONLIVRAISONID().getStrREFLIVRAISON();

            }
            List<String> lstData = new ArrayList<>();

            for (TBonLivraisonDetail OTBonLivraisonDetail : lstTBonLivraisonDetail) {
                String ItemData = OTBonLivraisonDetail.getLgFAMILLEID().getIntCIP() + ";"
                        + OTBonLivraisonDetail.getLgFAMILLEID().getStrDESCRIPTION() + ";"
                        + OTBonLivraisonDetail.getLgFAMILLEID().getIntPRICE() + ";"
                        + OTBonLivraisonDetail.getIntQTERECUE();

                lstData.add(ItemData);
                // ffw.write(OTOrderDetail.getIntCIP() + ";" + OTOrderDetail.getStrDESCRIPTION() + ";" +
                // OTOrderDetail.getIntPRICE() + ";" + OTOrderDetail.getLgFAMILLEID().getIntPAT() + ";" +
                // OTOrderDetail.getLgFAMILLEID().getIntPAF() + ";" + OTOrderDetail.getIntNUMBER() + ";" +
                // OTOrderDetail.getIntCOUTPAT() + ";" + OTOrderDetail.getIntCOUTPAF() + "\n");
            }
            FILEPATH = this.ExportToCsvByData(lstData, title + commonparameter.extension_csv);

        } catch (Exception e) {
            new logger().OCategory.info("Impossible de générer le fichier CSV");
        }

        return FILEPATH;
    }

    // exportation inventaire en csv
    public String ExportToCsvByData(List<String> lstData, String str_NAMEFILE) {

        String filepath = "";
        jdom.InitRessource();
        jdom.LoadRessource();
        date Key = new date();

        try {

            filepath = jdom.path_export_csv + str_NAMEFILE;

            CsvFiles OCsvFiles = new CsvFiles();
            OCsvFiles.setPath_outut(filepath);
            OCsvFiles.SaveToFile(lstData);

            System.out.println("Fichier csv généré avec succès! Chemin  " + filepath);

        } catch (Exception ex) {
            System.out.println(ex);
        }
        return filepath;
    }

    public TFamilleGrossiste findFamilleGrossiste(String lg_FAMILLE_ID, String lg_GROSSISTE_ID) {
        TFamilleGrossiste OTFamilleGrossiste = null;
        try {
            Query qry = this.getOdataManager().getEm().createQuery(
                    "SELECT DISTINCT t FROM TFamilleGrossiste t WHERE t.lgFAMILLEID.lgFAMILLEID LIKE ?1 AND (t.lgGROSSISTEID.lgGROSSISTEID = ?2 OR t.lgGROSSISTEID.strDESCRIPTION = ?2) AND t.strSTATUT LIKE ?3 ")
                    .setParameter(1, lg_FAMILLE_ID).setParameter(2, lg_GROSSISTE_ID)
                    .setParameter(3, commonparameter.statut_enable);
            qry.setMaxResults(1);
            if (qry.getResultList().size() > 0) {
                OTFamilleGrossiste = (TFamilleGrossiste) qry.getSingleResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return OTFamilleGrossiste;
    }

    public String ExportToCsv(List<TOrderDetail> lstTOrderDetail, TOrder OTOrder) {

        String str_NAMEFILE = "";
        String filepath = "";
        jdom.InitRessource();
        jdom.LoadRessource();
        date Key = new date();

        try {

            str_NAMEFILE = "COMMANDE_" + OTOrder.getStrREFORDER() + commonparameter.extension_csv;
            filepath = jdom.path_export_csv + str_NAMEFILE;

            List<String> lstData = new ArrayList<>();

            for (TOrderDetail OTOrderDetail : lstTOrderDetail) {
                TFamilleGrossiste tfg = findFamilleGrossiste(OTOrderDetail.getLgFAMILLEID().getLgFAMILLEID(),
                        OTOrderDetail.getLgORDERID().getLgGROSSISTEID().getLgGROSSISTEID());
                String ItemData = (OTOrderDetail.getLgFAMILLEID().getIntEAN13() != null
                        && !OTOrderDetail.getLgFAMILLEID().getIntEAN13().equals("")
                                ? OTOrderDetail.getLgFAMILLEID().getIntEAN13()
                                : (tfg != null ? tfg.getStrCODEARTICLE() : OTOrderDetail.getLgFAMILLEID().getIntCIP()))
                        + ";" + OTOrderDetail.getIntNUMBER();

                lstData.add(ItemData);

            }

            CsvFiles OCsvFiles = new CsvFiles();
            OCsvFiles.setPath_outut(filepath);
            OCsvFiles.SaveToFile(lstData);

            System.out.println("Fichier csv généré avec succès! Chemin  " + filepath);

        } catch (Exception ex) {
            System.out.println(ex);
        }
        return filepath;
    }

    public List<TOrder> listeOrderBySearch(String search_value, String lg_GROSSISTE_ID, String dt_DEBUT,
            String dt_FIN) {
        List<TOrder> lstTOrder = new ArrayList<TOrder>();
        Date dtFin;
        try {
            if (search_value.equalsIgnoreCase("") || lg_GROSSISTE_ID == null) {
                lg_GROSSISTE_ID = "%%";
            }
            if (search_value.equalsIgnoreCase("") || search_value == null) {
                search_value = "%%";
            }
            new logger().OCategory.info("search_value:" + search_value);

            if (dt_DEBUT.equalsIgnoreCase("") || dt_DEBUT == null) {
                dt_DEBUT = "2015-04-20";
                new logger().OCategory.info("dt_DEBUT:" + dt_DEBUT);
            }
            if (dt_FIN.equalsIgnoreCase("") || dt_FIN == null) {
                dtFin = new Date();
            } else {
                dtFin = this.getKey().stringToDate(dt_FIN, this.getKey().formatterMysqlShort);
            }
            Date dtDEBUT = this.getKey().stringToDate(dt_DEBUT, this.getKey().formatterMysqlShort);
            new logger().OCategory
                    .info("dtDEBUT   " + dtDEBUT + " dtFin " + dtFin + " lg_GROSSISTE_ID " + lg_GROSSISTE_ID);

            lstTOrder = this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TOrder t WHERE t.strREFORDER LIKE ?1 AND (t.strSTATUT = ?2 OR t.strSTATUT = ?3 OR t.strSTATUT = ?4) AND t.lgGROSSISTEID.lgGROSSISTEID LIKE ?5 AND (t.dtCREATED BETWEEN ?6 AND ?7) ORDER BY t.dtCREATED DESC")
                    .setParameter(1, "%" + search_value + "%").setParameter(2, commonparameter.statut_is_Process)
                    .setParameter(3, commonparameter.orderIsPassed).setParameter(4, commonparameter.statut_is_Closed)
                    .setParameter(5, lg_GROSSISTE_ID).setParameter(6, dtDEBUT).setParameter(7, dtFin).getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        new logger().OCategory.info("lstTOrder size " + lstTOrder.size());
        return lstTOrder;
    }

    public int isCommandProcess(String lgFamilleID) {
        bonLivraisonManagement bl = new bonLivraisonManagement(this.getOdataManager());
        int status = bl.articleStatus(lgFamilleID);

        return status;
    }

    public void deleteOrder(String lg_ORDER_ID) {

        TOrder OTOrder;
        List<TOrderDetail> lstTOrderDetail;
        int i = 0;
        try {

            OTOrder = this.FindOrder(lg_ORDER_ID);

            lstTOrderDetail = this.getTOrderDetail(OTOrder.getLgORDERID(), OTOrder.getStrSTATUT());

            for (TOrderDetail OTOrderDetails : lstTOrderDetail) {
                new logger().OCategory.info("Ligne detail order *** " + OTOrderDetails.getLgORDERDETAILID());
                TFamille Of = OTOrderDetails.getLgFAMILLEID();

                if (this.delete(OTOrderDetails)) {
                    int status = isCommandProcess(Of.getLgFAMILLEID());
                    switch (status) {
                    case 0:
                        Of.setBCODEINDICATEUR((short) 0);
                        break;
                    case 1:
                        Of.setBCODEINDICATEUR((short) 2);
                        break;

                    default:
                        Of.setBCODEINDICATEUR((short) 1);
                        break;
                    }
                    Of.setIntORERSTATUS((short) status);
                    this.merge(Of);
                    i++;
                }

            }

            if (lstTOrderDetail.size() > 0) {
                if (i == lstTOrderDetail.size()) {
                    this.delete(OTOrder);
                    this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
                } else {
                    this.buildErrorTraceMessage(i + "/" + lstTOrderDetail.size() + " produit(s) supprimé(s)");
                }

            } else {
                this.buildErrorTraceMessage("Aucune ligne trouvée dans la commande");
            }

        } catch (Exception E) {
            E.printStackTrace();
            this.buildErrorTraceMessage("Echec de suppression de la commande");
        }

    }

    public void SuggestionDetailEnable(TSuggestionOrderDetails OTSuggestionOrderDetails) {

        if (OTSuggestionOrderDetails != null) {
            OTSuggestionOrderDetails.setStrSTATUT(commonparameter.statut_enable);
            this.persiste(OTSuggestionOrderDetails);
        }
    }

    public void SuggestionEnable(TSuggestionOrder OTSuggestionOrder) {

        if (OTSuggestionOrder != null) {

            OTSuggestionOrder.setStrSTATUT(commonparameter.statut_enable);
            this.persiste(OTSuggestionOrder);
        }

    }

    public TOrder PasseOrderToGrossiste(String lg_ORDER_ID) {

        TOrder OTOrder = null;

        try {
            OTOrder = this.FindOrder(lg_ORDER_ID);
            if (!this.getOdataManager().getEm().getTransaction().isActive()) {
                this.getOdataManager().getEm().getTransaction().begin();
            }

            CriteriaBuilder cb = this.getOdataManager().getEm().getCriteriaBuilder();
            CriteriaUpdate<TOrderDetail> cu = cb.createCriteriaUpdate(TOrderDetail.class);
            Root<TOrderDetail> root = cu.from(TOrderDetail.class);
            Join<TOrderDetail, TOrder> j = root.join("lgORDERID", JoinType.INNER);
            cu.set(root.get(TOrderDetail_.strSTATUT), commonparameter.orderIsPassed)
                    .set(root.get(TOrderDetail_.dtUPDATED), new Date())
                    .set(root.get(TOrderDetail_.intORERSTATUS), (short) 3);
            cu.where(cb.equal(j.get(TOrder_.lgORDERID), lg_ORDER_ID));
            this.getOdataManager().getEm().createQuery(cu).executeUpdate();
            OTOrder.setStrSTATUT(commonparameter.orderIsPassed);
            OTOrder.setDtUPDATED(new Date());

            this.getOdataManager().getEm().merge(OTOrder);
            if (this.getOdataManager().getEm().getTransaction().isActive()) {
                this.getOdataManager().getEm().getTransaction().commit();
                this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));

            }

        } catch (Exception e) {
            this.getOdataManager().getEm().getTransaction().rollback();
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec d'enregistrement de la commande");
        }

        return OTOrder;
    }

    // annulation d'une commande en cours
    public TOrder RollBackPasseOrderToCommandeProcess(String lg_ORDER_ID) {

        TOrder OTOrder = null;
        List<TOrderDetail> lstTOrderDetail;
        int i = 0;

        try {
            OTOrder = this.FindOrder(lg_ORDER_ID);
            lstTOrderDetail = this.getTOrderDetail("", lg_ORDER_ID, OTOrder.getStrSTATUT());
            if (!this.getOdataManager().getEm().getTransaction().isActive()) {
                this.getOdataManager().getEm().getTransaction().begin();
            }
            for (TOrderDetail OTOrderDetail : lstTOrderDetail) {
                OTOrderDetail.setStrSTATUT(commonparameter.statut_is_Process);
                OTOrderDetail.setDtUPDATED(new Date());
                this.getOdataManager().getEm().merge(OTOrderDetail);

            }
            OTOrder.setStrSTATUT(commonparameter.statut_is_Process);
            OTOrder.setDtUPDATED(new Date());
            this.getOdataManager().getEm().merge(OTOrder);
            if (this.getOdataManager().getEm().getTransaction().isActive()) {
                this.getOdataManager().getEm().getTransaction().commit();
                this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
            }

        } catch (Exception e) {

            this.buildErrorTraceMessage("Echec de l'annulation de la commande");
            e.printStackTrace();
        }

        return OTOrder;
    }

    public void deleteUg(String str_REF_LIVRAISON, String lg_FAMILLE_ID) {
        EntityManager em = this.getOdataManager().getEm();
        try {
            List<TLot> tLots = this.getOdataManager().getEm()
                    .createQuery("SELECT o FROM TLot o WHERE o.strREFLIVRAISON=?1 AND o.lgFAMILLEID.lgFAMILLEID =?2  ",
                            TLot.class)
                    .setParameter(1, str_REF_LIVRAISON).setParameter(2, lg_FAMILLE_ID).getResultList();
            em.getTransaction().begin();
            for (TLot tLot : tLots) {

                em.remove(tLot);
            }
            em.getTransaction().commit();
            // em.close();
            this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
                // em.close();
            }
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec de suppression");
        }
    }
    // fin annulation d'une commande en cours

    public TOrder MakeSuggestionToOrder(TSuggestionOrder OTSuggestionOrder, String str_STATUT) {

        TOrder OTOrder = null;
        WarehouseManager OWarehouseManager = new WarehouseManager(this.getOdataManager());
        List<TSuggestionOrderDetails> ListTSuggestionOrderDetails;
        Date now = new Date();
        try {
            if (OTSuggestionOrder != null) {
                OTOrder = this.createOrder(OTSuggestionOrder.getLgGROSSISTEID().getLgGROSSISTEID(),
                        commonparameter.statut_is_Process);
                ListTSuggestionOrderDetails = OWarehouseManager
                        .getTSuggestionOrderDetails(OTSuggestionOrder.getLgSUGGESTIONORDERID());

                // Parcours la liste des DetailsSuggestions
                this.getOdataManager().BeginTransaction();
                for (TSuggestionOrderDetails OListTSuggestionOrderDetails : ListTSuggestionOrderDetails) {
                    if (OListTSuggestionOrderDetails.getStrSTATUT().equals(commonparameter.statut_is_Process)
                            || OListTSuggestionOrderDetails.getStrSTATUT().equals(commonparameter.statut_is_Auto)) {
                        this.createOrderDetail(OTOrder, OListTSuggestionOrderDetails.getLgFAMILLEID(),
                                OTSuggestionOrder.getLgGROSSISTEID(), OListTSuggestionOrderDetails.getIntNUMBER(),
                                OListTSuggestionOrderDetails.getIntPRICEDETAIL(),
                                OListTSuggestionOrderDetails.getIntPAFDETAIL());
                        OListTSuggestionOrderDetails.setStrSTATUT(commonparameter.statut_enable);
                        OListTSuggestionOrderDetails.setDtUPDATED(now);
                        this.getOdataManager().getEm().merge(OListTSuggestionOrderDetails);
                    }
                }
                OTSuggestionOrder.setStrSTATUT(commonparameter.statut_enable);
                OTSuggestionOrder.setDtUPDATED(now);
                this.getOdataManager().getEm().merge(OTSuggestionOrder);
                this.getOdataManager().CloseTransaction();
                this.buildSuccesTraceMessage(
                        "Suggestion " + OTSuggestionOrder.getStrREF() + " transformée en commande avec succès");

            }

        } catch (Exception E) {
            E.printStackTrace();
            this.buildErrorTraceMessage("Echec de transformation de suggestion en commande");
        }

        return OTOrder;

    }

    public TOrderDetail CreateTOrderDetail(String lg_ORDER_ID, String lg_famille_id, String lg_GROSSISTE_ID,
            int int_NUMBER) {

        TOrderDetail OTOrderDetail = null;
        TGrossiste OTGrossiste = SearchGrossiste(lg_GROSSISTE_ID);

        try {

            OTOrderDetail = this.findFamilleInTOrderDetail(lg_ORDER_ID, lg_famille_id);
            TFamille OTFamille = new familleManagement(this.getOdataManager()).getTFamille(lg_famille_id);

            if (OTOrderDetail == null) {

                new logger().OCategory.info(" OTOrderDetail is null creation dun nouveau detail ------ ***** ----- ");

                OTOrderDetail = new TOrderDetail();
                OTOrderDetail.setLgORDERDETAILID(this.getKey().getComplexId());
                OTOrderDetail.setLgORDERID(this.FindOrder(lg_ORDER_ID));
                OTOrderDetail.setLgFAMILLEID(OTFamille);
                OTOrderDetail.setLgGROSSISTEID(OTGrossiste);
                OTOrderDetail.setIntNUMBER(int_NUMBER);
                OTOrderDetail.setIntQTEREPGROSSISTE(int_NUMBER);
                OTOrderDetail.setIntQTEMANQUANT(int_NUMBER);
                OTOrderDetail.setIntPRICE(int_NUMBER * OTFamille.getIntPAF());
                OTOrderDetail.setIntPAFDETAIL(OTFamille.getIntPAF());
                OTOrderDetail.setIntPRICEDETAIL(OTFamille.getIntPRICE());
                OTOrderDetail.setStrSTATUT(commonparameter.statut_is_Process);
                OTOrderDetail.setDtCREATED(new Date());
            } else {

                OTOrderDetail.setIntNUMBER(OTOrderDetail.getIntNUMBER() + int_NUMBER);
                OTOrderDetail.setIntQTEMANQUANT(OTOrderDetail.getIntNUMBER());
                OTOrderDetail.setIntPRICE(int_NUMBER * OTFamille.getIntPAF());
                OTOrderDetail.setIntPAFDETAIL(OTFamille.getIntPAF());
                OTOrderDetail.setIntPRICEDETAIL(OTFamille.getIntPRICE());
                OTOrderDetail.setIntQTEREPGROSSISTE(OTOrderDetail.getIntNUMBER());
                OTOrderDetail.setDtUPDATED(new Date());
            }
            OTOrderDetail.setIntORERSTATUS((short) 2);
            new logger().OCategory.info(" creation de OTOrderDetail Details ------ ***** ----- ");

            this.persiste(OTOrderDetail);
            TFamille of = OTOrderDetail.getLgFAMILLEID();
            of.setBCODEINDICATEUR((short) 1);
            of.setIntORERSTATUS((short) 2);
            this.merge(of);
            this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
            return OTOrderDetail;
        } catch (NoResultException e) {

            new logger().OCategory.info("impossible de creer OTOrderDetail   " + e.toString());
            return null;
        }
    }

    public TOrderDetail CreateTOrderDetail(String lg_ORDER_ID, String lg_famille_id, String lg_GROSSISTE_ID,
            int int_NUMBER, int int_PAF_DETAIL) {

        TOrderDetail OTOrderDetail = null;
        TGrossiste OTGrossiste = SearchGrossiste(lg_GROSSISTE_ID);

        try {

            OTOrderDetail = this.findFamilleInTOrderDetail(lg_ORDER_ID, lg_famille_id);
            TFamille OTFamille = new familleManagement(this.getOdataManager()).getTFamille(lg_famille_id);

            if (OTOrderDetail == null) {

                new logger().OCategory.info(" OTOrderDetail is null creation dun nouveau detail ------ ***** ----- ");

                OTOrderDetail = new TOrderDetail();
                OTOrderDetail.setLgORDERDETAILID(this.getKey().getComplexId());
                OTOrderDetail.setLgORDERID(this.FindOrder(lg_ORDER_ID));
                OTOrderDetail.setLgFAMILLEID(OTFamille);
                OTOrderDetail.setLgGROSSISTEID(OTGrossiste);
                OTOrderDetail.setIntNUMBER(int_NUMBER);
                OTOrderDetail.setIntQTEREPGROSSISTE(int_NUMBER);
                OTOrderDetail.setIntQTEMANQUANT(int_NUMBER);
                OTOrderDetail.setIntPRICE(int_NUMBER * int_PAF_DETAIL);
                OTOrderDetail.setIntPRICEDETAIL(OTFamille.getIntPRICE());
                OTOrderDetail.setIntPAFDETAIL(int_PAF_DETAIL);
                OTOrderDetail.setStrSTATUT(commonparameter.statut_is_Process);
                OTOrderDetail.setDtCREATED(new Date());
                this.persiste(OTOrderDetail);
            } else {

                OTOrderDetail.setIntNUMBER(OTOrderDetail.getIntNUMBER() + int_NUMBER);
                OTOrderDetail.setIntQTEMANQUANT(OTOrderDetail.getIntNUMBER());
                OTOrderDetail.setIntPRICE(OTOrderDetail.getIntNUMBER() * int_PAF_DETAIL);
                OTOrderDetail.setIntPRICEDETAIL(OTFamille.getIntPRICE());
                OTOrderDetail.setIntPAFDETAIL(int_PAF_DETAIL);
                OTOrderDetail.setIntQTEREPGROSSISTE(OTOrderDetail.getIntNUMBER());
                OTOrderDetail.setDtUPDATED(new Date());
                this.merge(OTOrderDetail);

            }
            OTOrderDetail.setIntORERSTATUS((short) 2);

            new logger().OCategory.info(" creation de OTOrderDetail Details ------ ***** ----- ");

            TFamille OF = OTOrderDetail.getLgFAMILLEID();
            OF.setBCODEINDICATEUR((short) 1);
            OF.setIntORERSTATUS((short) 2);
            this.merge(OF);
            this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
            return OTOrderDetail;
        } catch (NoResultException e) {
            e.printStackTrace();
            new logger().OCategory.info("impossible de creer OTOrderDetail   " + e.toString());
            return null;
        }
    }

    public TOrderDetail createTOrderDetailVIACSV(String lg_ORDER_ID, String lg_famille_id, String lg_GROSSISTE_ID,
            int int_NUMBER, int int_PAF_DETAIL, int pu) {

        TOrderDetail OTOrderDetail = null;
        TGrossiste OTGrossiste = SearchGrossiste(lg_GROSSISTE_ID);

        try {

            OTOrderDetail = this.findFamilleInTOrderDetail(lg_ORDER_ID, lg_famille_id);
            TFamille OTFamille = new familleManagement(this.getOdataManager()).getTFamille(lg_famille_id);

            if (OTOrderDetail == null) {

                new logger().OCategory.info(" OTOrderDetail is null creation dun nouveau detail ------ ***** ----- ");

                OTOrderDetail = new TOrderDetail();
                OTOrderDetail.setLgORDERDETAILID(this.getKey().getComplexId());
                OTOrderDetail.setLgORDERID(this.FindOrder(lg_ORDER_ID));
                OTOrderDetail.setLgFAMILLEID(OTFamille);
                OTOrderDetail.setLgGROSSISTEID(OTGrossiste);
                OTOrderDetail.setIntNUMBER(int_NUMBER);
                OTOrderDetail.setIntQTEREPGROSSISTE(int_NUMBER);
                OTOrderDetail.setIntQTEMANQUANT(int_NUMBER);
                OTOrderDetail.setIntPRICE(int_NUMBER * int_PAF_DETAIL);
                OTOrderDetail.setIntPRICEDETAIL(pu);
                OTOrderDetail.setIntPAFDETAIL(int_PAF_DETAIL);
                OTOrderDetail.setStrSTATUT(commonparameter.statut_is_Process);
                OTOrderDetail.setDtCREATED(new Date());
                this.persiste(OTOrderDetail);
            } else {

                OTOrderDetail.setIntNUMBER(OTOrderDetail.getIntNUMBER() + int_NUMBER);
                OTOrderDetail.setIntQTEMANQUANT(OTOrderDetail.getIntNUMBER());
                OTOrderDetail.setIntPRICE(OTOrderDetail.getIntNUMBER() * int_PAF_DETAIL);
                OTOrderDetail.setIntPRICEDETAIL(OTFamille.getIntPRICE());
                OTOrderDetail.setIntPAFDETAIL(int_PAF_DETAIL);
                OTOrderDetail.setIntQTEREPGROSSISTE(OTOrderDetail.getIntNUMBER());
                OTOrderDetail.setDtUPDATED(new Date());
                this.merge(OTOrderDetail);

            }
            OTOrderDetail.setIntORERSTATUS((short) 2);

            new logger().OCategory.info(" creation de OTOrderDetail Details ------ ***** ----- ");

            TFamille OF = OTOrderDetail.getLgFAMILLEID();
            OF.setBCODEINDICATEUR((short) 1);
            OF.setIntORERSTATUS((short) 2);
            this.merge(OF);
            this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
            return OTOrderDetail;
        } catch (NoResultException e) {
            e.printStackTrace();
            new logger().OCategory.info("impossible de creer OTOrderDetail   " + e.toString());
            return null;
        }
    }

    public TFamilleGrossiste getTProductItemStock(TFamille OTFamille) {
        TFamilleGrossiste OTFamilleGrossiste = null;
        try {

            OTFamilleGrossiste = (TFamilleGrossiste) this.getOdataManager().getEm()
                    .createQuery("SELECT t FROM TFamilleGrossiste t WHERE t.lgFAMILLEID.lgFAMILLEID LIKE ?1 ")
                    .setParameter("1", OTFamille.getLgFAMILLEID()).getSingleResult();
        } catch (Exception e) {
            this.buildErrorTraceMessage(e.getMessage());
        }
        return OTFamilleGrossiste;
    }

    public TOrderDetail AddToTOrderDetail(TFamille OTFamille, TGrossiste OTGrossiste, TOrder OTOrder, int int_qte) {

        String lg_GROSSISTE_ID = OTGrossiste.getLgGROSSISTEID();

        if (OTOrder == null) {

            OTOrder = this.createOrder(lg_GROSSISTE_ID, commonparameter.statut_is_Process);
        }

        TOrderDetail OTOrderDetail = new TOrderDetail();
        OTOrderDetail.setLgORDERDETAILID(this.getKey().getComplexId());
        OTOrderDetail.setLgORDERID(OTOrder);
        OTOrderDetail.setLgFAMILLEID(OTFamille);
        OTOrderDetail.setLgGROSSISTEID(OTGrossiste);

        new logger().OCategory.info("QTE " + int_qte);

        OTOrderDetail.setIntNUMBER(int_qte);
        OTOrderDetail.setIntQTEMANQUANT(int_qte);
        OTOrderDetail.setIntPRICE(int_qte * OTFamille.getIntPAT());
        OTOrderDetail.setStrSTATUT(commonparameter.statut_is_Process);
        OTOrderDetail.setDtCREATED(new Date());
        OTOrderDetail.setIntORERSTATUS((short) 2);
        this.persiste(OTOrderDetail);
        OTFamille.setIntORERSTATUS((short) 2);
        this.merge(OTFamille);
        new logger().OCategory.info("Mise a jour de OTOrderDetail " + OTOrderDetail.getIntNUMBER());
        this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
        return OTOrderDetail;
    }

    public TOrderDetail UpdateTOrderDetail(String lg_ORDERDETAIL_ID, String lg_ORDER_ID, String lg_famille_id,
            String lg_GROSSISTE_ID, int int_NUMBER) {
        TOrderDetail OTOrderDetail = null;
        TGrossiste OTGrossiste = null;
        TFamille OTFamille = null;
        try {

            OTOrderDetail = this.getOdataManager().getEm().find(TOrderDetail.class, lg_ORDERDETAIL_ID);
            OTGrossiste = SearchGrossiste(lg_GROSSISTE_ID);
            OTFamille = new familleManagement(this.getOdataManager()).getTFamille(lg_famille_id);
            OTOrderDetail.setLgORDERID(this.FindOrder(lg_ORDER_ID));
            OTOrderDetail.setLgFAMILLEID(OTFamille);
            OTOrderDetail.setLgGROSSISTEID(OTGrossiste);
            OTOrderDetail.setIntNUMBER(int_NUMBER);
            OTOrderDetail.setIntQTEMANQUANT(int_NUMBER);
            OTOrderDetail.setIntQTEREPGROSSISTE(OTOrderDetail.getIntNUMBER());
            OTOrderDetail.setIntPRICE(OTOrderDetail.getIntQTEREPGROSSISTE() * OTFamille.getIntPAT());
            OTOrderDetail.setStrSTATUT(commonparameter.statut_is_Process);
            OTOrderDetail.setDtUPDATED(new Date());
            this.persiste(OTOrderDetail);
            this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
            // this.refresh(OTOrderDetail);
            // new logger().OCategory.info(" update de OTOrderDetail " + OTOrderDetail.getLgORDERDETAILID());

        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec de mise a jour de l'article dans la commande");
            /*
             * OTOrderDetail = this.CreateTOrderDetail(lg_ORDER_ID, lg_famille_id, lg_GROSSISTE_ID, int_NUMBER); new
             * logger().OCategory.info(" create de OTPreenregistrementDetail  " + OTOrderDetail.getLgORDERDETAILID() +
             * "    " + e.toString()); return OTOrderDetail;
             */
        }
        return OTOrderDetail;
    }

    public TOrderDetail UpdateTOrderDetail(String lg_ORDERDETAIL_ID, String lg_ORDER_ID, String lg_famille_id,
            String lg_GROSSISTE_ID, int int_NUMBER, String str_STATUT, int int_PRICE, int int_PAF) {
        TOrderDetail OTOrderDetail = null;
        TGrossiste OTGrossiste = null;
        TFamille OTFamille = null;
        TOrder OTOrder = null;
        try {

            OTOrderDetail = this.getOdataManager().getEm().find(TOrderDetail.class, lg_ORDERDETAIL_ID);
            OTOrder = OTOrderDetail.getLgORDERID();
            OTGrossiste = SearchGrossiste(lg_GROSSISTE_ID);
            OTFamille = new familleManagement(this.getOdataManager()).getTFamille(lg_famille_id);
            OTOrderDetail.setLgORDERID(this.FindOrder(lg_ORDER_ID));
            OTOrderDetail.setLgFAMILLEID(OTFamille);
            OTOrderDetail.setLgGROSSISTEID(OTGrossiste);
            OTOrderDetail.setIntNUMBER(int_NUMBER);
            OTOrderDetail.setIntQTEREPGROSSISTE(int_NUMBER);
            OTOrderDetail.setIntQTEMANQUANT(int_NUMBER);
            OTOrderDetail.setIntPRICE(int_NUMBER * int_PAF);
            OTOrderDetail.setIntPAFDETAIL(int_PAF);
            OTOrderDetail.setIntPRICEDETAIL(int_PRICE);
            OTOrderDetail.setStrSTATUT(str_STATUT);
            OTOrderDetail.setDtUPDATED(new Date());
            OTOrderDetail.setPrixAchat(int_PAF);
            // OTOrderDetail.setPrixUnitaire(int_PRICE);
            OTOrder.setDtUPDATED(new Date());
            if (this.persiste(OTOrderDetail) && this.persiste(OTOrder)) {
                this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));

            } else {
                this.buildErrorTraceMessage("Echec de l'opération");
            }
            // this.refresh(OTOrderDetail);
            // new logger().OCategory.info(" update de OTOrderDetail " + OTOrderDetail.getLgORDERDETAILID());

            return OTOrderDetail;
        } catch (Exception e) {
            // OTOrderDetail = this.CreateTOrderDetail(lg_ORDER_ID, lg_famille_id, lg_GROSSISTE_ID, int_NUMBER); a
            // decommenter en cas de probleme
            new logger().OCategory.info(" create de OTPreenregistrementDetail  " + OTOrderDetail.getLgORDERDETAILID()
                    + "    " + e.toString());
            return OTOrderDetail;
        }
    }

    public TGrossiste SearchGrossiste(String lg_GROSSISTE_ID) {

        TGrossiste OTGrossiste = null;

        try {
            OTGrossiste = (TGrossiste) this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TGrossiste t WHERE (t.lgGROSSISTEID = ?1 OR t.strLIBELLE = ?1) AND t.strSTATUT LIKE ?3")
                    .setParameter(1, lg_GROSSISTE_ID).setParameter(3, commonparameter.statut_enable).getSingleResult();

            new logger().OCategory.info("OTGrossiste  " + OTGrossiste.getLgGROSSISTEID());

        } catch (Exception E) {

        }
        return OTGrossiste;
    }

    // creation de commande
    public TOrder createOrder(String lgGROSSISTE_ID, String str_STATUT) {
        TOrder OTOrder = null;
        try {

            OTOrder = new TOrder();

            OTOrder.setLgORDERID(this.getKey().getComplexId());
            OTOrder.setLgUSERID(this.getOTUser());

            try {
                TGrossiste OTGrossiste = this.SearchGrossiste(lgGROSSISTE_ID);
                if (OTGrossiste != null) {
                    OTOrder.setLgGROSSISTEID(OTGrossiste);
                    OTOrder.setStrREFORDER(this.buildCommandeRef(new Date()));
                }
            } catch (Exception e) {
            }

            // OTOrder.setStrSTATUT(commonparameter.statut_is_Process);
            OTOrder.setStrSTATUT(str_STATUT);
            OTOrder.setDtCREATED(new Date());
            OTOrder.setDtUPDATED(new Date());
            this.persiste(OTOrder);
            this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));

        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec de création de la commande");
        }
        return OTOrder;

    }
    // fin creation de commande

    // mise a jour de commande
    public TOrder UpdateOrder(String lg_ORDER_ID, String lgGROSSISTE_ID) {
        TOrder OTOrder = null;
        try {

            OTOrder = this.FindOrder(lg_ORDER_ID);

            try {
                TGrossiste OTGrossiste = this.SearchGrossiste(lgGROSSISTE_ID);
                if (OTGrossiste != null) {
                    OTOrder.setLgGROSSISTEID(OTGrossiste);
                }
            } catch (Exception e) {
            }
            OTOrder.setDtUPDATED(new Date());
            this.persiste(OTOrder);
            this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));

        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec de mise a jour de la commande");
        }
        return OTOrder;

    }

    // fin mise a jour de commande
    public TSuggestionOrder createTSuggestionOrder(String lgGROSSISTE_ID, String str_STATUT) {
        TSuggestionOrder OTSuggestionOrder = null;
        TGrossiste OTGrossiste = null;
        try {
            OTSuggestionOrder = new TSuggestionOrder();
            OTSuggestionOrder.setLgSUGGESTIONORDERID(this.getKey().getComplexId());
            OTSuggestionOrder.setStrREF("REF_" + this.getKey().getShortId(7));
            OTGrossiste = new grossisteManagement(this.getOdataManager()).getGrossiste(lgGROSSISTE_ID);
            if (OTGrossiste == null) {
                this.buildErrorTraceMessage("Echec d'enregistrement de la suggestion. Grossiste inexistant");
                return null;
            }
            OTSuggestionOrder.setLgGROSSISTEID(OTGrossiste);
            OTSuggestionOrder.setStrSTATUT(str_STATUT);
            OTSuggestionOrder.setDtCREATED(new Date());
            OTSuggestionOrder.setDtUPDATED(new Date());
            this.persiste(OTSuggestionOrder);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return OTSuggestionOrder;
    }

    // public TOrderDetail createOrderDetail(String lg_ORDER_ID, String lg_FAMILLE_ID, String lg_GROSSISTE_ID, int
    // int_NUMBER) { ancienne bonne version. a decommenter en cas de probleme
    //
    // TOrderDetail oTOrderDetail = null;
    // try {
    // TFamille OTFamille = new familleManagement(this.getOdataManager()).getTFamille(lg_FAMILLE_ID);
    // try {
    // oTOrderDetail = this.findFamilleInTOrderDetail(lg_ORDER_ID, OTFamille.getLgFAMILLEID());
    // // TOrderDetail OTOrderDetail = this.findFamilleInTOrderDetail(lg_ORDER_ID, OTFamille.getLgFAMILLEID());
    //
    // oTOrderDetail.setIntNUMBER(oTOrderDetail.getIntNUMBER() + int_NUMBER);
    // oTOrderDetail.setIntQTEREPGROSSISTE(oTOrderDetail.getIntQTEREPGROSSISTE() + int_NUMBER);
    // oTOrderDetail.setIntQTEMANQUANT(oTOrderDetail.getIntQTEMANQUANT() + int_NUMBER);
    // oTOrderDetail.setDtUPDATED(new Date());
    //
    // } catch (Exception E) {
    // oTOrderDetail = new TOrderDetail();
    // oTOrderDetail.setLgORDERDETAILID(this.getKey().getComplexId());
    // oTOrderDetail.setLgORDERID(this.FindOrder(lg_ORDER_ID));
    // oTOrderDetail.setIntNUMBER(int_NUMBER);
    // oTOrderDetail.setIntQTEREPGROSSISTE(oTOrderDetail.getIntNUMBER());
    // oTOrderDetail.setIntQTEMANQUANT(int_NUMBER);
    // oTOrderDetail.setLgFAMILLEID(OTFamille);
    // try {
    // TGrossiste OTGrossiste = this.SearchGrossiste(lg_GROSSISTE_ID);
    // oTOrderDetail.setLgGROSSISTEID(OTGrossiste);
    // } catch (Exception e) {
    // }
    // oTOrderDetail.setStrSTATUT(commonparameter.statut_is_Process);
    // oTOrderDetail.setDtCREATED(new Date());
    // }
    // oTOrderDetail.setIntPRICE(oTOrderDetail.getIntNUMBER() * OTFamille.getIntPAF());
    // this.persiste(oTOrderDetail);
    // this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
    // } catch (Exception e) {
    // e.printStackTrace();
    // this.buildErrorTraceMessage("Echec d'ajout de l'article à la commande");
    // }
    //
    // return oTOrderDetail;
    //
    // }
    public TOrderDetail initTOrderDetail(String lg_ORDER_ID, int int_NUMBER, int int_PAF, int int_PRICE,
            TFamille OTFamille, String lg_GROSSISTE_ID) {
        TOrderDetail OTOrderDetail = null;
        TGrossiste OTGrossiste = null;
        try {
            OTGrossiste = this.SearchGrossiste(lg_GROSSISTE_ID);
            if (OTGrossiste == null) {
                this.buildErrorTraceMessage("Echec d'ajout du produit. Grossiste inexistant");
                return null;
            }
            OTOrderDetail = new TOrderDetail();
            OTOrderDetail.setLgORDERDETAILID(this.getKey().getComplexId());
            OTOrderDetail.setLgORDERID(this.FindOrder(lg_ORDER_ID));
            OTOrderDetail.setIntNUMBER(int_NUMBER);
            OTOrderDetail.setIntQTEREPGROSSISTE(OTOrderDetail.getIntNUMBER());
            OTOrderDetail.setIntQTEMANQUANT(int_NUMBER);
            OTOrderDetail.setIntPAFDETAIL(int_PAF);
            OTOrderDetail.setIntPRICEDETAIL(int_PRICE);
            OTOrderDetail.setLgFAMILLEID(OTFamille);
            OTOrderDetail.setLgGROSSISTEID(OTGrossiste);
            OTOrderDetail.setStrSTATUT(commonparameter.statut_is_Process);
            OTOrderDetail.setDtCREATED(new Date());
            OTOrderDetail.setDtUPDATED(new Date());
            this.getOdataManager().getEm().persist(OTOrderDetail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return OTOrderDetail;
    }

    public TOrderDetail createOrderDetail(String lg_ORDER_ID, String lg_FAMILLE_ID, String lg_GROSSISTE_ID,
            int int_NUMBER, int int_PRICE, int int_PAF) {
        TOrderDetail oTOrderDetail = null;
        TFamille OTFamille = null;
        TFamilleGrossiste OTFamilleGrossiste = null;
        familleManagement OfamilleManagement = new familleManagement(this.getOdataManager());
        try {
            OTFamille = OfamilleManagement.getTFamille(lg_FAMILLE_ID);
            if (OTFamille == null) {
                this.buildErrorTraceMessage("Echec d'ajout de produit. Référence de produit inexistante");
                return null;
            }
            oTOrderDetail = this.findFamilleInTOrderDetail(lg_ORDER_ID, OTFamille.getLgFAMILLEID());
            this.getOdataManager().BeginTransaction();
            if (oTOrderDetail == null) {
                OTFamilleGrossiste = new familleGrossisteManagement(this.getOdataManager())
                        .findFamilleGrossiste(OTFamille.getLgFAMILLEID(), lg_GROSSISTE_ID);

                // oTOrderDetail = this.initTOrderDetail(lg_ORDER_ID, 0, (OTFamilleGrossiste != null &&
                // OTFamilleGrossiste.getIntPAF() != null ? OTFamilleGrossiste.getIntPAF() : OTFamille.getIntPAF()),
                // (OTFamilleGrossiste != null && OTFamilleGrossiste.getIntPRICE() != null ?
                // OTFamilleGrossiste.getIntPRICE() : OTFamille.getIntPRICE()), OTFamille, lg_GROSSISTE_ID);// commenter
                // le 30/03/2017
                oTOrderDetail = this.initTOrderDetail(lg_ORDER_ID, 0,
                        ((OTFamilleGrossiste != null && OTFamilleGrossiste.getIntPAF() != null
                                && OTFamilleGrossiste.getIntPAF() > 0) ? OTFamilleGrossiste.getIntPAF()
                                        : OTFamille.getIntPAF()),
                        ((OTFamilleGrossiste != null && OTFamilleGrossiste.getIntPRICE() != null
                                && OTFamilleGrossiste.getIntPRICE() > 0) ? OTFamilleGrossiste.getIntPRICE()
                                        : OTFamille.getIntPRICE()),
                        OTFamille, lg_GROSSISTE_ID);// modifie le 30/03/2017 kobena

            }
            oTOrderDetail.setIntNUMBER(oTOrderDetail.getIntNUMBER() + int_NUMBER);
            oTOrderDetail.setIntQTEREPGROSSISTE(oTOrderDetail.getIntQTEREPGROSSISTE() + int_NUMBER);
            oTOrderDetail.setIntQTEMANQUANT(oTOrderDetail.getIntQTEMANQUANT() + int_NUMBER);
            oTOrderDetail.setDtUPDATED(new Date());
            oTOrderDetail.setIntPRICE(oTOrderDetail.getIntNUMBER() * oTOrderDetail.getIntPAFDETAIL());
            oTOrderDetail.setIntORERSTATUS((short) 2);
            OTFamille.setBCODEINDICATEUR((short) 1);

            this.getOdataManager().getEm().merge(OTFamille);
            // this.persiste(oTOrderDetail); // a ddecommenter en cas de probleme
            this.getOdataManager().getEm().merge(oTOrderDetail);
            this.getOdataManager().CloseTransaction();
            this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec d'ajout de l'article à la commande");
        }

        return oTOrderDetail;

    }

    // a mettre dans une transaction et faire le commit a la fin
    public TOrderDetail createOrderDetail(TOrder OTOrder, TFamille OTFamille, TGrossiste OTGrossiste, int int_PRICE,
            int int_PAF) {
        TOrderDetail OTOrderDetail = null;
        Date ODate = new Date();
        try {
            OTOrderDetail = new TOrderDetail();
            OTOrderDetail.setLgORDERDETAILID(this.getKey().getComplexId());
            OTOrderDetail.setLgORDERID(OTOrder);
            OTOrderDetail.setIntNUMBER(0);
            OTOrderDetail.setIntQTEREPGROSSISTE(0);
            OTOrderDetail.setIntQTEMANQUANT(0);
            // OTOrderDetail.setIntPRICE(int_PAF); // a decommenter en cas de probleme 13/12/2016
            OTOrderDetail.setIntPRICE(0);
            OTOrderDetail.setIntPAFDETAIL(int_PAF);
            OTOrderDetail.setIntPRICEDETAIL(int_PRICE);
            OTOrderDetail.setLgFAMILLEID(OTFamille);
            OTOrderDetail.setLgGROSSISTEID(OTGrossiste);
            OTOrderDetail.setStrSTATUT(commonparameter.statut_is_Process);
            OTOrderDetail.setDtCREATED(ODate);
            OTOrderDetail.setDtUPDATED(ODate);
            OTOrderDetail.setIntORERSTATUS((short) 2);
            OTFamille.setBCODEINDICATEUR((short) 1);
            OTFamille.setIntORERSTATUS((short) 2);
            this.getOdataManager().getEm().merge(OTFamille);
            this.getOdataManager().getEm().persist(OTOrderDetail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return OTOrderDetail;
    }

    public TOrderDetail createOrderDetail(TOrder OTOrder, TFamille OTFamille, TGrossiste OTGrossiste, int int_NUMBER,
            int int_PRICE, int int_PAF) {
        TOrderDetail OTOrderDetail = null;
        Date ODate = new Date();
        try {
            OTOrderDetail = this.findFamilleInTOrderDetail(OTOrder.getLgORDERID(), OTFamille.getLgFAMILLEID());
            if (OTOrderDetail == null) {
                OTOrderDetail = this.createOrderDetail(OTOrder, OTFamille, OTGrossiste, int_PRICE, int_PAF);
            }
            OTOrderDetail.setIntNUMBER(OTOrderDetail.getIntNUMBER() + int_NUMBER);
            OTOrderDetail.setIntPRICE(int_PAF * OTOrderDetail.getIntNUMBER());
            OTOrderDetail.setIntQTEREPGROSSISTE(OTOrderDetail.getIntNUMBER());
            OTOrderDetail.setIntQTEMANQUANT(OTOrderDetail.getIntNUMBER());
            OTOrderDetail.setDtUPDATED(ODate);
            this.getOdataManager().getEm().merge(OTOrderDetail);
        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec d'ajout du produit à la commande");
        }
        return OTOrderDetail;
    }

    public TOrderDetail UpdateOrderDetail(String lg_ORDERDETAIL_ID, String lg_ORDER_ID, String lg_FAMILLE_GROSSISTE_ID,
            int int_NUMBER) {

        TOrderDetail oTOrderDetail = null;

        try {

            oTOrderDetail = this.getOdataManager().getEm().find(TOrderDetail.class, lg_ORDERDETAIL_ID);

            oTOrderDetail.setLgORDERID(this.FindOrder(lg_ORDER_ID));
            oTOrderDetail.setIntNUMBER(int_NUMBER);
            oTOrderDetail.setIntQTEMANQUANT(int_NUMBER);

            dal.TFamilleGrossiste OTFamilleGrossiste = this.getOdataManager().getEm().find(TFamilleGrossiste.class,
                    lg_FAMILLE_GROSSISTE_ID);
            if (OTFamilleGrossiste != null) {
                // oTOrderDetail.setLgFAMILLEGROSSISTEID(OTFamilleGrossiste);
            }

            oTOrderDetail.setStrSTATUT(commonparameter.statut_is_Process);
            oTOrderDetail.setDtUPDATED(new Date());

            this.persiste(oTOrderDetail);
            this.refresh(oTOrderDetail);

            new logger().OCategory.info(" update de oTOrderDetail  " + oTOrderDetail.getLgORDERDETAILID());

        } catch (Exception E) {

            // oTOrderDetail = this.createOrderDetail(lg_ORDER_ID, lg_FAMILLE_GROSSISTE_ID, int_NUMBER);
            new logger().OCategory.info(" CREATE de oTOrderDetail  " + oTOrderDetail.getLgORDERDETAILID());
        }
        return oTOrderDetail;
    }

    public TOrderDetail findFamilleInTOrderDetail(String lg_ORDER_ID, String lg_FAMILLE_ID) {
        TOrderDetail OTOrderDetail = null;
        try {
            new logger().OCategory.info("lg_ORDER_ID " + lg_ORDER_ID + " lg_FAMILLE_ID " + lg_FAMILLE_ID);
            Query qry = this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TOrderDetail t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgORDERID.lgORDERID LIKE ?2 AND (t.strSTATUT LIKE ?3 OR t.strSTATUT LIKE ?4) ")
                    .setParameter(1, lg_FAMILLE_ID).setParameter(2, lg_ORDER_ID)
                    .setParameter(3, commonparameter.orderIsPassed).setParameter(4, commonparameter.statut_is_Process);
            if (qry.getResultList().size() > 0) {
                OTOrderDetail = (TOrderDetail) qry.getSingleResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // this.buildErrorTraceMessage(e.getMessage());
        }
        return OTOrderDetail;
    }

    public TOrder FindOrder(String lg_ORDER_ID) {
        // TOrder OTOrder = (TOrder) this.find(lg_ORDER_ID, new TOrder());
        return this.getOrderByRef(lg_ORDER_ID);
    }

    public TOrderDetail FindOrderDetail(String lg_ORDERDETAIL_ID) {

        TOrderDetail OTOrderDetail = null;

        try {

            OTOrderDetail = this.getOdataManager().getEm().find(TOrderDetail.class, lg_ORDERDETAIL_ID);
            new logger().OCategory.info("Succes OTOrderDetail trouve   " + OTOrderDetail.getLgORDERDETAILID());

            return OTOrderDetail;

        } catch (NoResultException e) {

            new logger().OCategory.info("Error Detail inexistant   " + e.toString());

            return null;

        }

    }

    public TOrderDetail FindTOrderDetail(String lg_ORDERDETAIL_ID) {
        new logger().OCategory.info("recherche de TOrderDetail");
        TOrderDetail OTOrderDetail = null;
        try {
            OTOrderDetail = (TOrderDetail) this.find(lg_ORDERDETAIL_ID, new TOrderDetail());
        } catch (Exception e) {
            new logger().OCategory.info(e.toString());
        }
        return OTOrderDetail;
    }

    public TOrderDetail DeleteOrderDetail(String lg_ORDERDETAIL_ID) {

        TOrderDetail OTOrderDetail = null;

        try {
            OTOrderDetail = this.FindTOrderDetail(lg_ORDERDETAIL_ID);
            OTOrderDetail.setStrSTATUT(commonparameter.statut_delete);
            this.persiste(OTOrderDetail);

            return OTOrderDetail;

        } catch (Exception E) {
            return OTOrderDetail;
        }

    }

    public TOrder DeleteTOrder(String lg_ORDER_ID) {
        TOrder OTOrder = this.FindOrder(lg_ORDER_ID);
        List<TOrderDetail> lstT = this.getTOrderDetail("", lg_ORDER_ID, OTOrder.getStrSTATUT());
        for (int i = 0; i < lstT.size(); i++) {

            TOrderDetail OTOrderDetail = lstT.get(i);
            OTOrderDetail.setStrSTATUT(commonparameter.statut_delete);

            this.persiste(OTOrderDetail);
            this.refresh(OTOrderDetail);

        }
        OTOrder.setStrSTATUT(commonparameter.statut_delete);
        this.persiste(OTOrder);
        this.refresh(OTOrder);
        return OTOrder;
    }

    public List<TOrderDetail> getTOrderDetail(String search_value, String lg_ORDER_ID, String str_STATUT) {
        List<TOrderDetail> lstT = new ArrayList<>();
        try {
            if (search_value.equalsIgnoreCase("")) {
                search_value = "%%";
            }
            lstT = this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TOrderDetail t WHERE t.strSTATUT LIKE ?1 AND (t.lgORDERID.lgORDERID LIKE ?2 OR t.lgORDERID.strREFORDER LIKE ?2)  AND (t.lgFAMILLEID.intCIP LIKE ?4 OR t.lgFAMILLEID.intEAN13 LIKE ?4 OR t.lgFAMILLEID.strDESCRIPTION LIKE ?4) ORDER BY t.dtUPDATED DESC, t.lgFAMILLEID.strDESCRIPTION ASC")
                    .
                    // setParameter(1, commonparameter.statut_is_Process).
                    setParameter(1, str_STATUT).setParameter(2, lg_ORDER_ID).setParameter(4, search_value + "%")
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        new logger().OCategory.info("lstT taille " + lstT.size());
        return lstT;
    }

    public List<TOrderDetail> getTOrderDetail(String lg_ORDER_ID, String str_STATUT) {
        List<TOrderDetail> lstT = new ArrayList<>();
        new logger().OCategory.info("lg_ORDER_ID " + lg_ORDER_ID + " str_STATUT " + str_STATUT);
        try {
            lstT = this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TOrderDetail t WHERE t.strSTATUT LIKE ?1 AND (t.lgORDERID.lgORDERID LIKE ?2 OR t.lgORDERID.strREFORDER LIKE ?2) ")
                    .setParameter(1, str_STATUT).setParameter(2, lg_ORDER_ID).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lstT;
    }

    public List<TOrderDetail> getTOrderDetailNotDelete(String lg_ORDER_ID) {
        List<TOrderDetail> lstT = new ArrayList<TOrderDetail>();
        new logger().OCategory.info("lg_ORDER_ID " + lg_ORDER_ID);
        try {
            lstT = this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TOrderDetail t WHERE t.strSTATUT NOT LIKE ?1 AND t.lgORDERID.lgORDERID LIKE ?2 ")
                    .setParameter(1, commonparameter.statut_delete).setParameter(2, lg_ORDER_ID).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lstT;
    }

    public int Order_PrixDeVente(String lg_ORDER_ID) {

        int PrixDeVenteTotal = 0;

        try {
            TOrder OTOrder = this.FindOrder(lg_ORDER_ID);
            List<TOrderDetail> lstOrderDetail = this.getTOrderDetail("", lg_ORDER_ID, OTOrder.getStrSTATUT());

            for (TOrderDetail olstOrderDetail : lstOrderDetail) {

                PrixDeVenteTotal = olstOrderDetail.getLgFAMILLEID().getIntPRICE() + PrixDeVenteTotal;

            }

            new logger().OCategory.info("PrixDeVenteTotal   " + PrixDeVenteTotal);
            return PrixDeVenteTotal;

        } catch (Exception E) {

            new logger().OCategory.info("PrixDeVenteTotal   " + PrixDeVenteTotal + " ERREUR" + E);
            return PrixDeVenteTotal;
        }

    }

    public int Order_PrixDAchat(String lg_ORDER_ID) {

        int PrixDAchatTotal = 0;

        try {
            TOrder OTOrder = this.FindOrder(lg_ORDER_ID);
            List<TOrderDetail> lstOrderDetail = this.getTOrderDetail("", lg_ORDER_ID, OTOrder.getStrSTATUT());

            for (TOrderDetail olstOrderDetail : lstOrderDetail) {

                PrixDAchatTotal = olstOrderDetail.getLgFAMILLEID().getIntPAT() + PrixDAchatTotal;

            }

            new logger().OCategory.info("PrixDAchatTotal   " + PrixDAchatTotal);
            return PrixDAchatTotal;

        } catch (Exception E) {

            new logger().OCategory.info("PrixDAchatTotal   " + PrixDAchatTotal + " ERREUR" + E);
            return PrixDAchatTotal;
        }

    }

    // gestion du bon de livraison
    // mise a jour des quantités en fonction des produits livrés
    public TBonLivraisonDetail UpdateTBonLivraisonDetailFromBonLivraison(String lg_BON_LIVRAISON_DETAIL,
            int int_QTE_LIVRE, int int_QUANTITE_FREE) {
        TBonLivraisonDetail OTBonLivraisonDetail = null;
        try {

            OTBonLivraisonDetail = this.getOdataManager().getEm().find(TBonLivraisonDetail.class,
                    lg_BON_LIVRAISON_DETAIL);
            OTBonLivraisonDetail.setIntQTERECUE(OTBonLivraisonDetail.getIntQTERECUE() + int_QTE_LIVRE);
            OTBonLivraisonDetail
                    .setIntQTEMANQUANT(OTBonLivraisonDetail.getIntQTEMANQUANT() - (int_QTE_LIVRE - int_QUANTITE_FREE));
            OTBonLivraisonDetail.setIntQTEUG(OTBonLivraisonDetail.getIntQTEUG() + int_QUANTITE_FREE);
            OTBonLivraisonDetail.setDtUPDATED(new Date());
            this.getOdataManager().getEm().merge(OTBonLivraisonDetail);
            return OTBonLivraisonDetail;
        } catch (Exception e) {
            e.printStackTrace();
            return OTBonLivraisonDetail;
        }
    }

    // mise a jour des quantités en fonction des produits livrés
    public boolean ClosureOrder(String lg_ORDER_ID) {

        boolean result = false;
        List<TOrderDetail> lstTOrderDetail = new ArrayList<TOrderDetail>();
        int i = 0;
        try {

            TOrder OTOrder = this.FindOrder(lg_ORDER_ID);
            lstTOrderDetail = this.getTOrderDetail("", OTOrder.getLgORDERID(), OTOrder.getStrSTATUT());
            new logger().OCategory.info("lstTOrderDetail size " + lstTOrderDetail.size());

            for (TOrderDetail OTOrderDetail : lstTOrderDetail) {
                this.refresh(OTOrderDetail);
                new logger().OCategory.info("Statut " + OTOrderDetail.getStrSTATUT());
                if (OTOrderDetail.getStrSTATUT().equals(commonparameter.statut_is_Closed)) {
                    i++;
                }
            }
            new logger().OCategory.info("valeur final i " + i);
            if (i == lstTOrderDetail.size()) {
                OTOrder.setStrSTATUT(commonparameter.statut_is_Closed);
                OTOrder.setDtUPDATED(new Date());
                this.persiste(OTOrder);
            }

            this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec de clôture de la commande");
            new logger().OCategory.info("Echec de clôture de la commande");
        }
        return result;
    }

    public boolean ClosureOrderForce(String lg_ORDER_ID) {

        boolean result = false;
        List<TOrderDetail> lstTOrderDetail = new ArrayList<TOrderDetail>();
        int i = 0;
        try {

            TOrder OTOrder = this.FindOrder(lg_ORDER_ID);
            lstTOrderDetail = this.getTOrderDetail("", OTOrder.getLgORDERID(), OTOrder.getStrSTATUT());
            new logger().OCategory.info("lstTOrderDetail size " + lstTOrderDetail.size());

            for (TOrderDetail OTOrderDetail : lstTOrderDetail) {
                this.refresh(OTOrderDetail);
                new logger().OCategory.info("Statut " + OTOrderDetail.getStrSTATUT());
                OTOrderDetail.setStrSTATUT(commonparameter.statut_is_Closed);
                OTOrderDetail.setDtUPDATED(new Date());
                this.persiste(OTOrderDetail);
            }
            this.ClosureOrder(OTOrder.getLgORDERID());
            // this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec de clôture de la commande");
            new logger().OCategory.info("Echec de clôture de la commande");
        }
        return result;
    }

    public TOrderDetail FindOrderDetail(String lg_ORDER_ID, String lg_FAMILLE_ID) {

        TOrderDetail OTOrderDetail = null;

        try {
            OTOrderDetail = (TOrderDetail) this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TOrderDetail t WHERE t.lgORDERID.lgORDERID = ?1 AND t.lgFAMILLEID.lgFAMILLEID = ?2 AND (t.strSTATUT LIKE ?3 OR t.strSTATUT LIKE ?4)")
                    .setParameter(1, lg_ORDER_ID).setParameter(2, lg_FAMILLE_ID)
                    .setParameter(3, commonparameter.orderIsPassed).setParameter(4, commonparameter.orderIsPartial)
                    .getSingleResult();
            new logger().OCategory.info("Succes OTOrderDetail trouve   " + OTOrderDetail.getLgORDERDETAILID());
        } catch (NoResultException e) {

            new logger().OCategory.info("Error Detail inexistant   " + e.toString());
        }
        return OTOrderDetail;
    }

    // liste des commandes d'un produit sur une periode
    public List<TOrderDetail> listeCommandeByProductAndPeriod(String search_value, Date dtDEBUT, Date dtFin,
            String lg_FAMILLE_ID, String lg_ORDER_ID, String lg_GROSSISTE_ID) {

        List<TOrderDetail> lstTOrderDetail = new ArrayList<TOrderDetail>();

        try {
            if (search_value.equalsIgnoreCase("") || search_value == null) {
                search_value = "%%";
            }
            new logger().OCategory.info("dtDEBUT   " + dtDEBUT + " dtFin " + dtFin);
            try {
                lstTOrderDetail = this.getOdataManager().getEm()
                        .createQuery("SELECT t FROM TOrderDetail t WHERE (t.dtCREATED BETWEEN ?3 AND ?4) "
                                + "AND t.lgFAMILLEID.lgFAMILLEID LIKE ?6 AND (t.lgFAMILLEID.strDESCRIPTION LIKE ?1 OR t.lgFAMILLEID.intCIP LIKE ?1 OR t.lgFAMILLEID.intEAN13 LIKE ?1 "
                                + "OR t.lgFAMILLEID.strNAME LIKE ?1 OR t.lgORDERID.strREFORDER LIKE ?1 OR t.lgGROSSISTEID.strLIBELLE LIKE ?1) "
                                + "AND t.lgORDERID.lgORDERID LIKE ?2 AND t.lgGROSSISTEID.lgGROSSISTEID LIKE ?5 AND (t.lgORDERID.strSTATUT = ?7 OR t.lgORDERID.strSTATUT = ?8) "
                                + "ORDER BY t.lgORDERID.dtCREATED ASC")
                        .setParameter(1, search_value + "%").setParameter(2, lg_ORDER_ID).setParameter(3, dtDEBUT)
                        .setParameter(4, dtFin).setParameter(5, lg_GROSSISTE_ID).setParameter(6, lg_FAMILLE_ID)
                        .setParameter(7, commonparameter.orderIsPassed)
                        .setParameter(8, commonparameter.statut_is_Closed).getResultList();
            } catch (Exception e) {
                e.printStackTrace();

            }
        } catch (Exception e) {
            e.printStackTrace();
            this.setMessage(commonparameter.PROCESS_FAILED);
        }
        new logger().OCategory.info("lstTOrderDetail taille " + lstTOrderDetail.size());
        return lstTOrderDetail;
    }

    public List<TOrderDetail> listeCommandeByProductAndPeriod(String search_value, Date dtDEBUT, Date dtFin,
            String lg_FAMILLE_ID, String lg_ORDER_ID, String lg_GROSSISTE_ID, String lg_FABRIQUANT_ID,
            String lg_FAMILLEARTICLE_ID, String lg_ZONE_GEO_ID) {

        List<TOrderDetail> lstTOrderDetail = new ArrayList<TOrderDetail>();

        try {
            if (search_value.equalsIgnoreCase("") || search_value == null) {
                search_value = "%%";
            }
            new logger().OCategory.info("dtDEBUT   " + dtDEBUT + " dtFin " + dtFin);
            try {
                lstTOrderDetail = this.getOdataManager().getEm().createQuery(
                        "SELECT t FROM TOrderDetail t WHERE (t.lgORDERID.dtCREATED >= ?3 AND t.lgORDERID.dtCREATED <=?4) AND t.lgFAMILLEID.lgFAMILLEID LIKE ?6 AND (t.lgFAMILLEID.strDESCRIPTION LIKE ?1 OR t.lgFAMILLEID.intCIP LIKE ?1 OR t.lgFAMILLEID.strNAME LIKE ?1 OR t.lgORDERID.strREFORDER LIKE ?1 OR t.lgGROSSISTEID.strLIBELLE LIKE ?1 OR t.lgFAMILLEID.intEAN13 LIKE ?1) AND t.lgORDERID.lgORDERID LIKE ?2 AND t.lgGROSSISTEID.lgGROSSISTEID LIKE ?5 AND (t.lgORDERID.strSTATUT = ?7 OR t.lgORDERID.strSTATUT = ?8) AND t.lgFAMILLEID.lgFABRIQUANTID.lgFABRIQUANTID LIKE ?9 AND t.lgFAMILLEID.lgFAMILLEARTICLEID.lgFAMILLEARTICLEID LIKE ?10 AND t.lgFAMILLEID.lgZONEGEOID.lgZONEGEOID LIKE ?11 ORDER BY t.lgORDERID.dtCREATED ASC")
                        .setParameter(1, search_value + "%").setParameter(2, lg_ORDER_ID).setParameter(3, dtDEBUT)
                        .setParameter(4, dtFin).setParameter(5, lg_GROSSISTE_ID).setParameter(6, lg_FAMILLE_ID)
                        .setParameter(7, commonparameter.orderIsPassed)
                        .setParameter(8, commonparameter.statut_is_Closed).setParameter(9, lg_FABRIQUANT_ID)
                        .setParameter(10, lg_FAMILLEARTICLE_ID).setParameter(11, lg_ZONE_GEO_ID).getResultList();
            } catch (Exception e) {
                e.printStackTrace();

            }
        } catch (Exception e) {
            e.printStackTrace();
            this.setMessage(commonparameter.PROCESS_FAILED);
        }
        new logger().OCategory.info("lstTOrderDetail taille " + lstTOrderDetail.size());
        return lstTOrderDetail;
    }
    // fin liste des commandes d'un produit sur une periode

    // quantité d'entree ou sortie d'un article
    public int getQauntityCmdeByArticle(String search_value, Date dtDEBUT, Date dtFin, String lg_FAMILLE_ID,
            String lg_ORDER_ID, String lg_GROSSISTE_ID) {
        int result = 0;
        List<TOrderDetail> lstTOrderDetail = new ArrayList<TOrderDetail>();

        try {
            if (search_value.equalsIgnoreCase("") || search_value == null) {
                search_value = "%%";
            }
            lstTOrderDetail = this.listeCommandeByProductAndPeriod(search_value, dtDEBUT, dtFin, lg_FAMILLE_ID,
                    lg_ORDER_ID, lg_GROSSISTE_ID);
            for (TOrderDetail OTOrderDetail : lstTOrderDetail) {
                result += OTOrderDetail.getIntNUMBER();
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.setMessage(commonparameter.PROCESS_FAILED);
        }
        new logger().OCategory.info("result:" + result);
        return result;
    }

    // fin quantité d'entree ou sortie d'un article
    // generation du numero de commande
    public String buildCommandeRef(Date ODate) throws JSONException {
        TParameters OTParameters = this.getOdataManager().getEm().find(TParameters.class,
                "KEY_LAST_ORDER_COMMAND_NUMBER");
        TParameters OTParameters_KEY_SIZE_ORDER_NUMBER = this.getOdataManager().getEm().find(TParameters.class,
                "KEY_SIZE_ORDER_NUMBER");
        this.refresh(OTParameters);

        String jsondata = OTParameters.getStrVALUE();
        int int_last_code = 0;
        int_last_code = int_last_code + 1;

        new logger().OCategory.info("jsondata =  " + jsondata);
        try {
            JSONArray jsonArray = new JSONArray(jsondata);
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            int_last_code = new Integer(jsonObject.getString("int_last_code"));
            Date dt_last_date = date.stringToDate(jsonObject.getString("str_last_date"), date.formatterMysqlShort2);

            String str_lasd = this.getKey().DateToString(dt_last_date, this.getKey().formatterMysqlShort2);
            String str_actd = this.getKey().DateToString(ODate, this.getKey().formatterMysqlShort2);

            if (!str_lasd.equals(str_actd)) {
                int_last_code = 0;
            }

            new logger().OCategory.info(int_last_code + "  " + dt_last_date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // KEY_SIZE_ORDER_NUMBER
        Calendar now = Calendar.getInstance();
        int hh = now.get(Calendar.HOUR_OF_DAY);
        int mois = now.get(Calendar.MONTH) + 1;
        int jour = now.get(Calendar.DAY_OF_MONTH);
        String mois_tostring = "";

        int intsize = ((int_last_code + 1) + "").length();
        int intsize_tobuild = new Integer(OTParameters_KEY_SIZE_ORDER_NUMBER.getStrVALUE());
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
        String str_code = jour + "" + mois_tostring + "" + this.getKey().getYear(ODate) + "_" + str_last_code;
        JSONObject json = new JSONObject();
        JSONArray arrayObj = new JSONArray();
        json.put("int_last_code", str_last_code);
        json.put("str_last_date", this.getKey().DateToString(ODate, this.getKey().formatterMysqlShort2));
        arrayObj.put(json);
        String jsonData = arrayObj.toString();

        OTParameters.setStrVALUE(jsonData);
        // this.persiste(OTParameters);
        new logger().OCategory.info(jsonData);
        new logger().OCategory.info(str_code);
        return str_code;
    }
    // fin generation du numero de commande

    // liste des artcles d'une commande
    public List<TOrderDetail> listeOrderDetail(String lg_ORDER_ID) {
        List<TOrderDetail> lstTOrderDetail = new ArrayList<TOrderDetail>();
        try {
            lstTOrderDetail = this.getOdataManager().getEm()
                    .createQuery("SELECT t FROM TOrderDetail t WHERE t.lgORDERID.lgORDERID LIKE ?1")
                    .setParameter(1, lg_ORDER_ID).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new logger().OCategory.info("lstTOrderDetail taille " + lstTOrderDetail.size());

        return lstTOrderDetail;
    }
    // fin liste des artcles d'une commande

    // liste des commandes
    public List<TOrder> listeOrder(String search_value, String str_STATUT) {
        List<TOrder> lstTOrder = new ArrayList<>();
        try {
            if (search_value.equalsIgnoreCase("") || search_value == null) {
                search_value = "%%";
            }
            lstTOrder = this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TOrder t, TOrderDetail to WHERE t.lgORDERID = to.lgORDERID.lgORDERID AND (t.strREFORDER LIKE ?1 OR t.lgGROSSISTEID.strLIBELLE LIKE ?1 OR to.lgFAMILLEID.intCIP LIKE ?1 OR to.lgFAMILLEID.intEAN13 LIKE ?1 OR to.lgFAMILLEID.strDESCRIPTION LIKE ?1) AND (t.strSTATUT LIKE ?3 OR t.strSTATUT LIKE ?4 ) GROUP BY to.lgORDERID.lgORDERID ORDER BY t.dtUPDATED DESC ")
                    .setParameter(1, search_value + "%").setParameter(3, str_STATUT)
                    .setParameter(4, DateConverter.STATUT_PHARMA).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new logger().OCategory.info("lstTOrder taille " + lstTOrder.size());

        return lstTOrder;
    }

    public List<TOrder> listeOrder(String search_value) {
        List<TOrder> lstTOrder = new ArrayList<>();
        try {
            if (search_value.equalsIgnoreCase("") || search_value == null) {
                search_value = "%%";
            }
            lstTOrder = this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TOrder t, TOrderDetail o WHERE o.lgORDERID.lgORDERID = t.lgORDERID AND (t.strREFORDER LIKE ?1 OR t.lgGROSSISTEID.strLIBELLE LIKE ?1 OR o.lgFAMILLEID.intCIP LIKE ?1 OR o.lgFAMILLEID.intEAN13 LIKE ?1) AND (t.strSTATUT LIKE ?2 OR t.strSTATUT LIKE ?3) GROUP BY t.lgORDERID ORDER BY t.dtCREATED DESC ")
                    .setParameter(1, search_value + "%").setParameter(2, commonparameter.statut_is_Waiting)
                    .setParameter(3, commonparameter.orderIsPassed).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new logger().OCategory.info("lstTOrder taille " + lstTOrder.size());

        return lstTOrder;
    }

    public List<TOrder> listeOrder(String search_value, Date dtDEBUT, Date dtFin) {
        List<TOrder> lstTOrder = new ArrayList<TOrder>();
        try {
            if (search_value.equalsIgnoreCase("") || search_value == null) {
                search_value = "%%";
            }
            lstTOrder = this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TOrder t WHERE (t.strREFORDER LIKE ?1 OR t.lgGROSSISTEID.strLIBELLE LIKE ?1) AND (t.strSTATUT LIKE ?2 OR t.strSTATUT LIKE ?3) AND (t.dtCREATED >= ?4 AND t.dtCREATED <= ?5) ORDER BY t.dtCREATED DESC ")
                    .setParameter(1, search_value + "%").setParameter(2, commonparameter.statut_is_Waiting)
                    .setParameter(3, commonparameter.orderIsPassed).setParameter(4, dtDEBUT).setParameter(5, dtFin)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new logger().OCategory.info("lstTOrder taille " + lstTOrder.size());

        return lstTOrder;
    }
    // fin liste des artcles d'une commande

    public boolean ImportOrder(String str_FILE, String lg_ORDER_ID, String mode, String lg_GROSSISTE_ID, String format,
            String extension) {
        boolean result = false;
        List<String> lstString = new ArrayList<>();
        TOrder OTOrder = null;
        TFamille OTFamille = null;
        familleManagement OfamilleManagement = new familleManagement(this.getOdataManager());
        int totalLineImport = 0;
        try {
            List listexcel;
            XlsFiles_with_POI OXlsFiles_with_POI = new XlsFiles_with_POI(str_FILE);
            CsvFiles_with_Opencvs O = new CsvFiles_with_Opencvs();
            if (extension.equalsIgnoreCase(".xls")) {
                listexcel = OXlsFiles_with_POI.LoadDataToFiles_with_POI();
                lstString = OXlsFiles_with_POI.getAndInsertDataForFileExtract_with_POI(listexcel);
                System.out.println("lstString  +++++++++++++" + lstString + " listexcel  @@@@@@@@@@@  " + listexcel);
            } else if (extension.equalsIgnoreCase(".xlsx")) {
                listexcel = OXlsFiles_with_POI.LoadDataToFiles2_with_POI();
                lstString = OXlsFiles_with_POI.getAndInsertDataForFileExtract_with_POI(listexcel);
            } else if (extension.equalsIgnoreCase(".csv")) {
                lstString = O.LoadDataWithPointVirgule(str_FILE, ';');
            }
            if (mode.equalsIgnoreCase("mode_insert")) {
                System.out.println("lg_GROSSISTE_ID ************************ " + lg_GROSSISTE_ID);
                OTOrder = this.createOrder(lg_GROSSISTE_ID, commonparameter.statut_is_Process);
                System.out.println("OTOrder  ************************ " + OTOrder);
                if (format.equalsIgnoreCase(Parameter.format_commande_1)) {
                    for (int i = 0; i < lstString.size(); i++) { // lstData: liste des lignes du fichier xls ou csv
                        new logger().OCategory.info("i:" + i + " ///ligne--------" + lstString.get(i)); // ligne courant
                        String[] tabString = lstString.get(i).split(";"); // on case la ligne courante pour recuperer
                                                                          // les differentes colonnes
                        OTFamille = OfamilleManagement.getTFamille(tabString[0]);
                        if (OTFamille != null) {
                            totalLineImport = totalLineImport + this.importCreateOrderFormat1(
                                    OTFamille.getLgFAMILLEID(), OTOrder.getLgORDERID(), lg_GROSSISTE_ID, tabString);
                        }
                    }
                } else if (format.equalsIgnoreCase(Parameter.format_commande_2)) {
                    for (int i = 0; i < lstString.size(); i++) { // lstData: liste des lignes du fichier xls ou csv
                        new logger().OCategory.info("i:" + i + " ///ligne--------" + lstString.get(i)); // ligne courant
                        String[] tabString = lstString.get(i).split(";"); // on case la ligne courante pour recuperer
                                                                          // les differentes colonnes
                        OTFamille = OfamilleManagement.getTFamille(tabString[2]);
                        System.out.println("tabString " + tabString[0] + " " + tabString[1] + " " + tabString[2]);
                        // new logger().OCategory.info("Produit trouvé:"+OTFamille.getStrDESCRIPTION());
                        if (OTFamille != null) {
                            totalLineImport = totalLineImport + this.importCreateOrderFormat2(
                                    OTFamille.getLgFAMILLEID(), OTOrder.getLgORDERID(), lg_GROSSISTE_ID, tabString);
                        }
                    }
                }
            } else if (mode.equalsIgnoreCase("mode_update")) {
                if (format.equalsIgnoreCase(Parameter.format_commande_1)) {
                    totalLineImport = this.importUpdateOrderFormat1(OTOrder, lstString);
                } else if (format.equalsIgnoreCase(Parameter.format_commande_2)) {
                    totalLineImport = this.importUpdateOrderFormat2(OTOrder, lstString);
                }
            }
            if (totalLineImport > 0) {
                result = true;
                if (totalLineImport == lstString.size()) {
                    this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
                } else {
                    this.buildSuccesTraceMessage(totalLineImport + "/" + lstString.size() + " ont été pris en compte");
                }
            } else {
                this.delete(OTOrder);
                this.buildSuccesTraceMessage("Aucun produit n'est trouvé ou disponible dans cette commande");
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec d'importation des données");
        }
        return result;
    }

    public int importCreateOrderFormat1(String lg_FAMILLE_ID, String lg_ORDER_ID, String lg_GROSSISTE_ID,
            String tab[]) {
        int result = 0;
        try {
            if (Integer.parseInt(tab[3]) > 0) {
                if (tab.length == 4) {

                    if (this.CreateTOrderDetail(lg_ORDER_ID, lg_FAMILLE_ID, lg_GROSSISTE_ID,
                            Integer.parseInt(tab[3])) != null) {
                        result++;
                    }
                } else {

                    if (this.CreateTOrderDetail(lg_ORDER_ID, lg_FAMILLE_ID, lg_GROSSISTE_ID, Integer.parseInt(tab[3]),
                            Integer.parseInt(tab[4])) != null) {
                        result++;
                    }
                }
            }

        } catch (Exception e) {
        }
        return result;
    }

    public int importCreateOrderFormat2(String lg_FAMILLE_ID, String lg_ORDER_ID, String lg_GROSSISTE_ID,
            String tab[]) {
        int result = 0;
        try {/* Integer.parseInt(tab[4]) modifie le 06/01/2016 la quantite recue au lieu de quantite commandee */

            if (this.CreateTOrderDetail(lg_ORDER_ID, lg_FAMILLE_ID, lg_GROSSISTE_ID, Integer.parseInt(tab[5]),
                    (int) Double.parseDouble(tab[6])) != null) {
                result++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int importUpdateOrderFormat1(TOrder OTOrder, List<String> lstString) {
        int result = 0;
        TOrderDetail OTOrderDetail = null;
        try {
            if (OTOrder.getStrSTATUT().equalsIgnoreCase(commonparameter.orderIsPassed)) {
                for (int i = 0; i < lstString.size(); i++) { // lstData: liste des lignes du fichier xls ou csv
                    new logger().OCategory.info("i:" + i + " ///ligne--------" + lstString.get(i)); // ligne courant
                    String[] tabString = lstString.get(i).split(";"); // on case la ligne courante pour recuperer les
                                                                      // differentes colonnes
                    OTOrderDetail = this.getTOrderDetailByCIPAndOrder(OTOrder.getLgORDERID(), tabString[0]);
                    if (OTOrderDetail != null && this.updateQuantiteReponseGrossisteByOrderDetail(OTOrderDetail,
                            Integer.parseInt(tabString[2]))) {
                        i++;
                    }
                }
            } else {
                for (int i = 0; i < lstString.size(); i++) { // lstData: liste des lignes du fichier xls ou csv
                    new logger().OCategory.info("i:" + i + " ///ligne--------" + lstString.get(i)); // ligne courant
                    String[] tabString = lstString.get(i).split(";"); // on case la ligne courante pour recuperer les
                                                                      // differentes colonnes
                    OTOrderDetail = this.getTOrderDetailByCIPAndOrder(OTOrder.getLgORDERID(), tabString[0]);
                    if (tabString.length == 4) {
                        if (OTOrderDetail != null && this.updateQuantiteReponseGrossisteByOrderDetail(OTOrderDetail,
                                Integer.parseInt(tabString[3]))) {
                            i++;
                        }
                    } else {
                        if (OTOrderDetail != null && this.updateQuantiteReponseGrossisteByOrderDetail(OTOrderDetail,
                                Integer.parseInt(tabString[3]), Integer.parseInt(tabString[4]))) {
                            i++;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return result;
    }

    public int importUpdateOrderFormat2(TOrder OTOrder, List<String> lstString) {
        int result = 0;
        TOrderDetail OTOrderDetail = null;
        try {
            if (OTOrder.getStrSTATUT().equalsIgnoreCase(commonparameter.orderIsPassed)) {
                for (int i = 0; i < lstString.size(); i++) { // lstData: liste des lignes du fichier xls ou csv
                    new logger().OCategory.info("i:" + i + " ///ligne--------" + lstString.get(i)); // ligne courant
                    String[] tabString = lstString.get(i).split(";"); // on case la ligne courante pour recuperer les
                                                                      // differentes colonnes
                    OTOrderDetail = this.getTOrderDetailByCIPAndOrder(OTOrder.getLgORDERID(), tabString[2]);
                    if (OTOrderDetail != null && this.updateQuantiteReponseGrossisteByOrderDetail(OTOrderDetail,
                            Integer.parseInt(tabString[4]))) {
                        i++;
                    }
                }
            } else {
                for (int i = 0; i < lstString.size(); i++) { // lstData: liste des lignes du fichier xls ou csv
                    new logger().OCategory.info("i:" + i + " ///ligne--------" + lstString.get(i)); // ligne courant
                    String[] tabString = lstString.get(i).split(";"); // on case la ligne courante pour recuperer les
                                                                      // differentes colonnes
                    OTOrderDetail = this.getTOrderDetailByCIPAndOrder(OTOrder.getLgORDERID(), tabString[2]);
                    if (tabString.length == 4) {
                        if (OTOrderDetail != null && this.updateQuantiteReponseGrossisteByOrderDetail(OTOrderDetail,
                                Integer.parseInt(tabString[4]))) {
                            i++;
                        }
                    } else {
                        if (OTOrderDetail != null && this.updateQuantiteReponseGrossisteByOrderDetail(OTOrderDetail,
                                Integer.parseInt(tabString[4]), Integer.parseInt(tabString[6]))) {
                            i++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec d'importation du fichier de commande");
        }
        return result;
    }
    // fin importation d'une commande
    // retrouver une ligne de commande a partir d'une commande et d'un article

    public TOrderDetail getTOrderDetailByCIPAndOrder(String lg_ORDER_ID, String int_CIP) {
        TOrderDetail OTOrderDetail = null;
        try {
            OTOrderDetail = (TOrderDetail) this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TOrderDetail t WHERE t.lgORDERID.lgORDERID = ?1 AND t.lgFAMILLEID.intCIP = ?2 AND t.strSTATUT NOT LIKE ?3")
                    .setParameter(1, lg_ORDER_ID).setParameter(2, int_CIP)
                    .setParameter(3, commonparameter.statut_delete).getSingleResult();
            // new logger().OCategory.info("Quantite " + OTOrderDetail.getLgORDERID().getStrREFORDER());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return OTOrderDetail;
    }
    // fin retrouver une ligne de commande a partir d'une commande et d'un article

    // mise a jour de la quantité reponse du grossiste
    public boolean updateQuantiteReponseGrossisteByOrderDetail(TOrderDetail OTOrderDetail, int int_NUMBER) {
        boolean result = false;
        try {
            if (int_NUMBER > 0) {
                OTOrderDetail.setIntPRICE(int_NUMBER * OTOrderDetail.getIntPAFDETAIL());
                OTOrderDetail.setIntQTEREPGROSSISTE(int_NUMBER);
                OTOrderDetail.setDtUPDATED(new Date());
                this.persiste(OTOrderDetail);
                result = true;
            } else {
                result = this.addToruptureProduct(OTOrderDetail);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    // fin mise a jour de la quantité reponse du grossiste

    // mise a jour de la quantité reponse du grossiste
    public boolean updateQuantiteReponseGrossisteByOrderDetail(TOrderDetail OTOrderDetail, int int_NUMBER,
            int int_PAF_DETAIL) {
        boolean result = false;
        try {
            if (int_NUMBER > 0) {
                OTOrderDetail.setIntQTEREPGROSSISTE(int_NUMBER);
                OTOrderDetail.setIntPAFDETAIL(int_PAF_DETAIL);
                OTOrderDetail.setIntPRICE(int_NUMBER * int_PAF_DETAIL);
                OTOrderDetail.setDtUPDATED(new Date());
                this.persiste(OTOrderDetail);
                result = true;
            } else {
                result = this.addToruptureProduct(OTOrderDetail);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    // fin mise a jour de la quantité reponse du grossiste

    // lecture d'un fichier csv
    public List<String[]> loadData(String file) {

        List<String[]> allRows = new ArrayList<>();
        try {
            CSVReader reader = new CSVReader(new FileReader(file));
            allRows = reader.readAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allRows;
    }
    // fin lecture d'un fichier csv

    // recuperation d'un commande par la reference
    public TOrder getOrderByRef(String str_REF) {
        TOrder OTOrder = null;
        try {
            OTOrder = (TOrder) this.getOdataManager().getEm()
                    .createQuery("SELECT t FROM TOrder t WHERE t.strREFORDER = ?1 OR t.lgORDERID = ?1")
                    .setParameter(1, str_REF).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return OTOrder;
    }
    // fin recuperation d'un commande par la reference

    // recuperation du montant d'une commande
    public int getMontantCommande(String str_REF, String str_STATUT) {
        List<TOrderDetail> lstTOrderDetail;
        int i = 0;
        try {
            lstTOrderDetail = this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TOrderDetail t WHERE (t.lgORDERID.strREFORDER LIKE ?1 OR t.lgORDERID.lgORDERID LIKE ?1) AND t.strSTATUT = ?2")
                    .setParameter(1, str_REF).setParameter(2, str_STATUT).getResultList();
            for (TOrderDetail OTOrderDetail : lstTOrderDetail) {
                i += OTOrderDetail.getIntPRICE();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    // fin recuperation du montant d'une commande
    // code ajouté
    // ajout d'un produit dans la liste des ruptures de produits lors de la passation d'une commande
    public boolean addToruptureProduct(TOrderDetail OTOrderDetail) {
        boolean result = false;
        TRuptureHistory OTRuptureHistory = null;
        try {
            // TOrderDetail OTOrderDetail = (TOrderDetail) this.getOdataManager().getEm().createQuery("SELECT t FROM
            // TOrderDetail t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgORDERID.lgORDERID = ?2 AND t.strSTATUT NOT
            // LIKE ?3")
            // .setParameter(1, lg_FAMILLE_ID).setParameter(2, lg_ORDER_ID).setParameter(3,
            // commonparameter.statut_delete).getSingleResult();

            if (OTOrderDetail != null) {
                OTRuptureHistory = this.getTRuptureHistoryByFamille(OTOrderDetail.getLgFAMILLEID());
                if (OTRuptureHistory != null) {
                    OTRuptureHistory.setIntNUMBER(OTRuptureHistory.getIntNUMBER() + OTOrderDetail.getIntNUMBER());
                    OTRuptureHistory.setDtUPDATED(new Date());
                    OTRuptureHistory.setGrossisteId(OTOrderDetail.getLgGROSSISTEID());
                    if (this.persiste(OTRuptureHistory) && this.delete(OTOrderDetail)
                            && this.createOrUpdateTSnapShopRuptureStock(OTOrderDetail.getLgFAMILLEID(),
                                    OTRuptureHistory.getIntNUMBER(),
                                    OTOrderDetail.getLgFAMILLEID().getIntQTEREAPPROVISIONNEMENT(),
                                    OTOrderDetail.getLgFAMILLEID().getIntSEUILMIN()) != null) {
                        this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
                        result = true;
                    } else {
                        this.buildErrorTraceMessage("Echec de l'ajout du produit à la liste des ruptures");
                    }
                } else {
                    this.buildErrorTraceMessage("Echec de l'ajout du produit à la liste des ruptures");
                }
            } else {
                this.buildErrorTraceMessage("Erreur! Produit inexistant dans cette commande");
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec de l'ajout du produit à la liste des ruptures");
        }

        return result;
    }

    public TRuptureHistory createRupture(TFamille OTFamille, int int_NUMBER) {
        TRuptureHistory OTRuptureHistory = null;
        try {
            OTRuptureHistory = new TRuptureHistory();
            OTRuptureHistory.setLgRUPTUREHISTORYID(this.getKey().getComplexId());
            OTRuptureHistory.setLgFAMILLEID(OTFamille);
            OTRuptureHistory.setIntNUMBER(int_NUMBER);
            OTRuptureHistory.setDtCREATED(new Date());
            OTRuptureHistory.setStrSTATUT(commonparameter.statut_enable);
            this.persiste(OTRuptureHistory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return OTRuptureHistory;
    }

    public TRuptureHistory getTRuptureHistoryByFamille(TFamille OTFamille) {
        TRuptureHistory OTRuptureHistory = null;
        try {
            OTRuptureHistory = (TRuptureHistory) this.getOdataManager().getEm()
                    .createQuery(
                            "SELECT t FROM TRuptureHistory t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.strSTATUT = ?2")
                    .setParameter(1, OTFamille.getLgFAMILLEID()).setParameter(2, commonparameter.statut_enable)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            OTRuptureHistory = this.createRupture(OTFamille, 0);
        }
        return OTRuptureHistory;
    }
    // fin ajout d'un produit dans la liste des ruptures de produits lors de la passation d'une commande

    // fin code ajouté
    // prix d'achat total d'une commande
    public int getPriceTotalAchat(List<TOrderDetail> lstTOrderDetail) {
        int result = 0;
        try {
            for (TOrderDetail OTOrderDetail : lstTOrderDetail) {
                result += OTOrderDetail.getIntPRICE();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    // fin prix d'achat total d'une commande

    // prix de vente total d'une commande
    public int getPriceTotalVente(List<TOrderDetail> lstTOrderDetail) {
        int result = 0;
        try {
            for (TOrderDetail OTOrderDetail : lstTOrderDetail) {
                result += OTOrderDetail.getIntPRICEDETAIL() * OTOrderDetail.getIntQTEREPGROSSISTE();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    // fin prix de vente total d'une commande

    // prix de vente total d'une commande
    public int getQuantityByCommande(List<TOrderDetail> lstTOrderDetail) {
        int result = 0;
        try {
            for (TOrderDetail OTOrderDetail : lstTOrderDetail) {
                result += OTOrderDetail.getIntQTEREPGROSSISTE();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    // fin prix de vente total d'une commande

    /// Snapshot rupture de stock
    public TSnapShopRuptureStock createOrUpdateTSnapShopRuptureStock(TFamille lg_FAMILLE_ID, int int_QTY,
            int int_QTY_PROPOSE, int int_SEUI_PROPOSE) {
        TSnapShopRuptureStock ruptureStock = null;
        List<TSnapShopRuptureStock> listrRuptureStocks = new ArrayList<>();
        try {
            listrRuptureStocks = this.getOdataManager().getEm().createQuery(
                    "SELECT o FROM TSnapShopRuptureStock o WHERE o.lgFAMILLEID.lgFAMILLEID =?1 AND o.dtDAY =?2 AND o.strSTATUT=?3")
                    .setParameter(1, lg_FAMILLE_ID.getLgFAMILLEID()).setParameter(2, new Date(), TemporalType.DATE)
                    .setParameter(3, commonparameter.statut_enable).getResultList();

            if (listrRuptureStocks.isEmpty()) {
                ruptureStock = createTSnapShopRuptureStock(lg_FAMILLE_ID, int_QTY, int_QTY_PROPOSE, int_SEUI_PROPOSE);
            } else {
                ruptureStock = listrRuptureStocks.get(0);
                ruptureStock = updateTSnapShopRuptureStock(ruptureStock, int_QTY, int_QTY_PROPOSE, int_SEUI_PROPOSE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ruptureStock;
    }

    private TSnapShopRuptureStock createTSnapShopRuptureStock(TFamille lg_FAMILLE_ID, int int_QTY, int int_QTY_PROPOSE,
            int int_SEUI_PROPOSE) {
        TSnapShopRuptureStock ruptureStock = null;
        try {

            ruptureStock = new TSnapShopRuptureStock(this.getKey().gettimeid());
            ruptureStock.setDtCREATED(new Date());
            ruptureStock.setDtDAY(new Date());
            ruptureStock.setIntNUMBERTRANSACTION(1);
            ruptureStock.setIntQTY(int_QTY);
            ruptureStock.setIntQTYPROPOSE(int_QTY_PROPOSE);
            ruptureStock.setIntSEUIPROPOSE(int_SEUI_PROPOSE);
            ruptureStock.setLgFAMILLEID(lg_FAMILLE_ID);
            ruptureStock.setStrSTATUT(commonparameter.statut_enable);
            this.persiste(ruptureStock);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ruptureStock;
    }

    private TSnapShopRuptureStock updateTSnapShopRuptureStock(TSnapShopRuptureStock ruptureStock, int int_QTY,
            int int_QTY_PROPOSE, int int_SEUI_PROPOSE) {

        try {

            ruptureStock.setDtUPDATED(new Date());
            ruptureStock.setIntNUMBERTRANSACTION(ruptureStock.getIntNUMBERTRANSACTION() + 1);
            ruptureStock.setIntQTY(ruptureStock.getIntQTY() + int_QTY);
            ruptureStock.setIntQTYPROPOSE(ruptureStock.getIntSEUIPROPOSE() + int_QTY_PROPOSE);
            ruptureStock.setIntSEUIPROPOSE(ruptureStock.getIntSEUIPROPOSE() + int_SEUI_PROPOSE);
            this.merge(ruptureStock);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ruptureStock;
    }

    private boolean findIfNotDesabled(TFamille famille) {
        try {
            TypedQuery<TFamille> tq = this.getOdataManager().getEm()
                    .createQuery("SELECT o FROM TFamille o WHERE o.strSTATUT='enable'", TFamille.class);
            return !tq.getResultList().isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void mergeOrder(JSONArray checkedList) {
        try {
            TOrder first = this.getOdataManager().getEm().find(TOrder.class, checkedList.get(0).toString());

            TGrossiste OGrossiste = first.getLgGROSSISTEID();
            if (!this.getOdataManager().getEm().getTransaction().isActive()) {
                this.getOdataManager().getEm().getTransaction().begin();
            }
            for (int i = 1; i < checkedList.length(); i++) {
                TOrder order = this.getOdataManager().getEm().find(TOrder.class, checkedList.get(i).toString());

                for (TOrderDetail orderDetail : order.getTOrderDetailCollection()) {
                    if (findIfNotDesabled(orderDetail.getLgFAMILLEID())) {
                        TOrderDetail isExist = getOrderDetailByFamilleAndOrder(
                                orderDetail.getLgFAMILLEID().getLgFAMILLEID(), first.getLgORDERID(),
                                commonparameter.statut_is_Process);
                        if (isExist != null) {

                            isExist.setIntNUMBER(isExist.getIntNUMBER() + orderDetail.getIntNUMBER());
                            isExist.setIntQTEMANQUANT(isExist.getIntQTEMANQUANT() + orderDetail.getIntQTEMANQUANT());
                            isExist.setIntQTEREPGROSSISTE(
                                    isExist.getIntQTEREPGROSSISTE() + orderDetail.getIntQTEREPGROSSISTE());
                            this.getOdataManager().getEm().merge(isExist);

                        } else {
                            createMergeOrderDetails(orderDetail.getLgFAMILLEID(), OGrossiste, orderDetail, first);

                        }
                    }
                    this.getOdataManager().getEm().remove(orderDetail);
                }
                this.getOdataManager().getEm().remove(order);
            }
            if (this.getOdataManager().getEm().getTransaction().isActive()) {
                this.getOdataManager().getEm().getTransaction().commit();
            }
            this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
        } catch (JSONException ex) {
            ex.printStackTrace();
            this.buildErrorTraceMessage("Echec de fusion des commandes");
        }

    }

    private TOrderDetail getOrderDetailByFamilleAndOrder(String lg_FAMILLE_ID, String lg_ORDER_ID, String status) {
        TOrderDetail orderDetail = null;
        List<TOrderDetail> list;
        try {
            list = this.getOdataManager().getEm().createQuery(
                    "SELECT o FROM TOrderDetail o WHERE o.lgFAMILLEID.lgFAMILLEID=?1 AND o.lgORDERID.lgORDERID=?2 AND o.strSTATUT=?3 AND o.lgFAMILLEID.strSTATUT='enable'")
                    .setParameter(1, lg_FAMILLE_ID).setParameter(2, lg_ORDER_ID).setParameter(3, status)
                    .getResultList();
            if (!list.isEmpty()) {
                orderDetail = list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderDetail;
    }

    private TOrderDetail createMergeOrderDetails(TFamille OFamille, TGrossiste OGrossiste, TOrderDetail orderDetail,
            TOrder order) {
        TOrderDetail OTOrderDetail = null;
        try {
            OTOrderDetail = new TOrderDetail(this.getKey().getComplexId());
            OTOrderDetail.setLgORDERID(order);
            OTOrderDetail.setLgFAMILLEID(OFamille);
            OTOrderDetail.setLgGROSSISTEID(OGrossiste);
            OTOrderDetail.setIntNUMBER(orderDetail.getIntNUMBER());
            OTOrderDetail.setIntQTEREPGROSSISTE(orderDetail.getIntNUMBER());
            OTOrderDetail.setIntQTEMANQUANT(orderDetail.getIntNUMBER());
            OTOrderDetail.setIntPRICE(orderDetail.getIntNUMBER() * OFamille.getIntPAF());
            OTOrderDetail.setIntPAFDETAIL(OFamille.getIntPAF());
            OTOrderDetail.setIntPRICEDETAIL(OFamille.getIntPRICE());
            OTOrderDetail.setStrSTATUT(commonparameter.statut_is_Process);
            OTOrderDetail.setDtCREATED(new Date());
            this.getOdataManager().getEm().persist(OTOrderDetail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return OTOrderDetail;

    }

    public TOrderDetail createOrderDetail_(String lg_ORDER_ID, String lg_FAMILLE_ID, String lg_GROSSISTE_ID,
            int int_NUMBER, int int_PRICE, int int_PAF) {

        TOrderDetail oTOrderDetail = null;
        try {
            TFamille OTFamille = new familleManagement(this.getOdataManager()).getTFamille(lg_FAMILLE_ID);
            try {
                oTOrderDetail = this.findFamilleInTOrderDetail(lg_ORDER_ID, OTFamille.getLgFAMILLEID());
                // TOrderDetail OTOrderDetail = this.findFamilleInTOrderDetail(lg_ORDER_ID, OTFamille.getLgFAMILLEID());

                if (oTOrderDetail != null) {
                    oTOrderDetail.setIntNUMBER(oTOrderDetail.getIntNUMBER() + int_NUMBER);
                    oTOrderDetail.setIntQTEREPGROSSISTE(oTOrderDetail.getIntQTEREPGROSSISTE() + int_NUMBER);
                    oTOrderDetail.setIntQTEMANQUANT(oTOrderDetail.getIntQTEMANQUANT() + int_NUMBER);
                    oTOrderDetail.setDtUPDATED(new Date());
                } else {
                    oTOrderDetail.setIntNUMBER(int_NUMBER);
                    oTOrderDetail.setIntQTEREPGROSSISTE(int_NUMBER);
                    oTOrderDetail.setIntQTEMANQUANT(int_NUMBER);
                    oTOrderDetail.setIntPAFDETAIL(int_PAF);
                    oTOrderDetail.setIntPRICEDETAIL(int_PRICE);
                    oTOrderDetail.setDtUPDATED(new Date());
                }

            } catch (Exception E) {
                oTOrderDetail = new TOrderDetail();
                oTOrderDetail.setLgORDERDETAILID(this.getKey().getComplexId());
                oTOrderDetail.setLgORDERID(this.FindOrder(lg_ORDER_ID));
                oTOrderDetail.setIntNUMBER(int_NUMBER);
                oTOrderDetail.setIntQTEREPGROSSISTE(oTOrderDetail.getIntNUMBER());
                oTOrderDetail.setIntQTEMANQUANT(int_NUMBER);
                oTOrderDetail.setIntPAFDETAIL(int_PAF);
                oTOrderDetail.setIntPRICEDETAIL(int_PRICE);
                oTOrderDetail.setLgFAMILLEID(OTFamille);
                try {
                    TGrossiste OTGrossiste = this.SearchGrossiste(lg_GROSSISTE_ID);
                    oTOrderDetail.setLgGROSSISTEID(OTGrossiste);
                } catch (Exception e) {
                }
                oTOrderDetail.setStrSTATUT(commonparameter.statut_is_Process);
                oTOrderDetail.setDtCREATED(new Date());
                oTOrderDetail.setDtUPDATED(new Date());
            }
            oTOrderDetail.setIntORERSTATUS((short) 2);
            oTOrderDetail.setIntPRICE(oTOrderDetail.getIntNUMBER() * oTOrderDetail.getIntPAFDETAIL());
            this.persiste(oTOrderDetail);
            OTFamille.setIntORERSTATUS((short) 2);
            this.merge(OTFamille);
            this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec d'ajout de l'article à la commande");
        }

        return oTOrderDetail;

    }

    public TOrderDetail UpdateOrderDetail_(String lg_ORDERDETAIL_ID, String lg_ORDER_ID, String lg_FAMILLE_GROSSISTE_ID,
            int int_NUMBER) {

        TOrderDetail oTOrderDetail = null;

        try {

            oTOrderDetail = this.getOdataManager().getEm().find(TOrderDetail.class, lg_ORDERDETAIL_ID);

            oTOrderDetail.setLgORDERID(this.FindOrder(lg_ORDER_ID));
            oTOrderDetail.setIntNUMBER(int_NUMBER);
            oTOrderDetail.setIntQTEMANQUANT(int_NUMBER);

            dal.TFamilleGrossiste OTFamilleGrossiste = this.getOdataManager().getEm().find(TFamilleGrossiste.class,
                    lg_FAMILLE_GROSSISTE_ID);
            if (OTFamilleGrossiste != null) {
                // oTOrderDetail.setLgFAMILLEGROSSISTEID(OTFamilleGrossiste);
            }

            oTOrderDetail.setStrSTATUT(commonparameter.statut_is_Process);
            oTOrderDetail.setDtUPDATED(new Date());

            this.persiste(oTOrderDetail);
            this.refresh(oTOrderDetail);

            new logger().OCategory.info(" update de oTOrderDetail  " + oTOrderDetail.getLgORDERDETAILID());

        } catch (Exception E) {

            // oTOrderDetail = this.createOrderDetail(lg_ORDER_ID, lg_FAMILLE_GROSSISTE_ID, int_NUMBER);
            new logger().OCategory.info(" CREATE de oTOrderDetail  " + oTOrderDetail.getLgORDERDETAILID());
        }
        return oTOrderDetail;
    }

    public TOrderDetail findFamilleInTOrderDetail_(String lg_ORDER_ID, String lg_FAMILLE_ID) {
        TOrderDetail OTOrderDetail = null;
        try {
            new logger().OCategory.info("lg_ORDER_ID " + lg_ORDER_ID + " lg_FAMILLE_ID " + lg_FAMILLE_ID);
            OTOrderDetail = (TOrderDetail) this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TOrderDetail t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgORDERID.lgORDERID LIKE ?2 AND (t.strSTATUT LIKE ?3 OR t.strSTATUT LIKE ?4) ")
                    .setParameter(1, lg_FAMILLE_ID).setParameter(2, lg_ORDER_ID)
                    .setParameter(3, commonparameter.orderIsPassed).setParameter(4, commonparameter.statut_is_Process)
                    .getSingleResult();
        } catch (Exception e) {
            this.buildErrorTraceMessage(e.getMessage());
        }
        return OTOrderDetail;
    }

    // gestion des evaluations des offres de prix
    /*
     * public TEvaluationoffreprix addProductToEvaluateOffer(String str_PRESTATAIRE) { TEvaluationoffreprix
     * OTEvaluationoffreprix = null; try {
     *
     * OTEvaluationoffreprix = new TEvaluationoffreprix();
     * OTEvaluationoffreprix.setLgEVALUATIONOFFREPRIXID(this.getKey().getComplexId());
     * OTEvaluationoffreprix.setStrPRESTATAIRE(str_PRESTATAIRE); OTEvaluationoffreprix.setIntPRICEOFFRE(0);
     * OTEvaluationoffreprix.setStrSTATUT(commonparameter.statut_is_Process); OTEvaluationoffreprix.setDtCREATED(new
     * Date()); if (this.persiste(OTEvaluationoffreprix)) { this.buildSuccesTraceMessage("Produit ajouté avec succès");
     * } else { this.buildErrorTraceMessage("Echec d'ajout produit"); }
     *
     * } catch (Exception e) { e.printStackTrace();
     * this.buildErrorTraceMessage("Echec d'ajout produit. Veuillez contacter votre administrateur"); } return
     * OTEvaluationoffreprix; }
     */
    public boolean addProductToEvaluateOffer(String lg_PRODUCT_ID, int int_NUMBER, int int_NUMBER_GRATUIT,
            int int_PRICE_OFFRE) {
        boolean result = false;
        TFamille OTFamille = null;
        TEvaluationoffreprix OTEvaluationoffreprix = null;
        Date now = new Date();
        // int int_PRICE_OFFRE_TOTAL_NEW = 0, int_PRICE_OFFRE_OLD = 0;
        try {
            OTFamille = this.getOdataManager().getEm().find(TFamille.class, lg_PRODUCT_ID);

            if (OTFamille == null) {
                this.buildErrorTraceMessage("Echec d'ajout, produit inexistant");
                return result;
            }

            OTEvaluationoffreprix = (TEvaluationoffreprix) this.getOdataManager().getEm()
                    .createQuery("SELECT t FROM TEvaluationoffreprix t WHERE t.lgFAMILLEID.lgFAMILLEID LIKE ?1")
                    .setParameter(1, lg_PRODUCT_ID).getSingleResult();

            // int_PRICE_OFFRE_OLD = (OTEvaluationoffreprixDetail.getIntNUMBER() +
            // OTEvaluationoffreprixDetail.getIntNUMBERGRATUIT()) * OTEvaluationoffreprixDetail.getIntPRICEOFFRE();
            OTEvaluationoffreprix.setIntNUMBER(OTEvaluationoffreprix.getIntNUMBER() + int_NUMBER);
            OTEvaluationoffreprix.setIntNUMBERGRATUIT(OTEvaluationoffreprix.getIntNUMBERGRATUIT() + int_NUMBER_GRATUIT);
            OTEvaluationoffreprix.setIntPRICEOFFRE(int_PRICE_OFFRE);
            OTEvaluationoffreprix.setDtUPDATED(now);
            // int_PRICE_OFFRE_TOTAL_NEW = OTEvaluationoffreprix.getIntPRICEOFFRE() - int_PRICE_OFFRE_OLD +
            // ((OTEvaluationoffreprixDetail.getIntNUMBER() + OTEvaluationoffreprixDetail.getIntNUMBERGRATUIT()) *
            // OTEvaluationoffreprixDetail.getIntPRICEOFFRE());

            /*
             * new
             * logger().OCategory.info("int_PRICE_OFFRE_TOTAL_NEW:"+int_PRICE_OFFRE_TOTAL_NEW+"|int_PRICE_OFFRE_OLD:"+
             * int_PRICE_OFFRE_OLD); //mise a jour du montant total de l'evaluation depuis la base
             * OTEvaluationoffreprix.setIntPRICEOFFRE(int_PRICE_OFFRE_TOTAL_NEW);
             * this.getOdataManager().getEm().merge(OTEvaluationoffreprix);
             */
            if (this.persiste(OTEvaluationoffreprix)) {
                this.buildSuccesTraceMessage("Produit ajouté avec succès");
                result = true;
            } else {
                this.buildErrorTraceMessage("Echec d'ajout produit");
            }

        } catch (Exception e) {
            e.printStackTrace();

            if (this.intProductToEvaluateOffer(OTFamille, int_NUMBER, int_NUMBER_GRATUIT, int_PRICE_OFFRE) != null) {
                this.buildSuccesTraceMessage("Produit ajouté avec succès");
                result = true;
            } else {
                this.buildErrorTraceMessage("Echec d'ajout produit. Veuillez contacter votre administrateur");
            }
        }
        return result;
    }

    public TEvaluationoffreprix intProductToEvaluateOffer(TFamille OTFamille, int int_NUMBER, int int_NUMBER_GRATUIT,
            int int_PRICE_OFFRE) {
        TEvaluationoffreprix OTEvaluationoffreprix = null;
        Date now = new Date();
        Date dtDEBUT;
        Double qteVenteArticle = 0.0;
        SnapshotManager OSnapshotManager = new SnapshotManager(this.getOdataManager(), this.getOTUser());
        // int int_PRICE_OFFRE_TOTAL_NEW = 0, int_PRICE_OFFRE_OLD = 0;
        try {

            OTEvaluationoffreprix = new TEvaluationoffreprix();
            dtDEBUT = date.GetDebutMois();
            for (int i = 1; i <= 3; i++) {
                qteVenteArticle += OSnapshotManager.getQauntityVenteByArticle(OTFamille.getStrDESCRIPTION(),
                        date.getFirstDayofSomeMonth(Integer.parseInt(date.getoMois(dtDEBUT))
                                - (i + Integer.parseInt(date.getoMois(dtDEBUT)))),
                        date.getLastDayofSomeMonth(Integer.parseInt(date.getoMois(dtDEBUT))
                                - (i + Integer.parseInt(date.getoMois(dtDEBUT)))),
                        OTFamille.getLgFAMILLEID(), "%%", "%%");
                new logger().OCategory.info("qteVenteArticle de " + i + ": " + qteVenteArticle);
            }

            OTEvaluationoffreprix.setLgEVALUATIONOFFREPRIXID(this.getKey().getComplexId());
            OTEvaluationoffreprix.setLgFAMILLEID(OTFamille);

            OTEvaluationoffreprix.setIntNUMBER(int_NUMBER);
            OTEvaluationoffreprix.setIntNUMBERGRATUIT(int_NUMBER_GRATUIT);
            OTEvaluationoffreprix.setIntPRICEOFFRE(int_PRICE_OFFRE);

            OTEvaluationoffreprix.setIntMOISLIQUIDATION(
                    (OTEvaluationoffreprix.getIntNUMBER() + OTEvaluationoffreprix.getIntNUMBERGRATUIT()) * 3
                            / qteVenteArticle.intValue());
            OTEvaluationoffreprix.setIntQTEPRODUCTVENDU(qteVenteArticle.intValue());
            OTEvaluationoffreprix.setStrSTATUT(commonparameter.statut_enable);
            OTEvaluationoffreprix.setDtCREATED(now);
            OTEvaluationoffreprix.setDtUPDATED(now);
            if (this.persiste(OTEvaluationoffreprix)) {
                this.buildSuccesTraceMessage("Produit ajouté avec succès");
            } else {
                this.buildErrorTraceMessage("Echec d'ajout produit");
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec d'ajout produit. Veuillez contacter votre administrateur");
        }
        return OTEvaluationoffreprix;
    }

    /*
     * public TEvaluationoffreprix updateProductToEvaluateOffer(String lg_EVALUATIONOFFREPRIX_ID, String
     * str_PRESTATAIRE) {
     *
     * TEvaluationoffreprix OTEvaluationoffreprix = null; try { OTEvaluationoffreprix =
     * this.getOdataManager().getEm().find(TEvaluationoffreprix.class, lg_EVALUATIONOFFREPRIX_ID); if
     * (OTEvaluationoffreprix == null) {
     * this.buildErrorTraceMessage("Echec de mise à jour, produit inexistant dans l'offre"); return null; }
     *
     * OTEvaluationoffreprix.setStrPRESTATAIRE(str_PRESTATAIRE); OTEvaluationoffreprix.setDtUPDATED(new Date()); if
     * (this.persiste(OTEvaluationoffreprix)) {
     * this.buildSuccesTraceMessage("Produit mise à jour effectué avec succès"); } else {
     * this.buildErrorTraceMessage("Echec de mise à jour du produit dans l'offre"); }
     *
     * } catch (Exception e) { e.printStackTrace();
     * this.buildErrorTraceMessage("Echec de mise à jour. Veuillez contacter votre administrateur"); } return
     * OTEvaluationoffreprix; }
     */
    public TEvaluationoffreprix updateProductToEvaluateOffer(String lg_EVALUATIONOFFREPRIX_ID, int int_NUMBER,
            int int_NUMBER_GRATUIT, int int_PRICE_OFFRE) {

        TEvaluationoffreprix OEvaluationoffreprix = null;
        // int int_PRICE_OFFRE_TOTAL_NEW = 0, int_PRICE_OFFRE_OLD = 0;

        try {
            OEvaluationoffreprix = this.getOdataManager().getEm().find(TEvaluationoffreprix.class,
                    lg_EVALUATIONOFFREPRIX_ID);
            if (OEvaluationoffreprix == null) {
                this.buildErrorTraceMessage("Echec de mise à jour, produit inexistant dans l'offre");
                return null;
            }
            /*
             * OEvaluationoffreprix = OTEvaluationoffreprixDetail.getLgEVALUATIONOFFREPRIXID(); int_PRICE_OFFRE_OLD =
             * (OTEvaluationoffreprixDetail.getIntNUMBER() + OTEvaluationoffreprixDetail.getIntNUMBERGRATUIT()) *
             * OTEvaluationoffreprixDetail.getIntPRICEOFFRE();
             */
            OEvaluationoffreprix.setIntNUMBER(int_NUMBER);
            OEvaluationoffreprix.setIntNUMBERGRATUIT(int_NUMBER_GRATUIT);
            OEvaluationoffreprix.setIntPRICEOFFRE(int_PRICE_OFFRE);
            OEvaluationoffreprix.setIntMOISLIQUIDATION(
                    (OEvaluationoffreprix.getIntNUMBER() + OEvaluationoffreprix.getIntNUMBERGRATUIT()) * 3
                            / OEvaluationoffreprix.getIntQTEPRODUCTVENDU());
            OEvaluationoffreprix.setDtUPDATED(new Date());
            /*
             * int_PRICE_OFFRE_TOTAL_NEW = OEvaluationoffreprix.getIntPRICEOFFRE() - int_PRICE_OFFRE_OLD +
             * ((OTEvaluationoffreprixDetail.getIntNUMBER() + OTEvaluationoffreprixDetail.getIntNUMBERGRATUIT()) *
             * OTEvaluationoffreprixDetail.getIntPRICEOFFRE()); new
             * logger().OCategory.info("int_PRICE_OFFRE_TOTAL_NEW:"+int_PRICE_OFFRE_TOTAL_NEW+"|int_PRICE_OFFRE_OLD:"+
             * int_PRICE_OFFRE_OLD); //mise a jour du montant total de l'evaluation depuis la base
             * OEvaluationoffreprix.setIntPRICEOFFRE(int_PRICE_OFFRE_TOTAL_NEW);
             * this.getOdataManager().getEm().merge(OEvaluationoffreprix);
             */
            if (this.persiste(OEvaluationoffreprix)) {
                this.buildSuccesTraceMessage("Produit mise à jour effectué avec succès");

            } else {
                this.buildErrorTraceMessage("Echec de mise à jour du produit dans l'offre");
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec de mise à jour. Veuillez contacter votre administrateur");
        }
        return OEvaluationoffreprix;
    }

    /*
     * public boolean deleteProductToEvaluateOffer(String lg_EVALUATIONOFFREPRIX_ID) { boolean result = false; long
     * count = 0; try { Object resultat =
     * this.getOdataManager().getEm().createNativeQuery("CALL `proc_evaluationoffreprix`('"
     * +lg_EVALUATIONOFFREPRIX_ID+"', '"+commonparameter.statut_delete+"')").getSingleResult(); count =
     * Long.valueOf(String.valueOf(resultat)); new logger().OCategory.info(count); result = true;
     * this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES")); } catch (Exception e) {
     * e.printStackTrace(); this.buildErrorTraceMessage("Echec de suppression de l'évaluation de l'offre");
     *
     * } return result; }
     */
    public boolean deleteProductToEvaluateOffer(String lg_EVALUATIONOFFREPRIXL_ID) {
        boolean result = false;
        TEvaluationoffreprix OTEvaluationoffreprix = null;

        try {
            OTEvaluationoffreprix = this.getOdataManager().getEm().find(TEvaluationoffreprix.class,
                    lg_EVALUATIONOFFREPRIXL_ID);
            if (OTEvaluationoffreprix == null) {
                this.buildErrorTraceMessage("Echec de suppression. Produit inexistant dans l'offre");
                return result;
            }
            /*
             * OTEvaluationoffreprix = OTEvaluationoffreprixDetail.getLgEVALUATIONOFFREPRIXID();
             * OTEvaluationoffreprix.setIntPRICEOFFRE(OTEvaluationoffreprix.getIntPRICEOFFRE() -
             * ((OTEvaluationoffreprixDetail.getIntNUMBER() + OTEvaluationoffreprixDetail.getIntNUMBERGRATUIT()) *
             * OTEvaluationoffreprixDetail.getIntNUMBER())); OTEvaluationoffreprix.setDtUPDATED(new Date());
             * this.getOdataManager().getEm().merge(OTEvaluationoffreprix);
             */
            if (this.delete(OTEvaluationoffreprix)) {
                this.buildSuccesTraceMessage("Produit retiré de l'offre de prix avec succès");
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void closureEvaluationOffer() {
        try {
            this.getOdataManager().getEm().createNativeQuery("CALL `proc_evaluationoffreprix`()").getSingleResult();
            this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec de la clôture de l'évaluation");
        }
    }

    public List<TEvaluationoffreprix> getAllTEvaluationoffreprix(String search_value, String str_STATUT) {
        List<TEvaluationoffreprix> lstEvaluationoffreprix = new ArrayList<TEvaluationoffreprix>();
        try {
            lstEvaluationoffreprix = this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TEvaluationoffreprix t WHERE (t.lgFAMILLEID.strDESCRIPTION LIKE ?1 OR t.lgFAMILLEID.intCIP LIKE ?1 OR t.lgFAMILLEID.intEAN13 LIKE ?1) AND t.strSTATUT = ?2 ORDER BY t.dtUPDATED DESC")
                    .setParameter(1, search_value + "%").setParameter(2, commonparameter.statut_enable).getResultList();
        } catch (Exception e) {
        }
        return lstEvaluationoffreprix;
    }

    public List<TEvaluationoffreprix> getAllTEvaluationoffreprix(String search_value, String str_STATUT, int start,
            int limit) {
        List<TEvaluationoffreprix> lstEvaluationoffreprix = new ArrayList<TEvaluationoffreprix>();
        try {
            lstEvaluationoffreprix = this.getOdataManager().getEm().createQuery(
                    "SELECT t FROM TEvaluationoffreprix t WHERE (t.lgFAMILLEID.strDESCRIPTION LIKE ?1 OR t.lgFAMILLEID.intCIP LIKE ?1 OR t.lgFAMILLEID.intEAN13 LIKE ?1) AND t.strSTATUT = ?2 ORDER BY t.dtUPDATED DESC")
                    .setParameter(1, search_value + "%").setParameter(2, commonparameter.statut_enable)
                    .setFirstResult(start).setMaxResults(limit).getResultList();
        } catch (Exception e) {
        }
        return lstEvaluationoffreprix;
    }

    public Double getTotalAchatOffre(List<TEvaluationoffreprix> lstTEvaluationoffreprix) {
        Double result = 0.0;
        try {
            for (TEvaluationoffreprix OTEvaluationoffreprix : lstTEvaluationoffreprix) {
                result += OTEvaluationoffreprix.getIntPRICEOFFRE()
                        * (OTEvaluationoffreprix.getIntNUMBER() + OTEvaluationoffreprix.getIntNUMBERGRATUIT());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * public List<TEvaluationoffreprixDetail> getAllTEvaluationoffreprixDetail(String search_value, String
     * lg_EVALUATIONOFFREPRIX_ID, String str_STATUT, int start, int limit) { List<TEvaluationoffreprixDetail>
     * lstTEvaluationoffreprixDetail = new ArrayList<TEvaluationoffreprixDetail>(); try { lstTEvaluationoffreprixDetail
     * = this.getOdataManager().getEm().
     * createQuery("SELECT t FROM TEvaluationoffreprixDetail t WHERE (t.lgFAMILLEID.strDESCRIPTION LIKE ?1 OR t.lgFAMILLEID.intCIP LIKE ?1 OR t.lgFAMILLEID.intEAN13 LIKE ?1) AND t.strSTATUT = ?2 AND t.lgEVALUATIONOFFREPRIXID.lgEVALUATIONOFFREPRIXID = ?3 ORDER BY t.lgFAMILLEID.strDESCRIPTION"
     * ) .setParameter(1, search_value + "%").setParameter(2, commonparameter.statut_enable).setParameter(3,
     * lg_EVALUATIONOFFREPRIX_ID).setFirstResult(start).setMaxResults(limit).getResultList(); } catch (Exception e) { }
     * return lstTEvaluationoffreprixDetail; }
     *
     * public List<TEvaluationoffreprixDetail> getAllTEvaluationoffreprixDetail(String search_value, String
     * lg_EVALUATIONOFFREPRIX_ID, String str_STATUT) { List<TEvaluationoffreprixDetail> lstTEvaluationoffreprixDetail =
     * new ArrayList<TEvaluationoffreprixDetail>(); try { lstTEvaluationoffreprixDetail =
     * this.getOdataManager().getEm().
     * createQuery("SELECT t FROM TEvaluationoffreprixDetail t WHERE (t.lgFAMILLEID.strDESCRIPTION LIKE ?1 OR t.lgFAMILLEID.intCIP LIKE ?1 OR t.lgFAMILLEID.intEAN13 LIKE ?1) AND t.strSTATUT = ?2 AND t.lgEVALUATIONOFFREPRIXID.lgEVALUATIONOFFREPRIXID = ?3 ORDER BY t.lgFAMILLEID.strDESCRIPTION"
     * ) .setParameter(1, search_value + "%").setParameter(2, commonparameter.statut_enable).setParameter(3,
     * lg_EVALUATIONOFFREPRIX_ID).getResultList(); } catch (Exception e) { } return lstTEvaluationoffreprixDetail; }
     */
    // fin gestion des evaluations des offres de prix
    // check des produits d'une commande
    public List<String> checkImport(List<String> lstData, String format) {
        List<String> lst = new ArrayList<String>();
        TFamille OTFamille = null;
        familleManagement OfamilleManagement = new familleManagement(this.getOdataManager());
        try {
            if (format.equalsIgnoreCase(Parameter.format_commande_1)) {
                for (int i = 0; i < lstData.size(); i++) { // lstData: liste des lignes du fichier xls ou csv
                    new logger().OCategory.info("i:" + i + " ///ligne--------" + lstData.get(i)); // ligne courant
                    String[] tabString = lstData.get(i).split(";"); // on case la ligne courante pour recuperer les
                                                                    // differentes colonnes
                    OTFamille = OfamilleManagement.getTFamille(tabString[0]);
                    if (OTFamille == null) {
                        new logger().OCategory.info("Ligne inexistante " + i);
                        lst.add((i + 1) + ";" + lstData.get(i));
                    }
                }

            } else if (format.equalsIgnoreCase(Parameter.format_commande_2)) {
                for (int i = 0; i < lstData.size(); i++) { // lstData: liste des lignes du fichier xls ou csv
                    new logger().OCategory.info("i:" + i + " ///ligne--------" + lstData.get(i)); // ligne courant
                    String[] tabString = lstData.get(i).split(";"); // on case la ligne courante pour recuperer les
                                                                    // differentes colonnes
                    OTFamille = OfamilleManagement.getTFamille(tabString[2]);
                    if (OTFamille == null) {
                        new logger().OCategory.info("Ligne inexistante " + i);
                        lst.add(lstData.get(i));
                    }
                }

            }

            this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));

        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec de vérification du fichier. Aucune ligne n'a été pris en compte");
        }
        return lst;
    }

    // fin check des produits d'une commande
    private int getStatusInOrder(String lgFamilleID, String orderId) {
        int status = 0;
        try {
            long count = (long) this.getOdataManager().getEm().createQuery(
                    "SELECT COUNT(p) FROM TOrder r,TOrderDetail p WHERE p.lgORDERID.lgORDERID=r.lgORDERID AND (p.intORERSTATUS=?2 OR p.intORERSTATUS=?3 OR p.intORERSTATUS=?4 ) AND  p.lgFAMILLEID.lgFAMILLEID =?1 AND p.lgORDERDETAILID <>?5  ORDER BY p.intORERSTATUS DESC")
                    .setParameter(1, lgFamilleID).setParameter(2, (short) 2).setParameter(3, (short) 3)
                    .setParameter(4, (short) 4).setParameter(5, orderId).setMaxResults(1).getSingleResult();
            if (count > 0) {
                status = new Integer(count + "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public TSuggestionOrder createTSuggestionOrderBACK(String lgGROSSISTE_ID, String str_STATUT) {
        TSuggestionOrder OTSuggestionOrder = null;
        TGrossiste OTGrossiste = null;
        try {
            OTSuggestionOrder = new TSuggestionOrder();
            OTSuggestionOrder.setLgSUGGESTIONORDERID(this.getKey().getComplexId());
            OTSuggestionOrder.setStrREF("REF_" + this.getKey().getShortId(7));
            OTGrossiste = new grossisteManagement(this.getOdataManager()).getGrossiste(lgGROSSISTE_ID);
            if (OTGrossiste == null) {
                this.buildErrorTraceMessage("Echec d'enregistrement de la suggestion. Grossiste inexistant");
                return null;
            }
            OTSuggestionOrder.setLgGROSSISTEID(OTGrossiste);
            OTSuggestionOrder.setStrSTATUT(str_STATUT);
            OTSuggestionOrder.setDtCREATED(new Date());
            OTSuggestionOrder.setDtUPDATED(new Date());
            this.getOdataManager().getEm().merge(OTSuggestionOrder);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return OTSuggestionOrder;
    }

    public TSuggestionOrder createSuggestionOrder(String lgGROSSISTE_ID, String str_STATUT) {
        TSuggestionOrder OTSuggestionOrder = null;
        TGrossiste OTGrossiste = null;
        try {
            OTSuggestionOrder = new TSuggestionOrder();
            OTSuggestionOrder.setLgSUGGESTIONORDERID(this.getKey().getComplexId());
            OTSuggestionOrder.setStrREF("REF_" + this.getKey().getShortId(7));
            OTGrossiste = this.getOdataManager().getEm().getReference(TGrossiste.class, lgGROSSISTE_ID);
            if (OTGrossiste == null) {
                this.buildErrorTraceMessage("Echec d'enregistrement de la suggestion. Grossiste inexistant");
                return null;
            }
            OTSuggestionOrder.setLgGROSSISTEID(OTGrossiste);
            OTSuggestionOrder.setStrSTATUT(str_STATUT);
            OTSuggestionOrder.setDtCREATED(new Date());
            OTSuggestionOrder.setDtUPDATED(new Date());

            this.getOdataManager().getEm().persist(OTSuggestionOrder);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return OTSuggestionOrder;
    }

    // 24 11 2017
    public void createOrderItem(TOrder lgORDERID, TFamille OTFamille, TGrossiste OTGrossiste,
            TFamilleGrossiste OTFamilleGrossiste, int int_NUMBER) {

        try {

            TOrderDetail OTOrderDetail = new TOrderDetail();
            OTOrderDetail.setLgORDERDETAILID(this.getKey().getComplexId());
            OTOrderDetail.setLgORDERID(lgORDERID);
            OTOrderDetail.setIntNUMBER(int_NUMBER);
            OTOrderDetail.setIntQTEREPGROSSISTE(int_NUMBER);
            OTOrderDetail.setIntQTEMANQUANT(int_NUMBER);
            OTOrderDetail.setIntPAFDETAIL(OTFamilleGrossiste.getIntPAF());
            OTOrderDetail.setIntPRICEDETAIL(OTFamilleGrossiste.getIntPRICE());
            OTOrderDetail.setIntPRICE(OTOrderDetail.getIntPAFDETAIL() * int_NUMBER);
            OTOrderDetail.setLgFAMILLEID(OTFamille);
            OTOrderDetail.setLgGROSSISTEID(OTGrossiste);
            OTOrderDetail.setStrSTATUT(commonparameter.statut_is_Process);
            OTOrderDetail.setDtCREATED(new Date());
            OTOrderDetail.setDtUPDATED(new Date());
            OTOrderDetail.setIntORERSTATUS((short) 2);
            OTOrderDetail.setPrixAchat(OTFamilleGrossiste.getIntPAF());
            // OTOrderDetail.setPrixUnitaire(OTFamilleGrossiste.getIntPRICE());
            this.getOdataManager().getEm().persist(OTOrderDetail);
            OTFamille.setBCODEINDICATEUR((short) 1);
            this.getOdataManager().getEm().merge(OTFamille);

            this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec d'ajout de l'article à la commande");
        }

    }

    public JSONObject getTotalAchatVente(String lgORDERID) {
        JSONObject json = new JSONObject();
        try {
            EntityManager em = this.getOdataManager().getEm();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TOrderDetail> root = cq.from(TOrderDetail.class);
            Join<TOrderDetail, TOrder> join = root.join("lgORDERID", JoinType.INNER);
            cq.multiselect(cb.sum(root.get(TOrderDetail_.intPRICE)),
                    cb.sum(cb.prod(root.get(TOrderDetail_.intQTEREPGROSSISTE), root.get(TOrderDetail_.intPRICEDETAIL))),
                    cb.count(root));
            cq.where(cb.equal(join.get(TOrder_.lgORDERID), lgORDERID));

            List<Object[]> ob = em.createQuery(cq).getResultList();
            ob.forEach((t) -> {
                try {
                    json.put("PRIX_ACHAT_TOTAL", Integer.valueOf(t[0] + ""))
                            .put("PRIX_VENTE_TOTAL", Integer.valueOf(t[1] + ""))
                            .put("COUNT", Integer.valueOf(t[2] + ""));
                } catch (JSONException ex) {
                    Logger.getLogger(orderManagement.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    // creation de commande
    public JSONObject addOrder(String lgGROSSISTE_ID, String lg_FAMILLE_ID, String lg_GROSSISTE_ID, String str_STATUT,
            int int_NUMBER) {
        JSONObject json = new JSONObject();
        try {

            TFamilleGrossiste OTFamilleGrossiste = null;

            TFamille OTFamille = this.getOdataManager().getEm().find(TFamille.class, lg_FAMILLE_ID);
            if (OTFamille == null) {
                this.buildErrorTraceMessage("Echec d'ajout de produit. Référence de produit inexistante");
                return null;
            }

            TGrossiste OTGrossiste = this.SearchGrossiste(lg_GROSSISTE_ID);
            if (OTGrossiste == null) {
                this.buildErrorTraceMessage("Echec d'ajout du produit. Grossiste inexistant");
                return null;
            }
            try {
                OTFamilleGrossiste = new familleGrossisteManagement(this.getOdataManager())
                        .findFamilleGrossiste(OTFamille.getLgFAMILLEID(), lg_GROSSISTE_ID);
            } catch (Exception e) {

            }
            this.getOdataManager().BeginTransaction();
            if (OTFamilleGrossiste == null) {
                OTFamilleGrossiste = new TFamilleGrossiste();
                OTFamilleGrossiste.setLgFAMILLEID(OTFamille);
                OTFamilleGrossiste.setLgGROSSISTEID(OTGrossiste);
                OTFamilleGrossiste.setIntPAF(OTFamille.getIntPAF());
                OTFamilleGrossiste.setIntPRICE(OTFamille.getIntPRICE());
                OTFamilleGrossiste.setStrCODEARTICLE("");
                // OTFamilleGrossiste.setStrCODEARTICLE(OTFamille.getIntCIP());
                this.getOdataManager().getEm().persist(OTFamilleGrossiste);
            }
            TOrder OTOrder = new TOrder();
            OTOrder.setLgORDERID(this.getKey().getComplexId());
            OTOrder.setLgUSERID(this.getOTUser());
            OTOrder.setLgGROSSISTEID(OTGrossiste);
            OTOrder.setStrREFORDER(this.buildCommandeRef(new Date()));
            OTOrder.setStrSTATUT(str_STATUT);
            OTOrder.setDtCREATED(new Date());
            OTOrder.setDtUPDATED(new Date());
            this.getOdataManager().getEm().persist(OTOrder);
            this.createOrderItem(OTOrder, OTFamille, OTGrossiste, OTFamilleGrossiste, int_NUMBER);
            this.getOdataManager().CloseTransaction();
            this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
            JSONObject details = getTotalAchatVente(OTOrder.getLgORDERID());
            json.put("LGORDERID", OTOrder.getLgORDERID()).put("PRIX_ACHAT_TOTAL", details.get("PRIX_ACHAT_TOTAL"))
                    .put("PRIX_VENTE_TOTAL", details.get("PRIX_VENTE_TOTAL")).put("COUNT", details.getInt("COUNT"));
        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec de création de la commande");
        }
        return json;

    }
    // fin creation de commande

    public JSONObject addOrUpdateOrderItem(String lgORDERID, String lg_FAMILLE_ID, String lg_GROSSISTE_ID,
            int int_NUMBER) {
        JSONObject json = new JSONObject();
        try {
            TOrderDetail OTOrderDetail;
            TFamilleGrossiste OTFamilleGrossiste = null;

            TFamille OTFamille = this.getOdataManager().getEm().find(TFamille.class, lg_FAMILLE_ID);
            if (OTFamille == null) {
                this.buildErrorTraceMessage("Echec d'ajout de produit. Référence de produit inexistante");
                return null;
            }
            OTOrderDetail = this.findFamilleInTOrderDetail(lgORDERID, OTFamille.getLgFAMILLEID());
            TGrossiste OTGrossiste = this.SearchGrossiste(lg_GROSSISTE_ID);
            if (OTGrossiste == null) {
                this.buildErrorTraceMessage("Echec d'ajout du produit. Grossiste inexistant");
                return null;
            }
            try {
                OTFamilleGrossiste = new familleGrossisteManagement(this.getOdataManager())
                        .findFamilleGrossiste(OTFamille.getLgFAMILLEID(), lg_GROSSISTE_ID);
            } catch (Exception e) {
            }
            this.getOdataManager().BeginTransaction();
            if (OTOrderDetail == null) {
                TOrder Oder = this.getOdataManager().getEm().find(TOrder.class, lgORDERID);
                OTOrderDetail = new TOrderDetail();
                OTOrderDetail.setLgORDERDETAILID(this.getKey().getComplexId());
                OTOrderDetail.setLgORDERID(Oder);
                OTOrderDetail.setIntNUMBER(int_NUMBER);
                OTOrderDetail.setIntQTEREPGROSSISTE(int_NUMBER);
                OTOrderDetail.setIntQTEMANQUANT(int_NUMBER);
                OTOrderDetail.setIntPAFDETAIL(((OTFamilleGrossiste != null && OTFamilleGrossiste.getIntPAF() != null
                        && OTFamilleGrossiste.getIntPAF() > 0) ? OTFamilleGrossiste.getIntPAF()
                                : OTFamille.getIntPAF()));
                OTOrderDetail.setIntPRICEDETAIL(((OTFamilleGrossiste != null && OTFamilleGrossiste.getIntPRICE() != null
                        && OTFamilleGrossiste.getIntPRICE() > 0) ? OTFamilleGrossiste.getIntPRICE()
                                : OTFamille.getIntPRICE()));
                OTOrderDetail.setIntPRICE(OTOrderDetail.getIntPAFDETAIL() * int_NUMBER);
                OTOrderDetail.setLgFAMILLEID(OTFamille);
                OTOrderDetail.setLgGROSSISTEID(OTGrossiste);
                OTOrderDetail.setStrSTATUT(commonparameter.statut_is_Process);
                OTOrderDetail.setDtCREATED(new Date());
                OTOrderDetail.setDtUPDATED(OTOrderDetail.getDtCREATED());
                OTOrderDetail.setIntORERSTATUS((short) 2);
                OTOrderDetail.setPrixAchat(OTOrderDetail.getIntPAFDETAIL());
                // OTOrderDetail.setPrixUnitaire(OTOrderDetail.getIntPRICEDETAIL());
                this.getOdataManager().getEm().persist(OTOrderDetail);
                OTFamille.setBCODEINDICATEUR((short) 1);
                this.getOdataManager().getEm().merge(OTFamille);
            } else {
                OTOrderDetail.setIntNUMBER(OTOrderDetail.getIntNUMBER() + int_NUMBER);
                OTOrderDetail.setIntQTEREPGROSSISTE(OTOrderDetail.getIntNUMBER());
                OTOrderDetail.setIntQTEMANQUANT(OTOrderDetail.getIntNUMBER());
                OTOrderDetail.setIntPRICE(OTOrderDetail.getIntPAFDETAIL() * OTOrderDetail.getIntNUMBER());
                OTOrderDetail.setDtUPDATED(new Date());
                this.getOdataManager().getEm().merge(OTOrderDetail);
            }
            this.getOdataManager().CloseTransaction();
            this.buildSuccesTraceMessage(this.getOTranslate().getValue("SUCCES"));
            JSONObject details = getTotalAchatVente(lgORDERID);
            json.put("LGORDERID", lgORDERID).put("PRIX_ACHAT_TOTAL", details.get("PRIX_ACHAT_TOTAL"))
                    .put("PRIX_VENTE_TOTAL", details.get("PRIX_VENTE_TOTAL")).put("COUNT", details.getInt("COUNT"));
        } catch (Exception e) {
            e.printStackTrace();
            this.buildErrorTraceMessage("Echec d'ajout de l'article à la commande");
        }
        return json;
    }

    private List<Predicate> predicats(CriteriaBuilder cb, Root<TOrderDetail> root, String lg_ORDER_ID, String filtre,
            String searchValue) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get(TOrderDetail_.lgORDERID).get(TOrder_.lgORDERID), lg_ORDER_ID));
        if (!StringUtils.isEmpty(searchValue)) {
            predicates.add(cb.or(cb.like(root.get(TOrderDetail_.lgFAMILLEID).get(TFamille_.intCIP), searchValue + "%"),
                    cb.like(root.get(TOrderDetail_.lgFAMILLEID).get(TFamille_.strNAME), searchValue + "%")));
        }
        if (!StringUtils.isEmpty(filtre) && !DateConverter.ALL.equals(filtre)) {
            predicates.add(cb.notEqual(root.get(TOrderDetail_.intPRICEDETAIL),
                    root.get(TOrderDetail_.lgFAMILLEID).get(TFamille_.intPRICE)));
        }

        return predicates;
    }

    public List<TOrderDetail> getOrderDetail(String searchValue, String lg_ORDER_ID, String filtre, int start,
            int limit) {
        try {
            EntityManager em = this.getOdataManager().getEm();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TOrderDetail> cq = cb.createQuery(TOrderDetail.class);
            Root<TOrderDetail> root = cq.from(TOrderDetail.class);
            cq.select(root).orderBy(cb.desc(root.get(TOrderDetail_.dtUPDATED)),
                    cb.asc(root.get(TOrderDetail_.lgFAMILLEID).get(TFamille_.strNAME)));
            List<Predicate> predicates = predicats(cb, root, lg_ORDER_ID, filtre, searchValue);
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            TypedQuery<TOrderDetail> q = em.createQuery(cq);
            q.setFirstResult(start);
            q.setMaxResults(limit);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    public long getOrderDetailCount(String searchValue, String lg_ORDER_ID, String filtre) {
        try {
            EntityManager em = this.getOdataManager().getEm();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TOrderDetail> root = cq.from(TOrderDetail.class);
            cq.select(cb.count(root));
            List<Predicate> predicates = predicats(cb, root, lg_ORDER_ID, filtre, searchValue);
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            TypedQuery<Long> q = em.createQuery(cq);
            return q.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return 0;
        }
    }
}
