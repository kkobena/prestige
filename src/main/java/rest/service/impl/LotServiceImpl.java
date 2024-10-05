/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import org.json.JSONObject;
import rest.service.LogService;
import rest.service.LotService;
import rest.service.NotificationService;
import rest.service.TransactionService;
import util.FunctionUtils;

/**
 *
 * @author Hermann N'ZI
 */
@Stateless
public class LotServiceImpl implements LotService {

    private static final Logger LOG = Logger.getLogger(LotServiceImpl.class.getName());
    private static final String MVT_QUERY = "SELECT f.int_CIP, f.str_NAME, f.int_PAF, f.int_PRICE,g.str_LIBELLE, l.int_NUM_LOT, l.str_REF_LIVRAISON,l.int_NUMBER, l.int_NUMBER_GRATUIT,\n"
            + "l.dt_SORTIE_USINE, l.dt_PEREMPTION\n" + "FROM t_lot l, t_famille f, t_grossiste g\n"
            + "WHERE l.lg_FAMILLE_ID = f.lg_FAMILLE_ID AND f.lg_GROSSISTE_ID = g.lg_GROSSISTE_ID";

    private static final String MVT_QUERY_COUNT = "SELECT COUNT(l.lg_LOT_ID) FROM t_lot l";

    @EJB
    private LogService logService;
    @EJB
    private LotService lotService;
    @EJB
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public JSONObject getAllLots(String dtStart, String dtEnd, int limit, int start) {
        return FunctionUtils.returnData(this.getAllLots(dtStart, dtEnd, limit, start));
    }

    public List<rest.service.dto.LotDTO> getAllLots(String dtStart, String dtEnd, int limit, int start, boolean all) {
        List<rest.service.dto.LotDTO> list = new ArrayList<>();
        fetchAllLots(dtStart, dtEnd, limit, start, all).forEach(e -> list.add(buildLot(e)));
        return list;

    }

    private List<Tuple> fetchAllLots(String dtStart, String dtEnd, int limit, int start, boolean all) {
        String sql = (MVT_QUERY);
        LOG.log(Level.INFO, "sql---  getAllLots {0}", sql);
        try {
            Query query = em.createNativeQuery(sql, Tuple.class).setParameter(1, java.sql.Date.valueOf(dtStart))
                    .setParameter(2, java.sql.Date.valueOf(dtEnd));
            if (!all) {
                query.setFirstResult(start);
                query.setMaxResults(limit);
            }
            return query.getResultList();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new ArrayList<>();
        }
    }

    private rest.service.dto.LotDTO buildLot(Tuple t) {

        String LotId = t.get("lg_LOT_ID", String.class);

        return rest.service.dto.LotDTO.builder().lg_FAMILLE_ID(Integer.MIN_VALUE)
                .int_CIP(t.get("int_CIP", Integer.class)).str_NAME(t.get("str_NAME", String.class))
                .int_PRICE(t.get("int_PRICE", Integer.class)).int_PAF(t.get("int_PAF", Integer.class))
                .int_NUM_LOT(t.get("int_NUM_LOT", String.class)).int_NUMBER(t.get("int_NUMBER", Integer.class))
                .dt_CREATED(t.get("dt_CREATED", String.class)).dt_UPDATED(t.get("dt_UPDATED", String.class))
                .int_PAF(t.get("int_PAF", Integer.class)).lg_GROSSISTE_ID(t.get("lg_GROSSISTE_ID", String.class))
                .str_REF_LIVRAISON(t.get("str_REF_LIVRAISON", String.class))
                .dt_SORTIE_USINE(t.get("dt_SORTIE_USINE", String.class))
                .int_NUMBER_GRATUIT(t.get("int_NUMBER_GRATUIT", Integer.class))
                .dt_PEREMPTION(t.get("dt_PEREMPTION", String.class))
                .int_QTY_VENDUE(t.get("int_QTY_VENDUE", Integer.class)).lg_USER_ID(t.get("lg_USER_ID", String.class))
                .build();
        /*
         * .heureOpreration(t.get("heureOpreration", String.class)) .dateOpreration(t.get("dateOpreration",
         * java.sql.Date.class).toLocalDate() .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).build();
         */
    }

}
