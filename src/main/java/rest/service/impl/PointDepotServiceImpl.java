
package rest.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import rest.service.PointDepotService;
import rest.service.dto.PointDepotDTO;

/**
 *
 * @author airman
 */

@Stateless
public class PointDepotServiceImpl implements PointDepotService {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public JSONObject getPointDepot(String dtStart, String dtEnd, String emplacementId) {
        LocalDate startDate = StringUtils.isEmpty(dtStart) ? LocalDate.now() : LocalDate.parse(dtStart);
        LocalDate endDate = StringUtils.isEmpty(dtEnd) ? LocalDate.now() : LocalDate.parse(dtEnd);

        boolean isCumulative = !startDate.isEqual(endDate);

        List<PointDepotDTO> data = findPointDepotData(startDate, endDate, emplacementId, isCumulative);

        JSONObject json = new JSONObject();
        json.put("rows", new JSONArray(data));
        json.put("total", data.size());
        return json;
    }

    private List<PointDepotDTO> findPointDepotData(LocalDate startDate, LocalDate endDate, String emplacementId,
            boolean isCumulative) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(
                    "SELECT SUM(m.montantNet) AS MontantTotalNet, SUM(m.montantCredit) AS Credit, SUM(m.montantRegle) AS Especes, ");
            sql.append("u.str_FIRST_NAME AS Caissiere, e.str_NAME AS DEPOT ");

            if (!isCumulative) {
                sql.append(", DATE(m.mvtdate) AS DateTransaction ");
            }

            sql.append("FROM mvttransaction m ");
            sql.append("INNER JOIN t_user u ON m.lg_USER_ID = u.lg_USER_ID ");
            sql.append("INNER JOIN t_emplacement e ON e.lg_EMPLACEMENT_ID = m.lg_EMPLACEMENT_ID ");
            sql.append("WHERE m.lg_EMPLACEMENT_ID <> 1 AND m.typeMvtCaisseId IN (4, 5, 8, 9) ");
            sql.append("AND DATE(m.mvtdate) BETWEEN ?1 AND ?2 ");

            if (StringUtils.isNotEmpty(emplacementId) && !"ALL".equalsIgnoreCase(emplacementId)) {
                sql.append("AND m.lg_EMPLACEMENT_ID = ?3 ");
            }

            sql.append("GROUP BY m.lg_USER_ID, u.str_FIRST_NAME, e.str_NAME ");
            if (!isCumulative) {
                sql.append(", DATE(m.mvtdate)");
            }

            javax.persistence.Query query = em.createNativeQuery(sql.toString(), Tuple.class);
            query.setParameter(1, java.sql.Date.valueOf(startDate));
            query.setParameter(2, java.sql.Date.valueOf(endDate));

            if (StringUtils.isNotEmpty(emplacementId) && !"ALL".equalsIgnoreCase(emplacementId)) {
                query.setParameter(3, emplacementId);
            }

            List<Tuple> result = query.getResultList();

            String periodLabel = startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " - "
                    + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            return result.stream().map(tuple -> buildDtoFromTuple(tuple, isCumulative, periodLabel))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private PointDepotDTO buildDtoFromTuple(Tuple tuple, boolean isCumulative, String periodLabel) {
        String dateTransaction = isCumulative ? periodLabel : tuple.get("DateTransaction", java.sql.Date.class)
                .toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return PointDepotDTO.builder().montantTotalNet(tuple.get("MontantTotalNet", BigDecimal.class).longValue())
                .credit(tuple.get("Credit", BigDecimal.class).longValue())
                .especes(tuple.get("Especes", BigDecimal.class).longValue())
                .caissiere(tuple.get("Caissiere", String.class)).depot(tuple.get("DEPOT", String.class))
                .dateTransaction(dateTransaction).build();
    }
}
