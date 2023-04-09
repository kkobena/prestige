package rest.service.impl;

import dal.TBonLivraison;
import dal.TBonLivraisonDetail;
import dal.TBonLivraisonDetail_;
import dal.TBonLivraison_;
import dal.TFamille_;
import dal.TGrossiste_;
import dal.TOrder_;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import rest.service.EtatControlBonService;
import rest.service.dto.EtatControlBon;
import rest.service.dto.builder.EtatControlBonBuilder;
import util.FunctionUtils;

/**
 *
 * @author koben
 */
@Stateless
public class EtatControlBonServiceImpl implements EtatControlBonService {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    private boolean returnFullBLLAuthority;
    private static final String DELETE = "delete";

    @Override
    public List<EtatControlBon> list(String search, String dtStart, String dtEnd, String grossisteId, int start, int limit, boolean all) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBonLivraison> cq = cb.createQuery(TBonLivraison.class);
        Root<TBonLivraison> root = cq.from(TBonLivraison.class);
        cq.select(root).distinct(true).orderBy(cb.desc(root.get(TBonLivraison_.dtDATELIVRAISON)));
        List<Predicate> predicates = listPredicates(cb, root,
                LocalDate.parse(dtStart), LocalDate.parse(dtEnd), grossisteId, search);
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        TypedQuery<TBonLivraison> q = em.createQuery(cq);
        if (!all) {
            q.setFirstResult(start);
            q.setMaxResults(limit);

        }
        return q.getResultList().stream().map(e -> EtatControlBonBuilder.build(e))
                .peek(e1 -> e1.setReturnFullBl((!DELETE.equals(e1.getStrSTATUT()) && e1.getIntHTTC() > 0) && this.returnFullBLLAuthority))
                .collect(Collectors.toList());
    }

    public long count(String search, String dtStart, String dtEnd, String grossisteId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TBonLivraison> root = cq.from(TBonLivraison.class);
        cq.select(cb.countDistinct(root));
        List<Predicate> predicates = listPredicates(cb, root,
                LocalDate.parse(dtStart), LocalDate.parse(dtEnd), grossisteId, search);
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        TypedQuery<Long> q = em.createQuery(cq);
        return Objects.isNull(q.getSingleResult()) ? 0 : q.getSingleResult();

    }

    private List<Predicate> listPredicates(CriteriaBuilder cb, Root<TBonLivraison> root,
            LocalDate dtStart, LocalDate dtEnd, String grossisteId, String search) {
        List<Predicate> predicates = new ArrayList<>();

        Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TBonLivraison_.dtDATELIVRAISON)),
                java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
        predicates.add(btw);
        if (StringUtils.isNotEmpty(grossisteId)) {
            predicates.add(cb.equal(root.get(TBonLivraison_.lgORDERID).get(TOrder_.lgGROSSISTEID).get(TGrossiste_.lgGROSSISTEID), grossisteId));
        }
        if (StringUtils.isNotEmpty(search)) {
            search = search + "%";
            Join<TBonLivraison, TBonLivraisonDetail> join = root.join(TBonLivraison_.tBonLivraisonDetailCollection);
            predicates.add(cb.or(cb.like(root.get(TBonLivraison_.strREFLIVRAISON), search),
                    cb.like(root.get(TBonLivraison_.lgORDERID).get(TOrder_.strREFORDER), search),
                    cb.like(join.get(TBonLivraisonDetail_.lgFAMILLEID).get(TFamille_.intCIP), search),
                    cb.like(join.get(TBonLivraisonDetail_.lgFAMILLEID).get(TFamille_.strNAME), search)
            ));
        }
        return predicates;
    }

    @Override
    public JSONObject list(String search, String dtStart, String dtEnd, String grossisteId, int start, int limit) {
        long count = count(search, dtStart, dtEnd, grossisteId);
        return FunctionUtils.returnData(list(search, dtStart, dtEnd, grossisteId, start, limit, false), count);
    }

    @Override
    public void hasReturnFullBLLAuthority(boolean b) {
        this.returnFullBLLAuthority = b;
    }

}
