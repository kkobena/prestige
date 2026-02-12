package rest.service.impl;

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
import org.json.JSONObject;
import rest.service.ArticleMvtService;
import rest.service.dto.ArticleMvtDTO;
import util.FunctionUtils;

@Stateless
public class ArticleMvtServiceImpl implements ArticleMvtService {

    private static final Logger LOG = Logger.getLogger(ArticleMvtServiceImpl.class.getName());

    // ✅ Requête principale (alias explicites -> Tuple.get("alias", Type.class) fiable)
    private static final String MVT_QUERY = "SELECT DISTINCT " + " f.lg_FAMILLE_ID AS lgFamilleId, "
            + " CAST(f.int_CIP AS CHAR) AS codeCip, " + " f.str_NAME AS strName, " + " f.int_PRICE AS prixVente, "
            + " f.int_PAF AS prixAchat " + "FROM t_famille f "
            + "JOIN hmvtproduit h ON f.lg_FAMILLE_ID = h.lg_FAMILLE_ID " + "WHERE DATE(h.mvtdate) BETWEEN ?1 AND ?2 "
            + "AND ( ?3 IS NULL OR CAST(f.int_CIP AS CHAR) LIKE ?3 OR f.str_NAME LIKE ?3 ) " + "ORDER BY f.str_NAME";

    // ✅ Count cohérent avec la requête (mêmes filtres)
    private static final String MVT_QUERY_COUNT = "SELECT COUNT(DISTINCT f.lg_FAMILLE_ID) " + "FROM t_famille f "
            + "JOIN hmvtproduit h ON f.lg_FAMILLE_ID = h.lg_FAMILLE_ID " + "WHERE DATE(h.mvtdate) BETWEEN ?1 AND ?2 "
            + "AND ( ?3 IS NULL OR CAST(f.int_CIP AS CHAR) LIKE ?3 OR f.str_NAME LIKE ?3 ) ";

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public JSONObject getAllArticleMvt(String dtStart, String dtEnd, String query, int limit, int start) {
        int total = getCount(dtStart, dtEnd, query);
        return FunctionUtils.returnData(this.getAllArticleMvt(dtStart, dtEnd, query, limit, start, true), total);
    }

    @Override
    public JSONObject getAllArticleMvt(String dtStart, String dtEnd, String query) {
        return FunctionUtils.returnData(this.getAllArticleMvt(dtStart, dtEnd, query, 0, 0, false));
    }

    @Override
    public List<ArticleMvtDTO> getAllArticleMvt(String dtStart, String dtEnd, String query, int limit, int start,
            boolean all) {
        return fetchAllArticleMvt(dtStart, dtEnd, query, limit, start, all).stream().map(this::build)
                .collect(Collectors.toList());
    }

    private List<Tuple> fetchAllArticleMvt(String dtStart, String dtEnd, String query, int limit, int start,
            boolean all) {
        LOG.log(Level.INFO, "sql--- fetchAllArticleMvt {0}", MVT_QUERY);
        try {
            Query q = em.createNativeQuery(MVT_QUERY, Tuple.class).setParameter(1, java.sql.Date.valueOf(dtStart))
                    .setParameter(2, java.sql.Date.valueOf(dtEnd));

            String like = StringUtils.isBlank(query) ? null : "%" + query.trim() + "%";
            q.setParameter(3, like);

            // pagination uniquement si all=true (comme ton exemple)
            if (all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);
            }

            return q.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new ArrayList<>();
        }
    }

    private int getCount(String dtStart, String dtEnd, String query) {
        LOG.log(Level.INFO, "sql--- getCount {0}", MVT_QUERY_COUNT);
        try {
            Query q = em.createNativeQuery(MVT_QUERY_COUNT).setParameter(1, java.sql.Date.valueOf(dtStart))
                    .setParameter(2, java.sql.Date.valueOf(dtEnd));

            String like = StringUtils.isBlank(query) ? null : "%" + query.trim() + "%";
            q.setParameter(3, like);

            return ((Number) q.getSingleResult()).intValue();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    private ArticleMvtDTO build(Tuple t) {
        // ⚠️ aliases = ceux du SELECT AS ...
        String lgFamilleId = t.get("lgFamilleId", String.class);
        String codeCip = t.get("codeCip", String.class);
        String strName = t.get("strName", String.class);

        Integer prixVente = t.get("prixVente", Integer.class);
        Integer prixAchat = t.get("prixAchat", Integer.class);

        // sécurise nulls
        prixVente = Objects.isNull(prixVente) ? 0 : prixVente;
        prixAchat = Objects.isNull(prixAchat) ? 0 : prixAchat;

        return ArticleMvtDTO.builder().lgFamilleId(lgFamilleId).codeCip(codeCip).strName(strName).prixVente(prixVente)
                .prixAchat(prixAchat).build();
    }
}
