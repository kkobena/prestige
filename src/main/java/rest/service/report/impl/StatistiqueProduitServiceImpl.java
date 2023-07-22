package rest.service.report.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import rest.service.dto.StatistiqueProduitAnnuelleDTO;
import rest.service.report.StatistiqueProduitService;
import util.CommonUtils;
import util.DateConverter;
import util.FunctionUtils;

/**
 *
 * @author koben
 */
@Stateless
public class StatistiqueProduitServiceImpl implements StatistiqueProduitService {

    private static final Logger LOG = Logger.getLogger(StatistiqueProduitServiceImpl.class.getName());
    private static final String STATUT = "is_Closed";

    private static final String YEAR_SQL_QUERY = " SELECT f.`lg_FAMILLE_ID` AS id, f.`int_CIP` codeCip,f.`str_NAME` as libelle, SUM(CASE WHEN MONTH(p.`dt_UPDATED`)=1 THEN d.`int_QUANTITY` ELSE 0 END ) AS janvier\n"
            + ", SUM(CASE WHEN MONTH(p.`dt_UPDATED`)=2 THEN d.`int_QUANTITY` ELSE 0 END ) AS fevrier,"
            + " SUM(CASE WHEN MONTH(p.`dt_UPDATED`)=3 THEN d.`int_QUANTITY` ELSE 0 END ) AS mars "
            + ", SUM(CASE WHEN MONTH(p.`dt_UPDATED`)=4 THEN d.`int_QUANTITY` ELSE 0 END ) AS avril"
            + ", SUM(CASE WHEN MONTH(p.`dt_UPDATED`)=5 THEN d.`int_QUANTITY` ELSE 0 END ) AS mai, "
            + " SUM(CASE WHEN MONTH(p.`dt_UPDATED`)=6 THEN d.`int_QUANTITY` ELSE 0 END ) AS juin,"
            + " SUM(CASE WHEN MONTH(p.`dt_UPDATED`)=7 THEN d.`int_QUANTITY` ELSE 0 END ) AS juillet"
            + ", SUM(CASE WHEN MONTH(p.`dt_UPDATED`)=8 THEN d.`int_QUANTITY` ELSE 0 END ) AS aout"
            + ", SUM(CASE WHEN MONTH(p.`dt_UPDATED`)=9 THEN d.`int_QUANTITY` ELSE 0 END ) AS septembre"
            + ", SUM(CASE WHEN MONTH(p.`dt_UPDATED`)=10 THEN d.`int_QUANTITY` ELSE 0 END ) AS octobre"
            + ", SUM(CASE WHEN MONTH(p.`dt_UPDATED`)=11 THEN d.`int_QUANTITY` ELSE 0 END ) AS novembre"
            + ", SUM(CASE WHEN MONTH(p.`dt_UPDATED`)=12 THEN d.`int_QUANTITY` ELSE 0 END ) AS decembre "
            + " FROM  t_preenregistrement p,t_preenregistrement_detail d,t_famille f,t_user u WHERE p.`lg_PREENREGISTREMENT_ID`=d.`lg_PREENREGISTREMENT_ID` "
            + " AND f.`lg_FAMILLE_ID`=d.`lg_FAMILLE_ID` AND p.`int_PRICE` >0 AND p.`b_IS_CANCEL`=0 AND p.`lg_TYPE_VENTE_ID` <> ?1 AND  u.`lg_USER_ID`=p.`lg_USER_ID` AND u.`lg_EMPLACEMENT_ID`=?2 "
            + "  AND p.`str_STATUT`=?3 AND YEAR(p.`dt_UPDATED`)=?4 {likeStatement} {rayonStatement}  GROUP  BY  id ORDER BY libelle ";

    private static final String YEAR_COUNT_SQL_QUERY = "SELECT  count(DISTINCT f.`lg_FAMILLE_ID`) FROM  t_preenregistrement p,t_preenregistrement_detail d,t_famille f,t_user u WHERE p.`lg_PREENREGISTREMENT_ID`=d.`lg_PREENREGISTREMENT_ID` "
            + " AND f.`lg_FAMILLE_ID`=d.`lg_FAMILLE_ID` AND p.`int_PRICE` >0 AND p.`b_IS_CANCEL`=0 AND p.`lg_TYPE_VENTE_ID` <> ?1 AND  u.`lg_USER_ID`=p.`lg_USER_ID` AND u.`lg_EMPLACEMENT_ID`=?2  "
            + "  AND p.`str_STATUT`=?3 AND YEAR(p.`dt_UPDATED`)=?4 {likeStatement} {rayonStatement}  ";
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public JSONObject getIntervalAnnees() {
        int[] months = CommonUtils.getYears();

        JSONArray datas = new JSONArray();
        for (int i = 0; i < months.length; i++) {
            int value = months[i];
            JSONObject json = new JSONObject();
            json.put("value", value);
            datas.put(json);

        }
        return FunctionUtils.returnData(datas);
    }

    @Override
    public List<StatistiqueProduitAnnuelleDTO> getVenteProduits(Integer year, String search, String userEmplacement,
            String rayonId, int start, int limit, boolean all) {
        if (Objects.isNull(year)) {
            year = Year.now().getValue();
        }
        return getVentes(year, search, userEmplacement, rayonId, start, limit, all).stream().map(this::build)
                .collect(Collectors.toList());
    }

    @Override
    public JSONObject getVenteProduits(Integer year, String search, String userEmplacement, String rayonId, int start,
            int limit) {
        if (Objects.isNull(year)) {
            year = Year.now().getValue();
        }
        long count = getCountVentes(year, search, userEmplacement, rayonId);
        return FunctionUtils
                .returnData(this.getVenteProduits(year, search, userEmplacement, rayonId, start, limit, false), count);
    }

    private String builQuery(String search, String rayonId, String sql) {

        if (StringUtils.isNotEmpty(search)) {
            sql = sql.replace("{likeStatement}",
                    " AND (f.`int_CIP` LIKE '" + search + "%' OR f.`str_NAME` LIKE '" + search + "%' )");
        } else {
            sql = sql.replace("{likeStatement}", "");
        }
        if (StringUtils.isNotBlank(rayonId)) {
            sql = sql.replace("{rayonStatement}", String.format(" AND f.`lg_ZONE_GEO_ID`=%s ", rayonId));
        } else {
            sql = sql.replace("{rayonStatement}", "");
        }
        LOG.log(Level.INFO, "sql--- getVenteProduits annuelle vente {0}", sql);
        return sql;
    }

    List<Tuple> getVentes(int year, String search, String userEmplacement, String rayonId, int start, int limit,
            boolean all) {
        try {
            Query query = em.createNativeQuery(builQuery(search, rayonId, YEAR_SQL_QUERY), Tuple.class)
                    .setParameter(1, DateConverter.DEPOT_EXTENSION).setParameter(2, userEmplacement)
                    .setParameter(3, STATUT).setParameter(4, year);
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

    private long getCountVentes(int year, String search, String userEmplacement, String rayonId) {
        try {
            Query query = em.createNativeQuery(builQuery(search, rayonId, YEAR_COUNT_SQL_QUERY));
            query.setParameter(1, DateConverter.DEPOT_EXTENSION);
            query.setParameter(2, userEmplacement);
            query.setParameter(3, STATUT);
            query.setParameter(4, year);
            return ((BigInteger) query.getSingleResult()).longValue();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    private StatistiqueProduitAnnuelleDTO build(Tuple t) {
        return StatistiqueProduitAnnuelleDTO.builder().id(t.get("id", String.class))
                .codeCip(t.get("codeCip", String.class)).libelle(t.get("libelle", String.class))
                .janvier(t.get("janvier", BigDecimal.class).intValue())
                .fevrier(t.get("fevrier", BigDecimal.class).intValue()).mars(t.get("mars", BigDecimal.class).intValue())
                .avril(t.get("avril", BigDecimal.class).intValue()).mai(t.get("mai", BigDecimal.class).intValue())
                .juin(t.get("juin", BigDecimal.class).intValue()).juillet(t.get("juillet", BigDecimal.class).intValue())
                .septembre(t.get("septembre", BigDecimal.class).intValue())
                .octobre(t.get("octobre", BigDecimal.class).intValue())
                .novembre(t.get("novembre", BigDecimal.class).intValue())
                .decembre(t.get("decembre", BigDecimal.class).intValue()).build();
    }
}
