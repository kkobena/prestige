package rest;

import bll.commandeManagement.orderManagement;
import bll.configManagement.familleGrossisteManagement;
import bll.teller.tellerManagement;
import dal.TFamilleGrossiste;
import dal.TFamilleStock;
import dal.TOrderDetail;
import dal.TUser;
import dal.dataManager;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import toolkits.parameters.commonparameter;
import toolkits.utils.date;
import util.DateConverter;

/**
 * Migration REST de webservices/commandemanagement/orderdetail/ws_data.jsp (details d'une commande, aussi charge par
 * les ecrans Generer facture / bordereaux / differes) : MEMES methodes metier bll.commandeManagement.orderManagement,
 * memes cles JSON. Contrairement a la JSP historique, l'EntityManager est correctement ferme apres chaque appel.
 */
@Path("v1/order-detail")
@Produces("application/json")
@Consumes("application/json")
public class OrderDetailRessource {

    private static final Logger LOG = Logger.getLogger(OrderDetailRessource.class.getName());

    @Inject
    private HttpServletRequest servletRequest;

    @GET
    @Path("list")
    public Response list(@DefaultValue("") @QueryParam("lg_ORDER_ID") String lgOrderIdParam,
            @DefaultValue("") @QueryParam("search_value") String searchValue,
            @DefaultValue("") @QueryParam("str_STATUT") String strStatutParam,
            @DefaultValue("") @QueryParam("filtre") String filtreParam,
            @DefaultValue("0") @QueryParam("start") int start, @DefaultValue("0") @QueryParam("limit") int limit) {
        TUser sessionUser = (TUser) servletRequest.getSession().getAttribute(commonparameter.AIRTIME_USER);
        dataManager odm = new dataManager();
        try {
            String lgOrderId = StringUtils.isNotEmpty(lgOrderIdParam) ? lgOrderIdParam : "%%";
            String filtre = StringUtils.isNotEmpty(filtreParam) ? filtreParam : DateConverter.ALL;
            date key = new date();

            odm.initEntityManager();
            tellerManagement otm = new tellerManagement(odm, sessionUser);
            orderManagement oom = new orderManagement(odm, sessionUser);
            familleGrossisteManagement ofgm = new familleGrossisteManagement(odm);
            long total = oom.getOrderDetailCount(searchValue, lgOrderId, filtre);
            List<TOrderDetail> lst = oom.getOrderDetail(searchValue, lgOrderId, filtre, start, limit);

            JSONArray arrayObj = new JSONArray();
            for (TOrderDetail o : lst) {
                try {
                    odm.getEm().refresh(o);
                } catch (Exception ignore) {
                }
                JSONObject json = new JSONObject();
                TFamilleStock stock = otm.geProductItemStock(o.getLgFAMILLEID().getLgFAMILLEID());
                TFamilleGrossiste fg = ofgm.findGrossiste(o.getLgFAMILLEID(), o.getLgORDERID().getLgGROSSISTEID());
                json.put("lg_ORDERDETAIL_ID", o.getLgORDERDETAILID());
                json.put("lg_ORDER_ID", o.getLgORDERID().getLgORDERID());
                json.put("int_NUMBER", o.getIntNUMBER());
                json.put("int_PRICE", o.getIntPRICE());
                json.put("int_QTE_MANQUANT", o.getIntQTEMANQUANT());
                json.put("lg_GROSSISTE_ID", o.getLgORDERID().getLgGROSSISTEID().getLgGROSSISTEID());
                json.put("lg_GROSSISTE_LIBELLE", o.getLgORDERID().getLgGROSSISTEID().getStrLIBELLE());
                json.put("bool_BL", o.getBoolBL());
                json.put("lg_FAMILLE_ID", o.getLgFAMILLEID().getLgFAMILLEID());
                json.put("lg_FAMILLE_NAME", o.getLgFAMILLEID().getStrNAME());
                json.put("lg_FAMILLE_CIP", (fg != null ? fg.getStrCODEARTICLE() : o.getLgFAMILLEID().getIntCIP()));
                json.put("lg_FAMILLE_PRIX_VENTE", o.getIntPRICEDETAIL());
                json.put("lg_FAMILLE_PRIX_ACHAT", o.getLgFAMILLEID().getIntPAF());
                json.put("int_PAF", o.getIntPAFDETAIL());
                json.put("int_PRICE_MACHINE", o.getLgFAMILLEID().getIntPRICE());
                json.put("int_PRIX_REFERENCE", o.getLgFAMILLEID().getIntPRICETIPS());
                json.put("int_QTE_LIVRE", o.getIntNUMBER() - o.getIntQTEMANQUANT());
                json.put("int_QTE_REP_GROSSISTE", o.getIntQTEREPGROSSISTE());
                json.put("prixDiff", o.getIntPRICEDETAIL().compareTo(o.getLgFAMILLEID().getIntPRICE()) != 0);
                json.put("int_SEUIL", o.getLgFAMILLEID().getIntSEUILMIN());

                int qteReassort = 0;
                try {
                    qteReassort = stock.getIntNUMBERAVAILABLE() - o.getLgFAMILLEID().getIntSEUILMIN();
                    if (qteReassort < 0) {
                        qteReassort = -1 * qteReassort;
                    } else {
                        qteReassort = 0;
                    }
                } catch (Exception ignore) {
                }
                json.put("int_QTE_REASSORT", qteReassort);
                try {
                    json.put("lg_FAMILLE_QTE_STOCK", stock.getIntNUMBERAVAILABLE());
                } catch (Exception e) {
                    json.put("lg_FAMILLE_QTE_STOCK", 0);
                }
                json.put("str_CODE_ARTICLE", fg != null ? fg.getStrCODEARTICLE() : "");
                json.put("str_STATUT", o.getStrSTATUT());
                Date dtCreated = o.getDtCREATED();
                if (dtCreated != null) {
                    json.put("dt_CREATED", key.DateToString(dtCreated, key.formatterOrange));
                }
                Date dtUpdated = o.getDtUPDATED();
                if (dtUpdated != null) {
                    json.put("dt_UPDATED", key.DateToString(dtUpdated, key.formatterOrange));
                }
                arrayObj.put(json);
            }
            String result = "{\"total\":\"" + total + " \",\"results\":" + arrayObj.toString() + "}";
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "details de commande", e);
            return Response.ok().entity(new JSONObject().put("total", 0).put("results", new JSONArray()).toString())
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }
}
