package rest;

import bll.preenregistrement.Preenregistrement;
import dal.TPreenregistrementDetail;
import dal.TUser;
import dal.dataManager;
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
import toolkits.utils.jdom;

/**
 * Migration REST de webservices/sm_user/detailsvente/ws_data.jsp (detail des produits d'une vente) : MEMES methodes
 * metier bll.preenregistrement.Preenregistrement, memes cles JSON. Utilise par la fenetre "Detail des produits" de la
 * liste des bons par organismes ; la JSP historique reste en place pour les autres ecrans qui la consomment encore.
 */
@Path("v1/details-vente")
@Produces("application/json")
@Consumes("application/json")
public class DetailsVenteRessource {

    private static final Logger LOG = Logger.getLogger(DetailsVenteRessource.class.getName());

    @Inject
    private HttpServletRequest servletRequest;

    @GET
    @Path("list")
    public Response list(@DefaultValue("") @QueryParam("lg_PREENREGISTREMENT_ID") String preenregistrementId,
            @DefaultValue("") @QueryParam("str_STATUT") String statut,
            @DefaultValue("") @QueryParam("search_value") String searchValue,
            @DefaultValue("0") @QueryParam("start") int start, @DefaultValue("0") @QueryParam("limit") int limitParam) {
        TUser sessionUser = (TUser) servletRequest.getSession().getAttribute(commonparameter.AIRTIME_USER);
        if (sessionUser == null) {
            return Response.ok().entity(new JSONObject().put("total", 0).put("results", new JSONArray())
                    .put("errors", util.Constant.DECONNECTED_MESSAGE).toString()).build();
        }
        dataManager odm = new dataManager();
        try {
            int limit = limitParam > 0 ? limitParam : jdom.int_size_pagination;
            odm.initEntityManager();
            Preenregistrement op = new Preenregistrement(odm, sessionUser);
            boolean boolUpdatePrice = Boolean.parseBoolean(
                    String.valueOf(servletRequest.getSession().getAttribute(commonparameter.UPDATE_PRICE)));
            List<TPreenregistrementDetail> lst = op.getListePreenregistrementDetail(false, searchValue,
                    preenregistrementId, statut, start, limit);
            int total = op.getPreenregistrementDetailCount(searchValue, preenregistrementId, statut);

            JSONArray arrayObj = new JSONArray();
            for (TPreenregistrementDetail detail : lst) {
                JSONObject json = new JSONObject();
                json.put("lg_PREENREGISTREMENT_DETAIL_ID", detail.getLgPREENREGISTREMENTDETAILID());
                json.put("lg_PREENREGISTREMENT_ID", detail.getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID());
                json.put("str_REF", detail.getLgPREENREGISTREMENTID().getStrREF());
                json.put("lg_FAMILLE_ID", detail.getLgFAMILLEID().getLgFAMILLEID());
                json.put("bl_PROMOTED", detail.getLgFAMILLEID().getBlPROMOTED());
                json.put("int_FAMILLE_PRICE", detail.getIntPRICEUNITAIR());
                json.put("int_FREE_PACK_NUMBER", detail.getIntFREEPACKNUMBER());
                json.put("str_FAMILLE_NAME", detail.getLgFAMILLEID().getStrDESCRIPTION());
                json.put("lg_USER_ID", detail.getLgPREENREGISTREMENTID().getLgUSERID().getStrFIRSTNAME());
                json.put("int_QUANTITY", detail.getIntQUANTITY());
                json.put("int_QUANTITY_SERVED",
                        (detail.getLgPREENREGISTREMENTID().getStrSTATUT().equals(commonparameter.statut_is_Closed)
                                ? detail.getIntQUANTITYSERVED() + detail.getIntAVOIR()
                                : detail.getIntQUANTITYSERVED()));
                json.put("int_PRICE_DETAIL", detail.getIntPRICE());
                json.put("b_IS_AVOIR", detail.getBISAVOIR());
                json.put("int_CIP", detail.getLgFAMILLEID().getIntCIP());
                json.put("bool_UPDATE_PRICE", boolUpdatePrice);
                json.put("int_AVOIR", detail.getIntAVOIR());
                json.put("int_EAN13", detail.getLgFAMILLEID().getIntEAN13());
                json.put("dt_CREATED", date.DateToString(detail.getDtCREATED(), date.formatterMysql));
                json.put("str_STATUT", detail.getStrSTATUT());
                arrayObj.put(json);
            }
            return Response.ok().entity(new JSONObject().put("total", total).put("results", arrayObj).toString())
                    .build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "details vente list", e);
            return Response.ok().entity(new JSONObject().put("total", 0).put("results", new JSONArray()).toString())
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }
}
