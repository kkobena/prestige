/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.Params;
import commonTasks.dto.PerimesDTO;
import dal.TEmplacement;
import dal.TEventLog;
import dal.TFamille;
import dal.TFamilleStock;
import dal.TUser;
import dal.TWarehouse;
import dal.enumeration.TypeLog;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.json.JSONArray;
import org.json.JSONObject;
import rest.service.GestionPerimesService;
import rest.service.SuggestionService;
import toolkits.parameters.commonparameter;
import util.DateConverter;

/**
 *
 * @author koben
 */
@Stateless
public class GestionPerimesServiceImpl implements GestionPerimesService {

    private static final Logger LOG = Logger.getLogger(GestionPerimesServiceImpl.class.getName());

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @EJB
    private SuggestionService suggestionService;

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public JSONObject addPerime(String lg_FAMILLE_ID, Integer int_NUMBER, String int_NUM_LOT, String dt_peremption,
            TUser user) {
        List<TWarehouse> list = checkIsExist(lg_FAMILLE_ID);
        String empl = user.getLgEMPLACEMENTID().getLgEMPLACEMENTID();
        if (list.isEmpty()) {
            return update(lg_FAMILLE_ID, int_NUMBER, empl, dt_peremption, user, int_NUM_LOT);
        } else {

            TWarehouse tw = getTWarehouse(lg_FAMILLE_ID, int_NUM_LOT);
            if (tw != null) {
                return updateStock(tw, int_NUMBER, empl);
            } else {
                return updateFamillyStock(user, lg_FAMILLE_ID, int_NUMBER, int_NUM_LOT, dt_peremption, list, empl);
            }
        }
    }

    private List<TWarehouse> checkIsExist(String lg_FAMILLE_ID) {

        List<TWarehouse> list = getEntityManager().createQuery(
                "SELECT o FROM TWarehouse o WHERE  FUNCTION('DATE', o.dtCREATED)= FUNCTION('DATE',?1) AND o.lgFAMILLEID.lgFAMILLEID=?2  AND o.strSTATUT=?3")
                .setParameter(1, new Date()).setParameter(2, lg_FAMILLE_ID)
                .setParameter(3, commonparameter.statut_pending).getResultList();
        return list;

    }

    private TFamilleStock getTProductItemStock(String lg_FAMILLE_ID, String lg_EMPLACEMENT_ID) {

        try {
            TypedQuery<TFamilleStock> q = getEntityManager().createQuery(
                    "SELECT t FROM TFamilleStock t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?2 AND t.strSTATUT='enable'",
                    TFamilleStock.class).setParameter(1, lg_FAMILLE_ID).setParameter(2, lg_EMPLACEMENT_ID)
                    .setFirstResult(0).setMaxResults(1);
            return q.getSingleResult();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "getTProductItemStock {0}", e);
            return null;
        }

    }

    public JSONObject updateStock(TWarehouse OWarehouse, Integer int_NUMBER, String lg_EMPLACEMENT_ID) {

        JSONObject json = new JSONObject();

        try {

            TFamilleStock OTFamilleStock = getTProductItemStock(OWarehouse.getLgFAMILLEID().getLgFAMILLEID(),
                    lg_EMPLACEMENT_ID);
            if (OTFamilleStock.getIntNUMBERAVAILABLE() < (int_NUMBER + OWarehouse.getIntNUMBER())) {
                json.put("success", false);
                json.put("message",
                        "Cette ligne existe déjà avec quantité <span style='font-weight:900;'>"
                                + OWarehouse.getIntNUMBER()
                                + "</span>\n La somme des différentes quantités es supérieure à celle du stock");
            } else {
                OWarehouse.setIntNUMBER(int_NUMBER + OWarehouse.getIntNUMBER());
                OWarehouse.setStockFinal(OTFamilleStock.getIntNUMBERAVAILABLE() - OWarehouse.getIntNUMBER());
                OWarehouse.setDtUPDATED(new Date());
                getEntityManager().merge(OWarehouse);
                json.put("success", true);

            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "getTProductItemStock {0}", e);
        }
        return json;

    }

    public JSONObject updateFamillyStock(TUser lgUSERID, String lg_FAMILLE_ID, Integer int_NUMBER, String int_NUM_LOT,
            String dt_peremption, List<TWarehouse> list, String emplacement) {
        JSONObject json = new JSONObject();

        int totalCount = list.stream().map((tWarehouse) -> tWarehouse.getIntNUMBER()).reduce(0, Integer::sum);

        try {
            TFamille OTProductItem = getEntityManager().find(TFamille.class, lg_FAMILLE_ID);
            TFamilleStock OTFamilleStock = getTProductItemStock(lg_FAMILLE_ID, emplacement);
            if (OTFamilleStock.getIntNUMBERAVAILABLE() < (int_NUMBER + totalCount)) {
                json.put("success", false);
                json.put("message",
                        "La quantité correspondant à ce produit est supérieure à celle du stock\n Quantité Stock: <span style='font-weight:900'>"
                                + OTFamilleStock.getIntNUMBERAVAILABLE()
                                + "</span> < Quantité à retirer :<span style='font-weight:900'>"
                                + (int_NUMBER + totalCount) + "</span>");
            } else {
                TWarehouse OTWarehouse = new TWarehouse();
                OTWarehouse.setLgWAREHOUSEID(UUID.randomUUID().toString());
                OTWarehouse.setLgUSERID(lgUSERID);
                OTWarehouse.setLgFAMILLEID(OTProductItem);
                OTWarehouse.setIntNUMBER(int_NUMBER);
                OTWarehouse.setDtPEREMPTION(java.sql.Date.valueOf(dt_peremption));
                OTWarehouse.setDtCREATED(new Date());
                OTWarehouse.setDtUPDATED(new Date());
                OTWarehouse.setIntNUMLOT(int_NUM_LOT);
                OTWarehouse.setStrSTATUT(commonparameter.statut_pending);
                OTWarehouse.setStockInitial(OTFamilleStock.getIntNUMBERAVAILABLE());
                OTWarehouse.setStockFinal(OTFamilleStock.getIntNUMBERAVAILABLE() - OTWarehouse.getIntNUMBER());
                getEntityManager().persist(OTWarehouse);
                json.put("success", true);

            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "getTProductItemStock {0}", e);
        }
        return json;

    }

    private JSONObject update(String lg_FAMILLE_ID, int int_NUMBER, String empl, String dt_peremption, TUser user,
            String int_NUM_LOT) {
        JSONObject json = new JSONObject();
        try {
            TFamille OTProductItem = getEntityManager().find(TFamille.class, lg_FAMILLE_ID);
            TFamilleStock OTFamilleStock = getTProductItemStock(lg_FAMILLE_ID, empl);
            if (OTFamilleStock.getIntNUMBERAVAILABLE() < int_NUMBER) {
                json.put("success", false);
                json.put("message",
                        "La quantité  à retirer est supérieure à celle en stock<br> Vous pouvez faire un ajustement du stock <br> Quantité en stock <span style='color:red;font-weight:900;'>"
                                + OTFamilleStock.getIntNUMBERAVAILABLE() + "</span>");
            } else {
                TWarehouse OTWarehouse = new TWarehouse();
                OTWarehouse.setLgWAREHOUSEID(UUID.randomUUID().toString());
                OTWarehouse.setLgUSERID(user);
                OTWarehouse.setLgFAMILLEID(OTProductItem);
                OTWarehouse.setIntNUMBER(int_NUMBER);
                OTWarehouse.setDtPEREMPTION(java.sql.Date.valueOf(dt_peremption));
                OTWarehouse.setDtCREATED(new Date());
                OTWarehouse.setDtUPDATED(new Date());
                OTWarehouse.setIntNUMLOT(int_NUM_LOT);
                OTWarehouse.setStrSTATUT(commonparameter.statut_pending);
                OTWarehouse.setStockInitial(OTFamilleStock.getIntNUMBERAVAILABLE());
                OTWarehouse.setStockFinal(OTFamilleStock.getIntNUMBERAVAILABLE() - int_NUMBER);
                getEntityManager().persist(OTWarehouse);
                json.put("success", true);

            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "getTProductItemStock {0}", e);
            json.put("success", false);
        }
        return json;
    }

    private TWarehouse getTWarehouse(String lg_FAMILLE_ID, String lot) {

        try {
            TypedQuery<TWarehouse> q = getEntityManager().createQuery(
                    "SELECT o FROM TWarehouse o WHERE  FUNCTION('DATE', o.dtCREATED)= FUNCTION('DATE',?1) AND o.lgFAMILLEID.lgFAMILLEID=?2  AND o.strSTATUT=?3 AND o.intNUMLOT=?4 ",
                    TWarehouse.class).setParameter(1, new Date()).setParameter(2, lg_FAMILLE_ID)
                    .setParameter(3, commonparameter.statut_pending).setParameter(4, lot);
            return q.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "getTProductItemStock {0}", e);
            return null;
        }

    }

    @Override
    public JSONObject updatePerime(Params params) {
        JSONObject json = new JSONObject();
        try {

            TUser user = params.getOperateur();

            TWarehouse OWarehouse = getEntityManager().find(TWarehouse.class, params.getRef());
            TFamilleStock OTFamilleStock = getTProductItemStock(OWarehouse.getLgFAMILLEID().getLgFAMILLEID(),
                    user.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
            if (OTFamilleStock.getIntNUMBERAVAILABLE() < params.getValue()) {
                json.put("success", false);
                json.put("message",
                        "La quantité  à retirer est supérieure à celle en stock<br> Vous pouvez faire un ajustement du stock <br> Quantité en stock <span style='color:red;font-weight:900;'>"
                                + OTFamilleStock.getIntNUMBERAVAILABLE() + "</span>");
            } else {

                OWarehouse.setLgUSERID(user);

                OWarehouse.setIntNUMBER(params.getValue());

                OWarehouse.setDtUPDATED(new Date());
                OWarehouse.setIntNUMLOT(params.getRefTwo());

                getEntityManager().merge(OWarehouse);
                json.put("success", true);

            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "updateStock {0}", e);
        }
        return json;
    }

    @Override
    public void removePerime(String id) {
        getEntityManager().remove(getEntityManager().find(TWarehouse.class, id));
    }

    @Override
    public JSONObject completePerimes(String id, TUser user) {

        JSONObject json = new JSONObject();

        try {
            List<TWarehouse> list;
            TWarehouse OTWarehouse = getEntityManager().find(TWarehouse.class, id);
            list = getEntityManager().createQuery(
                    "SELECT o FROM TWarehouse o WHERE  FUNCTION('DATE', o.dtCREATED)= FUNCTION('DATE',?1) AND o.strSTATUT=?2")
                    .setParameter(1, OTWarehouse.getDtCREATED()).setParameter(2, commonparameter.statut_pending)
                    .getResultList();
            int i = 0;
            MvtProduitObselete mvtProduit = new MvtProduitObselete();
            TEmplacement emplacement = user.getLgEMPLACEMENTID();

            for (TWarehouse tWarehouse : list) {
                // TFamille OTProductItem = this.getOdataManager().getEm().find(TFamille.class,
                // tWarehouse.getLgFAMILLEID().getLgFAMILLEID());
                TFamille famille = tWarehouse.getLgFAMILLEID();
                TFamilleStock OTFamilleStock = getTProductItemStock(tWarehouse.getLgFAMILLEID().getLgFAMILLEID(),
                        emplacement.getLgEMPLACEMENTID());
                Integer stockInit = OTFamilleStock.getIntNUMBERAVAILABLE();
                if (OTFamilleStock.getIntNUMBERAVAILABLE() > 0) {
                    OTFamilleStock
                            .setIntNUMBERAVAILABLE(OTFamilleStock.getIntNUMBERAVAILABLE() - tWarehouse.getIntNUMBER());
                    OTFamilleStock.setIntNUMBER(OTFamilleStock.getIntNUMBERAVAILABLE());
                    OTFamilleStock.setDtUPDATED(new Date());
                    tWarehouse.setIntNUMBERDELETE(tWarehouse.getIntNUMBER());
                    tWarehouse.setDtUPDATED(new Date());
                    tWarehouse.setStrSTATUT(commonparameter.statut_delete);
                    getEntityManager().merge(OTFamilleStock);
                    getEntityManager().merge(tWarehouse);
                    mvtProduit.saveMvtProduit(famille.getIntPRICE(), tWarehouse.getLgWAREHOUSEID(),
                            DateConverter.PERIME, famille, user, emplacement, tWarehouse.getIntNUMBER(), stockInit,
                            stockInit - tWarehouse.getIntNUMBER(), 0, getEntityManager());
                    String desc = "Saisis de périmé du  produit " + famille.getIntCIP() + " " + famille.getStrNAME()
                            + " stock initial= " + stockInit + " qté saisie= " + tWarehouse.getIntNUMBER()
                            + " qté après saisie = " + OTFamilleStock.getIntNUMBERAVAILABLE()
                            + " . Saisie effectuée par " + user.getStrFIRSTNAME() + " " + user.getStrLASTNAME();
                    updateItem(user, famille.getIntCIP(), desc, TypeLog.SAISIS_PERIMES, famille);
                    suggestionService.makeSuggestionAuto(OTFamilleStock, famille);
                    i++;
                }
            }

            json.put("success", true);
            json.put("NB", i);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "updateStock {0}", e);
            json.put("success", false);

        }
        return json;
    }

    private void updateItem(TUser user, String ref, String desc, TypeLog typeLog, Object T) {
        TEventLog eventLog = new TEventLog(UUID.randomUUID().toString());
        eventLog.setLgUSERID(user);
        eventLog.setDtCREATED(new Date());
        eventLog.setDtUPDATED(new Date());
        eventLog.setStrCREATEDBY(user.getStrLOGIN());
        eventLog.setStrSTATUT(commonparameter.statut_enable);
        eventLog.setStrTABLECONCERN(T.getClass().getName());
        eventLog.setTypeLog(typeLog);
        eventLog.setStrDESCRIPTION(desc + " référence [" + ref + " ]");
        eventLog.setStrTYPELOG(ref);
        getEntityManager().persist(eventLog);
    }

    @Override
    public JSONObject getPerimesSaisiEnCours(int start, int limit) {
        long count = fetchPerimesSaisiEnCoursCount();
        JSONObject json = new JSONObject();
        json.put("data", new JSONArray(fetchPerimesSaisiEnCours(start, limit)));
        json.put("total", count);
        return json;
    }

    private List<PerimesDTO> fetchPerimesSaisiEnCours(int start, int limit) {
        List<PerimesDTO> datas = new ArrayList<>();
        try {
            String qry = "SELECT  `t_warehouse`.`lg_FAMILLE_ID`, `t_warehouse`.`int_NUM_LOT`, `t_warehouse`.`int_NUMBER`, DATE_FORMAT(`t_warehouse`.`dt_CREATED`,'%d/%m/%Y %H:%i') AS DATEENTREE, `t_famille`.`int_CIP`,";
            qry += "  DATE_FORMAT(`t_warehouse`.`dt_PEREMPTION`,'%d/%m/%Y') AS  dtPEREMPTION,`t_famille`.`str_NAME`, `t_warehouse`.`lg_WAREHOUSE_ID`,`t_warehouse`.`stock_initial`,`t_warehouse`.`stock_final` FROM  `t_famille`  INNER JOIN `t_warehouse` ON (`t_famille`.`lg_FAMILLE_ID` = `t_warehouse`.`lg_FAMILLE_ID`)";
            qry += " WHERE  DATE(`t_warehouse`.`dt_CREATED`) = CURDATE() AND  `t_warehouse`.`str_STATUT`='"
                    + commonparameter.statut_pending + "'   ORDER BY `t_warehouse`.`dt_CREATED` DESC LIMIT " + start
                    + ", " + limit + "";
            List<Object[]> list = this.getEntityManager().createNativeQuery(qry).getResultList();

            for (Object[] objects : list) {
                PerimesDTO perimesDTO = new PerimesDTO();
                perimesDTO.setProduitId(objects[0] + "");
                perimesDTO.setLot(objects[1] + "");
                perimesDTO.setQuantity(Integer.valueOf(objects[2] + ""));
                perimesDTO.setDatePeremption(objects[5] + "");
                perimesDTO.setProduitCip(objects[4] + "");
                perimesDTO.setProduitLibelle(objects[6] + "");
                perimesDTO.setDateEntree(objects[3] + "");
                perimesDTO.setId(objects[7] + "");
                perimesDTO.setStockInitial(Integer.valueOf(objects[8] + ""));
                perimesDTO.setStockFinal(Integer.valueOf(objects[9] + ""));
                datas.add(perimesDTO);

            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "fetchPerimesSaisiEnCours {0}", e);
        }
        return datas;
    }

    private long fetchPerimesSaisiEnCoursCount() {

        long count = 0;

        try {

            String qry = "SELECT COUNT( `t_warehouse`.`lg_FAMILLE_ID`) FROM  `t_famille`  INNER JOIN `t_warehouse` ON (`t_famille`.`lg_FAMILLE_ID` = `t_warehouse`.`lg_FAMILLE_ID`) WHERE  DATE(`t_warehouse`.`dt_CREATED`) = CURDATE() AND  `t_warehouse`.`str_STATUT`='"
                    + commonparameter.statut_pending + "'";

            Object object = this.getEntityManager().createNativeQuery(qry).getSingleResult();
            count = Long.valueOf(object + "");

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "fetchPerimesSaisiEnCoursCount {0}", e);
        }

        return count;
    }
}
