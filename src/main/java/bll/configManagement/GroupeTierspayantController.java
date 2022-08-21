package bll.configManagement;

import bll.common.Parameter;
import bll.configManagement.exceptions.NonexistentEntityException;
import bll.entity.EntityData;
import bll.transaction.Transaction;
import bll.transaction.impl.TransactionImpl;
import dal.AnnulationSnapshot;
import dal.TBonLivraison;
import dal.TBonLivraisonDetail;
import dal.TBonLivraisonDetail_;
import dal.TBonLivraison_;
import dal.TCashTransaction;
import dal.TCashTransaction_;
import dal.TClient;
import dal.TCodeTva;
import dal.TCompany;
import dal.TCompany_;
import dal.TCompteClientTiersPayant;
import dal.TDossierReglement;
import dal.TDossierReglement_;
import dal.TEmplacement;
import dal.TEmplacement_;
import dal.TEventLog;
import dal.TEventLog_;
import dal.TFacture;
import dal.TFactureDetail;
import dal.TFacture_;
import dal.TFamille;
import dal.TFamilleGrossiste;
import dal.TFamilleStock;
import dal.TFamilleStock_;
import dal.TFamille_;
import dal.TFamillearticle;
import dal.TFamillearticle_;
import dal.TGrossiste;
import dal.TGrossiste_;
import dal.TGroupeFactures;
import dal.TGroupeFactures_;
import dal.TGroupeTierspayant;
import dal.TLot;
import dal.TLot_;
import dal.TModeReglement;
import dal.TModeReglement_;
import dal.TMvtCaisse;
import dal.TMvtCaisse_;
import dal.TOrder;
import dal.TParameters;
import dal.TPreenregistrement;
import dal.TPreenregistrementCompteClient;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TPreenregistrementCompteClientTiersPayent_;
import dal.TPreenregistrementCompteClient_;
import dal.TPreenregistrementDetail;
import dal.TPreenregistrementDetail_;
import dal.TPreenregistrement_;
import dal.TRecettes;
import dal.TReglement;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dal.TTiersPayant;
import dal.TTypeFacture;
import dal.TTypeMvtCaisse;
import dal.TTypeMvtCaisse_;
import dal.TTypeReglement;
import dal.TTypeVente;
import dal.TTypeVente_;
import dal.TUser;
import dal.TUser_;
import dal.TZoneGeographique;
import dal.TZoneGeographique_;
import dal.enumeration.TypeLog;
import dal.jconnexion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.Case;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Subquery;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import toolkits.parameters.commonparameter;
import toolkits.utils.Util;
import toolkits.utils.date;
import util.DateConverter;

/**
 *
 * @author KKOFFI
 */
public class GroupeTierspayantController implements Serializable {

    private final date key = new date();
    static final DateFormat DATEFORMAT = new SimpleDateFormat("dd/MM/yyyy");
    static final DateFormat DATEFORMATYYYY = new SimpleDateFormat("yyyy-MM-dd");

    public GroupeTierspayantController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public boolean create(TGroupeTierspayant TGroupeTierspayant) {
        boolean isOk = false;
        if (TGroupeTierspayant.getTTiersPayantList() == null) {
            TGroupeTierspayant.setTTiersPayantList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<TTiersPayant> attachedTTiersPayantList = new ArrayList<>();
            for (TTiersPayant TTiersPayantListTTiersPayantToAttach : TGroupeTierspayant.getTTiersPayantList()) {
                TTiersPayantListTTiersPayantToAttach = em.getReference(TTiersPayantListTTiersPayantToAttach.getClass(), TTiersPayantListTTiersPayantToAttach.getLgTIERSPAYANTID());
                attachedTTiersPayantList.add(TTiersPayantListTTiersPayantToAttach);
            }
            TGroupeTierspayant.setTTiersPayantList(attachedTTiersPayantList);
            em.persist(TGroupeTierspayant);
            for (TTiersPayant TTiersPayantListTTiersPayant : TGroupeTierspayant.getTTiersPayantList()) {
                TGroupeTierspayant oldLgGROUPEIDOfTTiersPayantListTTiersPayant = TTiersPayantListTTiersPayant.getLgGROUPEID();
                TTiersPayantListTTiersPayant.setLgGROUPEID(TGroupeTierspayant);
                TTiersPayantListTTiersPayant = em.merge(TTiersPayantListTTiersPayant);
                if (oldLgGROUPEIDOfTTiersPayantListTTiersPayant != null) {
                    oldLgGROUPEIDOfTTiersPayantListTTiersPayant.getTTiersPayantList().remove(TTiersPayantListTTiersPayant);
                    em.merge(oldLgGROUPEIDOfTTiersPayantListTTiersPayant);
                }
            }
            em.getTransaction().commit();
            isOk = true;

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (em != null) {

            }

        }

        return isOk;
    }

    public boolean edit(int idGroupe, String str_LIBELLE, String str_ADRESSE, String str_TELEPHONE) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        boolean isOk = false;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TGroupeTierspayant persistentTGroupeTierspayant = em.find(TGroupeTierspayant.class, idGroupe);
            persistentTGroupeTierspayant.setStrLIBELLE(str_LIBELLE);
            persistentTGroupeTierspayant.setStrADRESSE(str_ADRESSE);
            persistentTGroupeTierspayant.setStrTELEPHONE(str_TELEPHONE);
            em.merge(persistentTGroupeTierspayant);

            em.getTransaction().commit();
            isOk = true;
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {

            }
        }
        return isOk;
    }

    public boolean destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        boolean isOk = false;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TGroupeTierspayant TGroupeTierspayant;
            try {
                TGroupeTierspayant = em.getReference(TGroupeTierspayant.class, id);
                TGroupeTierspayant.getLgGROUPEID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The TGroupeTierspayant with id " + id + " no longer exists.", enfe);
            }
            List<TTiersPayant> TTiersPayantList = TGroupeTierspayant.getTTiersPayantList();
            for (TTiersPayant TTiersPayantListTTiersPayant : TTiersPayantList) {
                TTiersPayantListTTiersPayant.setLgGROUPEID(null);
                TTiersPayantListTTiersPayant = em.merge(TTiersPayantListTTiersPayant);
            }
            em.remove(TGroupeTierspayant);
            em.getTransaction().commit();
            isOk = true;
        } finally {
            if (em != null) {

            }

        }
        return isOk;
    }

    public List<TGroupeTierspayant> findTGroupeTierspayantEntities(String search) {
        return findTGroupeTierspayantEntities(true, -1, -1, search);
    }

    public List<TGroupeTierspayant> findTGroupeTierspayantEntities(int maxResults, int firstResult, String search) {
        return findTGroupeTierspayantEntities(false, maxResults, firstResult, search);
    }

    private List<TGroupeTierspayant> findTGroupeTierspayantEntities(boolean all, int maxResults, int firstResult, String search) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TGroupeTierspayant> cq = cb.createQuery(TGroupeTierspayant.class);
            Root<TGroupeTierspayant> root = cq.from(TGroupeTierspayant.class);
            cq.select(root);
            cq.where(cb.like(root.get("strLIBELLE"), search + "%"));
            Query q = em.createQuery(cq);

            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {

        }
    }

    public TGroupeTierspayant findTGroupeTierspayant(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TGroupeTierspayant.class, id);
        } finally {

        }
    }

    public int getTGroupeTierspayantCount(String search) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TGroupeTierspayant> rt = cq.from(TGroupeTierspayant.class);
            cq.select(cb.count(rt));
            cq.where(cb.like(rt.get("strLIBELLE"), search + "%"));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {

        }
    }

    public JSONObject addTiersPayants2Groupe(JSONArray listtp, int lgGOUPE) {
        JSONObject json = new JSONObject();
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TGroupeTierspayant g = em.find(TGroupeTierspayant.class, lgGOUPE);
            int cont = 0;
            for (int i = 0; i < listtp.length(); i++) {
                TTiersPayant payant = em.find(TTiersPayant.class, listtp.getString(i));
                payant.setLgGROUPEID(g);
                g.getTTiersPayantList().add(payant);
                em.merge(payant);
                em.merge(g);
                cont++;

            }
            em.getTransaction().commit();
            json.put("status", cont).put("message", "<span style='color:blue;'>" + cont + "</span> Tierspayant(s) associé(s) à ce groupe <span style='font-weight:800;'><u>" + g.getStrLIBELLE() + "</u></span>");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) {

            }
        }

        return json;
    }

    public List<TTiersPayant> findTGroupeTierspayantTierspayant(boolean all, int maxResults, int firstResult, int idGp, String search) {
        EntityManager em = null;
        try {

            em = getEntityManager();
//            TGroupeTierspayant groupeTierspayant = findTGroupeTierspayant(idGp);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TTiersPayant> cq = cb.createQuery(TTiersPayant.class);
            Root<TTiersPayant> root = cq.from(TTiersPayant.class);
            Join<TTiersPayant, TTiersPayant> j = root.join("lgGROUPEID", JoinType.INNER);
            cq.select(root);
            Predicate criteria = cb.conjunction();
            if (idGp > 0) {
                criteria = cb.and(criteria, cb.equal(root.get("lgGROUPEID").get("lgGROUPEID"), idGp));
            }
            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.or(cb.like(root.get("strFULLNAME"), search + "%"), cb.like(root.get("strNAME"), search + "%")));
            }
            criteria = cb.and(criteria, cb.equal(root.get("strSTATUT"), "enable"));
            cq.where(criteria);
            Query q = em.createQuery(cq);

            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            if (em != null) {

            }
        }
    }

    public int findTGroupeTierspayantTierspayantCount(int idGp, String search) {
        EntityManager em = getEntityManager();

        try {

            TGroupeTierspayant groupeTierspayant = findTGroupeTierspayant(idGp);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TTiersPayant> root = cq.from(TTiersPayant.class);
            Join<TTiersPayant, TTiersPayant> j = root.join("lgGROUPEID", JoinType.INNER);
            cq.select(cb.count(j));
            Predicate criteria = cb.conjunction();
            if (idGp > 0) {
                criteria = cb.and(criteria, cb.equal(root.get("lgGROUPEID").get("lgGROUPEID"), idGp));
            }
            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.or(cb.like(root.get("strFULLNAME"), search + "%"), cb.like(root.get("strNAME"), search + "%")));
            }
            criteria = cb.and(criteria, cb.equal(root.get("strSTATUT"), "enable"));
            cq.where(criteria);
//            cq.where(cb.equal(root.get("lgGROUPEID"), groupeTierspayant), cb.like(root.get("strFULLNAME"), search + "%"), cb.equal(root.get("strSTATUT"), "enable"));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();

        } finally {

        }
    }

    public List<TTiersPayant> findTGroupeTierspayantNOTINTierspayant(boolean all, int maxResults, int firstResult, int idGp, String search) {
        EntityManager em = null;
        try {

            em = getEntityManager();
            TGroupeTierspayant groupeTierspayant = findTGroupeTierspayant(idGp);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TTiersPayant> cq = cb.createQuery(TTiersPayant.class);
            Root<TTiersPayant> root = cq.from(TTiersPayant.class);
            cq.select(root);
            cq.where(cb.and(cb.equal(root.get("strSTATUT"), "enable")), cb.or(cb.isNull(root.get("lgGROUPEID")), cb.notEqual(root.get("lgGROUPEID"), groupeTierspayant)), cb.or(cb.like(root.get("strFULLNAME"), search + "%"), cb.like(root.get("strNAME"), search + "%")));
//         
            Query q = em.createQuery(cq);

            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            if (em != null) {

            }
        }
    }

    public int findTGroupeTierspayantNOTINTierspayantcount(int idGp, String search) {
        EntityManager em = null;
        try {
//            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            em = getEntityManager();
            TGroupeTierspayant groupeTierspayant = findTGroupeTierspayant(idGp);
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TTiersPayant> root = cq.from(TTiersPayant.class);
            cq.select(cb.count(root));

            cq.where(cb.and(cb.equal(root.get("strSTATUT"), "enable")), cb.or(cb.isNull(root.get("lgGROUPEID")), cb.notEqual(root.get("lgGROUPEID"), groupeTierspayant)), cb.or(cb.like(root.get("strFULLNAME"), search + "%"), cb.like(root.get("strNAME"), search + "%")));
//         
            Query q = em.createQuery(cq);

            return ((Long) q.getSingleResult()).intValue();
        } finally {
            if (em != null) {

            }
        }
    }

    public JSONObject removeTiersPayants2Groupe(JSONArray listtp, int lgGOUPE) {
        JSONObject json = new JSONObject();
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TGroupeTierspayant g = em.find(TGroupeTierspayant.class, lgGOUPE);
            int cont = 0;
            for (int i = 0; i < listtp.length(); i++) {
                TTiersPayant payant = em.find(TTiersPayant.class, listtp.getString(i));

                payant.setLgGROUPEID(null);

                em.merge(payant);
                em.merge(g);
                cont++;

            }
            em.getTransaction().commit();

            json.put("status", cont).put("message", "<span style='color:blue;'>" + cont + "</span> Tierspayant(s) sumprimés de ce groupe <span style='font-weight:800;'><u>" + g.getStrLIBELLE() + "</u></span>");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) {

            }
        }
        return json;
    }

    //ajout en masse
    public JSONObject addSelection2Groupe(int lgGOUPE, String search_value) {
        JSONObject json = new JSONObject();
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<TTiersPayant> list = this.findTGroupeTierspayantNOTINTierspayant(true, -1, -1, lgGOUPE, search_value);
            TGroupeTierspayant g = em.find(TGroupeTierspayant.class, lgGOUPE);
            int cont = 0;
            for (TTiersPayant payant : list) {

                payant.setLgGROUPEID(g);
                g.getTTiersPayantList().add(payant);
                em.merge(payant);
                em.merge(g);
                cont++;

            }
            em.getTransaction().commit();
            json.put("status", cont).put("message", "<span style='color:blue;'>" + cont + "</span> Tierspayant(s) associé(s) à ce groupe <span style='font-weight:800;'><u>" + g.getStrLIBELLE() + "</u></span>");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) {

            }
        }

        return json;
    }

//suppression en masse
    public JSONObject removeSelection2Groupe(int lgGOUPE, String search_value) {
        JSONObject json = new JSONObject();
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<TTiersPayant> list = this.findTGroupeTierspayantTierspayant(true, -1, -1, lgGOUPE, search_value);
            TGroupeTierspayant g = em.find(TGroupeTierspayant.class, lgGOUPE);
            int cont = 0;
            for (TTiersPayant payant : list) {

                payant.setLgGROUPEID(null);

                em.merge(payant);
                em.merge(g);
                cont++;

            }
            em.getTransaction().commit();
            json.put("status", cont).put("message", "<span style='color:blue;'>" + cont + "</span> Tierspayant(s) supprimé de ce groupe <span style='font-weight:800;'><u>" + g.getStrLIBELLE() + "</u></span>");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) {

            }
        }

        return json;
    }
// les bons d'un groupe

    public List<TPreenregistrementCompteClientTiersPayent> getGroupeBons(boolean all, String dt_start, String dt_end, int firstResult, int maxResults, String lgTP_ID, Integer lgGRP, String refBon) {
        EntityManager em = null;
        try {

            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TPreenregistrementCompteClientTiersPayent> cq = cb.createQuery(TPreenregistrementCompteClientTiersPayent.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementCompteClientTiersPayent, TCompteClientTiersPayant> cmpt = root.join("lgCOMPTECLIENTTIERSPAYANTID", JoinType.INNER);
            Join<TCompteClientTiersPayant, TTiersPayant> cmp = cmpt.join("lgTIERSPAYANTID", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            if (lgGRP != 0 && lgGRP > 0) {
                Join<TTiersPayant, TGroupeTierspayant> tps = cmp.join("lgGROUPEID", JoinType.INNER);
                ParameterExpression<Integer> p = cb.parameter(Integer.class, "lgGROUPEID");

                criteria = cb.and(criteria, cb.equal(tps.get("lgGROUPEID"), p));
            }
            if (!"".equals(lgTP_ID)) {

                ParameterExpression<String> p = cb.parameter(String.class, "lgTIERSPAYANTID");
                criteria = cb.and(criteria, cb.equal(root.get("lgCOMPTECLIENTTIERSPAYANTID").get("lgTIERSPAYANTID").get("lgTIERSPAYANTID"), p));
            }

            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("strSTATUTFACTURE"), "unpaid"));
            if (!"".equals(refBon)) {
                criteria = cb.and(criteria, cb.like(root.get("strREFBON"), refBon + "%"));
            }

            Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            Predicate pu2 = cb.greaterThan(root.get("intPRICE"), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("lgPREENREGISTREMENTID").get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            cq.orderBy(cb.desc(pr.get("intPRICE")));
            cq.where(criteria, cb.and(pu), cb.and(pu2), cb.and(btw));

            Query q = em.createQuery(cq);
            if (lgGRP != 0 && lgGRP > 0) {

                q.setParameter("lgGROUPEID", lgGRP);
            }
            if (!"".equals(lgTP_ID)) {
                q.setParameter("lgTIERSPAYANTID", lgTP_ID);
            }
//           

            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            if (em != null) {

            }
        }
    }

    public int getGroupeBonsCount(String dt_start, String dt_end, String lgTP_ID, Integer lgGRP, String refBon) {
        EntityManager em = null;
        try {

            em = getEntityManager();
//         
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementCompteClientTiersPayent, TCompteClientTiersPayant> cmpt = root.join("lgCOMPTECLIENTTIERSPAYANTID", JoinType.INNER);
            Join<TCompteClientTiersPayant, TTiersPayant> cmp = cmpt.join("lgTIERSPAYANTID", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            if (lgGRP != 0 && lgGRP > 0) {
                Join<TTiersPayant, TGroupeTierspayant> tps = cmp.join("lgGROUPEID", JoinType.INNER);
                ParameterExpression<Integer> p = cb.parameter(Integer.class, "lgGROUPEID");

                criteria = cb.and(criteria, cb.equal(tps.get("lgGROUPEID"), p));
            }
            if (!"".equals(lgTP_ID)) {

                ParameterExpression<String> p = cb.parameter(String.class, "lgTIERSPAYANTID");
                criteria = cb.and(criteria, cb.equal(root.get("lgCOMPTECLIENTTIERSPAYANTID").get("lgTIERSPAYANTID").get("lgTIERSPAYANTID"), p));
            }

            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("strSTATUTFACTURE"), "unpaid"));
            if (!"".equals(refBon)) {
                criteria = cb.and(criteria, cb.like(root.get("strREFBON"), refBon + "%"));
            }

            Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            Predicate pu2 = cb.greaterThan(root.get("intPRICE"), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("lgPREENREGISTREMENTID").get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));

            cq.select(cb.count(root));
            cq.where(criteria, cb.and(pu), cb.and(pu2), cb.and(btw));

            Query q = em.createQuery(cq);
            if (lgGRP != 0 && lgGRP > 0) {

                q.setParameter("lgGROUPEID", lgGRP);
            }
            if (!"".equals(lgTP_ID)) {
                q.setParameter("lgTIERSPAYANTID", lgTP_ID);
            }

            return ((Long) q.getSingleResult()).intValue();
        } finally {
            if (em != null) {

            }
        }
    }

    public List<TTiersPayant> findTierspayant(boolean all, String search_value, String lg_TYPE_TIERS_PAYANT_ID, int lgGRP, int firstResult, int maxResults) {
        EntityManager em = null;
        try {
            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TTiersPayant> cq = cb.createQuery(TTiersPayant.class);
            Root<TTiersPayant> root = cq.from(TTiersPayant.class);
            Join<TTiersPayant, TTiersPayant> tp = root.join("lgTYPETIERSPAYANTID", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            if (lgGRP > 0) {
                Join<TTiersPayant, TGroupeTierspayant> tps = root.join("lgGROUPEID", JoinType.INNER);
                ParameterExpression<Integer> p = cb.parameter(Integer.class, "lgGROUPEID");

                criteria = cb.and(criteria, cb.equal(root.get("lgGROUPEID").get("lgGROUPEID"), p));
            }

            if (!"".equals(search_value)) {
                criteria = cb.and(cb.like(root.get("strFULLNAME"), search_value + "%"));
            }
            if (!"".equals(lg_TYPE_TIERS_PAYANT_ID)) {

                ParameterExpression<String> p = cb.parameter(String.class, "lgTYPETIERSPAYANTID");
                criteria = cb.and(criteria, cb.equal(root.get("lgTYPETIERSPAYANTID").get("lgTYPETIERSPAYANTID"), p));
            }

            criteria = cb.and(criteria, cb.equal(root.get("strSTATUT"), "enable"));
            cq.where(criteria);

            Query q = em.createQuery(cq);
            if (lgGRP > 0) {

                q.setParameter("lgGROUPEID", lgGRP);
            }
            if (!"".equals(lg_TYPE_TIERS_PAYANT_ID)) {
                q.setParameter("lgTYPETIERSPAYANTID", lg_TYPE_TIERS_PAYANT_ID);
            }
//           q.setParameter("bISCANCEL", false);

            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }

            return q.getResultList();

        } finally {
            if (em != null) {

            }
        }

    }

    public int findTierspayantCount(String search_value, String lg_TYPE_TIERS_PAYANT_ID, int lgGRP) {
        EntityManager em = null;
        try {
            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TTiersPayant> root = cq.from(TTiersPayant.class);
            Join<TTiersPayant, TTiersPayant> tp = root.join("lgTYPETIERSPAYANTID", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            if (lgGRP > 0) {
                Join<TTiersPayant, TGroupeTierspayant> tps = root.join("lgGROUPEID", JoinType.INNER);
                ParameterExpression<Integer> p = cb.parameter(Integer.class, "lgGROUPEID");

                criteria = cb.and(criteria, cb.equal(root.get("lgGROUPEID").get("lgGROUPEID"), p));
            }

            if (!"".equals(search_value)) {
                criteria = cb.and(cb.like(root.get("strFULLNAME"), search_value + "%"));
            }
            if (!"".equals(lg_TYPE_TIERS_PAYANT_ID)) {

                ParameterExpression<String> p = cb.parameter(String.class, "lgTYPETIERSPAYANTID");
                criteria = cb.and(criteria, cb.equal(root.get("lgTYPETIERSPAYANTID").get("lgTYPETIERSPAYANTID"), p));
            }

            criteria = cb.and(criteria, cb.equal(root.get("strSTATUT"), "enable"));
            cq.select(cb.count(root));
            cq.where(criteria);

            Query q = em.createQuery(cq);
            if (lgGRP > 0) {

                q.setParameter("lgGROUPEID", lgGRP);
            }
            if (!"".equals(lg_TYPE_TIERS_PAYANT_ID)) {
                q.setParameter("lgTYPETIERSPAYANTID", lg_TYPE_TIERS_PAYANT_ID);
            }
//           q.setParameter("bISCANCEL", false);

            return ((Long) q.getSingleResult()).intValue();

        } finally {
            if (em != null) {

            }
        }

    }

    public Map<String, LinkedHashSet<TFacture>> generateGroupeFacture(String dt_start, String dt_end, int lgGRP, JSONArray tplist, JSONArray listExclud, int mode, TUser OUser) {
        Map<String, LinkedHashSet<TFacture>> grfact = new HashMap<>();
        final Map<String, LinkedHashSet<TFacture>> _grfact = new HashMap<>();
        List<TTiersPayant> grtp;
        switch (mode) {
            case 0:
                grtp = this.findTGroupeTierspayantTierspayant(true, -1, -1, lgGRP, "");
                Map<TGroupeTierspayant, List<TTiersPayant>> mapList = grtp.stream().collect(Collectors.groupingBy(s -> s.getLgGROUPEID()));
                EntityManager em = getEntityManager();
                em.getTransaction().begin();
                mapList.entrySet().forEach((t) -> {
                    TGroupeTierspayant g = t.getKey();
                    TParameters OParameters = em.find(TParameters.class, Parameter.KEY_CODE_FACTURE);
                    String CODEFACTURE = OParameters.getStrVALUE();

                    OParameters.setStrVALUE((Integer.valueOf(CODEFACTURE) + 1) + "");
                    em.merge(OParameters);
                    LinkedHashSet<TFacture> listfact = generateInvoices(t.getValue(), dt_start, dt_end, g, em, CODEFACTURE, OUser);
                    _grfact.put(CODEFACTURE, listfact);
                    String description = "Création de  factures groupées : du  " + dt_start + " au " + dt_end + " groupe tiers-payant: " + g.getStrLIBELLE() + " ";
                    updateItem(OUser, "", description, TypeLog.GENERATION_DE_FACTURE, "t_facture", em);
                });
                em.getTransaction().commit();
                break;
            case 1:
                grtp = this.findTGroupeTierspayantTierspayant(lgGRP, listExclud);
                grfact = generateInvoices(grtp, dt_start, dt_end, lgGRP, OUser);
                try {
                    String description = "Création de  factures groupées : du  " + dt_start + " au " + dt_end + " groupe tiers-payant: " + this.getEntityManager().find(TGroupeTierspayant.class, lgGRP).getStrLIBELLE() + " ";
                    updateItem(OUser, "", description, TypeLog.GENERATION_DE_FACTURE, "t_facture", this.getEntityManager());
                } catch (Exception e) {
                }

                break;
            case 2:
                grtp = this.findTierspayant(tplist);
                grfact = generateInvoices(grtp, dt_start, dt_end, OUser);
                try {
                    String description = "Création de  factures groupées : du  " + dt_start + " au " + dt_end + " groupe tiers-payant: " + this.getEntityManager().find(TGroupeTierspayant.class, lgGRP).getStrLIBELLE() + " ";
                    updateItem(OUser, "", description, TypeLog.GENERATION_DE_FACTURE, "t_facture", this.getEntityManager());
                } catch (Exception e) {
                }
                break;
        }

//        this.do_event_log("", "Création de  factures groupées : du  " + dt_start + " au " + dt_end, OUser.getStrFIRSTNAME(), commonparameter.statut_enable, "t_facture", "t_facture", "Facturation", OUser.getLgUSERID());
        return (mode == 0 ? _grfact : grfact);
    }

    public void do_event_log(String ID_INSCRIPTION,
            String str_DESCRIPTION, String str_CREATED_BY,
            String str_STATUT, String str_TABLE_CONCERN,
            String str_MODULE_CONCERN, String str_TYPE_LOG, String lg_USER_ID) {
        EntityManager em = getEntityManager();

        try {

            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }

            em.createNativeQuery("INSERT INTO `t_event_log` (`lg_EVENT_LOG_ID`, `str_DESCRIPTION`, `str_CREATED_BY`, `str_STATUT`, `str_TABLE_CONCERN`,`str_MODULE_CONCERN`,`str_TYPE_LOG`,`lg_USER_ID`) VALUES(?,?,?,?,?,?,?,?)")
                    .setParameter(1, key.gettimeid())
                    .setParameter(2, str_DESCRIPTION)
                    .setParameter(3, str_CREATED_BY)
                    .setParameter(4, str_STATUT)
                    .setParameter(5, str_TABLE_CONCERN)
                    .setParameter(6, str_MODULE_CONCERN)
                    .setParameter(7, str_TYPE_LOG)
                    .setParameter(8, lg_USER_ID)
                    .executeUpdate();
            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }

        } finally {
            if (em != null) {

            }

        }
    }

    private Map<Integer, LinkedHashSet<TFacture>> generateFacture(List<TTiersPayant> tp, String dt_start, String dt_end, JSONArray listExclud, Integer lgGRP, TUser u) {
        EntityManager em = getEntityManager();
        TParameters OParameters = em.find(TParameters.class, Parameter.KEY_CODE_FACTURE);
        em.getTransaction().begin();
        Map<Integer, LinkedHashSet<TFacture>> grfact = new HashMap<>();
        LinkedHashSet<TFacture> factures = new LinkedHashSet<>();
        Map<TGroupeTierspayant, List<TTiersPayant>> grtp = tp.stream().collect(Collectors.groupingBy(TTiersPayant::getLgGROUPEID));

        grtp.entrySet().forEach((t) -> {
            TGroupeTierspayant g = t.getKey();
            List<TTiersPayant> payants = t.getValue();
            String CODEFACTURE = OParameters.getStrVALUE();
            OParameters.setStrVALUE((Integer.valueOf(CODEFACTURE) + 1) + "");
            em.merge(OParameters);
            payants.forEach((p) -> {

                List<TPreenregistrementCompteClientTiersPayent> finalTp = this.getGroupeBonsInterval(dt_start, dt_end, p.getLgTIERSPAYANTID(), lgGRP, listExclud);

                switch (getCase(p)) {

                    case 1:
                        final long montantFact = finalTp.stream().mapToLong((_qty) -> {
                            return _qty.getIntPRICE();
                        }).sum();
                        if (p.getIntMONTANTFAC() < montantFact) {
                            Integer virtualAmont = 0;
                            int myCount = 0;
                            for (TPreenregistrementCompteClientTiersPayent op : finalTp) {
                                virtualAmont += op.getIntPRICE();
                                myCount++;
                                if (virtualAmont > p.getIntMONTANTFAC()) {
                                    finalTp = finalTp.subList(0, myCount - 1);
                                    break;
                                }

                            }
                        }

                        break;
                    case 2:
                        int count = finalTp.size();
                        int _count = p.getIntNBREBONS();
                        if (count > _count) {

                            finalTp.subList(0, _count);
                        }
                        break;
                    default:

                        break;

                }
                try {
                    if (finalTp.size() > 0) {
                        TFacture of = this.createInvoices(finalTp, date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, u);
                        if (of != null) {
                            factures.add(of);
                            TGroupeFactures f = createGroupeFacture(g, of, CODEFACTURE, em);
                            of.getTGroupeFacturesList().add(f);
                        }
                    }

                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            });
            grfact.put(g.getLgGROUPEID(), factures);

        });
        em.getTransaction().commit();
        return grfact;
    }

    public TFacture createInvoices(List<TPreenregistrementCompteClientTiersPayent> list, Date dt_debut, Date dt_fin, TTiersPayant OTTiersPayant, EntityManager em, TUser u) {

        LongAdder totalRemise = new LongAdder();
        final double TauxRemise = (OTTiersPayant.getDblPOURCENTAGEREMISE() != null ? (OTTiersPayant.getDblPOURCENTAGEREMISE() / 100) : 1);
        LongAdder totalBrut = new LongAdder();

        double montantForfetaire = 0;

        montantForfetaire = OTTiersPayant.getDblREMISEFORFETAIRE();

        TTypeFacture OTTypeFacture = em.find(TTypeFacture.class, commonparameter.KEY_TYPE_FACTURE_TIERSPAYANT);
        TTypeMvtCaisse OTTypeMvtCaisse = em.find(TTypeMvtCaisse.class, commonparameter.KEY_TYPE_FACTURE_TIERSPAYANT);
        final TFacture OFacture = this.createInvoiceItem(dt_debut, dt_fin, 0d, null, OTTypeFacture, OTTypeMvtCaisse.getStrCODECOMPTABLE(), OTTiersPayant.getLgTIERSPAYANTID(), 0, 0, 0, em, OTTiersPayant, u);

        if (OFacture != null) {
            list.forEach((pr) -> {
//                    double montantremise = Math.round((Math.round(pr.getIntPRICE() * (OTTiersPayant.getDblPOURCENTAGEREMISE() / 100))));
                Integer montantremise = DateConverter.getRemise(TauxRemise, pr.getIntPERCENT(), findItems(pr.getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID(), em));
                totalRemise.add(montantremise);
                totalBrut.add(pr.getIntPRICE());
                Integer montantNetDetails = pr.getIntPRICE() - montantremise;
                if (this.invoiceDetail(OFacture, pr, montantNetDetails, pr.getLgPREENREGISTREMENTID().getStrREFBON(), pr.getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID(), pr.getIntPRICE(), montantremise, em)) {
                    pr.setStrSTATUTFACTURE(commonparameter.CHARGED);
                    em.merge(pr);

                }

            });
            int _totalBrut = totalBrut.intValue();
            int _totalRemise = totalRemise.intValue();
            OFacture.setIntNBDOSSIER(list.size());
            OFacture.setDblMONTANTBrut(new BigDecimal(_totalBrut));
            OFacture.setDblMONTANTCMDE((_totalBrut - montantForfetaire) - _totalRemise);
            OFacture.setDblMONTANTRESTANT((_totalBrut - montantForfetaire) - _totalRemise);
            OFacture.setDblMONTANTFOFETAIRE(new BigDecimal(montantForfetaire));
            OFacture.setDblMONTANTREMISE(new BigDecimal(_totalRemise));
            em.persist(OFacture);
            updateInvoicePlafond(OFacture, OTTiersPayant, em);
        }
        return OFacture;

    }
private boolean getParametreFacturation(){
    try {
         TParameters o = getEntityManager().find(TParameters.class, Parameter.KEY_CODE_NUMERARTION_FACTURE);
         return  Integer.valueOf(o.getStrVALUE()).compareTo(1)==0;
    } catch (Exception e) {
        return false;
    }
}
    public TFacture createInvoiceItem(Date dt_debut, Date dt_fin, double d_montant, String str_pere, TTypeFacture OTTypeFacture, String str_CODE_COMPTABLE, String str_CUSTOMER, Integer NB_DOSSIER, long montantRemise, long montantFofetaire, EntityManager em, TTiersPayant tiersPayant, TUser u) {
        try {
            TFacture OTFacture = new TFacture();
            if (OTTypeFacture == null) {
                return null;
            }
            TParameters OParameters = em.find(TParameters.class, Parameter.KEY_CODE_FACTURE);

            String CODEFACTURE = OParameters.getStrVALUE();

            OTFacture.setLgFACTUREID(new date().getComplexId());
            OTFacture.setDtDEBUTFACTURE(dt_debut);

            if (str_pere == null) {
                OTFacture.setStrPERE(OTFacture.getLgFACTUREID());
            } else {
                OTFacture.setStrPERE(str_pere);
            }

            OTFacture.setLgTYPEFACTUREID(OTTypeFacture);
            OTFacture.setDtFINFACTURE(dt_fin);
            OTFacture.setStrCUSTOMER(str_CUSTOMER);
            OTFacture.setDtDATEFACTURE(new Date());
            //add nombre dossier
            OTFacture.setDblMONTANTCMDE((d_montant - montantRemise));
             boolean numerationFacture=getParametreFacturation();
            if(numerationFacture){
                 OTFacture.setStrCODEFACTURE(LocalDate.now().format(DateTimeFormatter.ofPattern("yy")).concat("_").concat(CODEFACTURE));
            }else{
                OTFacture.setStrCODEFACTURE(CODEFACTURE); 
            }
            
            OTFacture.setStrCODECOMPTABLE(str_CODE_COMPTABLE);
            OTFacture.setDblMONTANTRESTANT((d_montant - montantRemise));
            OTFacture.setDblMONTANTPAYE(0.0);
            OTFacture.setDblMONTANTBrut(new BigDecimal(0));
            OTFacture.setDblMONTANTFOFETAIRE(new BigDecimal(0));
            OTFacture.setDblMONTANTREMISE(new BigDecimal(0));
            OTFacture.setIntNBDOSSIER(NB_DOSSIER);
            OTFacture.setDtCREATED(new Date());
            OTFacture.setTiersPayant(tiersPayant);
            OTFacture.setStrSTATUT(commonparameter.statut_enable);
            em.persist(OTFacture);
            OParameters.setStrVALUE((Integer.valueOf(CODEFACTURE) + 1) + "");
            em.merge(OParameters);

            return OTFacture;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

    }

    public void updateItem(TUser user, String ref, String desc, TypeLog typeLog, String t, EntityManager em) {
        TEventLog eventLog = new TEventLog(UUID.randomUUID().toString());
        eventLog.setLgUSERID(user);
        eventLog.setDtCREATED(new Date());
        eventLog.setDtUPDATED(new Date());
        eventLog.setStrCREATEDBY(user.getStrLOGIN());
        eventLog.setStrSTATUT(commonparameter.statut_enable);
        eventLog.setStrTABLECONCERN(t);
        eventLog.setTypeLog(typeLog);
        eventLog.setStrDESCRIPTION(desc + " référence [" + ref + " ]");
        eventLog.setStrTYPELOG(ref);
        em.persist(eventLog);
    }

    private void updateInvoicePlafond(TFacture facture, TTiersPayant OTTiersPayant, EntityManager em) {
        boolean isAbsolute = OTTiersPayant.getBCANBEUSE();
        if (!isAbsolute) {
            OTTiersPayant.setDbCONSOMMATIONMENSUELLE(0);
            OTTiersPayant.setBCANBEUSE(true);
            em.merge(OTTiersPayant);
        }
        List<TCompteClientTiersPayant> list = (List<TCompteClientTiersPayant>) OTTiersPayant.getTCompteClientTiersPayantCollection();
        list.stream().filter((compteClientTiersPayant) -> (!compteClientTiersPayant.getBIsAbsolute())).map((compteClientTiersPayant) -> {
            compteClientTiersPayant.setBCANBEUSE(true);
            return compteClientTiersPayant;
        }).map((compteClientTiersPayant) -> {
            compteClientTiersPayant.setDbCONSOMMATIONMENSUELLE(0);
            return compteClientTiersPayant;
        }).forEachOrdered((compteClientTiersPayant) -> {
            em.merge(compteClientTiersPayant);
        });
    }

    private List<TPreenregistrementDetail> findItems(String OTPreenregistrement, EntityManager em) {

        try {

            TypedQuery<TPreenregistrementDetail> q = em.
                    createQuery("SELECT t FROM TPreenregistrementDetail t WHERE  t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1", TPreenregistrementDetail.class).
                    setParameter(1, OTPreenregistrement);

            return q.getResultList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public boolean invoiceDetail(TFacture OTFacture, TPreenregistrementCompteClientTiersPayent payent, int montant, String str_ref_description, String str_PKEY_PREENREGISTREMENT, int montantBrut, int montantRemise, EntityManager em) {
        TFactureDetail OTFactureDetail = new TFactureDetail();
        //EntityManager em = getEntityManager();
        try {

            if (OTFacture == null) {

                return false;
            }
            String str_CATEGORY = "";
            if (payent.getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getLgCATEGORYCLIENTID() != null) {
                str_CATEGORY = payent.getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getLgCATEGORYCLIENTID().getStrLIBELLE();
            }
            TPreenregistrement preenregistrement = payent.getLgPREENREGISTREMENTID();
            OTFactureDetail.setLgFACTUREDETAILID(new date().getComplexId());
            OTFactureDetail.setLgFACTUREID(OTFacture);
            OTFactureDetail.setStrREF(payent.getLgPREENREGISTREMENTCOMPTECLIENTPAYENTID());
            OTFactureDetail.setStrREFDESCRIPTION(str_ref_description);
            OTFactureDetail.setStrCATEGORYCLIENT(str_CATEGORY);
            OTFactureDetail.setDblMONTANT(Double.valueOf(montant));
            OTFactureDetail.setDblMONTANTBrut(new BigDecimal(montantBrut));
            OTFactureDetail.setDblMONTANTPAYE(0.0);
            OTFactureDetail.setPKey(str_PKEY_PREENREGISTREMENT);
            OTFactureDetail.setDblMONTANTREMISE(new BigDecimal(montantRemise));
            OTFactureDetail.setDblMONTANTRESTANT(Double.valueOf(montant));
            OTFactureDetail.setStrSTATUT(commonparameter.statut_enable);
            OTFactureDetail.setDtCREATED(new Date());
            OTFactureDetail.setDtUPDATED(new Date());
            OTFactureDetail.setStrFIRSTNAMECUSTOMER(preenregistrement.getStrFIRSTNAMECUSTOMER());
            OTFactureDetail.setStrLASTNAMECUSTOMER(preenregistrement.getStrLASTNAMECUSTOMER());
            OTFactureDetail.setStrNUMEROSECURITESOCIAL(preenregistrement.getStrNUMEROSECURITESOCIAL());
            OTFactureDetail.setTaux(payent.getIntPERCENT());
            OTFactureDetail.setAyantDroit(preenregistrement.getAyantDroit());
            OTFactureDetail.setClient(preenregistrement.getClient());
            OTFactureDetail.setMontantRemiseVente(preenregistrement.getIntPRICEREMISE());
            OTFactureDetail.setMontantTvaVente(preenregistrement.getMontantTva());
            OTFactureDetail.setMontantVente(preenregistrement.getIntPRICE());
            OTFactureDetail.setDateOperation(preenregistrement.getDtUPDATED());
            em.persist(OTFactureDetail);

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

    }

    //groupe facture
    public TFacture createInvoiceItemForGrp(Date dt_debut, Date dt_fin, double d_montant, String str_pere, TTypeFacture OTTypeFacture, String str_CODE_COMPTABLE, String str_CUSTOMER, Integer NB_DOSSIER, long montantRemise, long montantFofetaire, EntityManager em) {
        try {
            TFacture OTFacture = new TFacture();
            if (OTTypeFacture == null) {
                return null;
            }
            TParameters OParameters = em.find(TParameters.class, Parameter.KEY_CODE_FACTURE);

            String CODEFACTURE = OParameters.getStrVALUE();

            OTFacture.setLgFACTUREID(new date().getComplexId());
            OTFacture.setDtDEBUTFACTURE(dt_debut);

            if (str_pere == null) {
                OTFacture.setStrPERE(OTFacture.getLgFACTUREID());
            } else {
                OTFacture.setStrPERE(str_pere);
            }

            OTFacture.setLgTYPEFACTUREID(OTTypeFacture);
            OTFacture.setDtFINFACTURE(dt_fin);
            OTFacture.setStrCUSTOMER(str_CUSTOMER);
            OTFacture.setDtDATEFACTURE(new Date());
            //add nombre dossier
            OTFacture.setDblMONTANTCMDE((d_montant - montantRemise));
             boolean numerationFacture=getParametreFacturation();
            if(numerationFacture){
                 OTFacture.setStrCODEFACTURE(LocalDate.now().format(DateTimeFormatter.ofPattern("yy")).concat("_").concat(CODEFACTURE));
            }else{
                OTFacture.setStrCODEFACTURE(CODEFACTURE); 
            }
           
            OTFacture.setStrCODECOMPTABLE(str_CODE_COMPTABLE);
            OTFacture.setDblMONTANTRESTANT((d_montant - montantRemise));
            OTFacture.setDblMONTANTPAYE(0.0);
            OTFacture.setDblMONTANTBrut(new BigDecimal(0));
            OTFacture.setDblMONTANTFOFETAIRE(new BigDecimal(0));
            OTFacture.setDblMONTANTREMISE(new BigDecimal(0));
            OTFacture.setIntNBDOSSIER(NB_DOSSIER);
            OTFacture.setDtCREATED(new Date());
            OTFacture.setStrSTATUT(commonparameter.statut_enable);
            em.persist(OTFacture);
            OParameters.setStrVALUE((Integer.valueOf(CODEFACTURE) + 1) + "");
            em.merge(OParameters);

            return OTFacture;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

    }

    public List<TTiersPayant> findTierspayant(JSONArray lstp) {
        EntityManager em = getEntityManager();
        List<TTiersPayant> list = new ArrayList<>();
        try {

            for (int i = 0; i < lstp.length(); i++) {
                TTiersPayant payant = em.find(TTiersPayant.class, lstp.getString(i));
                list.add(payant);
            }

            //return q.getResultList();
        } catch (Exception e) {

        }
        return list;
    }

    public List<TPreenregistrementCompteClientTiersPayent> getSelectedTpBons(boolean all, JSONArray lstp, String dt_start, String dt_end, int start, int limit) {
        List<TPreenregistrementCompteClientTiersPayent> list = new ArrayList<>();
        try {
            List<TTiersPayant> tp = findTierspayant(lstp);
            tp.forEach((tTiersPayant) -> {
                list.addAll(this.getGroupeBons(all, dt_start, dt_end, start, limit, tTiersPayant.getLgTIERSPAYANTID(), -1, ""));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private Map<String, LinkedHashSet<TFacture>> generateFacture(List<TTiersPayant> payants, String dt_start, String dt_end, Integer lgGRP, TUser u) {
        EntityManager em = getEntityManager();
        TParameters OParameters = em.find(TParameters.class, Parameter.KEY_CODE_FACTURE);
        em.getTransaction().begin();
        Map<String, LinkedHashSet<TFacture>> grfact = new HashMap<>();
        LinkedHashSet<TFacture> factures = new LinkedHashSet<>();

        TGroupeTierspayant g = em.find(TGroupeTierspayant.class, lgGRP);

        String CODEFACTURE = OParameters.getStrVALUE();
        OParameters.setStrVALUE((Integer.valueOf(CODEFACTURE) + 1) + "");
        em.merge(OParameters);
        payants.forEach((p) -> {

            List<TPreenregistrementCompteClientTiersPayent> finalTp = this.getGroupeBons(true, dt_start, dt_end, -1, -1, p.getLgTIERSPAYANTID(), lgGRP, "");

            switch (getCase(p)) {

                case 1:

                    long montantFact = finalTp.stream().mapToLong((_qty) -> {
                        return _qty.getIntPRICE();
                    }).sum();

                    if (p.getIntMONTANTFAC() < montantFact) {
                        Integer virtualAmont = 0;
                        int myCount = 0;
                        for (TPreenregistrementCompteClientTiersPayent op : finalTp) {

                            virtualAmont += op.getIntPRICE();
                            myCount++;

                            if (virtualAmont > p.getIntMONTANTFAC()) {
                                finalTp = finalTp.subList(0, myCount - 1);
                                break;
                            }

                        }
                    }

                    break;
                case 2:

                    int count = finalTp.size();
                    int _count = p.getIntNBREBONS();
                    if (count > _count) {

                        finalTp = finalTp.subList(0, _count);

                    }
                    break;
                default:

                    break;

            }
            try {

                if (finalTp.size() > 0) {
                    TFacture of = this.createInvoices(finalTp, date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, u);

                    factures.add(of);
                    createGroupeFacture(g, of, CODEFACTURE, em);
                }

            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        });
        grfact.put(CODEFACTURE, factures);

        em.getTransaction().commit();
        return grfact;
    }

    private int getCase(TTiersPayant p) {
        int i = 0;
        if (p.getIntNBREBONS() > 0 && p.getIntMONTANTFAC() > 0) {
            i = 1;
        } else if (p.getIntNBREBONS() > 0 && p.getIntMONTANTFAC() <= 0) {
            i = 2;
        } else if (p.getIntNBREBONS() <= 0 && p.getIntMONTANTFAC() > 0) {
            i = 1;
        }
        return i;
    }

    private TGroupeFactures createGroupeFacture(TGroupeTierspayant g, TFacture op, String codeFacture, EntityManager em) {
        TGroupeFactures factures = null;
        try {

            factures = new TGroupeFactures();
            factures.setDtCREATED(op.getDtCREATED());
            factures.setDtDEBUTFACTURE(op.getDtDEBUTFACTURE());
            factures.setDtFINFACTURE(op.getDtFINFACTURE());
            factures.setDtUPDATED(op.getDtUPDATED());
            factures.setStrCODEFACTURE(codeFacture);
            factures.setIntAMOUNT(op.getDblMONTANTRESTANT().intValue());
            factures.setIntNBDOSSIER(op.getIntNBDOSSIER().shortValue());
            factures.setLgGROUPEID(g);
            factures.setLgFACTURESID(op);
            factures.setIntPAYE(0);
            em.persist(factures);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return factures;
    }

    public List<TPreenregistrementCompteClientTiersPayent> getGroupeBonsInterval(String dt_start, String dt_end, String lgTP_ID, Integer lgGRP, JSONArray listExclud) {
        EntityManager em = null;
        try {

            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TPreenregistrementCompteClientTiersPayent> cq = cb.createQuery(TPreenregistrementCompteClientTiersPayent.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementCompteClientTiersPayent, TCompteClientTiersPayant> cmpt = root.join("lgCOMPTECLIENTTIERSPAYANTID", JoinType.INNER);
            Join<TCompteClientTiersPayant, TTiersPayant> cmp = cmpt.join("lgTIERSPAYANTID", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            if (lgGRP != 0 && lgGRP > 0) {
                Join<TTiersPayant, TGroupeTierspayant> tps = cmp.join("lgGROUPEID", JoinType.INNER);
                ParameterExpression<Integer> p = cb.parameter(Integer.class, "lgGROUPEID");

                criteria = cb.and(criteria, cb.equal(tps.get("lgGROUPEID"), p));
            }
            if (!"".equals(lgTP_ID)) {

                ParameterExpression<String> p = cb.parameter(String.class, "lgTIERSPAYANTID");
                criteria = cb.and(criteria, cb.equal(root.get("lgCOMPTECLIENTTIERSPAYANTID").get("lgTIERSPAYANTID").get("lgTIERSPAYANTID"), p));
            }

            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("strSTATUTFACTURE"), "unpaid"));
            for (int i = 0; i < listExclud.length(); i++) {
                String idPr;
                try {
                    idPr = listExclud.getString(i);
                    criteria = cb.and(criteria, cb.notEqual(root.get("lgPREENREGISTREMENTCOMPTECLIENTPAYENTID"), idPr));
                } catch (JSONException ex) {

                }

            }

            Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            Predicate pu2 = cb.greaterThan(root.get("intPRICE"), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("lgPREENREGISTREMENTID").get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));

            cq.where(criteria, cb.and(pu), cb.and(pu2), cb.and(btw));

            Query q = em.createQuery(cq);
            if (lgGRP != 0 && lgGRP > 0) {

                q.setParameter("lgGROUPEID", lgGRP);
            }
            if (!"".equals(lgTP_ID)) {
                q.setParameter("lgTIERSPAYANTID", lgTP_ID);
            }

            return q.getResultList();
        } finally {
            if (em != null) {

            }
        }
    }

    public List<TPreenregistrementCompteClientTiersPayent> getGroupeBonsInterval(String dt_start, String dt_end, String lgTP_ID, JSONArray listExclud) {
        EntityManager em = null;
        try {

            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TPreenregistrementCompteClientTiersPayent> cq = cb.createQuery(TPreenregistrementCompteClientTiersPayent.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementCompteClientTiersPayent, TCompteClientTiersPayant> cmpt = root.join("lgCOMPTECLIENTTIERSPAYANTID", JoinType.INNER);
            Join<TCompteClientTiersPayant, TTiersPayant> cmp = cmpt.join("lgTIERSPAYANTID", JoinType.INNER);
            Predicate criteria = cb.conjunction();

            if (!"".equals(lgTP_ID)) {

                ParameterExpression<String> p = cb.parameter(String.class, "lgTIERSPAYANTID");
                criteria = cb.and(criteria, cb.equal(root.get("lgCOMPTECLIENTTIERSPAYANTID").get("lgTIERSPAYANTID").get("lgTIERSPAYANTID"), p));
            }

            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("strSTATUTFACTURE"), "unpaid"));
            for (int i = 0; i < listExclud.length(); i++) {
                String idPr;
                try {
                    idPr = listExclud.getString(i);
                    criteria = cb.and(criteria, cb.notEqual(root.get("lgPREENREGISTREMENTCOMPTECLIENTPAYENTID"), idPr));
                } catch (JSONException ex) {

                }

            }

            Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            Predicate pu2 = cb.greaterThan(root.get("intPRICE"), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("lgPREENREGISTREMENTID").get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));

            cq.where(criteria, cb.and(pu), cb.and(pu2), cb.and(btw));

            Query q = em.createQuery(cq);

            if (!"".equals(lgTP_ID)) {
                q.setParameter("lgTIERSPAYANTID", lgTP_ID);
            }

            return q.getResultList();
        } finally {
            if (em != null) {

            }
        }
    }

    ///facture avec selection de bon
    private LinkedHashSet<TFacture> generateFacture(String dt_start, String dt_end, JSONArray listExclud, String lgTP, TUser us) {
        EntityManager em = getEntityManager();

        em.getTransaction().begin();

        LinkedHashSet<TFacture> factures = new LinkedHashSet<>();
        TTiersPayant p = em.find(TTiersPayant.class, lgTP);
        List<TPreenregistrementCompteClientTiersPayent> finalTp = this.getGroupeBonsInterval(dt_start, dt_end, p.getLgTIERSPAYANTID(), listExclud);

        switch (getCase(p)) {

            case 1:
                final long montantFact = finalTp.stream().mapToLong((_qty) -> {
                    return _qty.getIntPRICE();
                }).sum();
                if (p.getIntMONTANTFAC() < montantFact) {
                    Integer virtualAmont = 0;
                    int myCount = 0;
                    for (TPreenregistrementCompteClientTiersPayent op : finalTp) {
                        virtualAmont += op.getIntPRICE();
                        myCount++;
                        if (virtualAmont > p.getIntMONTANTFAC()) {
                            finalTp = finalTp.subList(0, myCount - 1);
                            break;
                        }

                    }
                }

                break;
            case 2:
                int count = finalTp.size();
                int _count = p.getIntNBREBONS();
                if (count > _count) {
                    int virtualCount = 0;
                    finalTp.subList(0, _count);
                }
                break;
            default:

                break;

        }
        try {
            if (finalTp.size() > 0) {
                TFacture of = this.createInvoices(finalTp, date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, us);
                if (of != null) {
                    factures.add(of);

                }
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        em.getTransaction().commit();
        return factures;
    }

    public List<TPreenregistrementCompteClientTiersPayent> getSelectedTpBons(JSONArray lstp, String dt_start, String dt_end, String refBon) {
        List<TPreenregistrementCompteClientTiersPayent> list = new ArrayList<>();
        try {
            List<TTiersPayant> tp = findTierspayant(lstp);
            tp.forEach((tTiersPayant) -> {
                list.addAll(this.getGroupeBons(true, dt_start, dt_end, -1, -1, tTiersPayant.getLgTIERSPAYANTID(), -1, refBon));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<TPreenregistrementCompteClientTiersPayent> getGroupeSelectedBons(JSONArray selectedBons) {
        EntityManager em = null;
        List<TPreenregistrementCompteClientTiersPayent> list = new ArrayList<>();
        try {

            em = getEntityManager();

            for (int i = 0; i < selectedBons.length(); i++) {

                try {

                    TPreenregistrementCompteClientTiersPayent pr = em.find(TPreenregistrementCompteClientTiersPayent.class, selectedBons.getString(i));
                    list.add(pr);
                } catch (JSONException ex) {

                }

            }

            return list;
        } finally {
            if (em != null) {

            }
        }
    }

    public List<TPreenregistrementCompteClientTiersPayent> findAllBons(boolean all, String dt_start, String dt_end, int firstResult, int maxResults, String lgTP_ID, String refBon) {
        EntityManager em = null;
        try {

            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TPreenregistrementCompteClientTiersPayent> cq = cb.createQuery(TPreenregistrementCompteClientTiersPayent.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementCompteClientTiersPayent, TCompteClientTiersPayant> cmpt = root.join("lgCOMPTECLIENTTIERSPAYANTID", JoinType.INNER);
            Join<TCompteClientTiersPayant, TTiersPayant> cmp = cmpt.join("lgTIERSPAYANTID", JoinType.INNER);
            Predicate criteria = cb.conjunction();

            if (!"".equals(lgTP_ID)) {

                ParameterExpression<String> p = cb.parameter(String.class, "lgTIERSPAYANTID");
                criteria = cb.and(criteria, cb.equal(root.get("lgCOMPTECLIENTTIERSPAYANTID").get("lgTIERSPAYANTID").get("lgTIERSPAYANTID"), p));
            }

            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("strSTATUTFACTURE"), "unpaid"));
            if (!"".equals(refBon)) {
                criteria = cb.and(criteria, cb.like(root.get("strREFBON"), refBon + "%"));
            }

            Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            Predicate pu2 = cb.greaterThan(root.get("intPRICE"), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("lgPREENREGISTREMENTID").get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));

            cq.where(criteria, cb.and(pu), cb.and(pu2), cb.and(btw));

            Query q = em.createQuery(cq);

            if (!"".equals(lgTP_ID)) {
                q.setParameter("lgTIERSPAYANTID", lgTP_ID);
            }
//           

            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            if (em != null) {

            }
        }
    }

    public int allBonsCount(String dt_start, String dt_end, String lgTP_ID, String refBon) {
        EntityManager em = null;
        try {

            em = getEntityManager();
//         
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementCompteClientTiersPayent, TCompteClientTiersPayant> cmpt = root.join("lgCOMPTECLIENTTIERSPAYANTID", JoinType.INNER);
            Join<TCompteClientTiersPayant, TTiersPayant> cmp = cmpt.join("lgTIERSPAYANTID", JoinType.INNER);
            Predicate criteria = cb.conjunction();

            if (!"".equals(lgTP_ID)) {

                ParameterExpression<String> p = cb.parameter(String.class, "lgTIERSPAYANTID");
                criteria = cb.and(criteria, cb.equal(root.get("lgCOMPTECLIENTTIERSPAYANTID").get("lgTIERSPAYANTID").get("lgTIERSPAYANTID"), p));
            }

            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("strSTATUTFACTURE"), "unpaid"));
            if (!"".equals(refBon)) {
                criteria = cb.and(criteria, cb.like(root.get("strREFBON"), refBon + "%"));
            }

            Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            Predicate pu2 = cb.greaterThan(root.get("intPRICE"), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("lgPREENREGISTREMENTID").get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));

            cq.select(cb.count(root));
            cq.where(criteria, cb.and(pu), cb.and(pu2), cb.and(btw));

            Query q = em.createQuery(cq);

            if (!"".equals(lgTP_ID)) {
                q.setParameter("lgTIERSPAYANTID", lgTP_ID);
            }

            return ((Long) q.getSingleResult()).intValue();
        } finally {
            if (em != null) {

            }
        }
    }

    //facture selection 
    public LinkedHashSet<TFacture> generateFacture(String dt_start, String dt_end, JSONArray selectedList, TUser u) {
        EntityManager em = getEntityManager();
        TParameters OParameters = em.find(TParameters.class, Parameter.KEY_CODE_FACTURE);
        em.getTransaction().begin();

        LinkedHashSet<TFacture> factures = new LinkedHashSet<>();
        List<TPreenregistrementCompteClientTiersPayent> oblist = this.getGroupeSelectedBons(selectedList);

        Map<TTiersPayant, List<TPreenregistrementCompteClientTiersPayent>> mysList = oblist.stream().collect(Collectors.groupingBy(s -> s.getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID()));
        mysList.entrySet().forEach((t) -> {
            TTiersPayant p = t.getKey();
            List<TPreenregistrementCompteClientTiersPayent> finalTp = t.getValue();
            switch (getCase(p)) {

                case 1:

                    long montantFact = finalTp.stream().mapToLong((_qty) -> {
                        return _qty.getIntPRICE();
                    }).sum();

                    if (p.getIntMONTANTFAC() < montantFact) {
                        Integer virtualAmont = 0;
                        int myCount = 0;
                        for (TPreenregistrementCompteClientTiersPayent op : finalTp) {

                            virtualAmont += op.getIntPRICE();
                            myCount++;

                            if (virtualAmont > p.getIntMONTANTFAC()) {
                                finalTp = finalTp.subList(0, myCount - 1);
                                break;
                            }

                        }
                    }

                    break;
                case 2:

                    int count = finalTp.size();
                    int _count = p.getIntNBREBONS();
                    if (count > _count) {

                        finalTp = finalTp.subList(0, _count);

                    }
                    break;
                default:

                    break;

            }
            try {

                if (finalTp.size() > 0) {
                    TFacture of = this.createInvoices(finalTp, date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, u);

                    factures.add(of);

                }

            } catch (ParseException ex) {
                ex.printStackTrace();
            }

        });

        em.getTransaction().commit();
        return factures;

    }

    public JSONArray getGoupBons(boolean all, String dt_start, String dt_end, Integer idGroup, String search, int start, int limit) {
        JSONArray list = new JSONArray();
        EntityManager em = null;
        try {

            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementCompteClientTiersPayent, TCompteClientTiersPayant> cmpt = root.join("lgCOMPTECLIENTTIERSPAYANTID", JoinType.INNER);
            Join<TCompteClientTiersPayant, TTiersPayant> cmp = cmpt.join("lgTIERSPAYANTID", JoinType.INNER);
            Join<TTiersPayant, TGroupeTierspayant> tps = cmp.join("lgGROUPEID", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            if (idGroup > 0) {
                ParameterExpression<Integer> p = cb.parameter(Integer.class, "lgGROUPEID");

                criteria = cb.and(criteria, cb.equal(tps.get("lgGROUPEID"), p));
            }
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("strSTATUTFACTURE"), "unpaid"));
            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.like(root.get("lgCOMPTECLIENTTIERSPAYANTID").get("lgTIERSPAYANTID").get("strFULLNAME"), search + "%"));
            }

            Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            Predicate pu2 = cb.greaterThan(root.get("intPRICE"), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("lgPREENREGISTREMENTID").get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));

            cq.multiselect(root.get("lgCOMPTECLIENTTIERSPAYANTID").get("lgTIERSPAYANTID").get("lgTIERSPAYANTID"), root.get("lgCOMPTECLIENTTIERSPAYANTID").get("lgTIERSPAYANTID").get("strFULLNAME"), cb.count(root), cb.sumAsLong(root.get("intPRICE")))
                    .groupBy(root.get("lgCOMPTECLIENTTIERSPAYANTID").get("lgTIERSPAYANTID").get("lgTIERSPAYANTID"));
            cq.where(criteria, cb.and(pu), cb.and(pu2), cb.and(btw));

            Query q = em.createQuery(cq);
            if (idGroup > 0) {
                q.setParameter("lgGROUPEID", idGroup);
            }
            if (!all) {
                q.setMaxResults(limit);
                q.setFirstResult(start);
            }
            List<Object[]> oblist = q.getResultList();

            oblist.forEach((objects) -> {
                try {
                    JSONObject json = new JSONObject();
                    json.put("lgTIERSPAYANTID", objects[0]).put("str_LIB", objects[1]).put("NBBONS", objects[2]).put("AMOUNT", objects[3]);
                    list.put(json);
                } catch (JSONException ex) {

                }
            });

        } finally {
            if (em != null) {

            }
        }

        return list;
    }

    public int getGoupBons(String dt_start, String dt_end, Integer idGroup, String search) {
        JSONArray list = new JSONArray();
        EntityManager em = null;
        try {

            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementCompteClientTiersPayent, TCompteClientTiersPayant> cmpt = root.join("lgCOMPTECLIENTTIERSPAYANTID", JoinType.INNER);
            Join<TCompteClientTiersPayant, TTiersPayant> cmp = cmpt.join("lgTIERSPAYANTID", JoinType.INNER);
            Join<TTiersPayant, TGroupeTierspayant> tps = cmp.join("lgGROUPEID", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            if (idGroup > 0) {
                ParameterExpression<Integer> p = cb.parameter(Integer.class, "lgGROUPEID");

                criteria = cb.and(criteria, cb.equal(tps.get("lgGROUPEID"), p));
            }
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("strSTATUTFACTURE"), "unpaid"));
            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.like(root.get("lgCOMPTECLIENTTIERSPAYANTID").get("lgTIERSPAYANTID").get("strFULLNAME"), search + "%"));
            }

            Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            Predicate pu2 = cb.greaterThan(root.get("intPRICE"), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("lgPREENREGISTREMENTID").get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));

            cq.select(cb.count(root))
                    .groupBy(root.get("lgCOMPTECLIENTTIERSPAYANTID").get("lgTIERSPAYANTID").get("lgTIERSPAYANTID"));
            cq.where(criteria, cb.and(pu), cb.and(pu2), cb.and(btw));

            Query q = em.createQuery(cq);
            if (idGroup > 0) {
                q.setParameter("lgGROUPEID", idGroup);
            }
            return q.getResultList().size();

        } finally {
            if (em != null) {

            }
        }

    }

    public List<TTiersPayant> findTGroupeTierspayantTierspayant(int idGp, JSONArray listExclud) {
        EntityManager em = null;
        try {
//            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            em = getEntityManager();
            TGroupeTierspayant groupeTierspayant = findTGroupeTierspayant(idGp);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TTiersPayant> cq = cb.createQuery(TTiersPayant.class);
            Root<TTiersPayant> root = cq.from(TTiersPayant.class);
            Join<TTiersPayant, TTiersPayant> j = root.join("lgGROUPEID", JoinType.INNER);
            cq.select(root);
            Predicate p = cb.conjunction();
            p = cb.and(p, cb.equal(root.get("lgGROUPEID"), groupeTierspayant));
            p = cb.and(p, cb.equal(root.get("strSTATUT"), "enable"));
            for (int i = 0; i < listExclud.length(); i++) {
                try {
                    String id = listExclud.getString(i);
                    p = cb.and(p, cb.notEqual(root.get("lgTIERSPAYANTID"), id));
                } catch (JSONException ex) {

                }

            }
            cq.where(p);
            Query q = em.createQuery(cq);

            return q.getResultList();
        } finally {
            if (em != null) {

            }
        }
    }

    public LinkedHashSet<TFacture> generateFacture(String dt_start, String dt_end, String lgTP, TUser u) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();

        LinkedHashSet<TFacture> factures = new LinkedHashSet<>();
        List<TPreenregistrementCompteClientTiersPayent> oblist = this.findAllBons(true, dt_start, dt_end, -1, -1, lgTP, "");

        Map<TTiersPayant, List<TPreenregistrementCompteClientTiersPayent>> mysList = oblist.stream().collect(Collectors.groupingBy(s -> s.getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID()));
        mysList.entrySet().forEach((t) -> {
            TTiersPayant p = t.getKey();
            List<TPreenregistrementCompteClientTiersPayent> finalTp = t.getValue();
            switch (getCase(p)) {

                case 1:

                    long montantFact = finalTp.stream().mapToLong((_qty) -> {
                        return _qty.getIntPRICE();
                    }).sum();

                    if (p.getIntMONTANTFAC() < montantFact) {
                        Integer virtualAmont = 0;
                        int myCount = 0;
                        for (TPreenregistrementCompteClientTiersPayent op : finalTp) {

                            virtualAmont += op.getIntPRICE();
                            myCount++;

                            if (virtualAmont > p.getIntMONTANTFAC()) {
                                finalTp = finalTp.subList(0, myCount - 1);
                                break;
                            }

                        }
                    }

                    break;
                case 2:

                    int count = finalTp.size();
                    int _count = p.getIntNBREBONS();
                    if (count > _count) {

                        finalTp = finalTp.subList(0, _count);

                    }
                    break;
                default:

                    break;

            }
            try {

                if (finalTp.size() > 0) {
                    TFacture of = this.createInvoices(finalTp, date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, u);

                    factures.add(of);

                }

            } catch (ParseException ex) {
                ex.printStackTrace();
            }

        });

        em.getTransaction().commit();
        return factures;

    }

    public LinkedHashSet<TFacture> generateFactureWithExcludBons(String dt_start, String dt_end, JSONArray listExclut, String lgTP, TUser u) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();

        LinkedHashSet<TFacture> factures = new LinkedHashSet<>();
        List<TPreenregistrementCompteClientTiersPayent> oblist = this.getGroupeBonsInterval(dt_start, dt_end, lgTP, listExclut);

        Map<TTiersPayant, List<TPreenregistrementCompteClientTiersPayent>> mysList = oblist.stream().collect(Collectors.groupingBy(s -> s.getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID()));
        mysList.entrySet().forEach((t) -> {
            TTiersPayant p = t.getKey();
            List<TPreenregistrementCompteClientTiersPayent> finalTp = t.getValue();
            switch (getCase(p)) {

                case 1:

                    long montantFact = finalTp.stream().mapToLong((_qty) -> {
                        return _qty.getIntPRICE();
                    }).sum();

                    if (p.getIntMONTANTFAC() < montantFact) {
                        Integer virtualAmont = 0;
                        int myCount = 0;
                        for (TPreenregistrementCompteClientTiersPayent op : finalTp) {

                            virtualAmont += op.getIntPRICE();
                            myCount++;

                            if (virtualAmont > p.getIntMONTANTFAC()) {
                                finalTp = finalTp.subList(0, myCount - 1);
                                break;
                            }

                        }
                    }

                    break;
                case 2:

                    int count = finalTp.size();
                    int _count = p.getIntNBREBONS();
                    if (count > _count) {

                        finalTp = finalTp.subList(0, _count);

                    }
                    break;
                default:

                    break;

            }
            try {

                if (finalTp.size() > 0) {
                    TFacture of = this.createInvoices(finalTp, date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, u);

                    factures.add(of);

                }

            } catch (ParseException ex) {
                ex.printStackTrace();
            }

        });

        em.getTransaction().commit();
        return factures;

    }

    public Set<TFacture> generateFacture(String dt_start, String dt_end, JSONArray listExclud, JSONArray selectedBons, String lgTP, int mode, TUser u) {
        Set<TFacture> grfact = null;

        switch (mode) {
            case 0:
                grfact = this.generateFacture(dt_start, dt_end, lgTP, u);

                break;
            case 1:
                grfact = this.generateFactureWithExcludBons(dt_start, dt_end, listExclud, lgTP, u);

                break;
            case 2:
                grfact = this.generateFacture(dt_start, dt_end, selectedBons, u);

                break;
        }

        return grfact;
    }

    public Integer groupeTiersPayantAmount(Integer idGrp, String codeFact) {
        EntityManager em = null;
        try {

            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TGroupeFactures> root = cq.from(TGroupeFactures.class);
            Join<TGroupeFactures, TGroupeFactures> pr = root.join("lgGROUPEID", JoinType.INNER);
            cq.select(cb.sum(root.get("intAMOUNT")));
            cq.where(cb.and(cb.equal(root.get("strCODEFACTURE"), codeFact)), cb.and(cb.equal(root.get("lgGROUPEID").get("lgGROUPEID"), idGrp)));

            Query q = em.createQuery(cq);

            return (Integer) q.getSingleResult();
        } finally {
            if (em != null) {

            }
        }
    }

    public TGroupeTierspayant getGroupByCODEFACT(String codeFacture) {
        TGroupeTierspayant groupeTierspayant = null;
        try {
            EntityManager em = getEntityManager();
            groupeTierspayant = (TGroupeTierspayant) em.createQuery("SELECT o.lgGROUPEID FROM TGroupeFactures o WHERE o.strCODEFACTURE=?1 ").setParameter(1, codeFacture)
                    .setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupeTierspayant;
    }

    public TGroupeFactures getgroupeFactureByCodeFacture(String codeFacture) {
        TGroupeFactures groupeTierspayant = null;
        try {
            EntityManager em = getEntityManager();
            groupeTierspayant = (TGroupeFactures) em.createQuery("SELECT o FROM TGroupeFactures o WHERE o.strCODEFACTURE=?1 ").setParameter(1, codeFacture)
                    .setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupeTierspayant;
    }

    public JSONArray getGroupeInvoice(boolean all, String dt_start, String dt_end, String search, Integer idGrp, String CODEGROUPE, boolean ACTION_REGLER_FACTURE, int start, int limit) {
        JSONArray list = new JSONArray();
        EntityManager em = null;
        try {

            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TGroupeFactures> root = cq.from(TGroupeFactures.class);
            Join<TGroupeFactures, TGroupeTierspayant> pr = root.join("lgGROUPEID", JoinType.INNER);
            Join<TGroupeFactures, TFacture> gf = root.join("lgFACTURESID", JoinType.INNER);

            Predicate criteria = cb.conjunction();

            if (idGrp > 0) {
                criteria = cb.and(criteria, cb.equal(root.get("lgGROUPEID").get("lgGROUPEID"), idGrp));
            }
            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.or(cb.like(root.get("lgGROUPEID").get("strLIBELLE"), search + "%"),
                        cb.like(root.get("lgFACTURESID").get("strCODEFACTURE"), search + "%"), cb.like(root.get("strCODEFACTURE"), search + "%")));
            }
//           
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("dtCREATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            if ("".equals(CODEGROUPE)) {
                criteria = cb.and(criteria, btw);
            } else {
                criteria = cb.and(criteria, cb.equal(root.get(TGroupeFactures_.strCODEFACTURE), CODEGROUPE));
            }
            cq.multiselect(root.get("lgGROUPEID").get("lgGROUPEID"), root.get("lgGROUPEID").get("strLIBELLE"), cb.count(root), cb.sumAsLong(root.get("lgFACTURESID").get("dblMONTANTRESTANT")), root.get("strCODEFACTURE"), root.get("dtCREATED"), root.get("lgFACTURESID").get("strSTATUT"), cb.sumAsLong(root.get("lgFACTURESID").get("dblMONTANTCMDE")), cb.sumAsLong(root.get("lgFACTURESID").get("dblMONTANTPAYE")))
                    .groupBy(root.get("strCODEFACTURE"));
            cq.where(criteria);

            Query q = em.createQuery(cq);

            if (!all) {
                q.setMaxResults(limit);
                q.setFirstResult(start);
            }
            List<Object[]> oblist = q.getResultList();

            oblist.forEach((objects) -> {
                try {
                    JSONObject json = new JSONObject();
                    String status = "enable";
                    if (Long.valueOf(objects[3] + "") == 0) {
                        status = "paid";
                    }
                    json.put("lg_GROUPE_ID", objects[0]).put("str_LIB", objects[1]).put("NBFACTURES", objects[2]).put("MONTANTRESTANT", objects[3]).put("CODEFACTURE", objects[4]).put("DATECREATION", date.formatterShort.format(objects[5])).put("STATUT", status).put("AMOUNT", objects[7]).put("AMOUNTPAYE", objects[8]);
                    json.put("ACTION_REGLER_FACTURE", ACTION_REGLER_FACTURE);
                    list.put(json);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            });

        } finally {
            if (em != null) {

            }
        }
        return list;
    }

    public int getGroupeInvoiceCount(String dt_start, String dt_end, String search, Integer idGrp, String CODEGROUPE) {

        EntityManager em = null;
        try {

            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TGroupeFactures> root = cq.from(TGroupeFactures.class);
            Join<TGroupeFactures, TGroupeTierspayant> pr = root.join("lgGROUPEID", JoinType.INNER);
            Join<TGroupeFactures, TFacture> cmpt = root.join("lgFACTURESID", JoinType.INNER);

            Predicate criteria = cb.conjunction();
            if (idGrp > 0) {
                criteria = cb.and(criteria, cb.equal(root.get("lgGROUPEID").get("lgGROUPEID"), idGrp));
            }
            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.or(cb.like(root.get("lgGROUPEID").get("strLIBELLE"), search + "%"), cb.like(root.get("lgFACTURESID").get("strCODEFACTURE"), search + "%")));
            }

            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("dtCREATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            if ("".equals(CODEGROUPE)) {
                criteria = cb.and(criteria, btw);
            } else {
                criteria = cb.and(criteria, cb.equal(root.get(TGroupeFactures_.strCODEFACTURE), CODEGROUPE));
            }
            cq.multiselect(cb.count(root))
                    .groupBy(root.get("strCODEFACTURE"));
            cq.where(criteria);

            Query q = em.createQuery(cq);

            return q.getResultList().size();

        } finally {
            if (em != null) {

            }
        }

    }

    public List<TGroupeFactures> getgroupeFacturesByCodeFacture(String codeFacture, Integer idGRP) {
        List<TGroupeFactures> groupeTierspayant = new ArrayList<>();
        try {
            EntityManager em = getEntityManager();
            groupeTierspayant = em.createQuery("SELECT o FROM TGroupeFactures o WHERE o.strCODEFACTURE=?1 AND o.lgGROUPEID.lgGROUPEID=?2 ")
                    .setParameter(1, codeFacture).setParameter(2, idGRP)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupeTierspayant;
    }

    public List<TFacture> getGroupeInvoiceDetails(String search, String codeFacture) {
        JSONArray list = new JSONArray();
        EntityManager em = null;
        try {

            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TFacture> cq = cb.createQuery(TFacture.class);
            Root<TFacture> root = cq.from(TFacture.class);
            Join< TGroupeFactures, TFacture> pr = root.join("tGroupeFacturesList", JoinType.INNER);

            Predicate criteria = cb.conjunction();

            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.or(cb.like(root.get("strCODEFACTURE"), search + "%")));
            }
            criteria = cb.and(criteria, cb.equal(pr.get("strCODEFACTURE"), codeFacture));
            criteria = cb.and(criteria, cb.notEqual(root.get("strSTATUT"), "paid"));
            cq.orderBy(cb.asc(pr.get("strCODEFACTURE")));
            cq.where(criteria);

            Query q = em.createQuery(cq);

            return q.getResultList();

        } finally {
            if (em != null) {

            }
        }

    }

    public List<TFacture> getGroupeInvoiceDetails(boolean all, String search, String lgTP, String codeFacture, int start, int limit) {

        EntityManager em = null;
        try {

            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TFacture> cq = cb.createQuery(TFacture.class);
            Root<TFacture> root = cq.from(TFacture.class);
            Join< TGroupeFactures, TFacture> pr = root.join("tGroupeFacturesList", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            if (!"".equals(lgTP)) {
//                 Join<TFacture, TTiersPayant> tp = root.join("str_CUSTOMER", JoinType.INNER);

                criteria = cb.and(criteria, cb.equal(root.get("strCUSTOMER"), lgTP));
            }
            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.or(cb.like(root.get("strCODEFACTURE"), search + "%")));
            }
            criteria = cb.and(criteria, cb.equal(pr.get("strCODEFACTURE"), codeFacture));

            cq.orderBy(cb.asc(pr.get("strCODEFACTURE")));
            cq.where(criteria);

            Query q = em.createQuery(cq);

            if (!all) {
                q.setMaxResults(limit);
                q.setFirstResult(start);
            }
            return q.getResultList();

        } finally {
            if (em != null) {

            }
        }

    }

    public int getGroupeInvoiceDetails(String search, String lgTP, String codeFacture) {

        EntityManager em = null;
        try {

            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TFacture> root = cq.from(TFacture.class);
            Join<TGroupeFactures, TFacture> pr = root.join("tGroupeFacturesList", JoinType.INNER);

            Predicate criteria = cb.conjunction();

            if (!"".equals(lgTP)) {

                criteria = cb.and(criteria, cb.equal(root.get("strCUSTOMER"), lgTP));
            }
            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.or(cb.like(root.get("strCODEFACTURE"), search + "%")));
            }
            criteria = cb.and(criteria, cb.equal(pr.get("strCODEFACTURE"), codeFacture));

            cq.select(cb.count(root));
            cq.where(criteria);

            Query q = em.createQuery(cq);

            return ((Long) q.getSingleResult()).intValue();

        } finally {
            if (em != null) {

            }
        }

    }

    public List<TFacture> getGroupeInvoiceDetails(JSONArray excludList, String codeFacture) {
        JSONArray list = new JSONArray();
        EntityManager em = null;
        try {

            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TFacture> cq = cb.createQuery(TFacture.class);
            Root<TFacture> root = cq.from(TFacture.class);
            Join< TGroupeFactures, TFacture> pr = root.join("tGroupeFacturesList", JoinType.INNER);

            Predicate criteria = cb.conjunction();
            for (int i = 0; i < excludList.length(); i++) {
                try {
                    criteria = cb.and(criteria, cb.notEqual(root.get("lgFACTUREID"), excludList.getString(i)));
                } catch (JSONException ex) {

                }

            }
            criteria = cb.and(criteria, cb.notEqual(root.get("strSTATUT"), "paid"));
            criteria = cb.and(criteria, cb.equal(pr.get("strCODEFACTURE"), codeFacture));
            cq.orderBy(cb.asc(pr.get("strCODEFACTURE")));
            cq.where(criteria);

            Query q = em.createQuery(cq);

            return q.getResultList();

        } finally {
            if (em != null) {

            }
        }

    }

    public int getGroupeInvoiceDetailsCount(String search, String codeFacture) {

        EntityManager em = null;
        try {

            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TFacture> root = cq.from(TFacture.class);
            Join< TGroupeFactures, TFacture> pr = root.join("tGroupeFacturesList", JoinType.INNER);

            Predicate criteria = cb.conjunction();

            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.or(cb.like(root.get("strCODEFACTURE"), search + "%")));
            }
            criteria = cb.and(criteria, cb.equal(pr.get("strCODEFACTURE"), codeFacture));
            criteria = cb.and(criteria, cb.notEqual(root.get("strSTATUT"), "paid"));
            cq.select(cb.count(root));
            cq.where(criteria);

            Query q = em.createQuery(cq);

            return ((Long) q.getSingleResult()).intValue();

        } finally {
            if (em != null) {

            }
        }

    }

    public boolean updateGroupFactureAmount(String LGFACTURE, Integer amount) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        boolean isOk = false;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TGroupeFactures factures = (TGroupeFactures) em.createQuery("SELECT o FROM TGroupeFactures o WHERE o.lgFACTURESID.lgFACTUREID =?1 ")
                    .setParameter(1, LGFACTURE).setMaxResults(1).getSingleResult();
            factures.setIntPAYE(amount);
            factures.setDtUPDATED(new Date());
            em.merge(factures);
            em.getTransaction().commit();
            isOk = true;
        } catch (Exception ex) {

            throw ex;
        } finally {
            if (em != null) {

            }

        }
        return isOk;
    }

    public List<TFacture> getGroupeSelectedInvoices(JSONArray selectedList) {
        List<TFacture> factlist = new ArrayList<>();
        EntityManager em = null;
        try {

            em = getEntityManager();

            for (int i = 0; i < selectedList.length(); i++) {
                try {
                    TFacture of = em.find(TFacture.class, selectedList.getString(i));
                    if (of != null) {
                        factlist.add(of);
                    }
                } catch (Exception ex) {

                }

            }

            return factlist;

        } finally {
            if (em != null) {

            }
        }

    }

    public JSONObject getReleveFacture(String dt_start, String dt_end, String search, String lgTP) {
        JSONObject json = new JSONObject();
        EntityManager em = null;
        try {

            em = getEntityManager();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            String query = "SELECT SUM(o.dblMONTANTRESTANT),SUM(o.dblMONTANTCMDE),SUM(o.dblMONTANTPAYE) FROM TFacture o,TTiersPayant p WHERE  FUNCTION('DATE',o.dtCREATED)>= FUNCTION('DATE',?3) AND FUNCTION('DATE',o.dtCREATED)<=FUNCTION('DATE',?4) AND  p.lgTIERSPAYANTID=o.strCUSTOMER ";

            if (!"".equals(lgTP)) {
                query += "AND o.strCUSTOMER LIKE ?1 ";
            }
            if (!"".equals(search)) {
                query += "AND (p.strFULLNAME LIKE ?2 OR p.strNAME LIKE ?2 OR p.strNUMEROCAISSEOFFICIEL LIKE ?2)";
            }
            Query q = em.createQuery(query);
            q.setParameter(3, dt_start);
            q.setParameter(4, dt_end);
            if (!"".equals(lgTP)) {
                q.setParameter(1, lgTP);
            }
            if (!"".equals(search)) {
                q.setParameter(2, search + "%");
            }

            List<Object[]> oblist = q.getResultList();

            oblist.forEach((objects) -> {
                try {

                    json.put("dblMONTANTRESTANT", objects[0]).put("dblMONTANTCMDE", objects[1]).put("dblMONTANTPAYE", objects[2]);

                } catch (JSONException ex) {

                }
            });

        } finally {
            if (em != null) {

            }
        }
        return json;
    }

    public Map<String, LinkedHashSet<TFacture>> generateInvoices(List<TTiersPayant> payants, String dt_start, String dt_end, Integer lgGRP, TUser us) {
        EntityManager em = getEntityManager();
        TParameters OParameters = em.find(TParameters.class, Parameter.KEY_CODE_FACTURE);
        em.getTransaction().begin();
        Map<String, LinkedHashSet<TFacture>> grfact = new HashMap<>();
        LinkedHashSet<TFacture> factures = new LinkedHashSet<>();

        TGroupeTierspayant g = em.find(TGroupeTierspayant.class, lgGRP);

        String CODEFACTURE = OParameters.getStrVALUE();
        OParameters.setStrVALUE((Integer.valueOf(CODEFACTURE) + 1) + "");
        em.merge(OParameters);
        payants.forEach((p) -> {

            List<TPreenregistrementCompteClientTiersPayent> finalTp = this.getGroupeBons(true, dt_start, dt_end, -1, -1, p.getLgTIERSPAYANTID(), lgGRP, "");

            switch (getCase(p)) {

                case 1:

                    long montantFact = finalTp.stream().mapToLong((_qty) -> {
                        return _qty.getIntPRICE();
                    }).sum();

                    if (p.getIntMONTANTFAC() < montantFact) {
                        Integer virtualAmont = 0;
                        int myCount = 0;
                        int volatilecount = 0;

                        for (TPreenregistrementCompteClientTiersPayent op : finalTp) {

                            if (virtualAmont > p.getIntMONTANTFAC()) {
                                try {
                                    if (myCount < finalTp.size()) {

                                        TFacture of = this.createInvoices(finalTp.subList(volatilecount, myCount - 1), date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, us);

                                        factures.add(of);

                                        createGroupeFacture(g, of, CODEFACTURE, em);

                                    } else if (myCount == (finalTp.size() - 1)) {

                                        TFacture of = this.createInvoices(finalTp.subList(volatilecount, finalTp.size()), date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, us);

                                        factures.add(of);
                                        createGroupeFacture(g, of, CODEFACTURE, em);
                                    }

                                    volatilecount = (myCount - 1);
                                    virtualAmont = (finalTp.get(volatilecount).getIntPRICE()) + (finalTp.get(myCount).getIntPRICE());

                                } catch (ParseException ex) {

                                }
                            } else if ((virtualAmont <= p.getIntMONTANTFAC()) && (myCount == (finalTp.size() - 1))) {
                                try {

                                    TFacture of = this.createInvoices(finalTp.subList(volatilecount, finalTp.size()), date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, us);

                                    factures.add(of);

                                    createGroupeFacture(g, of, CODEFACTURE, em);
                                } catch (ParseException ex) {

                                }

                            }
                            virtualAmont += op.getIntPRICE();
                            myCount++;

                        }

                    } else {
                        try {
                            TFacture of = this.createInvoices(finalTp, date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, us);

                            factures.add(of);
                            createGroupeFacture(g, of, CODEFACTURE, em);
                        } catch (ParseException ex) {

                        }

                    }

                    break;
                case 2:

                    int count = p.getIntNBREBONS();
                    int decrementCount = finalTp.size();
                    int _count = p.getIntNBREBONS();
                    int virtualCnt = 0;
                    Date dtstart = null;
                    Date dtend = null;
                    try {
                        dtstart = date.formatterMysqlShort.parse(dt_start);
                        dtend = date.formatterMysqlShort.parse(dt_end);
                    } catch (Exception e) {
                    }

                    if (finalTp.size() > _count) {
                        while (decrementCount > 0) {

                            if (count < finalTp.size()) {
                                TFacture of = this.createInvoices(finalTp.subList(virtualCnt, count), dtstart, dtend, p, em, us);

                                factures.add(of);
                                createGroupeFacture(g, of, CODEFACTURE, em);

                            } else {
                                TFacture of = this.createInvoices(finalTp.subList(virtualCnt, finalTp.size()), dtstart, dtend, p, em, us);

                                factures.add(of);
                                createGroupeFacture(g, of, CODEFACTURE, em);

                            }
                            virtualCnt += _count;
                            count += _count;
                            decrementCount -= (_count);
                        }

                    } else {

                        TFacture of = this.createInvoices(finalTp.subList(virtualCnt, finalTp.size()), dtstart, dtend, p, em, us);

                        factures.add(of);
                        createGroupeFacture(g, of, CODEFACTURE, em);

                    }
                    break;
                default:

                    if (finalTp.size() > 0) {
                        try {
                            TFacture of = this.createInvoices(finalTp, date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, us);

                            factures.add(of);
                            createGroupeFacture(g, of, CODEFACTURE, em);
                        } catch (Exception e) {
                        }

                    }

                    break;

            }

        });
        grfact.put(CODEFACTURE, factures);

        em.getTransaction().commit();
        return grfact;
    }

    private LinkedHashSet<TFacture> generateInvoices(List<TTiersPayant> payants, String dt_start, String dt_end, TGroupeTierspayant g, EntityManager em, String CODEFACTURE, TUser u) {

        LinkedHashSet<TFacture> factures = new LinkedHashSet<>();

        payants.forEach((p) -> {

            List<TPreenregistrementCompteClientTiersPayent> finalTp = this.getGroupeBons(true, dt_start, dt_end, -1, -1, p.getLgTIERSPAYANTID(), -1, "");

            switch (getCase(p)) {

                case 1:

                    long montantFact = finalTp.stream().mapToLong((_qty) -> {
                        return _qty.getIntPRICE();
                    }).sum();

                    if (p.getIntMONTANTFAC() < montantFact) {
                        Integer virtualAmont = 0;
                        int myCount = 0;
                        int volatilecount = 0;

                        for (TPreenregistrementCompteClientTiersPayent op : finalTp) {

                            if (virtualAmont > p.getIntMONTANTFAC()) {
                                try {
                                    if (myCount < finalTp.size()) {

                                        TFacture of = this.createInvoices(finalTp.subList(volatilecount, myCount - 1), date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, u);

                                        factures.add(of);

                                        createGroupeFacture(g, of, CODEFACTURE, em);

                                    } else if (myCount == (finalTp.size() - 1)) {

                                        TFacture of = this.createInvoices(finalTp.subList(volatilecount, finalTp.size()), date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, u);

                                        factures.add(of);
                                        createGroupeFacture(g, of, CODEFACTURE, em);
                                    }

                                    volatilecount = (myCount - 1);
                                    virtualAmont = (finalTp.get(volatilecount).getIntPRICE()) + (finalTp.get(myCount).getIntPRICE());

                                } catch (ParseException ex) {

                                }
                            } else if ((virtualAmont <= p.getIntMONTANTFAC()) && (myCount == (finalTp.size() - 1))) {
                                try {

                                    TFacture of = this.createInvoices(finalTp.subList(volatilecount, finalTp.size()), date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, u);

                                    factures.add(of);

                                    createGroupeFacture(g, of, CODEFACTURE, em);
                                } catch (ParseException ex) {

                                }

                            }
                            virtualAmont += op.getIntPRICE();
                            myCount++;

                        }

                    } else {
                        if (finalTp.size() > 0) {
                            try {
                                TFacture of = this.createInvoices(finalTp, date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, u);

                                factures.add(of);
                                createGroupeFacture(g, of, CODEFACTURE, em);
                            } catch (ParseException ex) {

                            }
                        }

                    }

                    break;
                case 2:

                    int count = p.getIntNBREBONS();
                    int decrementCount = finalTp.size();
                    int _count = p.getIntNBREBONS();
                    int virtualCnt = 0;
                    Date dtstart = null;
                    Date dtend = null;
                    try {
                        dtstart = date.formatterMysqlShort.parse(dt_start);
                        dtend = date.formatterMysqlShort.parse(dt_end);
                    } catch (Exception e) {
                    }

                    if (finalTp.size() > _count) {
                        while (decrementCount > 0) {

                            if (count < finalTp.size()) {
                                TFacture of = this.createInvoices(finalTp.subList(virtualCnt, count), dtstart, dtend, p, em, u);

                                factures.add(of);
                                createGroupeFacture(g, of, CODEFACTURE, em);

                            } else {
                                TFacture of = this.createInvoices(finalTp.subList(virtualCnt, finalTp.size()), dtstart, dtend, p, em, u);

                                factures.add(of);
                                createGroupeFacture(g, of, CODEFACTURE, em);

                            }
                            virtualCnt += _count;
                            count += _count;
                            decrementCount -= (_count);
                        }

                    } else {

                        TFacture of = this.createInvoices(finalTp.subList(virtualCnt, finalTp.size()), dtstart, dtend, p, em, u);

                        factures.add(of);
                        createGroupeFacture(g, of, CODEFACTURE, em);

                    }
                    break;
                default:
                    if (finalTp.size() > 0) {
                        try {
                            TFacture of = this.createInvoices(finalTp, date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, u);

                            factures.add(of);
                            createGroupeFacture(g, of, CODEFACTURE, em);
                        } catch (Exception e) {
                        }

                    }
                    break;

            }

        });

        return factures;
    }

    public JSONArray statQty(boolean all, int year, String search, String empl,String rayonId, int start, int limit) {
        JSONArray array = new JSONArray();
        EntityManager em = this.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
           
            Predicate criteria = cb.conjunction();
            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.or(cb.like(root.get("lgFAMILLEID").get("strNAME"), search + "%"), cb.like(root.get("lgFAMILLEID").get("intCIP"), search + "%")));
            }
            if(StringUtils.isNotEmpty(rayonId)){
               Join<TPreenregistrementDetail, TFamille> prf = root.join("lgFAMILLEID", JoinType.INNER); 
               criteria=cb.and(criteria,cb.equal(prf.get(TFamille_.lgZONEGEOID).get(TZoneGeographique_.lgZONEGEOID), rayonId));
            }
            criteria = cb.and(criteria, cb.equal(pr.get(TPreenregistrement_.bISCANCEL), false));
            criteria = cb.and(criteria, cb.notLike(pr.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            Predicate btw = cb.equal(cb.function("YEAR", Integer.class, root.get("dtCREATED")), year);
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(pr.get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), empl));
            Predicate pu = cb.greaterThan(pr.get("intPRICE"), 0);
            cq.multiselect(root.get("lgFAMILLEID").get("lgFAMILLEID"), root.get("lgFAMILLEID").get("strNAME"), root.get("lgFAMILLEID").get("intCIP"))
                    .groupBy(root.get("lgFAMILLEID").get("lgFAMILLEID"))
                    .orderBy(cb.asc(root.get("lgFAMILLEID").get("strNAME")));
            cq.where(criteria, pu, btw);
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(limit);
                q.setFirstResult(start);
            }
            List<Object[]> oblist = q.getResultList();

            oblist.forEach((objects) -> {
                try {

                    JSONObject json = new JSONObject();

                    json.put("id", objects[0]).put("DESC", objects[1]).put("CIP", objects[2]).put("ANNEE", year);
                    LocalDate today = LocalDate.now();
                    int ofset = today.getMonthValue();
                    if (year < today.getYear()) {
                        ofset = 12;
                    }
                    for (int i = 1; i <= ofset; i++) {
                        LocalDate date = LocalDate.of(today.getYear(), i, 1);
                        json.put(date.getMonth().name(), this.getQty(year, i, objects[0].toString(), empl));
                        array.put(json);
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            });

        } finally {
            if (em != null) {

            }
        }

        return array;
    }

    public JSONArray getListeTSnapshotFamillesell(String lg_FAMILLE_ID) {
        JSONArray tree = new JSONArray();
        List<EntityData> test = new ArrayList<>();
        try {
            jconnexion con = new jconnexion();
            con.initConnexion();
            String query = "SELECT YEAR(`t_preenregistrement`.`dt_UPDATED`),MONTHNAME(`t_preenregistrement`.`dt_UPDATED`), SUM(`t_preenregistrement_detail`.`int_QUANTITY`) FROM\n"
                    + "  `t_preenregistrement`\n"
                    + "  INNER JOIN `t_preenregistrement_detail` ON (`t_preenregistrement`.`lg_PREENREGISTREMENT_ID` = `t_preenregistrement_detail`.`lg_PREENREGISTREMENT_ID`)\n"
                    + "WHERE\n"
                    + "  `t_preenregistrement_detail`.`lg_FAMILLE_ID`=? AND\n"
                    + "   `t_preenregistrement`.`b_IS_CANCEL`=0 AND  `t_preenregistrement`.`int_PRICE` >0\n"
                    + "   AND  `t_preenregistrement`.`lg_TYPE_VENTE_ID` <> '5' AND\n"
                    + "   `t_preenregistrement_detail`.`int_QUANTITY` >0 AND   `t_preenregistrement`.`str_STATUT`='is_Closed'\n"
                    + "   GROUP BY YEAR(`t_preenregistrement`.`dt_UPDATED`),MONTHNAME(`t_preenregistrement`.`dt_UPDATED`) ORDER BY \n"
                    + "   YEAR(`t_preenregistrement`.`dt_UPDATED`) ASC";
            Connection _con = con.getConnection();
            PreparedStatement ps = _con.prepareStatement(query);
            ps.setString(1, lg_FAMILLE_ID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EntityData data = new EntityData();
                data.setStr_value1(rs.getInt(1) + "");
                data.setStr_value2(rs.getObject(2) + "");
                data.setStr_value3(rs.getObject(3) + "");
                test.add(data);
            }
            if (ps != null) {
                rs.close();
                ps.close();
                _con.close();
            }
            Map<String, List<EntityData>> m = test.stream().collect(Collectors.groupingBy(s -> s.getStr_value1()));

            for (Map.Entry<String, List<EntityData>> entry : m.entrySet()) {
                String t = entry.getKey();
                List<EntityData> value = entry.getValue();
                JSONObject json = new JSONObject();
                json.put("int_YEAR", Integer.valueOf(t));
                for (EntityData entityData : value) {
                    json.put(entityData.getStr_value2(), entityData.getStr_value3());
                }
                tree.put(json);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tree;
    }

    public int getQty(int year, int month, String id, String empl) {
        int result = 0;
        EntityManager em = this.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TFamille> prf = root.join("lgFAMILLEID", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), empl));
            Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            cb.and(criteria, pu);
            Predicate pu2 = cb.greaterThan(root.get(TPreenregistrementDetail_.intQUANTITY), 0);
//            cb.and(criteria,pu2);
            criteria = cb.and(criteria, cb.equal(prf.get(TFamille_.lgFAMILLEID), id));
            Predicate btw = cb.equal(cb.function("MONTH", Integer.class, root.get("dtCREATED")), month);
//            criteria=cb.and(criteria,btw);
            Predicate btw2 = cb.equal(cb.function("YEAR", Integer.class, root.get("dtCREATED")), year);
//            criteria=cb.and(criteria,btw2);
            cq.select(cb.sumAsLong(root.get(TPreenregistrementDetail_.intQUANTITY)));
            cq.where(criteria, btw, pu2, btw2, pu);
            Query q = em.createQuery(cq);
            Long r = (Long) q.getSingleResult();
            result = (r != null ? r.intValue() : 0);

        } finally {
            if (em != null) {

            }
        }

        return result;
    }

    public int statQty(int year, String search, String empl,String rayonId) {
     
        EntityManager em = this.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long[]> cq = cb.createQuery(Long[].class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
        
            Predicate criteria = cb.conjunction();
            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.or(cb.like(root.get("lgFAMILLEID").get("strNAME"), search + "%"), cb.like(root.get("lgFAMILLEID").get("intCIP"), search + "%")));
            }
             if(StringUtils.isNotEmpty(rayonId)){
               Join<TPreenregistrementDetail, TFamille> prf = root.join("lgFAMILLEID", JoinType.INNER); 
               criteria=cb.and(criteria,cb.equal(prf.get(TFamille_.lgZONEGEOID).get(TZoneGeographique_.lgZONEGEOID), rayonId));
            }
            criteria = cb.and(criteria, cb.notLike(pr.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            criteria = cb.and(criteria, cb.equal(pr.get(TPreenregistrement_.bISCANCEL), false));
            Predicate btw = cb.equal(cb.function("YEAR", Integer.class, root.get("dtCREATED")), year);
            criteria = cb.and(criteria, btw);
            criteria = cb.and(criteria, cb.equal(pr.get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(pr.get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), empl));
            Predicate pu = cb.greaterThan(pr.get("intPRICE"), 0);
            criteria = cb.and(criteria, pu);
            cq.multiselect(cb.count(root)).groupBy(root.get("lgFAMILLEID").get("lgFAMILLEID"));
            cq.where(criteria);

            Query q = em.createQuery(cq);

            return (q.getResultList().size());

        } finally {
            if (em != null) {

            }
        }

    }

    public JSONArray statAchatQty(boolean all, int year, String search, int start, int limit) {
        JSONArray array = new JSONArray();
        EntityManager em = this.getEntityManager();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TBonLivraisonDetail> root = cq.from(TBonLivraisonDetail.class);

            Join< TBonLivraisonDetail, TBonLivraison> pr = root.join("lgBONLIVRAISONID", JoinType.INNER);
            Join<TBonLivraisonDetail, TFamille> prf = root.join("lgFAMILLEID", JoinType.INNER);
            Predicate criteria = cb.conjunction();

            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.or(cb.like(root.get("lgFAMILLEID").get("strNAME"), search + "%"), cb.like(root.get("lgFAMILLEID").get("intCIP"), search + "%")));
            }

            Predicate btw = cb.equal(cb.function("YEAR", Integer.class, root.get("lgBONLIVRAISONID").get("dtUPDATED")), year);
            criteria = cb.and(criteria, cb.equal(root.get("lgBONLIVRAISONID").get("strSTATUT"), "is_Closed"));

            cq.multiselect(root.get("lgFAMILLEID").get("lgFAMILLEID"), root.get("lgFAMILLEID").get("strNAME"), root.get("lgFAMILLEID").get("intCIP"))
                    .groupBy(root.get("lgFAMILLEID").get("lgFAMILLEID"))
                    .orderBy(cb.asc(root.get("lgFAMILLEID").get("strNAME")));
            cq.where(criteria, btw);

            Query q = em.createQuery(cq);

            if (!all) {
                q.setMaxResults(limit);
                q.setFirstResult(start);
            }
            List<Object[]> oblist = q.getResultList();

            oblist.forEach((objects) -> {
                try {

                    JSONObject json = new JSONObject();

                    json.put("id", objects[0]).put("DESC", objects[1]).put("CIP", objects[2]).put("ANNEE", year);
                    LocalDate today = LocalDate.now();

                    for (int i = 1; i <= today.getMonthValue(); i++) {
                        LocalDate date = LocalDate.of(today.getYear(), i, 1);
                        JSONObject data = getAchatsData(year, i, objects[0].toString());
                        json.put(date.getMonth().name(), data.getInt("montant")).put(date.getMonth().name() + "QTY", data.get("intQTERECUE")).put(date.getMonth().name() + "UG", data.get("intQTEUG"));

                    }

                    array.put(json);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            });

        } finally {
            if (em != null) {

            }
        }

        return array;
    }

    public JSONObject getAchatsData(int year, int month, String id) {
        JSONObject result = new JSONObject();
        EntityManager em = this.getEntityManager();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TBonLivraisonDetail> root = cq.from(TBonLivraisonDetail.class);

            Join<TBonLivraisonDetail, TFamille> prf = root.join("lgFAMILLEID", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            criteria = cb.and(criteria, cb.equal(prf.get(TFamille_.lgFAMILLEID), id));
            Predicate btw = cb.equal(cb.function("MONTH", Integer.class, root.get(TBonLivraisonDetail_.dtCREATED)), month);
            Predicate btw2 = cb.equal(cb.function("YEAR", Integer.class, root.get(TBonLivraisonDetail_.dtCREATED)), year);

            cq.multiselect(cb.sumAsLong(root.get(TBonLivraisonDetail_.intQTERECUE)), cb.sumAsLong(root.get(TBonLivraisonDetail_.intQTEUG)), root.get(TBonLivraisonDetail_.intPAF));
            cq.where(criteria, btw);

            Query q = em.createQuery(cq);
            List<Object[]> r = q.getResultList();
            r.forEach((t) -> {
                try {

                    result.put("intQTERECUE", (t[0] != null ? Integer.valueOf(t[0] + "") : 0)).put("intQTEUG", (t[1] != null ? Integer.valueOf(t[1] + "") : 0))
                            .put("montant", (t[0] != null ? Integer.valueOf(t[0] + "") : 0) * (t[2] != null ? Integer.valueOf(t[2] + "") : 0));
                } catch (JSONException ex) {

                }

            });

        } finally {
            if (em != null) {

            }
        }

        return result;
    }

    public int statAchatQty(int year, String search) {

        EntityManager em = this.getEntityManager();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TBonLivraisonDetail> root = cq.from(TBonLivraisonDetail.class);

            Join< TBonLivraisonDetail, TBonLivraison> pr = root.join("lgBONLIVRAISONID", JoinType.INNER);
            Join<TBonLivraisonDetail, TFamille> prf = root.join("lgFAMILLEID", JoinType.INNER);
            Predicate criteria = cb.conjunction();

            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.or(cb.like(root.get("lgFAMILLEID").get("strNAME"), search + "%"), cb.like(root.get("lgFAMILLEID").get("intCIP"), search + "%")));
            }

            Predicate btw = cb.equal(cb.function("YEAR", Integer.class, root.get(TBonLivraisonDetail_.dtCREATED)), year);
            criteria = cb.and(criteria, cb.equal(root.get("lgBONLIVRAISONID").get("strSTATUT"), "is_Closed"));

            cq.multiselect(cb.count(root)).groupBy(root.get("lgFAMILLEID").get("lgFAMILLEID"));
            cq.where(criteria, btw);

            Query q = em.createQuery(cq);

            return (q.getResultList().size());

        } finally {
            if (em != null) {

            }
        }

    }
//chiffre d'affaire hors taxe

    public JSONObject getCa(String dt_start, String dt_end, String emp) {
        EntityManager em = this.getEntityManager();
        JSONObject jsono = new JSONObject();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TPreenregistrement> root = cq.from(TPreenregistrement.class);
            Predicate criteria = cb.conjunction();
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrement_.dtUPDATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(root.get(TPreenregistrement_.strSTATUT), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get(TPreenregistrement_.bISCANCEL), false));
            criteria = cb.and(criteria, cb.notLike(root.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            // criteria = cb.and(criteria, cb.notLike(root.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "4"));
            Predicate pu = cb.greaterThan(root.get(TPreenregistrement_.intPRICE), 0);
            criteria = cb.and(criteria, cb.equal(root.get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), emp));

            cq.multiselect(cb.sumAsLong(root.get(TPreenregistrement_.intPRICE)), cb.sumAsLong(root.get(TPreenregistrement_.intPRICEREMISE)));
            cq.where(criteria, pu, btw);

            Query q = em.createQuery(cq);
            List<Object[]> oblist = q.getResultList();
            oblist.forEach((t) -> {
                try {
                    jsono.put("CA", (t[0] != null ? new Integer(t[0].toString()) : 0)).put("REMISE", (t[1] != null ? new Integer(t[1].toString()) : 0))
                            .put("CANET", (t[0] != null ? new Integer(t[0].toString()) : 0) - (t[1] != null ? new Integer(t[1].toString()) : 0));
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            });

        } finally {
            if (em != null) {

            }
        }
        return jsono;
    }
// chiffre d'affaires par tva

    public JSONObject getTVA(String dt_start, String dt_end, String emp) {
        EntityManager em = this.getEntityManager();
        JSONObject jsono = new JSONObject();
        JSONArray tvaArray = new JSONArray();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TPreenregistrementDetail> cq = cb.createQuery(TPreenregistrementDetail.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementDetail, TFamille> prf = root.join("lgFAMILLEID", JoinType.INNER);
            Join< TFamille, TCodeTva> tva = prf.join("lgCODETVAID", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("lgPREENREGISTREMENTID").get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
//            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "4"));
            Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), emp));
            cq.where(criteria, pu, btw);
            Query q = em.createQuery(cq);
            List<TPreenregistrementDetail> oblist = q.getResultList();
            Map<TCodeTva, List<TPreenregistrementDetail>> mysList = oblist.stream().collect(Collectors.groupingBy(s -> s.getLgFAMILLEID().getLgCODETVAID()));
            Double montHT2 = 0.0;
            Double TVA = 0.0;
            Integer montantAchat = 0;
            for (Map.Entry<TCodeTva, List<TPreenregistrementDetail>> entry : mysList.entrySet()) {
                TCodeTva _key = entry.getKey();
                JSONObject json = new JSONObject();
                Double _montHT2 = 0.0;
                Double montTTC2 = 0.0;
                List<TPreenregistrementDetail> value = entry.getValue();
                for (TPreenregistrementDetail d : value) {
                    montTTC2 += d.getIntPRICE();
                    _montHT2 += (Double.valueOf(d.getIntPRICE()) / (1 + (Double.valueOf(_key.getIntVALUE()) / 100)));
                    montantAchat += (d.getLgFAMILLEID().getIntPAF() * d.getIntQUANTITY());
                }
                TVA += (montTTC2 - _montHT2);
                montHT2 += _montHT2;

                Double finalTVA = montTTC2 - _montHT2;
                json.put("name", _key.getStrNAME()).put("value", _key.getIntVALUE()).put("montant", Util.getFormattedLongValue(Math.round(finalTVA)) + " CFA");
                tvaArray.put(json);
                jsono.put("datatva", tvaArray);

            }
            if (oblist.isEmpty()) {
                jsono.put("datatva", new JSONArray());
                jsono.put("recettes", new JSONArray());

            }

            long marge = (Math.round(montHT2) - montantAchat);
            Double amount = montHT2 + TVA;
            jsono.put("montTTC", Math.round(amount));
            jsono.put("montHT", Util.getFormattedLongValue(Math.round(montHT2)) + " CFA");
            JSONArray modeRgleme = new JSONArray();

            Map<TPreenregistrement, List<TPreenregistrementDetail>> mysList2 = oblist.stream().collect(Collectors.groupingBy(s -> s.getLgPREENREGISTREMENTID()));
            long caTTCVNO = 0l;
            long caTTCVO = 0l;

            Integer remiseTTC = 0;

            List<TPreenregistrement> listpr = new ArrayList<>();
            for (Map.Entry<TPreenregistrement, List<TPreenregistrementDetail>> entry : mysList2.entrySet()) {
                TPreenregistrement _key = entry.getKey();
                listpr.add(_key);
                remiseTTC += _key.getIntPRICEREMISE();
                if ("VNO".equals(_key.getStrTYPEVENTE())) {
                    caTTCVNO += _key.getIntPRICE();
                } else if ("VO".equals(_key.getStrTYPEVENTE())) {
                    caTTCVO += _key.getIntPRICE();
                }

            }
            long total = caTTCVO + caTTCVNO;
            Double percentVO = (Double.valueOf(caTTCVO) * 100) / total;
            Double percentVNO = (Double.valueOf(caTTCVNO) * 100) / total;
            jsono.put("VNO", Util.getFormattedLongValue(caTTCVNO) + " CFA" + " (" + Math.round(percentVNO) + "%)");
            jsono.put("VO", Util.getFormattedLongValue(caTTCVO) + " CFA" + " (" + Math.round(percentVO) + "%)");

            jsono.put("remiseHT", Util.getFormattedIntegerValue(remiseTTC) + " CFA");
            jsono.put("montantHTCNET", Util.getFormattedLongValue((jsono.getLong("montTTC") - remiseTTC)) + " CFA");
            jsono.put("montTTC", Util.getFormattedLongValue(jsono.getLong("montTTC")) + " CFA");
            List<TModeReglement> list = getAllModeReglement();

            JSONObject allReg = new JSONObject();
            list.forEach((mode) -> {
                try {

                    allReg.putOnce(mode.getStrNAME(), new JSONObject().put("montant", 0).put("name", mode.getStrNAME()));
                    modeRgleme.put(mode.getStrNAME());
                } catch (JSONException ex) {

                }
            });
            listpr.forEach((prp) -> {
                TReglement rg = getReglement(prp.getLgREGLEMENTID().getLgREGLEMENTID());
                String modeName = rg.getLgMODEREGLEMENTID().getStrNAME();
                try {
                    JSONObject o = allReg.getJSONObject(modeName);
                    Integer montantv = getMontant(prp.getLgPREENREGISTREMENTID());
                    long price = Util.formatStringToLong(o.get("montant").toString()) + montantv;
                    o.put("montant", Util.getFormattedLongValue(price));
                    allReg.put(modeName, o);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            });

            jsono.put("recettes", allReg.toJSONArray(modeRgleme));
            JSONObject js = achats(dt_start, dt_end, total, marge);
            jsono.put("achats", js.get("achats"));
        } catch (JSONException ex) {
            ex.printStackTrace();
        } finally {
            if (em != null) {

            }
        }
        return jsono;
    }

    public JSONObject getTVAS(String dt_start, String dt_end, String emp) {
        EntityManager em = this.getEntityManager();
        JSONObject jsono = new JSONObject();
        JSONArray tvaArray = new JSONArray();
        try {
            long start = System.currentTimeMillis();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TPreenregistrementDetail> cq = cb.createQuery(TPreenregistrementDetail.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementDetail, TFamille> prf = root.join("lgFAMILLEID", JoinType.INNER);
            Join< TFamille, TCodeTva> tva = prf.join("lgCODETVAID", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("lgPREENREGISTREMENTID").get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
//            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "4"));
            Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), emp));
            cq.where(criteria, pu, btw);
            Query q = em.createQuery(cq);
            List<TPreenregistrementDetail> oblist = q.getResultList();
            System.out.println(" oblist " + (System.currentTimeMillis() - start) + " >>>>>>> " + oblist.size());
            LocalDateTime ti = LocalDateTime.now();
            System.out.println("ti******* " + ti.getMinute());
            JSONObject allReg = new JSONObject();
            //toutes ventes avec tva
            Stream<TPreenregistrementDetail> listTVA = oblist.stream().filter(t -> t.getLgFAMILLEID().getLgCODETVAID().getIntVALUE() == 18);
            //Map<TCodeTva, List<TPreenregistrementDetail>> listTVA = oblist.stream().collect(Collectors.groupingBy(s -> s.getLgPREENREGISTREMENTID()));
            Map<TPreenregistrement, List<TPreenregistrementDetail>> mysList2 = oblist.stream().collect(Collectors.groupingBy(s -> s.getLgPREENREGISTREMENTID()));
            System.out.println("finn  ******* " + (LocalDateTime.now().getMinute() - ti.getMinute()));
            long caTTCVNO = 0l;
            long caTTCVO = 0l;
            Integer remiseTTC = 0;
            Double montHT2 = 0.0;
            Double TVA = 0.0;
            Integer montantAchat = 0;
            Integer TVA0 = 0, TVA18 = 0;
            Double _montHT2 = 0.0;
            Double montTTC2 = 0.0;
            JSONObject json = new JSONObject();
            JSONArray _allReg = new JSONArray();
            long startZZZZ = System.currentTimeMillis();
            for (Map.Entry<TPreenregistrement, List<TPreenregistrementDetail>> entry : mysList2.entrySet()) {
                TPreenregistrement _key = entry.getKey();
//                System.out.println(" _key **************** "+_key);
                remiseTTC += _key.getIntPRICEREMISE();
                if ("VNO".equals(_key.getStrTYPEVENTE())) {
                    caTTCVNO += _key.getIntPRICE();
                } else if ("VO".equals(_key.getStrTYPEVENTE())) {
                    caTTCVO += _key.getIntPRICE();
                }
                TReglement rg = _key.getLgREGLEMENTID();
                String modeName = rg.getLgMODEREGLEMENTID().getStrNAME();
                try {
                    JSONObject o = allReg.optJSONObject(modeName);
                    Integer montantv = getMontant(_key.getLgPREENREGISTREMENTID());
                    if (o != null) {
                        long price = Util.formatStringToLong(o.get("montant").toString()) + montantv;
                        o.put("montant", Util.getFormattedLongValue(price));
                    } else {
                        o = new JSONObject();
                        o.put("montant", Util.getFormattedLongValue(montantv));
                    }
                    allReg.put(modeName, o);
                } catch (Exception e) {
                }
                List<TPreenregistrementDetail> value = entry.getValue();
                for (TPreenregistrementDetail d : value) {

                    TCodeTva codeTva = d.getLgFAMILLEID().getLgCODETVAID();
                    montTTC2 += d.getIntPRICE();
                    Double _ht = (Double.valueOf(d.getIntPRICE()) / (1 + (Double.valueOf(codeTva.getIntVALUE()) / 100)));
                    montantAchat += (d.getLgFAMILLEID().getIntPAF() * d.getIntQUANTITY());
                    _montHT2 += _ht;
                    if (codeTva.getIntVALUE() == 18) {
                        Double _tva = (d.getIntPRICE() - _ht);
                        TVA18 += _tva.intValue();
                    }

                }

            }
            _allReg.put(allReg);
            System.out.println("----------------------> " + allReg);
            System.out.println(" oblist************* " + (System.currentTimeMillis() - startZZZZ));
            int t2 = LocalDateTime.now().getMinute();
            System.out.println("ti******* fin  " + (t2 - ti.getMinute()));
            TVA += (montTTC2 - _montHT2);
            montHT2 += _montHT2;
            json.put("name", "TVA 18").put("value", 18).put("montant", Util.getFormattedLongValue(Math.round(TVA18)) + " CFA");
            tvaArray.put(json);
            // json.put("name", "TVA 0").put("value", 0).put("montant", " 0 CFA");
            // tvaArray.put(json);
            jsono.put("datatva", tvaArray);

            if (oblist.isEmpty()) {
                jsono.put("datatva", new JSONArray());
                jsono.put("recettes", new JSONArray());

            }

            long marge = (Math.round(montHT2) - montantAchat);
            Double amount = montHT2 + TVA;
            jsono.put("montTTC", Math.round(amount));
            jsono.put("montHT", Util.getFormattedLongValue(Math.round(montHT2)) + " CFA");
            JSONArray modeRgleme = new JSONArray();

            long total = caTTCVO + caTTCVNO;
            Double percentVO = (Double.valueOf(caTTCVO) * 100) / total;
            Double percentVNO = (Double.valueOf(caTTCVNO) * 100) / total;
            jsono.put("VNO", Util.getFormattedLongValue(caTTCVNO) + " CFA" + " (" + Math.round(percentVNO) + "%)");
            jsono.put("VO", Util.getFormattedLongValue(caTTCVO) + " CFA" + " (" + Math.round(percentVO) + "%)");

            jsono.put("remiseHT", Util.getFormattedIntegerValue(remiseTTC) + " CFA");// List<TModeReglement> list = getAllModeReglement();

            jsono.put("montantHTCNET", Util.getFormattedLongValue((jsono.getLong("montTTC") - remiseTTC)) + " CFA");
            jsono.put("montTTC", Util.getFormattedLongValue(jsono.getLong("montTTC")) + " CFA");

            jsono.put("recettes", _allReg);
            JSONObject js = achats(dt_start, dt_end, total, marge);
            jsono.put("achats", js.get("achats"));
        } catch (JSONException ex) {
            ex.printStackTrace();
        } finally {
            if (em != null) {

            }
        }
        return jsono;
    }

    public long getTotal(String dt_start, String dt_end, String emp) {
        EntityManager em = this.getEntityManager();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
//            Join<TPreenregistrementDetail, TFamille> prf = root.join("lgFAMILLEID", JoinType.INNER);
//            Join< TFamille, TCodeTva> tva = prf.join("lgCODETVAID", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("lgPREENREGISTREMENTID").get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), emp));
            cq.where(criteria, pu, btw);
            cq.select(cb.count(root));
            Query q = em.createQuery(cq);
            return (long) q.getSingleResult();
        } finally {

        }
    }

    public List<TCodeTva> getCodeTva() {
        EntityManager em = this.getEntityManager();
        return em.createNamedQuery("TCodeTva.findAll").getResultList();
    }

    public List<TModeReglement> getAllModeReglement() {
        EntityManager em = this.getEntityManager();
        return em.createNamedQuery("TModeReglement.findAll").getResultList();
    }

    private TReglement getReglement(String id) {
        return this.getEntityManager().getReference(TReglement.class, id);
    }

    public JSONObject getMvt(String dt_start, String dt_end) {
        EntityManager em = this.getEntityManager();
        JSONObject jsono = new JSONObject();

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TMvtCaisse> root = cq.from(TMvtCaisse.class);
            Join<TMvtCaisse, TTypeMvtCaisse> pr = root.join("lgTYPEMVTCAISSEID", JoinType.INNER);

            Predicate criteria = cb.conjunction();

            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("dtCREATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.notLike(pr.get("lgTYPEMVTCAISSEID"), "9"));
            criteria = cb.and(criteria, cb.notLike(pr.get("lgTYPEMVTCAISSEID"), "8"));

            cq.multiselect(cb.sumAsDouble(root.get("intAMOUNT")), pr.get("strNAME")).groupBy(pr.get("lgTYPEMVTCAISSEID"));
            cq.where(criteria, btw);

            Query q = em.createQuery(cq);
            List<Object[]> list = q.getResultList();
            JSONArray data = new JSONArray();

            list.forEach((t) -> {
                JSONObject ob = new JSONObject();
                try {
                    long _montant = (t[0] != null ? Double.valueOf(t[0] + "").longValue() : 0);

                    ob.putOnce("totalmvt", _montant);

                    ob.putOnce("mvt", t[1].toString());
                    ob.putOnce("montant", Util.getFormattedLongValue(_montant));
                } catch (JSONException ex) {

                }
                data.put(ob);

            });
            jsono.put("data", data).put("total", data.length());
        } catch (Exception e) {
            e.printStackTrace();

        }
        return jsono;
    }

    public JSONObject achats(String dt_start, String dt_end, long ttc, long marge) {
        EntityManager em = this.getEntityManager();
        JSONObject jsono = new JSONObject();

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TBonLivraison> root = cq.from(TBonLivraison.class);

            Predicate criteria = cb.conjunction();
            System.out.println(" dt " + dt_start + " " + dt_end);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(root.get(TBonLivraison_.strSTATUT), "is_Closed"));

//            Predicate pu = cb.greaterThan(root.get("intAMOUNT"), 0);
            cq.multiselect(cb.sumAsLong(root.get(TBonLivraison_.intHTTC)), cb.sumAsLong(root.get(TBonLivraison_.intMHT)), cb.sumAsLong(root.get(TBonLivraison_.intTVA)));
            cq.where(btw, criteria);

            Query q = em.createQuery(cq);
            List<Object[]> list = q.getResultList();

            long valueTTC = 0l, valueHT = 0, valueTVA = 0l;
            double ration = 0.0;
            for (Object[] objects : list) {

                valueTTC += (objects[0] != null ? Long.valueOf(objects[0] + "") : 0);
                valueHT += (objects[1] != null ? Long.valueOf(objects[1] + "") : 0);
                valueTVA += (objects[2] != null ? Long.valueOf(objects[2] + "") : 0);
            }

            if (valueTTC > 0) {
                ration = new BigDecimal(Double.valueOf(ttc) / Double.valueOf(valueTTC)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();//Math.round(Double.valueOf(ttc)/Double.valueOf(valueTTC));
            }
            JSONObject data = new JSONObject();
            data.put("th", Util.getFormattedLongValue(valueHT)).put("ttc", Util.getFormattedLongValue(valueTTC)).put("tva", Util.getFormattedLongValue(valueTVA));
            data.put("marge", Util.getFormattedLongValue(marge)).put("ratio", ration);
            jsono.put("achats", data);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return jsono;
    }

    private List<TGrossiste> getGroupe(String name) {
        return this.getEntityManager().createQuery("SELECT o FROM TGrossiste o WHERE o.strLIBELLE LIKE ?1 ").setParameter(1, name + "%").getResultList();
    }

    public JSONObject getGrossisteAchats(String dt_start, String dt_end) {
        EntityManager em = this.getEntityManager();
        JSONObject jsono = new JSONObject();
        JSONArray array = new JSONArray();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TBonLivraison> root = cq.from(TBonLivraison.class);
            Join<TBonLivraison, TOrder> or = root.join("lgORDERID", JoinType.INNER);
            Predicate criteria = cb.conjunction();

            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(root.get(TBonLivraison_.strSTATUT), "is_Closed"));
            List<TGrossiste> laborex = getGroupe("LABOREX");

            List<Predicate> orlist = new ArrayList<>();
            laborex.forEach((tGrossiste) -> {
                orlist.add(cb.like(or.get("lgGROSSISTEID").get("lgGROSSISTEID"), tGrossiste.getLgGROSSISTEID()));
            });
            Predicate[] orp = new Predicate[orlist.size()];
            orp = orlist.toArray(orp);
            criteria = cb.and(criteria, cb.or(orp));

            cq.multiselect(cb.sumAsLong(root.get(TBonLivraison_.intHTTC)), cb.sumAsLong(root.get(TBonLivraison_.intMHT)), cb.sumAsLong(root.get(TBonLivraison_.intTVA)));
            cq.where(btw, criteria);

            Query q = em.createQuery(cq);
            List<Object[]> list = q.getResultList();

            list.forEach((objects) -> {
                try {
                    JSONObject data = new JSONObject();
                    data.put("th", Util.getFormattedLongValue((objects[1] != null ? Long.valueOf(objects[1] + "") : 0))).put("ttc", Util.getFormattedLongValue((objects[0] != null ? Long.valueOf(objects[0] + "") : 0))).put("tva", Util.getFormattedLongValue((objects[2] != null ? Long.valueOf(objects[2] + "") : 0)));
                    data.put("grossiste", "LABOREX");
                    array.put(data);
                } catch (JSONException ex) {

                }
                // 
                List<String> quatresGrd = Arrays.asList("COPHARMED", "TEDIS", "DPCI");
                quatresGrd.forEach((string) -> {
                    try {
                        JSONObject one = achatGrossiste(dt_start, dt_end, string);
                        array.put(one.get(string));
                    } catch (JSONException ex) {

                    }
                });

            });
            array.put(achatAutres(dt_start, dt_end).get("AUTRES"));

            jsono.put("grossistes", array);

        } catch (Exception e) {
            e.printStackTrace();

        }
        JSONArray finaldatas = new JSONArray();

        JSONObject out = new JSONObject();
        try {

            out.put("data", jsono.getJSONArray("grossistes")).put("total", (jsono.length() > 0 ? 1 : 0));
        } catch (JSONException ex) {

        }
        return out;
    }

    public JSONObject achatGrossiste(String dt_start, String dt_end, String desc) {
        EntityManager em = this.getEntityManager();
        JSONObject jsono = new JSONObject();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TBonLivraison> root = cq.from(TBonLivraison.class);
            Join<TBonLivraison, TOrder> or = root.join("lgORDERID", JoinType.INNER);
            Predicate criteria = cb.conjunction();

            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(root.get(TBonLivraison_.strSTATUT), "is_Closed"));
            criteria = cb.and(criteria, cb.like(or.get("lgGROSSISTEID").get("strLIBELLE"), desc + "%"));

            cq.multiselect(cb.sumAsLong(root.get(TBonLivraison_.intHTTC)), cb.sumAsLong(root.get(TBonLivraison_.intMHT)), cb.sumAsLong(root.get(TBonLivraison_.intTVA)));
            cq.where(btw, criteria);

            Query q = em.createQuery(cq);
            List<Object[]> list = q.getResultList();

            list.forEach((objects) -> {
                try {
                    JSONObject data = new JSONObject();
                    data.put("th", Util.getFormattedLongValue((objects[1] != null ? Long.valueOf(objects[1] + "") : 0))).put("ttc", Util.getFormattedLongValue((objects[0] != null ? Long.valueOf(objects[0] + "") : 0))).put("tva", Util.getFormattedLongValue((objects[2] != null ? Long.valueOf(objects[2] + "") : 0)));
                    data.put("grossiste", desc);
                    jsono.put(desc, data);
                } catch (JSONException ex) {

                }

            });

        } catch (Exception e) {
            e.printStackTrace();

        }
        return jsono;
    }

    public JSONObject achatAutres(String dt_start, String dt_end) {
        EntityManager em = this.getEntityManager();
        JSONObject jsono = new JSONObject();
        try {
            List<String> quatresGrd = Arrays.asList("COPHARMED", "TEDIS", "DPCI", "LABOREX");
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TBonLivraison> root = cq.from(TBonLivraison.class);
            Join<TBonLivraison, TOrder> or = root.join("lgORDERID", JoinType.INNER);
            Predicate criteria = cb.conjunction();

            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(root.get(TBonLivraison_.strSTATUT), "is_Closed"));
            List<Predicate> orlist = new ArrayList<>();
            quatresGrd.forEach((desc) -> {
                orlist.add(cb.notLike(or.get("lgGROSSISTEID").get("strLIBELLE"), desc + "%"));
            });
            Predicate[] orp = new Predicate[orlist.size()];
            orp = orlist.toArray(orp);
            criteria = cb.and(criteria, cb.and(orp));

            cq.multiselect(cb.sumAsLong(root.get(TBonLivraison_.intHTTC)), cb.sumAsLong(root.get(TBonLivraison_.intMHT)), cb.sumAsLong(root.get(TBonLivraison_.intTVA)));
            cq.where(btw, criteria);

            Query q = em.createQuery(cq);
            List<Object[]> list = q.getResultList();

            list.forEach((objects) -> {
                try {
                    JSONObject data = new JSONObject();
                    data.put("th", Util.getFormattedLongValue((objects[1] != null ? Long.valueOf(objects[1] + "") : 0))).put("ttc", Util.getFormattedLongValue((objects[0] != null ? Long.valueOf(objects[0] + "") : 0))).put("tva", Util.getFormattedLongValue((objects[2] != null ? Long.valueOf(objects[2] + "") : 0)));
                    data.put("grossiste", "AUTRES");
                    jsono.put("AUTRES", data);
                } catch (JSONException ex) {

                }

            });

        } catch (Exception e) {
            e.printStackTrace();

        }
        return jsono;
    }

    public JSONArray creditsAccorde(boolean all, String dt_start, String dt_end, String search, String empl, int start, int limit) {
        EntityManager em = this.getEntityManager();

        JSONArray array = new JSONArray();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> or = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementCompteClientTiersPayent, TCompteClientTiersPayant> pt = root.join("lgCOMPTECLIENTTIERSPAYANTID", JoinType.INNER);
            Predicate criteria = cb.conjunction();

            Predicate btw = cb.between(cb.function("DATE", Date.class, or.get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(or.get(TPreenregistrement_.strSTATUT), "is_Closed"));
            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.or(cb.like(pt.get("lgTIERSPAYANTID").get("strNAME"), search + "%"), cb.like(pt.get("lgTIERSPAYANTID").get("lgTYPETIERSPAYANTID").get("strLIBELLETYPETIERSPAYANT"), search + "%")));
            }
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), empl));
            criteria = cb.and(criteria, cb.equal(or.get(TPreenregistrement_.bISCANCEL), false));
            criteria = cb.and(criteria, cb.equal(or.get(TPreenregistrement_.strTYPEVENTE), "VO"));
            Predicate pu = cb.greaterThan(or.get(TPreenregistrement_.intPRICE), 0);
            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
//            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "4"));
            Predicate pu2 = cb.greaterThan(root.get(TPreenregistrementCompteClientTiersPayent_.intPRICE), 0);
            cq.multiselect(cb.sumAsLong(root.get(TPreenregistrementCompteClientTiersPayent_.intPRICE)), cb.countDistinct(root),
                    pt.get("lgTIERSPAYANTID").get("strNAME"), pt.get("lgTIERSPAYANTID").get("lgTYPETIERSPAYANTID").get("strLIBELLETYPETIERSPAYANT"), cb.countDistinct(pt.get("lgCOMPTECLIENTID").get("lgCLIENTID")))
                    .groupBy(pt.get("lgTIERSPAYANTID").get("strNAME"));
            cq.where(btw, criteria, pu2, pu);

            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(limit);
                q.setFirstResult(start);
            }
            List<Object[]> list = q.getResultList();

            list.forEach((objects) -> {
                try {
                    JSONObject data = new JSONObject();
                    data.put("montant", Util.getFormattedLongValue((objects[0] != null ? Long.valueOf(objects[0] + "") : 0)));
                    data.put("nb", Util.getFormattedLongValue((objects[1] != null ? Long.valueOf(objects[1] + "") : 0)));
                    data.put("name", objects[2]);
                    data.put("nametype", objects[3]).put("nbclient", Util.getFormattedLongValue((objects[4] != null ? Long.valueOf(objects[4] + "") : 0)));
                    array.put(data);
                } catch (JSONException ex) {

                }

            });

        } catch (Exception e) {
            e.printStackTrace();

        }

        return array;
    }

    public int creditsAccorde(String dt_start, String dt_end, String search, String empl) {
        EntityManager em = this.getEntityManager();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> or = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementCompteClientTiersPayent, TCompteClientTiersPayant> pt = root.join("lgCOMPTECLIENTTIERSPAYANTID", JoinType.INNER);
            Predicate criteria = cb.conjunction();

            Predicate btw = cb.between(cb.function("DATE", Date.class, or.get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(or.get(TPreenregistrement_.strSTATUT), "is_Closed"));
            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.or(cb.like(pt.get("lgTIERSPAYANTID").get("strNAME"), search + "%"), cb.like(pt.get("lgTIERSPAYANTID").get("lgTYPETIERSPAYANTID").get("strLIBELLETYPETIERSPAYANT"), search + "%")));
            }
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), empl));
            criteria = cb.and(criteria, cb.equal(or.get(TPreenregistrement_.bISCANCEL), false));
            criteria = cb.and(criteria, cb.equal(or.get(TPreenregistrement_.strTYPEVENTE), "VO"));
            Predicate pu = cb.greaterThan(or.get(TPreenregistrement_.intPRICE), 0);
            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "4"));
            Predicate pu2 = cb.greaterThan(root.get(TPreenregistrementCompteClientTiersPayent_.intPRICE), 0);
            cq.multiselect(cb.countDistinct(root))
                    .groupBy(pt.get("lgTIERSPAYANTID").get("strNAME"));
            cq.where(btw, criteria, pu2, pu);

            Query q = em.createQuery(cq);

            return q.getResultList().size();

        } finally {
            if (em != null) {

            }
        }

    }

    public JSONArray creditsAccordeTotax(String dt_start, String dt_end, String search, String empl) {
        EntityManager em = this.getEntityManager();

        JSONArray array = new JSONArray();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> or = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementCompteClientTiersPayent, TCompteClientTiersPayant> pt = root.join("lgCOMPTECLIENTTIERSPAYANTID", JoinType.INNER);
            Predicate criteria = cb.conjunction();

            Predicate btw = cb.between(cb.function("DATE", Date.class, or.get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(or.get(TPreenregistrement_.strSTATUT), "is_Closed"));
            if (!"".equals(search)) {
                criteria = cb.and(criteria, cb.or(cb.like(pt.get("lgTIERSPAYANTID").get("strNAME"), search + "%"), cb.like(pt.get("lgTIERSPAYANTID").get("lgTYPETIERSPAYANTID").get("strLIBELLETYPETIERSPAYANT"), search + "%")));
            }
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), empl));
            criteria = cb.and(criteria, cb.equal(or.get(TPreenregistrement_.bISCANCEL), false));
            criteria = cb.and(criteria, cb.equal(or.get(TPreenregistrement_.strTYPEVENTE), "VO"));
            Predicate pu = cb.greaterThan(or.get(TPreenregistrement_.intPRICE), 0);
            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "4"));
            Predicate pu2 = cb.greaterThan(root.get(TPreenregistrementCompteClientTiersPayent_.intPRICE), 0);
            cq.multiselect(cb.sumAsLong(root.get(TPreenregistrementCompteClientTiersPayent_.intPRICE)), cb.count(root),
                    cb.countDistinct(pt.get("lgCOMPTECLIENTID").get("lgCLIENTID")));
            cq.where(btw, criteria, pu2, pu);

            Query q = em.createQuery(cq);

            List<Object[]> list = q.getResultList();

            list.forEach((objects) -> {
                try {
                    JSONObject data = new JSONObject();
                    data.put("montant", Util.getFormattedLongValue((objects[0] != null ? Long.valueOf(objects[0] + "") : 0)) + " CFA");
                    data.put("nb", Util.getFormattedLongValue((objects[1] != null ? Long.valueOf(objects[1] + "") : 0)));

                    data.put("nbclient", Util.getFormattedLongValue((objects[2] != null ? Long.valueOf(objects[2] + "") : 0)));
                    array.put(data);
                } catch (JSONException ex) {

                }

            });

        } catch (Exception e) {
            e.printStackTrace();

        }

        return array;
    }

    public JSONArray recapReglement(boolean all, String dt_start, String dt_end, String search, int start, int limit) {
        EntityManager em = this.getEntityManager();

        JSONArray array = new JSONArray();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TDossierReglement> root = cq.from(TDossierReglement.class);
            Join<TDossierReglement, TFacture> mapfac = root.join("lgFACTUREID", JoinType.INNER);

            Predicate criteria = cb.conjunction();

            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TDossierReglement_.dtCREATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));

            if (!"".equals(search)) {

                List<TTiersPayant> laborex = getPayants(search);
                if (!laborex.isEmpty()) {
                    List<Predicate> orlist = new ArrayList<>();
                    laborex.forEach((tGrossiste) -> {
                        orlist.add(cb.like(root.get(TDossierReglement_.strORGANISMEID), tGrossiste.getLgTIERSPAYANTID()));
                    });
                    Predicate[] orp = new Predicate[orlist.size()];
                    orp = orlist.toArray(orp);
                    criteria = cb.and(criteria, cb.or(orp));
                } else {
                    criteria = cb.and(criteria, cb.or(cb.like(mapfac.get(TFacture_.strCODEFACTURE), search + "%")));
                }

            }

            cq.multiselect(root.get(TDossierReglement_.dblAMOUNT), mapfac.get(TFacture_.dblMONTANTCMDE), mapfac.get(TFacture_.dblMONTANTRESTANT), mapfac.get(TFacture_.strCODEFACTURE), root.get(TDossierReglement_.strORGANISMEID))
                    .orderBy(cb.desc(root.get(TDossierReglement_.dtCREATED)));
            cq.where(btw, criteria);

            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(limit);
                q.setFirstResult(start);
            }
            List<Object[]> list = q.getResultList();

            list.forEach((objects) -> {
                try {
                    JSONObject data = new JSONObject();
                    data.put("montant", Util.getFormattedDoubleValue((objects[0] != null ? Double.valueOf(objects[0] + "") : 0)));
                    data.put("montantfact", Util.getFormattedDoubleValue((objects[1] != null ? Double.valueOf(objects[1] + "") : 0)));
                    data.put("rest", Util.getFormattedDoubleValue(Double.valueOf(objects[2] + "")));
                    data.put("code", objects[3]);
                    TTiersPayant tp = em.find(TTiersPayant.class, objects[4].toString());
                    data.put("nametp", tp.getStrNAME());
                    data.put("nametypetp", tp.getLgTYPETIERSPAYANTID().getStrLIBELLETYPETIERSPAYANT());
                    array.put(data);
                } catch (JSONException ex) {

                }

            });

        } catch (Exception e) {
            e.printStackTrace();

        }

        return array;
    }

    private List<TTiersPayant> getPayants(String name) {
        return this.getEntityManager().createQuery("SELECT o FROM TTiersPayant o WHERE (o.strNAME LIKE ?1 OR o.lgTYPETIERSPAYANTID.strLIBELLETYPETIERSPAYANT LIKE ?1) ").setParameter(1, name + "%").getResultList();
    }

    public int recapReglement(String dt_start, String dt_end, String search) {
        EntityManager em = this.getEntityManager();

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TDossierReglement> root = cq.from(TDossierReglement.class);
            Join<TDossierReglement, TFacture> mapfac = root.join("lgFACTUREID", JoinType.INNER);

            Predicate criteria = cb.conjunction();

            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TDossierReglement_.dtCREATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));

            if (!"".equals(search)) {

                List<TTiersPayant> laborex = getPayants(search);
                if (!laborex.isEmpty()) {
                    List<Predicate> orlist = new ArrayList<>();
                    laborex.forEach((tGrossiste) -> {
                        orlist.add(cb.like(root.get(TDossierReglement_.strORGANISMEID), tGrossiste.getLgTIERSPAYANTID()));
                    });
                    Predicate[] orp = new Predicate[orlist.size()];
                    orp = orlist.toArray(orp);
                    criteria = cb.and(criteria, cb.or(orp));
                } else {
                    criteria = cb.and(criteria, cb.or(cb.like(mapfac.get(TFacture_.strCODEFACTURE), search + "%")));
                }

            }

            cq.select(cb.count(root));

            cq.where(btw, criteria);

            Query q = em.createQuery(cq);

            return ((Long) q.getSingleResult()).intValue();

        } finally {
            if (em != null) {

            }
        }
    }

    //valorisation simple
    public JSONObject getDataValorisation(int mode, String lg_GROSSISTE_ID, String lg_FAMILLEARTICLE_ID, String lg_ZONE_GEO_ID, String str_BEGIN, String str_END, String empl) {
        EntityManager em = this.getEntityManager();
        JSONObject json = new JSONObject();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TFamilleStock> root = cq.from(TFamilleStock.class);
            Join<TFamilleStock, TFamille> fa = root.join("lgFAMILLEID", JoinType.INNER);
            Join<TFamille, TFamillearticle> far = fa.join("lgFAMILLEARTICLEID", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.equal(fa.get(TFamille_.strSTATUT), "enable"));

            switch (mode) {
                case 1:
                    if (!"".equals(lg_FAMILLEARTICLE_ID)) {
                        predicate = cb.and(predicate, cb.equal(far.get("lgFAMILLEARTICLEID"), lg_FAMILLEARTICLE_ID));
                    }
                    if (!"".equals(str_BEGIN) && !"".equals(str_END)) {
                        Predicate ge = cb.greaterThanOrEqualTo(far.get(TFamillearticle_.strCODEFAMILLE), str_BEGIN);
                        Predicate gl = cb.lessThanOrEqualTo(far.get(TFamillearticle_.strCODEFAMILLE), str_END);
                        predicate = cb.and(predicate, cb.and(ge), cb.and(gl));
                    }
                    if (!"".equals(str_BEGIN) && "".equals(str_END)) {
                        Predicate ge = cb.greaterThanOrEqualTo(far.get(TFamillearticle_.strCODEFAMILLE), str_BEGIN);
                        predicate = cb.and(predicate, cb.and(ge));
                    }
                    if ("".equals(str_BEGIN) && !"".equals(str_END)) {
                        Predicate gl = cb.lessThanOrEqualTo(far.get(TFamillearticle_.strCODEFAMILLE), str_END);
                        predicate = cb.and(predicate, cb.and(gl));
                    }
                    break;
                case 2:
                    Join<TFamille, TZoneGeographique> fz = fa.join("lgZONEGEOID", JoinType.INNER);
                    if (!"".equals(lg_ZONE_GEO_ID)) {
                        predicate = cb.and(predicate, cb.equal(fz.get("lgZONEGEOID"), lg_ZONE_GEO_ID));
                    }
                    if (!"".equals(str_BEGIN) && !"".equals(str_END)) {
                        Predicate ge = cb.greaterThanOrEqualTo(fz.get(TZoneGeographique_.strCODE), str_BEGIN);
                        Predicate gl = cb.lessThanOrEqualTo(fz.get(TZoneGeographique_.strCODE), str_END);
                        predicate = cb.and(predicate, cb.and(ge), cb.and(gl));
                    }
                    if (!"".equals(str_BEGIN) && "".equals(str_END)) {
                        Predicate ge = cb.greaterThanOrEqualTo(fz.get(TZoneGeographique_.strCODE), str_BEGIN);
                        predicate = cb.and(predicate, cb.and(ge));
                    }
                    if ("".equals(str_BEGIN) && !"".equals(str_END)) {
                        Predicate gl = cb.lessThanOrEqualTo(fz.get(TZoneGeographique_.strCODE), str_END);
                        predicate = cb.and(predicate, cb.and(gl));
                    }
                    break;
                case 3:
                    Join<TFamille, TGrossiste> fg = fa.join("lgGROSSISTEID", JoinType.INNER);
                    if (!"".equals(lg_GROSSISTE_ID)) {
                        predicate = cb.and(predicate, cb.equal(fg.get("lgGROSSISTEID"), lg_GROSSISTE_ID));
                    }
                    if (!"".equals(str_BEGIN) && !"".equals(str_END)) {
                        Predicate ge = cb.greaterThanOrEqualTo(fg.get(TGrossiste_.strCODE), str_BEGIN);
                        Predicate gl = cb.lessThanOrEqualTo(fg.get(TGrossiste_.strCODE), str_END);
                        predicate = cb.and(predicate, cb.and(ge), cb.and(gl));
                    }
                    if (!"".equals(str_BEGIN) && "".equals(str_END)) {
                        Predicate ge = cb.greaterThanOrEqualTo(fg.get(TGrossiste_.strCODE), str_BEGIN);
                        predicate = cb.and(predicate, cb.and(ge));
                    }
                    if ("".equals(str_BEGIN) && !"".equals(str_END)) {
                        Predicate gl = cb.lessThanOrEqualTo(fg.get(TGrossiste_.strCODE), str_END);
                        predicate = cb.and(predicate, cb.and(gl));
                    }
                    break;

                default:
                    break;
            }

            predicate = cb.and(predicate, cb.equal(root.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), empl));

            cq.multiselect(cb.sumAsLong(cb.prod(root.get(TFamilleStock_.intNUMBERAVAILABLE), fa.get(TFamille_.intPAF))), cb.sumAsLong(cb.prod(root.get(TFamilleStock_.intNUMBERAVAILABLE), fa.get(TFamille_.intPRICE))));

            cq.where(predicate);
            Query q = em.createQuery(cq);

            List<Object[]> list = q.getResultList();
            list.forEach((t) -> {
                try {
                    json.put("Achat", Util.getFormattedLongValue(t[0] != null ? Long.valueOf(t[0] + "") : 0)).put("Vente", Util.getFormattedLongValue(t[1] != null ? Long.valueOf(t[1] + "") : 0));
                } catch (JSONException ex) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject get(int mode, String date, String lg_GROSSISTE_ID, String lg_FAMILLEARTICLE_ID, String lg_ZONE_GEO_ID, String str_BEGIN, String str_END, String empl) {
        LocalDate today = LocalDate.now();
        LocalDate param = LocalDate.parse(date);
        if (today.compareTo(param) == 0) {
            return getDataValorisation(mode, lg_GROSSISTE_ID, lg_FAMILLEARTICLE_ID, lg_ZONE_GEO_ID, str_BEGIN, str_END, empl);
        } else {
            return getDataValorisation(mode, date, lg_GROSSISTE_ID, lg_FAMILLEARTICLE_ID, lg_ZONE_GEO_ID, str_BEGIN, str_END, empl);
        }

    }

    public JSONObject getDataValorisation(int mode, String date, String lg_GROSSISTE_ID, String lg_FAMILLEARTICLE_ID, String lg_ZONE_GEO_ID, String str_BEGIN, String str_END, String empl) {
        EntityManager em = this.getEntityManager();
        JSONObject json = new JSONObject();

//        try {
//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
//            Root<TStockSnapshot> root = cq.from(TStockSnapshot.class);
//
//            Join<TStockSnapshot, TFamilleStock> st = root.join("lgFAMILLESTOCKID", JoinType.INNER);
//            Join<TFamilleStock, TFamille> fa = st.join("lgFAMILLEID", JoinType.INNER);
//            Predicate predicate = cb.conjunction();
//            switch (mode) {
//                case 1:
//
//                    Join<TFamille, TFamillearticle> far = fa.join("lgFAMILLEARTICLEID", JoinType.INNER);
//                    if (!"".equals(lg_FAMILLEARTICLE_ID)) {
//
//                        predicate = cb.and(predicate, cb.equal(far.get("lgFAMILLEARTICLEID"), lg_FAMILLEARTICLE_ID));
//                    }
//                    if (!"".equals(str_BEGIN) && !"".equals(str_END)) {
//
//                        Predicate ge = cb.greaterThanOrEqualTo(far.get("strCODEFAMILLE"), str_BEGIN);
////                       
//                        Predicate gl = cb.lessThanOrEqualTo(far.get("strCODEFAMILLE"), str_END.substring(1));
//
//                        predicate = cb.and(predicate, cb.and(ge), cb.and(gl));
//
//                    }
//                    if (!"".equals(str_BEGIN) && "".equals(str_END)) {
//                        System.out.println("2");
//                        //Predicate ge = cb.greaterThanOrEqualTo(far.get(TFamillearticle_.strCODEFAMILLE), str_BEGIN);
//                        Predicate ge = cb.greaterThanOrEqualTo(far.get("strCODEFAMILLE"), str_BEGIN);
//                        predicate = cb.and(predicate, cb.and(ge));
//                    }
//                    if ("".equals(str_BEGIN) && !"".equals(str_END)) {
//                        System.out.println("3");
//                        Predicate gl = cb.lessThanOrEqualTo(far.get(TFamillearticle_.strCODEFAMILLE), str_END);
//                        predicate = cb.and(predicate, cb.and(gl));
//                    }
//                case 2:
//
//                    Join<TFamille, TZoneGeographique> fz = fa.join("lgZONEGEOID", JoinType.INNER);
//                    if (!"".equals(lg_ZONE_GEO_ID)) {
//                        predicate = cb.and(predicate, cb.equal(fz.get("lgZONEGEOID"), lg_ZONE_GEO_ID));
//                    }
//                    if (!"".equals(str_BEGIN) && !"".equals(str_END)) {
//                        Predicate ge = cb.greaterThanOrEqualTo(fz.get(TZoneGeographique_.strCODE), str_BEGIN);
//                        Predicate gl = cb.lessThanOrEqualTo(fz.get(TZoneGeographique_.strCODE), str_END);
//                        predicate = cb.and(predicate, cb.and(ge), cb.and(gl));
//                    }
//                    if (!"".equals(str_BEGIN) && "".equals(str_END)) {
//                        Predicate ge = cb.greaterThanOrEqualTo(fz.get(TZoneGeographique_.strCODE), str_BEGIN);
//                        predicate = cb.and(predicate, cb.and(ge));
//                    }
//                    if ("".equals(str_BEGIN) && !"".equals(str_END)) {
//                        Predicate gl = cb.lessThanOrEqualTo(fz.get(TZoneGeographique_.strCODE), str_END);
//                        predicate = cb.and(predicate, cb.and(gl));
//                    }
//                    break;
//                case 3:
//                    Join<TFamille, TGrossiste> fg = fa.join("lgGROSSISTEID", JoinType.INNER);
//                    if (!"".equals(lg_GROSSISTE_ID)) {
//                        predicate = cb.and(predicate, cb.equal(fg.get("lgGROSSISTEID"), lg_GROSSISTE_ID));
//                    }
//                    if (!"".equals(str_BEGIN) && !"".equals(str_END)) {
//                        Predicate ge = cb.greaterThanOrEqualTo(fg.get(TGrossiste_.strCODE), str_BEGIN);
//                        Predicate gl = cb.lessThanOrEqualTo(fg.get(TGrossiste_.strCODE), str_END);
//                        predicate = cb.and(predicate, cb.and(ge), cb.and(gl));
//                    }
//                    if (!"".equals(str_BEGIN) && "".equals(str_END)) {
//                        Predicate ge = cb.greaterThanOrEqualTo(fg.get(TGrossiste_.strCODE), str_BEGIN);
//                        predicate = cb.and(predicate, cb.and(ge));
//                    }
//                    if ("".equals(str_BEGIN) && !"".equals(str_END)) {
//                        Predicate gl = cb.lessThanOrEqualTo(fg.get(TGrossiste_.strCODE), str_END);
//                        predicate = cb.and(predicate, cb.and(gl));
//                    }
//                    break;
//                default:
//                    break;
//            }
//
//            predicate = cb.and(predicate, cb.equal(root.get(TStockSnapshot_.lgEMPL), empl));
//            predicate = cb.and(predicate, cb.equal(cb.function("DATE", Date.class, root.get(TStockSnapshot_.dtDAY)), java.sql.Date.valueOf(date)));
//            cq.multiselect(cb.sumAsLong(cb.prod(root.get(TStockSnapshot_.intNUMBER), root.get(TStockSnapshot_.tPAF))), cb.sumAsLong(cb.prod(root.get(TStockSnapshot_.intNUMBER), root.get(TStockSnapshot_.tPU))));
//
//            cq.where(predicate);
//            Query q = em.createQuery(cq);
//
//            List<Object[]> list = q.getResultList();
//            list.forEach((t) -> {
//                try {
//                    json.put("Achat", Util.getFormattedLongValue(t[0] != null ? Long.valueOf(t[0] + "") : 0)).put("Vente", Util.getFormattedLongValue(t[1] != null ? Long.valueOf(t[1] + "") : 0));
//                } catch (JSONException ex) {
//
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return json;
    }

    public JSONArray getFamilles(boolean all, String criteria, String lgEmp, int start, int limit) {
        EntityManager em = this.getEntityManager();
        JSONArray array = new JSONArray();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TFamilleStock> root = cq.from(TFamilleStock.class);

            Join<TFamilleStock, TFamille> fa = root.join("lgFAMILLEID", JoinType.INNER);
//            Join<TFamilleGrossiste, TFamille> st = fa.join("lgFAMILLEID", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            if (!"".equals(criteria)) {
                predicate = cb.and(predicate, cb.or(cb.like(fa.get(TFamille_.strNAME), criteria + "%"), cb.like(fa.get(TFamille_.intCIP), criteria + "%"), cb.like(fa.get(TFamille_.intEAN13), criteria + "%")/*, cb.like(st.get("strCODEARTICLE"), criteria + "%")*/, cb.like(fa.get(TFamille_.lgFAMILLEID), criteria + "%"), cb.like(fa.get(TFamille_.strDESCRIPTION), criteria + "%")));
            }

            predicate = cb.and(predicate, cb.equal(root.get(TFamilleStock_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(fa.get(TFamille_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(root.get(TFamilleStock_.lgEMPLACEMENTID).get(TEmplacement_.lgEMPLACEMENTID), lgEmp));
            predicate = cb.and(predicate, cb.equal(fa.get(TFamille_.boolDECONDITIONNE), Boolean.FALSE));

            cq.multiselect(fa.get(TFamille_.lgFAMILLEID), fa.get(TFamille_.strDESCRIPTION), fa.get(TFamille_.intCIP), fa.get(TFamille_.intPRICE), root.get(TFamilleStock_.intNUMBER), root.get(TFamilleStock_.intNUMBERAVAILABLE), fa.get(TFamille_.strCODETABLEAU));

            cq.where(predicate);
            Query q = em.createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);

            }

            List<Object[]> list = q.getResultList();

            list.forEach((t) -> {
                try {
                    JSONObject ob = new JSONObject();
                    ob.putOnce("lg_FAMILLE_ID", t[0] + "").putOnce("str_DESCRIPTION", t[1] + "")
                            .putOnce("CIP", t[2] + "").putOnce("int_PRICE", t[3] + "").putOnce("int_NUMBER", t[4] + "")
                            .putOnce("int_NUMBER_AVAILABLE", t[5] + "").put("lg_ZONE_GEO_ID", (t[6] != null ? t[6] + "" : ""));
                    array.put(ob);
                } catch (JSONException ex) {

                }
            });

        } finally {
            if (em != null) {

            }
        }
        return array;
    }

    public int getFamilles(String criteria, String lgEmp) {
        EntityManager em = this.getEntityManager();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TFamilleStock> root = cq.from(TFamilleStock.class);
            Join<TFamilleStock, TFamille> fa = root.join("lgFAMILLEID", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            if (!"".equals(criteria)) {
                predicate = cb.and(predicate, cb.or(cb.like(fa.get(TFamille_.strNAME), criteria + "%"), cb.like(fa.get(TFamille_.intCIP), criteria + "%"), cb.like(fa.get(TFamille_.intEAN13), criteria + "%")/*, cb.like(st.get("strCODEARTICLE"), criteria + "%")*/, cb.like(fa.get(TFamille_.lgFAMILLEID), criteria + "%"), cb.like(fa.get(TFamille_.strDESCRIPTION), criteria + "%")));
            }
            predicate = cb.and(predicate, cb.equal(root.get(TFamilleStock_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(fa.get(TFamille_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(root.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            predicate = cb.and(predicate, cb.equal(fa.get(TFamille_.boolDECONDITIONNE), Boolean.FALSE));
            cq.select(cb.count(fa.get(TFamille_.lgFAMILLEID)));
            cq.where(predicate);
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            if (em != null) {

            }
        }
    }

    public JSONArray getFamille(boolean all, String criteria, String lgEmp, int start, int limit) {
        EntityManager em = this.getEntityManager();
        JSONArray array = new JSONArray();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TFamille> root = cq.from(TFamille.class);
            Join<TFamille, TFamilleGrossiste> st = root.join("tFamilleGrossisteCollection", JoinType.INNER);
            Join<TFamille, TFamilleStock> fa = root.join("tFamilleStockCollection", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            if (!"".equals(criteria)) {
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TFamille_.strNAME), criteria + "%"), cb.like(root.get(TFamille_.intCIP), criteria + "%"), cb.like(root.get(TFamille_.intEAN13), criteria + "%"), cb.like(st.get("strCODEARTICLE"), criteria + "%"), cb.like(root.get(TFamille_.lgFAMILLEID), criteria + "%"), cb.like(root.get(TFamille_.strDESCRIPTION), criteria + "%")));
            }
            predicate = cb.and(predicate, cb.equal(root.get(TFamille_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(fa.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            cq.multiselect(root.get(TFamille_.lgFAMILLEID), root.get(TFamille_.strDESCRIPTION), root.get(TFamille_.intCIP), root.get(TFamille_.intPRICE), fa.get(TFamilleStock_.intNUMBER), fa.get(TFamilleStock_.intNUMBERAVAILABLE), root.get("lgZONEGEOID").get("strLIBELLEE"), root.get(TFamille_.intPAF)).orderBy(cb.asc(root.get(TFamille_.strDESCRIPTION))).distinct(true);
            cq.where(predicate);
            Query q = em.createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);
            }
            List<Object[]> list = q.getResultList();
            list.forEach((t) -> {
                try {
                    JSONObject ob = new JSONObject();
                    ob.putOnce("lg_FAMILLE_ID", t[0] + "").putOnce("str_DESCRIPTION", t[1] + "")
                            .putOnce("CIP", t[2] + "").putOnce("int_PRICE", t[3] + "").putOnce("int_NUMBER", t[5] + "")
                            .putOnce("int_NUMBER_AVAILABLE", t[5] + "").put("lg_ZONE_GEO_ID", t[6]).put("int_PAF", t[7] + "");
                    array.put(ob);
                } catch (JSONException ex) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public JSONArray orderData(boolean all, String criteria, String lgEmp, int start, int limit) {
        EntityManager em = this.getEntityManager();
        JSONArray array = new JSONArray();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TFamille> root = cq.from(TFamille.class);
            Join<TFamille, TFamilleGrossiste> st = root.join("tFamilleGrossisteCollection", JoinType.INNER);
            Join<TFamille, TFamilleStock> fa = root.join("tFamilleStockCollection", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            if (!"".equals(criteria)) {
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TFamille_.strNAME), criteria + "%"), cb.like(root.get(TFamille_.intCIP), criteria + "%"), cb.like(root.get(TFamille_.intEAN13), criteria + "%"), cb.like(st.get("strCODEARTICLE"), criteria + "%"), cb.like(root.get(TFamille_.lgFAMILLEID), criteria + "%"), cb.like(root.get(TFamille_.strDESCRIPTION), criteria + "%")));
            }
            predicate = cb.and(predicate, cb.equal(root.get(TFamille_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(root.get(TFamille_.boolDECONDITIONNE), 0));
            predicate = cb.and(predicate, cb.equal(fa.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            cq.multiselect(root.get(TFamille_.lgFAMILLEID), root.get(TFamille_.strDESCRIPTION), root.get(TFamille_.intCIP), root.get(TFamille_.intPRICE), fa.get(TFamilleStock_.intNUMBER), fa.get(TFamilleStock_.intNUMBERAVAILABLE), root.get("lgZONEGEOID").get("strLIBELLEE"), root.get(TFamille_.intPAF)).orderBy(cb.asc(root.get(TFamille_.strDESCRIPTION))).distinct(true);
            cq.where(predicate);
            Query q = em.createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);
            }
            List<Object[]> list = q.getResultList();
            list.forEach((t) -> {
                try {
                    JSONObject ob = new JSONObject();
                    ob.putOnce("lg_FAMILLE_ID", t[0] + "").putOnce("str_DESCRIPTION", t[1] + "")
                            .putOnce("CIP", t[2] + "").putOnce("int_PRICE", t[3] + "").putOnce("int_NUMBER", t[5] + "")
                            .putOnce("int_NUMBER_AVAILABLE", t[5] + "").put("lg_ZONE_GEO_ID", t[6]).put("int_PAF", t[7] + "");
                    array.put(ob);
                } catch (JSONException ex) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public int orderData(String criteria, String lgEmp) {
        EntityManager em = this.getEntityManager();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TFamille> root = cq.from(TFamille.class);
            Join<TFamille, TFamilleGrossiste> st = root.join("tFamilleGrossisteCollection", JoinType.INNER);
            Join<TFamille, TFamilleStock> fa = root.join("tFamilleStockCollection", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            if (!"".equals(criteria)) {
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TFamille_.strNAME), criteria + "%"), cb.like(root.get(TFamille_.intCIP), criteria + "%"), cb.like(root.get(TFamille_.intEAN13), criteria + "%"), cb.like(st.get("strCODEARTICLE"), criteria + "%"), cb.like(root.get(TFamille_.lgFAMILLEID), criteria + "%"), cb.like(root.get(TFamille_.strDESCRIPTION), criteria + "%")));
            }

            predicate = cb.and(predicate, cb.equal(fa.get(TFamilleStock_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(root.get(TFamille_.boolDECONDITIONNE), 0));
            predicate = cb.and(predicate, cb.equal(fa.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));

            cq.select(cb.countDistinct(root));

            cq.where(predicate);

            Query q = em.createQuery(cq);

            return ((Long) q.getSingleResult()).intValue();
        } finally {
            if (em != null) {

            }
        }

    }

    public int getFamille(String criteria, String lgEmp) {
        EntityManager em = this.getEntityManager();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TFamille> root = cq.from(TFamille.class);
            Join<TFamille, TFamilleGrossiste> st = root.join("tFamilleGrossisteCollection", JoinType.INNER);
            Join<TFamille, TFamilleStock> fa = root.join("tFamilleStockCollection", JoinType.INNER);
//            Join<TFamilleGrossiste, TFamille> st = fa.join("lgFAMILLEID", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            if (!"".equals(criteria)) {
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TFamille_.strNAME), criteria + "%"), cb.like(root.get(TFamille_.intCIP), criteria + "%"), cb.like(root.get(TFamille_.intEAN13), criteria + "%"), cb.like(st.get("strCODEARTICLE"), criteria + "%"), cb.like(root.get(TFamille_.lgFAMILLEID), criteria + "%"), cb.like(root.get(TFamille_.strDESCRIPTION), criteria + "%")));
            }

            predicate = cb.and(predicate, cb.equal(fa.get(TFamilleStock_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(fa.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));

            cq.select(cb.countDistinct(root));

            cq.where(predicate);

            Query q = em.createQuery(cq);

            return ((Long) q.getSingleResult()).intValue();
        } finally {
            if (em != null) {

            }
        }

    }

    public Integer getMontant(String id) {
        EntityManager em = getEntityManager();
        Integer integer = 0;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TCashTransaction> root = cq.from(TCashTransaction.class);
            Predicate p = cb.greaterThan(root.get(TCashTransaction_.intAMOUNT), 0);
            cq.select(root.get("intAMOUNT"));
            cq.where(cb.and(cb.equal(root.get(TCashTransaction_.strRESSOURCEREF), id), p));
            Query q = em.createQuery(cq);
            integer = (Integer) q.getSingleResult();

        } catch (Exception e) {

        }
        return (integer != null ? integer : 0);

    }

    private Integer venteCarnetRemise(String dt_start, String dt_end, String lgEmp) {
        LocalDate dtstart = LocalDate.parse(dt_start);
        LocalDate dtend = LocalDate.parse(dt_end);
        Period pd = Period.between(dtstart, dtend);
        EntityManager em = this.getEntityManager();
        Integer amount = 0;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);

            Root<TPreenregistrement> root = cq.from(TPreenregistrement.class);
            Join<TPreenregistrement, TUser> pu = root.join("lgUSERID", JoinType.INNER);
            Join<TPreenregistrement, TReglement> r = root.join("lgREGLEMENTID", JoinType.INNER);
            Predicate predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.equal(pu.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.bISCANCEL), Boolean.FALSE));
            predicate = cb.and(predicate, cb.notLike(root.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.strSTATUT), "is_Closed"));
            predicate = cb.and(predicate, cb.equal(root.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "3"));
            Predicate ge = cb.greaterThan(root.get(TPreenregistrement_.intPRICE), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrement_.dtUPDATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            cq.select(root.get(TPreenregistrement_.intPRICEREMISE));
            cq.where(predicate, btw, ge);
            Query q = em.createQuery(cq);
            amount = (Integer) q.getSingleResult();

        } catch (Exception e) {
//            e.printStackTrace();
        }
        return amount;
    }

    // balance vente caise 18/09/2017
    public JSONArray getBalance(String dt_start, String dt_end, String lgEmp) {

        JSONArray array = new JSONArray();
        LocalDate dtstart = LocalDate.parse(dt_start);
        LocalDate dtend = LocalDate.parse(dt_end);
        Period pd = Period.between(dtstart, dtend);
        if (pd.getMonths() > 1) {
            return getBalanceInterval(dt_start, dt_end, lgEmp);
        }
        EntityManager em = this.getEntityManager();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TPreenregistrement> root = cq.from(TPreenregistrement.class);
            Join<TPreenregistrement, TUser> pu = root.join("lgUSERID", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.equal(pu.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.bISCANCEL), Boolean.FALSE));
            predicate = cb.and(predicate, cb.notLike(root.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.strSTATUT), "is_Closed"));
            Predicate ge = cb.greaterThan(root.get(TPreenregistrement_.intPRICE), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrement_.dtUPDATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            cq.multiselect(cb.sum(root.get(TPreenregistrement_.intPRICE)),
                    cb.sum(root.get(TPreenregistrement_.intPRICEREMISE)),
                    cb.sum(cb.diff(root.get(TPreenregistrement_.intPRICE), root.get(TPreenregistrement_.intPRICEREMISE))),
                    root.get(TPreenregistrement_.strTYPEVENTE),
                    cb.selectCase().when(cb.equal(root.get(TPreenregistrement_.strTYPEVENTE), "VO"),
                            cb.sum(root.get(TPreenregistrement_.intCUSTPART)))
                            .otherwise(cb.sum(cb.diff(root.get(TPreenregistrement_.intPRICE),
                                    root.get(TPreenregistrement_.intPRICEREMISE)))), cb.count(root),
                    cb.quot(cb.sum(root.get(TPreenregistrement_.intPRICE)),
                            cb.count(root)), cb.selectCase()
                    .when(cb.equal(root.get(TPreenregistrement_.strTYPEVENTE), "VO"),
                            cb.sum(cb.diff(root.get(TPreenregistrement_.intPRICE),
                                    root.get(TPreenregistrement_.intCUSTPART)))).otherwise(0))
                    .groupBy(root.get(TPreenregistrement_.strTYPEVENTE)).orderBy(cb.desc(root.get(TPreenregistrement_.strTYPEVENTE)));
            cq.where(predicate, btw, ge);
            Query q = em.createQuery(cq);

            List<Object[]> list = q.getResultList();

            double Gbl = 0.0;
            int nb = 0;
            Integer totalcq = 0, totalcb = 0, totalEs = 0;
            for (Object[] t : list) {
                try {
                    //Integer TOTAL_DIFFERE = 0;
                    Integer VENTE_BRUT = Integer.valueOf(t[0] + ""),
                            TOTAL_REMISE = Integer.valueOf(t[1] + ""),
                            VENTE_NET = Integer.valueOf(t[2] + ""), TOTAL_CAISSE = Integer.valueOf(t[4] + ""), PANIER_MOYEN = Math.round(Double.valueOf(t[6] + "").intValue()),
                            PART_TIERSPAYANT = Integer.valueOf(t[7] + "");
//                    JSONObject transac = getBalanceRegl(dt_start, dt_end, t[3] + "", lgEmp, isOk);
                    if ("VO".equals(t[3] + "")) {
                        PART_TIERSPAYANT -= venteCarnetRemise(dt_start, dt_end, lgEmp);
                    }

                    Integer TOTAL_DIFFERE = getBalanceDiff(dt_start, dt_end, t[3] + "", lgEmp);

                    Integer montantES = getBalanceRegl(dt_start, dt_end, t[3] + "", lgEmp, "1");
                    montantES = (montantES != null ? montantES : 0);
                    montantES += getBalanceRegl(dt_start, dt_end, t[3] + "", lgEmp, "4");

                    Integer Cheques = getBalanceRegl(dt_start, dt_end, t[3] + "", lgEmp, "2");
                    Cheques = (Cheques != null ? Cheques : 0);
                    Integer cba = getBalanceRegl(dt_start, dt_end, t[3] + "", lgEmp, "3");
                    cba = (cba != null ? cba : 0);

                    /* if (y.compareTo(x) == 0) {
                        JSONObject json = getVenteAnnuler(dt_end, t[3] + "");

                        VENTE_BRUT -= json.getInt("monantTTC");
                        TOTAL_REMISE -= json.getInt("remise");
                        VENTE_NET -= json.getInt("montant");
                        TOTAL_CAISSE -= json.getInt("montantES");

                        PART_TIERSPAYANT -= json.getInt("tp");

                        montantES -= json.getInt("montantES");

                        Cheques -= json.getInt("cheques");
                        cba -= json.getInt("cb");
                        TOTAL_DIFFERE -= json.getInt("differe");
                        Long _PANIER_MOYEN = Math.round(Double.valueOf(VENTE_BRUT) / Integer.valueOf(t[5] + ""));
                        PANIER_MOYEN = _PANIER_MOYEN.intValue();

                    }*/
                    List<AnnulationSnapshot> annulationSnapshots = findAnnulation(LocalDate.parse(dt_start), LocalDate.parse(dt_end), t[3] + "", lgEmp, em);
                    if (!annulationSnapshots.isEmpty()) {
                        LongAdder espece = new LongAdder();
                        LongAdder cheque = new LongAdder();
                        LongAdder cban = new LongAdder();
                        LongAdder restDiff = new LongAdder();
                        LongAdder tp = new LongAdder();
//                        LongAdder vir = new LongAdder();
                        LongAdder totalBt = new LongAdder();
                        LongAdder totalNet = new LongAdder();
                        LongAdder totalRem = new LongAdder();
                        Map<TTypeReglement, List<AnnulationSnapshot>> mapan = annulationSnapshots.stream().filter(x -> Objects.nonNull(x)).collect(Collectors.groupingBy(AnnulationSnapshot::getReglement));
                        mapan.forEach((k, v) -> {
                            switch (k.getLgTYPEREGLEMENTID()) {
                                case DateConverter.MODE_ESP:
                                    v.forEach(l -> {
                                        totalRem.add(l.getRemise());
                                        totalNet.add(l.getMontant() - l.getRemise());
                                        totalBt.add(l.getMontant());
                                        tp.add(l.getMontantTP());
                                        espece.add(l.getMontantPaye());
//                                        Integer rest = l.getMontant() - l.getRemise() - l.getMontantTP() - l.getMontantPaye();
                                        restDiff.add(l.getMontantRestant());

                                    });
                                    break;
                                case DateConverter.MODE_CB:
                                    v.forEach(l -> {
                                        totalRem.add(l.getRemise());
                                        totalNet.add(l.getMontant() - l.getRemise());
                                        totalBt.add(l.getMontant());
                                        tp.add(l.getMontantTP());
                                        cban.add(l.getMontantPaye());
                                    });
                                    break;
                                case DateConverter.MODE_CHEQUE:
                                    v.forEach(l -> {
                                        totalRem.add(l.getRemise());
                                        totalNet.add(l.getMontant() - l.getRemise());
                                        totalBt.add(l.getMontant());
                                        tp.add(l.getMontantTP());
                                        cheque.add(l.getMontantPaye());
                                    });
                                    break;
                                /* case DateConverter.MODE_VIREMENT:
                                    v.forEach(l -> {
                                        totalRem.add(l.getRemise());
                                        totalNet.add(l.getMontant() - l.getRemise());
                                        totalBt.add(l.getMontant());
                                        tp.add(l.getMontantTP());
                                        vir.add(l.getMontantPaye());
                                    });
                                    break;*/
                                default:
                                    break;
                            }
                        });
                        montantES -= espece.intValue();
                        VENTE_NET -= totalNet.intValue();
                        Cheques -= cheque.intValue();
                        cba -= cban.intValue();
                        TOTAL_DIFFERE -= restDiff.intValue();
                        VENTE_BRUT -= totalBt.intValue();
                        PART_TIERSPAYANT -= tp.intValue();
                        TOTAL_CAISSE -= espece.intValue();
                        TOTAL_REMISE -= totalRem.intValue();
                        Long _PANIER_MOYEN = Math.round(Double.valueOf(VENTE_BRUT) / Integer.valueOf(t[5] + ""));
                        PANIER_MOYEN = _PANIER_MOYEN.intValue();
                    }
                    Gbl += VENTE_NET;
                    totalcq += Cheques;
                    totalcb += cba;
                    totalEs += montantES;
                    nb += Integer.valueOf(t[5] + "");
                    JSONObject ob = new JSONObject();
                    ob.put("VENTE_BRUT", VENTE_BRUT).put("TOTAL_DIFFERE", TOTAL_DIFFERE)
                            .put("TOTAL_REMISE", TOTAL_REMISE)
                            .put("VENTE_NET", VENTE_NET);
                    ob.put("str_TYPE_VENTE", t[3] + "");
                    ob.put("TOTAL_CAISSE", TOTAL_CAISSE);
                    ob.put("NB", Integer.valueOf(t[5] + ""));
                    ob.put("PANIER_MOYEN", PANIER_MOYEN);
                    ob.put("PART_TIERSPAYANT", PART_TIERSPAYANT);
                    if ("VNO".equals(t[3] + "")) {
                        ob.put("PART_TIERSPAYANT", 0);
                    }

                    ob.put("TOTAL_ESPECE", montantES);
                    ob.put("TOTAL_CHEQUE", Cheques);
                    ob.put("TOTAL_CARTEBANCAIRE", cba);

                    array.put(ob);
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

            }

            JSONObject totaux = new JSONObject();
            double ration;
            long achat = getMontantAchats(dt_start, dt_end, lgEmp);
            long marge = getMarge(dt_start, dt_end, lgEmp);
            double rat = 0.0;
            if (achat > 0) {
                ration = (Gbl / achat);
                rat = new BigDecimal(ration).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
            totaux.put("NB", nb).put("GLOBALESP", totalEs).put("GLOBALCB", totalcb).put("TOTALMARGE", marge)
                    .put("GLOBALCQ", totalcq).put("GLOBAL", Double.valueOf(Gbl).intValue()).put("TOTALACHAT", achat).put("TOTALRATIO", rat);
            array.put(totaux);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public JSONArray getBalanceInterval(String dt_start, String dt_end, String lgEmp) {
        JSONArray array = new JSONArray();
        String str_TYPE_VENTE = "", str_TYPE_VENTEVO = "";
        Integer NB = 0, NBT = 0, NBVO = 0;
        EntityManager em = this.getEntityManager();
        long PART_TIERSPAYANT = 0, TOTALMARGE = 0, TOTALACHAT = 0, GLOBALESP = 0, GLOBAL = 0, GLOBALCQ = 0, GLOBALCB = 0, PART_TIERSPAYANTVO = 0,
                TOTAL_REMISE = 0,
                VENTE_BRUT = 0,
                TOTAL_ESPECE = 0,
                TOTAL_CHEQUE = 0,
                TOTAL_CARTEBANCAIRE = 0,
                VENTE_NET = 0,
                TOTAL_DIFFERE = 0,
                TOTAL_CAISSE = 0,
                TOTAL_REMISEVO = 0,
                VENTE_BRUTVO = 0,
                TOTAL_ESPECEVO = 0,
                TOTAL_CHEQUEVO = 0,
                TOTAL_CARTEBANCAIREVO = 0,
                VENTE_NETVO = 0,
                TOTAL_DIFFEREVO = 0,
                TOTAL_CAISSEVO = 0;
        int isOk = 0;
        try {
            TParameters p;
            try {

                p = em.getReference(TParameters.class, "KEY_PARAMS");
                if (p != null) {
                    isOk = Integer.valueOf(p.getStrVALUE().trim());
                }
                LocalDate dtstart = LocalDate.parse(dt_start);
                LocalDate dtend = LocalDate.parse(dt_end);
                Period period = Period.between(dtstart, dtend);
                for (int i = 0; i < period.getMonths(); i++) {
                    LocalDate _dtStart = dtstart.plusMonths(i);
                    JSONArray _array = getBalance(_dtStart, _dtStart.plusDays(_dtStart.lengthOfMonth() - 1), lgEmp);
                    JSONObject totaux = _array.getJSONObject((_array.length() - 1));
                    NBT += totaux.getInt("NB");
                    TOTALMARGE += totaux.getLong("TOTALMARGE");
                    TOTALACHAT += totaux.getLong("TOTALACHAT");
                    GLOBALESP += totaux.getLong("GLOBALESP");
                    GLOBAL += totaux.getLong("GLOBAL");
                    GLOBALCQ += totaux.getLong("GLOBALCQ");
                    GLOBALCB += totaux.getLong("GLOBALCB");
                    for (int j = 0; j < _array.length() - 1; j++) {
                        JSONObject object = _array.getJSONObject(j);
                        if (object.getString("str_TYPE_VENTE").equals("VNO")) {
                            TOTAL_REMISE += object.getInt("TOTAL_REMISE");
                            NB += object.getInt("NB");
                            VENTE_BRUT += object.getLong("VENTE_BRUT");
                            TOTAL_ESPECE += object.getLong("TOTAL_ESPECE");
                            TOTAL_CHEQUE += object.getLong("TOTAL_CHEQUE");
                            TOTAL_CARTEBANCAIRE += object.getLong("TOTAL_CARTEBANCAIRE");
                            VENTE_NET += object.getLong("VENTE_NET");
                            TOTAL_DIFFERE += object.getLong("TOTAL_DIFFERE");
                            TOTAL_CAISSE += object.getLong("TOTAL_CAISSE");
                            str_TYPE_VENTE = object.getString("str_TYPE_VENTE");
                        } else {
                            PART_TIERSPAYANTVO += object.getLong("PART_TIERSPAYANT");
                            TOTAL_REMISEVO += object.getInt("TOTAL_REMISE");
                            NBVO += object.getInt("NB");
                            VENTE_BRUTVO += object.getLong("VENTE_BRUT");
                            TOTAL_ESPECEVO += object.getLong("TOTAL_ESPECE");
                            TOTAL_CHEQUEVO += object.getLong("TOTAL_CHEQUE");
                            TOTAL_CARTEBANCAIREVO += object.getLong("TOTAL_CARTEBANCAIRE");
                            VENTE_NETVO += object.getLong("VENTE_NET");
                            TOTAL_DIFFEREVO += object.getLong("TOTAL_DIFFERE");
                            TOTAL_CAISSEVO += object.getLong("TOTAL_CAISSE");
                            str_TYPE_VENTEVO = object.getString("str_TYPE_VENTE");
                        }
                    }

                }
                LocalDate dd = LocalDate.of(dtend.getYear(), dtend.getMonthValue(), 1);
                JSONArray endarray = getBalance(dd, dd.plusDays(dd.lengthOfMonth() - 1), lgEmp);
                JSONObject endtotaux = endarray.getJSONObject((endarray.length() - 1));
                NBT += endtotaux.getInt("NB");
                TOTALMARGE += endtotaux.getLong("TOTALMARGE");
                TOTALACHAT += endtotaux.getLong("TOTALACHAT");
                GLOBALESP += endtotaux.getLong("GLOBALESP");
                GLOBAL += endtotaux.getLong("GLOBAL");
                GLOBALCQ += endtotaux.getLong("GLOBALCQ");
                GLOBALCB += endtotaux.getLong("GLOBALCB");

                for (int j = 0; j < endarray.length() - 1; j++) {
                    JSONObject object = endarray.getJSONObject(j);
                    if (object.getString("str_TYPE_VENTE").equals("VNO")) {
                        TOTAL_REMISE += object.getInt("TOTAL_REMISE");
                        NB += object.getInt("NB");
                        VENTE_BRUT += object.getLong("VENTE_BRUT");
                        //PART_TIERSPAYANT+= object.getLong("PART_TIERSPAYANT");
                        TOTAL_ESPECE += object.getLong("TOTAL_ESPECE");
                        TOTAL_CHEQUE += object.getLong("TOTAL_CHEQUE");
                        TOTAL_CARTEBANCAIRE += object.getLong("TOTAL_CARTEBANCAIRE");
                        VENTE_NET += object.getLong("VENTE_NET");
                        TOTAL_DIFFERE += object.getLong("TOTAL_DIFFERE");
                        TOTAL_CAISSE += object.getLong("TOTAL_CAISSE");
                        str_TYPE_VENTE = object.getString("str_TYPE_VENTE");
                    } else {
                        PART_TIERSPAYANTVO += object.getLong("PART_TIERSPAYANT");
                        TOTAL_REMISEVO += object.getInt("TOTAL_REMISE");
                        NBVO += object.getInt("NB");
                        VENTE_BRUTVO += object.getLong("VENTE_BRUT");
                        TOTAL_ESPECEVO += object.getLong("TOTAL_ESPECE");
                        TOTAL_CHEQUEVO += object.getLong("TOTAL_CHEQUE");
                        TOTAL_CARTEBANCAIREVO += object.getLong("TOTAL_CARTEBANCAIRE");
                        VENTE_NETVO += object.getLong("VENTE_NET");
                        TOTAL_DIFFEREVO += object.getLong("TOTAL_DIFFERE");
                        TOTAL_CAISSEVO += object.getLong("TOTAL_CAISSE");
                        str_TYPE_VENTEVO = object.getString("str_TYPE_VENTE");
                    }
                }

                Long _PANIER_MOYEN = Math.round(Double.valueOf(VENTE_BRUT) / NB);
                JSONObject ob = new JSONObject();
                ob.put("VENTE_BRUT", VENTE_BRUT).put("TOTAL_DIFFERE", TOTAL_DIFFERE)
                        .put("TOTAL_REMISE", TOTAL_REMISE)
                        .put("VENTE_NET", VENTE_NET);
                ob.put("str_TYPE_VENTE", "VNO");
                ob.put("TOTAL_CAISSE", TOTAL_CAISSE);
                ob.put("NB", NB);
                ob.put("PANIER_MOYEN", _PANIER_MOYEN.intValue());
                ob.put("PART_TIERSPAYANT", 0);
                ob.put("TOTAL_ESPECE", TOTAL_ESPECE);
                ob.put("TOTAL_CHEQUE", TOTAL_CHEQUE);
                ob.put("TOTAL_CARTEBANCAIRE", TOTAL_CARTEBANCAIRE);
                array.put(ob);
                Long _PANIER_MOYENVO = Math.round(Double.valueOf(VENTE_BRUTVO) / NBVO);

                JSONObject obvo = new JSONObject();
                obvo.put("VENTE_BRUT", VENTE_BRUTVO).put("TOTAL_DIFFERE", TOTAL_DIFFEREVO)
                        .put("TOTAL_REMISE", TOTAL_REMISEVO)
                        .put("VENTE_NET", VENTE_NETVO);
                obvo.put("str_TYPE_VENTE", "VO");
                obvo.put("TOTAL_CAISSE", TOTAL_CAISSEVO);
                obvo.put("NB", NBVO);
                obvo.put("PANIER_MOYEN", _PANIER_MOYENVO.intValue());
                obvo.put("PART_TIERSPAYANT", PART_TIERSPAYANTVO);
                obvo.put("TOTAL_ESPECE", TOTAL_ESPECEVO);
                obvo.put("TOTAL_CHEQUE", TOTAL_CHEQUEVO);
                obvo.put("TOTAL_CARTEBANCAIRE", TOTAL_CARTEBANCAIREVO);
                array.put(obvo);
                double ration;
                double rat = 0.0;
                if (TOTALACHAT > 0) {
                    ration = (GLOBAL / TOTALACHAT);
                    rat = new BigDecimal(ration).setScale(2, RoundingMode.HALF_UP).doubleValue();
                }
                JSONObject totaux = new JSONObject();
                totaux.put("NB", NBT).put("GLOBALESP", GLOBALESP).put("GLOBALCB", GLOBALCB).put("TOTALMARGE", TOTALMARGE)
                        .put("GLOBALCQ", GLOBALCQ).put("GLOBAL", Double.valueOf(GLOBAL).intValue())
                        .put("TOTALACHAT", TOTALACHAT).put("TOTALRATIO", rat).put("TOTALRATIO", rat);
                array.put(totaux);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return array;
    }

    private List<AnnulationSnapshot> findAnnulation(LocalDate dt_start, LocalDate dt_end, String typeVenteId, String empl, EntityManager em) {
        try {
            TypedQuery<AnnulationSnapshot> q = em.createQuery("SELECT o FROM  AnnulationSnapshot o WHERE FUNCTION('DATE',o.dateOp)  BETWEEN ?1 AND ?2 AND o.emplacement.lgEMPLACEMENTID=?3 AND o.preenregistrement.strTYPEVENTE=?4 ", AnnulationSnapshot.class);
            q.setParameter(1, java.sql.Date.valueOf(dt_start), TemporalType.DATE);
            q.setParameter(2, java.sql.Date.valueOf(dt_end), TemporalType.DATE);
            q.setParameter(3, empl);
            q.setParameter(4, typeVenteId);

            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    public JSONArray getBalance(LocalDate dt_start, LocalDate dt_end, String lgEmp) {
        EntityManager em = this.getEntityManager();
        JSONArray array = new JSONArray();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

            Root<TPreenregistrement> root = cq.from(TPreenregistrement.class);
            Join<TPreenregistrement, TUser> pu = root.join("lgUSERID", JoinType.INNER);
            Join<TPreenregistrement, TReglement> r = root.join("lgREGLEMENTID", JoinType.INNER);
            Predicate predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.equal(pu.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.bISCANCEL), Boolean.FALSE));
            predicate = cb.and(predicate, cb.notLike(root.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.strSTATUT), "is_Closed"));
            Predicate ge = cb.greaterThan(root.get(TPreenregistrement_.intPRICE), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrement_.dtUPDATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            cq.multiselect(cb.sum(root.get(TPreenregistrement_.intPRICE)),
                    cb.sum(root.get(TPreenregistrement_.intPRICEREMISE)),
                    cb.sum(cb.diff(root.get(TPreenregistrement_.intPRICE), root.get(TPreenregistrement_.intPRICEREMISE))),
                    root.get(TPreenregistrement_.strTYPEVENTE),
                    cb.selectCase().when(cb.equal(root.get(TPreenregistrement_.strTYPEVENTE), "VO"),
                            cb.sum(root.get(TPreenregistrement_.intCUSTPART)))
                            .otherwise(cb.sum(cb.diff(root.get(TPreenregistrement_.intPRICE),
                                    root.get(TPreenregistrement_.intPRICEREMISE)))), cb.count(root),
                    cb.quot(cb.sum(root.get(TPreenregistrement_.intPRICE)),
                            cb.count(root)), cb.selectCase()
                    .when(cb.equal(root.get(TPreenregistrement_.strTYPEVENTE), "VO"),
                            cb.sum(cb.diff(root.get(TPreenregistrement_.intPRICE), root.get(TPreenregistrement_.intCUSTPART)))).otherwise(0))
                    .groupBy(root.get(TPreenregistrement_.strTYPEVENTE)).orderBy(cb.desc(root.get(TPreenregistrement_.strTYPEVENTE)));
            cq.where(predicate, btw, ge);
            Query q = em.createQuery(cq);

            List<Object[]> list = q.getResultList();

            double percent = 0, Gbl = 0, vo = 0, vno = 0;
            int nb = 0;
            Integer totalcq = 0, totalcb = 0, totalEs = 0;
            for (Object[] t : list) {
                try {
                    //Integer TOTAL_DIFFERE = 0;
                    Integer VENTE_BRUT = Integer.valueOf(t[0] + ""),
                            TOTAL_REMISE = Integer.valueOf(t[1] + ""),
                            VENTE_NET = Integer.valueOf(t[2] + ""), TOTAL_CAISSE = Integer.valueOf(t[4] + ""), PANIER_MOYEN = Math.round(Double.valueOf(t[6] + "").intValue()),
                            PART_TIERSPAYANT = Integer.valueOf(t[7] + "");
                    Integer TOTAL_DIFFERE = getBalanceDiff(dt_start.toString(), dt_end.toString(), t[3] + "", lgEmp);

                    Integer montantES = getBalanceRegl(dt_start.toString(), dt_end.toString(), t[3] + "", lgEmp, "1");
                    montantES = (montantES != null ? montantES : 0);
                    montantES += getBalanceRegl(dt_start.toString(), dt_end.toString(), t[3] + "", lgEmp, "4");

                    Integer Cheques = getBalanceRegl(dt_start.toString(), dt_end.toString(), t[3] + "", lgEmp, "2");
                    Cheques = (Cheques != null ? Cheques : 0);
                    Integer cba = getBalanceRegl(dt_start.toString(), dt_end.toString(), t[3] + "", lgEmp, "3");
                    cba = (cba != null ? cba : 0);
                    List<AnnulationSnapshot> annulationSnapshots = findAnnulation(dt_start, dt_end, t[3] + "", lgEmp, em);
                    if (!annulationSnapshots.isEmpty()) {
                        LongAdder espece = new LongAdder();
                        LongAdder cheque = new LongAdder();
                        LongAdder cban = new LongAdder();
                        LongAdder restDiff = new LongAdder();
                        LongAdder tp = new LongAdder();
//                        LongAdder vir = new LongAdder();
                        LongAdder totalBt = new LongAdder();
                        LongAdder totalNet = new LongAdder();
                        LongAdder totalRem = new LongAdder();
                        Map<TTypeReglement, List<AnnulationSnapshot>> mapan = annulationSnapshots.stream().filter(x -> Objects.nonNull(x)).collect(Collectors.groupingBy(AnnulationSnapshot::getReglement));
                        mapan.forEach((k, v) -> {
                            switch (k.getLgTYPEREGLEMENTID()) {
                                case DateConverter.MODE_ESP:
                                    v.forEach(l -> {
                                        totalRem.add(l.getRemise());
                                        totalNet.add(l.getMontant() - l.getRemise());
                                        totalBt.add(l.getMontant());
                                        tp.add(l.getMontantTP());
                                        espece.add(l.getMontantPaye());
                                        Integer rest = l.getMontant() - l.getRemise() - l.getMontantTP() - l.getMontantPaye();
                                        restDiff.add(rest.compareTo(0) > 0 ? rest : 0);

                                    });
                                    break;
                                case DateConverter.MODE_CB:
                                    v.forEach(l -> {
                                        totalRem.add(l.getRemise());
                                        totalNet.add(l.getMontant() - l.getRemise());
                                        totalBt.add(l.getMontant());
                                        tp.add(l.getMontantTP());
                                        cban.add(l.getMontantPaye());
                                    });
                                    break;
                                case DateConverter.MODE_CHEQUE:
                                    v.forEach(l -> {
                                        totalRem.add(l.getRemise());
                                        totalNet.add(l.getMontant() - l.getRemise());
                                        totalBt.add(l.getMontant());
                                        tp.add(l.getMontantTP());
                                        cheque.add(l.getMontantPaye());
                                    });
                                    break;
                                /* case DateConverter.MODE_VIREMENT:
                                    v.forEach(l -> {
                                        totalRem.add(l.getRemise());
                                        totalNet.add(l.getMontant() - l.getRemise());
                                        totalBt.add(l.getMontant());
                                        tp.add(l.getMontantTP());
                                        vir.add(l.getMontantPaye());
                                    });
                                    break;*/
                                default:
                                    break;
                            }
                        });
                        montantES -= espece.intValue();
                        VENTE_NET -= totalNet.intValue();
                        Cheques -= cheque.intValue();
                        cba -= cban.intValue();
                        TOTAL_DIFFERE -= restDiff.intValue();
                        VENTE_BRUT -= totalBt.intValue();
                        PART_TIERSPAYANT -= tp.intValue();
                        TOTAL_CAISSE -= espece.intValue();
                        TOTAL_REMISE -= totalRem.intValue();
                        Long _PANIER_MOYEN = Math.round(Double.valueOf(VENTE_BRUT) / Integer.valueOf(t[5] + ""));
                        PANIER_MOYEN = _PANIER_MOYEN.intValue();
                    }

                    Gbl += VENTE_NET;
                    totalcq += Cheques;
                    totalcb += cba;
                    totalEs += montantES;
                    nb += Integer.valueOf(t[5] + "");
                    JSONObject ob = new JSONObject();
                    ob.put("VENTE_BRUT", VENTE_BRUT).put("TOTAL_DIFFERE", TOTAL_DIFFERE)
                            .put("TOTAL_REMISE", TOTAL_REMISE)
                            .put("VENTE_NET", VENTE_NET);
                    ob.put("str_TYPE_VENTE", t[3] + "");
                    ob.put("TOTAL_CAISSE", TOTAL_CAISSE);
                    ob.put("NB", Integer.valueOf(t[5] + ""));
                    ob.put("PANIER_MOYEN", PANIER_MOYEN);
                    ob.put("PART_TIERSPAYANT", PART_TIERSPAYANT);
                    if ("VNO".equals(t[3] + "")) {
                        ob.put("PART_TIERSPAYANT", 0);
                    }

                    ob.put("TOTAL_ESPECE", montantES);
                    ob.put("TOTAL_CHEQUE", Cheques);
                    ob.put("TOTAL_CARTEBANCAIRE", cba);

                    array.put(ob);
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
            }
            JSONObject totaux = new JSONObject();

            long achat = getMontantAchats(dt_start.toString(), dt_end.toString(), lgEmp);
            long marge = getMarge(dt_start.toString(), dt_end.toString(), lgEmp);

            totaux.put("NB", nb).put("GLOBALESP", totalEs).put("GLOBALCB", totalcb).put("TOTALMARGE", marge)
                    .put("GLOBALCQ", totalcq).put("GLOBAL", Double.valueOf(Gbl).longValue()).put("TOTALACHAT", achat);
            array.put(totaux);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return array;
    }

    public JSONObject getVenteAnnuler(String dt_end, String typevente) {
        JSONObject json = new JSONObject();
        EntityManager em = this.getEntityManager();
        TCashTransaction cs = null;
        try {

            List<TPreenregistrement> list = em.createQuery("SELECT  o FROM TPreenregistrement o WHERE  FUNCTION('DATE', o.dtANNULER) =?1   AND o.lgTYPEVENTEID.lgTYPEVENTEID <> '5'  AND o.bISCANCEL=TRUE AND  FUNCTION('DATE',o.dtANNULER)> FUNCTION('DATE',o.dtUPDATED) AND o.strTYPEVENTE=?3 ")
                    .setParameter(1, java.sql.Date.valueOf(dt_end))
                    .setParameter(3, typevente)
                    .getResultList();

            Integer montant = 0, montantES = 0, remise = 0, tp = 0, monantTTC = 0, cheques = 0, dif = 0, cb = 0;
            for (TPreenregistrement op : list) {
                Integer _amount;
                TPreenregistrementCompteClient diff = null;
                List<TRecettes> recetteses = new ArrayList<>();
                try {
                    diff = (TPreenregistrementCompteClient) em.createQuery("SELECT  o FROM TPreenregistrementCompteClient o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1 ")
                            .setParameter(1, op.getLgPREENREGISTREMENTID()).setMaxResults(1)
                            .getSingleResult();
                } catch (Exception e) {
                }

                try {
                    if (!op.getLgTYPEVENTEID().getLgTYPEVENTEID().equals("3")) {
                        recetteses = montantEspece(op.getLgPREENREGISTREMENTID(), em);
                    }

                } catch (Exception e) {

                }

                monantTTC += op.getIntACCOUNT();
                montant += (op.getIntACCOUNT() - op.getIntREMISEPARA());
                _amount = op.getIntACCOUNT() - op.getIntREMISEPARA();
                if (!recetteses.isEmpty()) {
                    String reg = op.getLgREGLEMENTID().getLgMODEREGLEMENTID().getLgTYPEREGLEMENTID().getLgTYPEREGLEMENTID();
                    switch (reg) {
                        case "1":
                        case "4":
                            montantES += recetteses.stream().mapToInt((value) -> {
                                return value.getIntAMOUNT().intValue();
                            }).sum();
                            break;
                        case "2":
                            cheques += recetteses.stream().mapToInt((value) -> {
                                return value.getIntAMOUNT().intValue();
                            }).sum();
                            break;
                        case "3":
                            cb += recetteses.stream().mapToInt((value) -> {
                                return value.getIntAMOUNT().intValue();
                            }).sum();

                            break;

                        default:
                            break;

                    }

                }
                remise += op.getIntREMISEPARA();
                tp += (_amount - op.getIntCUSTPART());

                if (diff != null) {
                    dif += diff.getIntPRICERESTE();
                }

            }
            json.putOpt("montant", montant).putOpt("montantES", montantES)
                    .put("tp", tp).put("remise", remise).put("monantTTC", monantTTC)
                    .put("cb", cb).put("cheques", cheques).put("differe", dif);

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return json;
    }

    public List<TRecettes> montantEspece(String lgTransactionID, EntityManager em) {
        List<TRecettes> recetteses = new ArrayList<>();
        try {
            TypedQuery<TRecettes> query = em.createQuery("SELECT o FROM TRecettes o WHERE o.strREFFACTURE = ?1", TRecettes.class);
            query.setParameter(1, lgTransactionID);
            recetteses = query.getResultList();
        } catch (Exception e) {
        }

        return recetteses;
    }

    public JSONObject getVenteAnnulerExclude(String dt_end, String typevente) {
        JSONObject json = new JSONObject();
        EntityManager em = this.getEntityManager();
//        TCashTransaction cs = null;
        try {

            List<TPreenregistrement> list = em.createQuery("SELECT  o FROM TPreenregistrement o WHERE  FUNCTION('DATE', o.dtANNULER) =?1   AND o.lgTYPEVENTEID.lgTYPEVENTEID <> '5'  AND o.bISCANCEL=TRUE AND  FUNCTION('DATE',o.dtANNULER)> FUNCTION('DATE',o.dtUPDATED) AND o.strTYPEVENTE=?3 ")
                    .setParameter(1, java.sql.Date.valueOf(dt_end))
                    .setParameter(3, typevente)
                    .getResultList();

            Integer montant = 0, remise = 0, tp = 0, monantTTC = 0, dif = 0;
            Integer montantES = 0, cheques = 0, cb = 0;
            for (TPreenregistrement op : list) {
                Integer _amount;
                TPreenregistrementCompteClient diff = null;
                List<TRecettes> recetteses = new ArrayList<>();
                try {
                    diff = (TPreenregistrementCompteClient) em.createQuery("SELECT  o FROM TPreenregistrementCompteClient o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1 ")
                            .setParameter(1, op.getLgPREENREGISTREMENTID()).setMaxResults(1)
                            .getSingleResult();
                } catch (Exception e) {
                }

                try {
                    if (!op.getLgTYPEVENTEID().getLgTYPEVENTEID().equals("3")) {
                        recetteses = montantEspece(op.getLgPREENREGISTREMENTID(), em);
                    }

                } catch (Exception e) {
                }

                monantTTC += op.getIntACCOUNT();
                montant += (op.getIntACCOUNT() - op.getIntREMISEPARA());
                _amount = op.getIntACCOUNT() - op.getIntREMISEPARA();
                if (!recetteses.isEmpty()) {
                    String reg = op.getLgREGLEMENTID().getLgMODEREGLEMENTID().getLgTYPEREGLEMENTID().getLgTYPEREGLEMENTID();
                    switch (reg) {
                        case "1":
                        case "4":
                            montantES += recetteses.stream().mapToInt((value) -> {
                                return value.getIntAMOUNT().intValue();
                            }).sum();
                            break;
                        case "2":
                            cheques += recetteses.stream().mapToInt((value) -> {
                                return value.getIntAMOUNT().intValue();
                            }).sum();
                            break;
                        case "3":
                            cb += recetteses.stream().mapToInt((value) -> {
                                return value.getIntAMOUNT().intValue();
                            }).sum();

                            break;

                        default:
                            break;

                    }

                }
                remise += op.getIntREMISEPARA();
                tp += (_amount - op.getIntCUSTPART());

                if (diff != null) {
                    dif += diff.getIntPRICERESTE();
                }

            }
            json.putOpt("montant", montant).putOpt("montantES", montantES)
                    .put("tp", tp).put("remise", remise).put("monantTTC", monantTTC)
                    .put("cb", cb).put("cheques", cheques).put("differe", dif);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject getBalanceRegl(String dt_start, String dt_end, String typevente, String lgEmp) {
        JSONObject json = new JSONObject();
        EntityManager em = getEntityManager();

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TCashTransaction> root = cq.from(TCashTransaction.class);

            Join<TCashTransaction, TReglement> r = root.join("lgREGLEMENTID", JoinType.INNER);
            Subquery<String> sub = cq.subquery(String.class);
            Root<TPreenregistrement> pr = sub.from(TPreenregistrement.class);
            Join<TPreenregistrement, TUser> pu = pr.join("lgUSERID", JoinType.INNER);
            Predicate predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.equal(pu.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            predicate = cb.and(predicate, cb.notLike(pr.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            predicate = cb.and(predicate, cb.equal(pr.get(TPreenregistrement_.strSTATUT), "is_Closed"));
            predicate = cb.and(predicate, cb.equal(pr.get(TPreenregistrement_.bISCANCEL), false));
            predicate = cb.and(predicate, cb.equal(pr.get(TPreenregistrement_.strTYPEVENTE), typevente));
            Predicate ge = cb.greaterThan(pr.get(TPreenregistrement_.intPRICE), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, pr.get(TPreenregistrement_.dtUPDATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            sub.select(pr.get(TPreenregistrement_.lgPREENREGISTREMENTID)).where(predicate, btw, ge);

            Predicate ge2 = cb.greaterThan(root.get(TCashTransaction_.intAMOUNT), 0);

            cq.multiselect(
                    cb.selectCase().when(cb.equal(root.get(TCashTransaction_.lgTYPEREGLEMENTID), "1"),
                            cb.sum(root.get(TCashTransaction_.intAMOUNT)))
                            .otherwise(0), cb.selectCase().when(cb.equal(root.get(TCashTransaction_.lgTYPEREGLEMENTID), "2"),
                    cb.sum(root.get(TCashTransaction_.intAMOUNT)))
                    .otherwise(0), cb.selectCase().when(cb.equal(root.get(TCashTransaction_.lgTYPEREGLEMENTID), "3"),
                    cb.sum(root.get(TCashTransaction_.intAMOUNT)))
                    .otherwise(0), cb.selectCase().when(cb.equal(root.get(TCashTransaction_.lgTYPEREGLEMENTID), "4"),
                    cb.sum(root.get(TCashTransaction_.intAMOUNT)))
                    .otherwise(0));
            cq.where(ge2, cb.in(root.get(TCashTransaction_.strRESSOURCEREF)).value(sub));

            Query q = em.createQuery(cq);

            List<Object[]> list = q.getResultList();
            list.forEach((t) -> {
                try {

                    json.putOpt("montantES", Integer.valueOf(t[0].toString()) + Integer.valueOf(t[3].toString()))
                            .put("Cheques", Integer.valueOf(t[1].toString())).put("cb", Integer.valueOf(t[2].toString()));

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONArray getMVT(String dt_start, String dt_end, String emp) {
        JSONArray data = new JSONArray();
        EntityManager em = getEntityManager();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TMvtCaisse> root = cq.from(TMvtCaisse.class);
            Join<TMvtCaisse, TTypeMvtCaisse> tm = root.join("lgTYPEMVTCAISSEID", JoinType.INNER);
            Join<TMvtCaisse, TModeReglement> mvt = root.join("lgMODEREGLEMENTID", JoinType.INNER);
            Subquery<String> sub = cq.subquery(String.class);
            Root<TUser> urs = sub.from(TUser.class);
            Join<TUser, TEmplacement> um = urs.join("lgEMPLACEMENTID", JoinType.INNER);
            sub.select(urs.get(TUser_.lgUSERID)).where(cb.equal(um.get(TEmplacement_.lgEMPLACEMENTID), emp));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TMvtCaisse_.dtCREATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            Predicate p = cb.conjunction();
            p = cb.and(p, cb.notLike(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "8"), cb.notLike(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "9"));
            cq.multiselect(cb.sum(root.get(TMvtCaisse_.intAMOUNT)), tm.get(TTypeMvtCaisse_.strNAME), mvt.get("lgTYPEREGLEMENTID").get("strDESCRIPTION"))
                    .groupBy(tm.get(TTypeMvtCaisse_.strNAME), mvt.get("lgTYPEREGLEMENTID").get("strDESCRIPTION")).orderBy(cb.asc(mvt.get("lgTYPEREGLEMENTID").get("strDESCRIPTION")), cb.asc(tm.get(TTypeMvtCaisse_.strNAME)));
            cq.where(p, btw, cb.in(root.get(TMvtCaisse_.lgUSERID)).value(sub));
            Query q = em.createQuery(cq);
            List<Object[]> list = q.getResultList();

            list.forEach((t) -> {
                JSONObject ob = new JSONObject();
                try {

                    if (Util.MVTSORTIE.equals(t[1] + "")) {
                        ob.put("MONTANT", Util.getFormattedIntegerValue(Double.valueOf(t[0] + "").intValue() * (-1)))
                                .put("TYPEMVT", t[1] + "").put("REGLEMENT", t[2] + "");
                    } else {
                        ob.put("MONTANT", Util.getFormattedIntegerValue(Double.valueOf(t[0] + "").intValue()))
                                .put("TYPEMVT", t[1] + "").put("REGLEMENT", t[2] + "");
                    }

                    data.put(ob);
                } catch (JSONException ex) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public JSONArray getMVTClient(String dt_start, String dt_end, String emp) {
        JSONArray data = new JSONArray();
        EntityManager em = getEntityManager();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TMvtCaisse> root = cq.from(TMvtCaisse.class);
            Join<TMvtCaisse, TTypeMvtCaisse> tm = root.join("lgTYPEMVTCAISSEID", JoinType.INNER);
            Join<TMvtCaisse, TModeReglement> mvt = root.join("lgMODEREGLEMENTID", JoinType.INNER);
            Subquery<String> sub = cq.subquery(String.class);
            Root<TUser> urs = sub.from(TUser.class);
            Join<TUser, TEmplacement> um = urs.join("lgEMPLACEMENTID", JoinType.INNER);
            sub.select(urs.get(TUser_.lgUSERID)).where(cb.equal(um.get(TEmplacement_.lgEMPLACEMENTID), emp));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TMvtCaisse_.dtCREATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            Predicate p = cb.conjunction();
            p = cb.and(p, cb.notLike(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "8"), cb.notLike(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "9"));
            cq.multiselect(cb.sum(root.get(TMvtCaisse_.intAMOUNT)), tm.get(TTypeMvtCaisse_.strNAME), tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID))
                    .groupBy(tm.get(TTypeMvtCaisse_.strNAME), tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID));
            cq.where(p, btw, cb.in(root.get(TMvtCaisse_.lgUSERID)).value(sub));
            Query q = em.createQuery(cq);
            List<Object[]> list = q.getResultList();
            list.forEach((t) -> {
                JSONObject ob = new JSONObject();
                try {

                    if (Util.MVTSORTIE.equals(t[1] + "")) {
                        ob.put("MONTANT", Util.getFormattedIntegerValue(Double.valueOf(t[0] + "").intValue() * (1)) + " FCFA")
                                .put("TYPEMVT", t[1] + "").put("IDMVT", t[2] + "");
                    } else {
                        ob.put("MONTANT", Double.valueOf(t[0] + "").intValue())
                                .put("TYPEMVT", t[1] + "").put("IDMVT", t[2] + "");
                    }

                    data.put(ob);
                } catch (JSONException ex) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public Integer getBalanceDiff(String dt_start, String dt_end, String typevente, String lgEmp) {
        EntityManager em = this.getEntityManager();
        Integer diff = 0;

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);

            Root<TPreenregistrementCompteClient> root = cq.from(TPreenregistrementCompteClient.class);
            Join<TPreenregistrementCompteClient, TPreenregistrement> r = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementCompteClient, TUser> pu = root.join("lgUSERID", JoinType.INNER);

            Predicate predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.equal(pu.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            predicate = cb.and(predicate, cb.equal(r.get(TPreenregistrement_.bISCANCEL), Boolean.FALSE));
            predicate = cb.and(predicate, cb.notLike(r.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            predicate = cb.and(predicate, cb.equal(r.get(TPreenregistrement_.strSTATUT), "is_Closed"));
            Predicate ge = cb.greaterThan(r.get(TPreenregistrement_.intPRICE), 0);
            predicate = cb.and(predicate, cb.equal(r.get(TPreenregistrement_.strTYPEVENTE), typevente));
            Predicate btw = cb.between(cb.function("DATE", Date.class, r.get(TPreenregistrement_.dtUPDATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            cq.select(cb.sum(root.get(TPreenregistrementCompteClient_.intPRICERESTE)));
            cq.where(predicate, btw, ge);
            Query q = em.createQuery(cq);

            diff = (Integer) q.getSingleResult();
            if (diff == null) {
                diff = 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return diff;
    }

    //montant achat pour la balanve 
    public long getMontantAchats(String dt_start, String dt_end, String emp) {
        EntityManager em = this.getEntityManager();
        long achat = 0;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TBonLivraison> root = cq.from(TBonLivraison.class);
            Join<TBonLivraison, TUser> us = root.join("lgUSERID", JoinType.INNER);
            Join<TBonLivraison, TOrder> or = root.join("lgORDERID", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(root.get(TBonLivraison_.strSTATUT), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(us.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), emp));
            criteria = cb.and(criteria);
            cq.multiselect(cb.sumAsLong(root.get(TBonLivraison_.intHTTC)));
            cq.where(btw, criteria);
            Query q = em.createQuery(cq);
            Object _achat = (Object) q.getSingleResult();

            if (_achat != null) {
                achat = Long.valueOf(_achat.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return achat;
    }

    public long getMontantAchatsPara(String dt_start, String dt_end, String emp) {
        EntityManager em = this.getEntityManager();
        long achat = 0;
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TBonLivraisonDetail> root = cq.from(TBonLivraisonDetail.class);
            Join<TBonLivraisonDetail, TBonLivraison> us = root.join("lgBONLIVRAISONID", JoinType.INNER);

            Predicate criteria = cb.conjunction();

            Predicate btw = cb.between(cb.function("DATE", Date.class, us.get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(us.get(TBonLivraison_.strSTATUT), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("lgFAMILLEID").get("lgZONEGEOID").get("boolACCOUNT"), false));
            criteria = cb.and(criteria, cb.equal(us.get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), emp));
            criteria = cb.and(criteria);

            cq.multiselect(cb.sumAsLong(root.get(TBonLivraisonDetail_.intPAF)));
            cq.where(btw, criteria);

            Query q = em.createQuery(cq);
            Object _achat = (Object) q.getSingleResult();

            if (_achat != null) {
                achat = Long.valueOf(_achat.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return achat;
    }

    public long getMarge(String dt_start, String dt_end, String emp) {
        EntityManager em = this.getEntityManager();
        long marge = 0;

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<TPreenregistrementDetail> cq = cb.createQuery(TPreenregistrementDetail.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
//            Join<TPreenregistrementDetail, TFamille> prf = root.join("lgFAMILLEID", JoinType.INNER);
//            Join< TFamille, TCodeTva> tva = prf.join("lgCODETVAID", JoinType.INNER);
            Predicate criteria = cb.conjunction();

            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("lgPREENREGISTREMENTID").get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            //criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "4"));
            Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), emp));

            cq.where(criteria, pu, btw);

            Query q = em.createQuery(cq);
            List<TPreenregistrementDetail> oblist = q.getResultList();

            Map<TCodeTva, List<TPreenregistrementDetail>> mysList = oblist.stream().collect(Collectors.groupingBy(s -> s.getLgFAMILLEID().getLgCODETVAID()));

            Double montHT2 = 0.0;

            Integer montantAchat = 0;
            for (Map.Entry<TCodeTva, List<TPreenregistrementDetail>> entry : mysList.entrySet()) {
                TCodeTva key = entry.getKey();

                List<TPreenregistrementDetail> value = entry.getValue();
                for (TPreenregistrementDetail d : value) {

                    montHT2 += (Double.valueOf(d.getIntPRICE()) / (1 + (Double.valueOf(key.getIntVALUE()) / 100)));
                    montantAchat += (d.getLgFAMILLEID().getIntPAF() * d.getIntQUANTITY());
                }

            }

            marge = (Math.round(montHT2) - montantAchat);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return marge;
    }

    public long getMargePara(String dt_start, String dt_end, String emp) {
        EntityManager em = this.getEntityManager();
        long marge = 0;

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<TPreenregistrementDetail> cq = cb.createQuery(TPreenregistrementDetail.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementDetail, TFamille> prf = root.join("lgFAMILLEID", JoinType.INNER);
            Join< TFamille, TCodeTva> tva = prf.join("lgCODETVAID", JoinType.INNER);
            Predicate criteria = cb.conjunction();

            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("lgPREENREGISTREMENTID").get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "2"));
            Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), emp));
            criteria = cb.and(criteria, cb.equal(root.get(TPreenregistrementDetail_.boolACCOUNT), false));
            cq.where(criteria, pu, btw);

            Query q = em.createQuery(cq);
            List<TPreenregistrementDetail> oblist = q.getResultList();

            Map<TCodeTva, List<TPreenregistrementDetail>> mysList = oblist.stream().collect(Collectors.groupingBy(s -> s.getLgFAMILLEID().getLgCODETVAID()));

            Double montHT2 = 0.0;
            Double TVA = 0.0;
            Integer montantAchat = 0;
            for (Map.Entry<TCodeTva, List<TPreenregistrementDetail>> entry : mysList.entrySet()) {
                TCodeTva key = entry.getKey();
                JSONObject json = new JSONObject();
                Double _montHT2 = 0.0;
                Double montTTC2 = 0.0;
                List<TPreenregistrementDetail> value = entry.getValue();
                for (TPreenregistrementDetail d : value) {

                    montHT2 += (Double.valueOf(d.getIntPRICE()) / (1 + (Double.valueOf(key.getIntVALUE()) / 100)));
                    montantAchat += (d.getLgFAMILLEID().getIntPAF() * d.getIntQUANTITY());
                }

            }

            marge = (Math.round(montHT2) - montantAchat);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return marge;
    }

    // les produits périmés ou en voie de péremption
    public JSONArray getObseleteProducts(boolean all, String criteria, String dt_obsolete, String perimes, String cmbobsolete, String lgEmp, int start, int limit) {
        EntityManager em = this.getEntityManager();
        JSONArray array = new JSONArray();
        LocalDate today = LocalDate.now();
        // today.minusDays(14);

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TFamille> root = cq.from(TFamille.class);
            Join<TFamille, TFamilleStock> fa = root.join("tFamilleStockCollection", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            Predicate gth = cb.greaterThan(fa.get(TFamilleStock_.intNUMBERAVAILABLE), 0);
            predicate = cb.and(predicate, gth);
            if (!"".equals(criteria)) {
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TFamille_.strNAME), criteria + "%"), cb.like(root.get(TFamille_.intCIP), criteria + "%"), cb.like(root.get(TFamille_.intEAN13), criteria + "%"), cb.like(root.get(TFamille_.lgFAMILLEID), criteria + "%"), cb.like(root.get(TFamille_.strDESCRIPTION), criteria + "%")));
            }
            int mode = 0;
            if (!"".equals(perimes)) {
                mode = Integer.valueOf(perimes);
            } else if (!"".equals(cmbobsolete)) {
                mode = Integer.valueOf(cmbobsolete);
            } else if (!"".equals(dt_obsolete)) {
                mode = -1;
            }

            predicate = cb.and(predicate, cb.equal(root.get(TFamille_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(fa.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            switch (mode) {
                case -1:
                    Predicate avant = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(dt_obsolete));
                    predicate = cb.and(predicate, avant);
                    break;
                case 0:
                    Predicate get = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.plusWeeks(2)));
                    predicate = cb.and(predicate, get);
                    break;
                case 2:
                    Predicate ge = cb.greaterThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusMonths(1)));
                    Predicate l = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today));
                    predicate = cb.and(predicate, ge, l);
                    break;
                case 1:
                    Predicate le = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusMonths(1)));
                    predicate = cb.and(predicate, le);
                    break;
                case 3:
                    Predicate le2weeks = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusWeeks(2)));
                    predicate = cb.and(predicate, le2weeks);
                    break;
                case 4:
                    Predicate ge2weeks = cb.greaterThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusWeeks(2)));
                    predicate = cb.and(predicate, ge2weeks);
                    break;

                case 5:
                    Predicate leMonth = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.plusMonths(1)));
                    predicate = cb.and(predicate, leMonth);
                    break;
                case 6:
                    Predicate letwoweeks = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusWeeks(2)));
                    predicate = cb.and(predicate, letwoweeks);
                    break;
                case 7:
                    Predicate letoneweek = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusWeeks(1)));
                    predicate = cb.and(predicate, letoneweek);
                    break;

            }

            cq.multiselect(root.get(TFamille_.lgFAMILLEID), root.get(TFamille_.strDESCRIPTION), root.get(TFamille_.intCIP), cb.sum(cb.prod(root.get(TFamille_.intPRICE), fa.get(TFamilleStock_.intNUMBERAVAILABLE))), cb.sum(cb.prod(root.get(TFamille_.intPAF), fa.get(TFamilleStock_.intNUMBERAVAILABLE))), fa.get(TFamilleStock_.intNUMBERAVAILABLE), root.get("lgZONEGEOID").get("strLIBELLEE"),
                    cb.function("DATE_FORMAT", String.class, root.get(TFamille_.dtPEREMPTION), cb.literal("%Y-%m-%d")))
                    .groupBy(root.get(TFamille_.lgFAMILLEID))
                    .orderBy(cb.desc(root.get(TFamille_.dtPEREMPTION)));

            cq.where(predicate);
            Query q = em.createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);

            }

            List<Object[]> list = q.getResultList();
            JSONObject tataux = getObseleteProductsTotaux(criteria, dt_obsolete, perimes, cmbobsolete, lgEmp);

            list.forEach((t) -> {
                try {

                    JSONObject ob = new JSONObject();
                    ob.put("intPRICE", tataux.getInt("intPRICE")).put("PAF", tataux.getInt("intPAF")).put("intNUMBERAVAILABLE", tataux.getInt("intNUMBERAVAILABLE"));
                    ob.putOnce("lg_FAMILLE_ID", t[0] + "").putOnce("str_DESCRIPTION", t[1] + "")
                            .putOnce("int_CIP", t[2] + "").putOnce("int_PRICE", Integer.valueOf(t[3] + "")).putOnce("intPAF", Integer.valueOf(t[4] + ""))
                            .putOnce("int_NUMBER_AVAILABLE", Integer.valueOf(t[5] + "")).put("lg_EMPLACEMENT_ID", t[6]);
                    String date_perem = t[7] + "";

                    LocalDate dateTime = LocalDate.parse(date_perem);
                    Period p = Period.between(today, dateTime);
                    ob.put("Date", dateTime);
                    ob.put("dt_Peremtion", dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                    int nbJours = p.getDays();
                    int months = p.normalized().getMonths();

                    if (nbJours < 0) {
                        ob.put("dt_delay", "Périmé il y a " + ((-1) * nbJours) + " jour(s)");
                    } else if (months == 0 && nbJours == 0) {
                        ob.put("dt_delay", "Périme aujourd'hui");
                    } else {
                        String nbremois = (months > 0 ? months + " mois " : "");
                        String nbreJours = (nbJours > 0 ? nbJours + " jour(s) " : "");
                        ob.put("dt_delay", "Périme dans " + nbremois + "" + nbreJours);
                    }
                    ob.put("STATUS", (months > 0 && nbJours == 0 ? months : (nbJours < 0 ? -1 : nbJours)));
                    array.put(ob);

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public int getObseleteProductsCount(String criteria, String dt_obsolete, String perimes, String cmbobsolete, String lgEmp) {
        EntityManager em = this.getEntityManager();
        JSONArray array = new JSONArray();
        LocalDate today = LocalDate.now();
        // today.minusDays(14);

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TFamille> root = cq.from(TFamille.class);
            Join<TFamille, TFamilleStock> fa = root.join("tFamilleStockCollection", JoinType.INNER);

            Predicate predicate = cb.conjunction();
            Predicate gth = cb.greaterThan(fa.get(TFamilleStock_.intNUMBERAVAILABLE), 0);
            predicate = cb.and(predicate, gth);
            if (!"".equals(criteria)) {
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TFamille_.strNAME), criteria + "%"), cb.like(root.get(TFamille_.intCIP), criteria + "%"), cb.like(root.get(TFamille_.intEAN13), criteria + "%"), cb.like(root.get(TFamille_.lgFAMILLEID), criteria + "%"), cb.like(root.get(TFamille_.strDESCRIPTION), criteria + "%")));
            }
            int mode = 0;
            if (!"".equals(perimes)) {
                mode = Integer.valueOf(perimes);
            } else if (!"".equals(cmbobsolete)) {
                mode = Integer.valueOf(cmbobsolete);
            } else if (!"".equals(dt_obsolete)) {
                mode = -1;
            }

            predicate = cb.and(predicate, cb.equal(root.get(TFamille_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(fa.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            switch (mode) {
                case -1:
                    Predicate avant = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(dt_obsolete));
                    predicate = cb.and(predicate, avant);
                    break;
                case 0:
                    Predicate get = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.plusWeeks(2)));
                    predicate = cb.and(predicate, get);
                    break;
                case 2:
                    Predicate ge = cb.greaterThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusMonths(1)));
                    Predicate l = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today));
                    predicate = cb.and(predicate, ge, l);
                    break;
                case 1:
                    Predicate le = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusMonths(1)));
                    predicate = cb.and(predicate, le);
                    break;
                case 3:
                    Predicate le2weeks = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusWeeks(2)));
                    predicate = cb.and(predicate, le2weeks);
                    break;
                case 4:
                    Predicate ge2weeks = cb.greaterThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusWeeks(2)));
                    predicate = cb.and(predicate, ge2weeks);
                    break;

                case 5:
                    Predicate leMonth = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.plusMonths(1)));
                    predicate = cb.and(predicate, leMonth);
                    break;
                case 6:
                    Predicate letwoweeks = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusWeeks(2)));
                    predicate = cb.and(predicate, letwoweeks);
                    break;
                case 7:
                    Predicate letoneweek = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusWeeks(1)));
                    predicate = cb.and(predicate, letoneweek);
                    break;

            }

            cq.select(cb.count(root));

            cq.where(predicate);

            Query q = em.createQuery(cq);

            return ((Long) q.getSingleResult()).intValue();
        } finally {
            if (em != null) {

            }
        }

    }

    public JSONObject getObseleteProducts(String criteria, String dt_obsolete, String perimes, String cmbobsolete, String lgEmp) {
        EntityManager em = this.getEntityManager();
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        LocalDate today = LocalDate.now();
        // today.minusDays(14);

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TFamille> root = cq.from(TFamille.class);
            Join<TFamille, TFamilleStock> fa = root.join("tFamilleStockCollection", JoinType.INNER);

            Predicate predicate = cb.conjunction();
            Predicate gth = cb.greaterThan(fa.get(TFamilleStock_.intNUMBERAVAILABLE), 0);
            predicate = cb.and(predicate, gth);
            if (!"".equals(criteria)) {
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TFamille_.strNAME), criteria + "%"), cb.like(root.get(TFamille_.intCIP), criteria + "%"), cb.like(root.get(TFamille_.intEAN13), criteria + "%"), cb.like(root.get(TFamille_.lgFAMILLEID), criteria + "%"), cb.like(root.get(TFamille_.strDESCRIPTION), criteria + "%")));
            }
            int mode = 0;
            if (!"".equals(perimes)) {
                mode = Integer.valueOf(perimes);
            } else if (!"".equals(cmbobsolete)) {
                mode = Integer.valueOf(cmbobsolete);
            } else if (!"".equals(dt_obsolete)) {
                mode = -1;
            }

            predicate = cb.and(predicate, cb.equal(root.get(TFamille_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(fa.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            switch (mode) {
                case -1:
                    Predicate avant = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(dt_obsolete));
                    predicate = cb.and(predicate, avant);
                    break;
                case 0:
                    Predicate get = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.plusWeeks(2)));
                    predicate = cb.and(predicate, get);
                    break;
                case 2:
                    Predicate ge = cb.greaterThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusMonths(1)));
                    Predicate l = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today));
                    predicate = cb.and(predicate, ge, l);
                    break;
                case 1:
                    Predicate le = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusMonths(1)));
                    predicate = cb.and(predicate, le);
                    break;
                case 3:
                    Predicate le2weeks = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusWeeks(2)));
                    predicate = cb.and(predicate, le2weeks);
                    break;
                case 4:
                    Predicate ge2weeks = cb.greaterThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusWeeks(2)));
                    predicate = cb.and(predicate, ge2weeks);
                    break;

                case 5:
                    Predicate leMonth = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.plusMonths(1)));
                    predicate = cb.and(predicate, leMonth);
                    break;
                case 6:
                    Predicate letwoweeks = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusWeeks(2)));
                    predicate = cb.and(predicate, letwoweeks);
                    break;
                case 7:
                    Predicate letoneweek = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusWeeks(1)));
                    predicate = cb.and(predicate, letoneweek);
                    break;

            }

            cq.multiselect(root.get(TFamille_.lgFAMILLEID), root.get(TFamille_.strDESCRIPTION), root.get(TFamille_.intCIP), cb.sum(cb.prod(root.get(TFamille_.intPRICE), fa.get(TFamilleStock_.intNUMBERAVAILABLE))), cb.sum(cb.prod(root.get(TFamille_.intPAF), fa.get(TFamilleStock_.intNUMBERAVAILABLE))), fa.get(TFamilleStock_.intNUMBERAVAILABLE), root.get("lgZONEGEOID").get("strLIBELLEE"), cb.function("DATE_FORMAT", String.class, root.get(TFamille_.dtPEREMPTION), cb.literal("%Y-%m-%d")))
                    .groupBy(root.get(TFamille_.lgFAMILLEID))
                    .orderBy(cb.desc(root.get(TFamille_.dtPEREMPTION)));

            cq.where(predicate);
            Query q = em.createQuery(cq);

            List<Object[]> list = q.getResultList();

            list.forEach((t) -> {
                try {
                    JSONObject ob = new JSONObject();
                    ob.putOnce("lg_FAMILLE_ID", t[0] + "").putOnce("str_DESCRIPTION", t[1] + "")
                            .putOnce("int_CIP", t[2] + "").putOnce("int_PRICE", Integer.valueOf(t[3] + "")).putOnce("intPAF", Integer.valueOf(t[4] + ""))
                            .putOnce("int_NUMBER_AVAILABLE", Integer.valueOf(t[5] + "")).put("lg_EMPLACEMENT_ID", t[6]);
                    String date_perem = t[7] + "";

                    LocalDate dateTime = LocalDate.parse(date_perem);
                    Period p = Period.between(today, dateTime);
                    ob.put("Date", dateTime);
                    ob.put("dt_Peremtion", dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    int nbJours = p.getDays();
                    ob.put("STATUS", nbJours);

                    if (nbJours < 0) {
                        ob.put("dt_delay", "Expiré il y a " + ((-1) * nbJours) + " jour(s)");
                    } else if (nbJours == 0) {
                        ob.put("dt_delay", "Expire aujourd'hui");
                    } else {
                        ob.put("dt_delay", "Expire dans " + nbJours + " jour(s)");
                    }

                    array.put(ob);

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            });
            json.put("root", array);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject getObseleteProductsTotaux(String criteria, String dt_obsolete, String perimes, String cmbobsolete, String lgEmp) {
        EntityManager em = this.getEntityManager();
        JSONObject ob = new JSONObject();

        LocalDate today = LocalDate.now();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TFamille> root = cq.from(TFamille.class);
            Join<TFamille, TFamilleStock> fa = root.join("tFamilleStockCollection", JoinType.INNER);

            Predicate predicate = cb.conjunction();
            Predicate gth = cb.greaterThan(fa.get(TFamilleStock_.intNUMBERAVAILABLE), 0);
            predicate = cb.and(predicate, gth);
            if (!"".equals(criteria)) {
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TFamille_.strNAME), criteria + "%"), cb.like(root.get(TFamille_.intCIP), criteria + "%"), cb.like(root.get(TFamille_.intEAN13), criteria + "%"), cb.like(root.get(TFamille_.lgFAMILLEID), criteria + "%"), cb.like(root.get(TFamille_.strDESCRIPTION), criteria + "%")));
            }
            int mode = 0;
            if (!"".equals(perimes)) {
                mode = Integer.valueOf(perimes);
            } else if (!"".equals(cmbobsolete)) {
                mode = Integer.valueOf(cmbobsolete);
            } else if (!"".equals(dt_obsolete)) {
                mode = -1;
            }

            predicate = cb.and(predicate, cb.equal(root.get(TFamille_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(fa.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            switch (mode) {
                case -1:
                    Predicate avant = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(dt_obsolete));
                    predicate = cb.and(predicate, avant);
                    break;
                case 0:
                    Predicate get = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.plusWeeks(2)));
                    predicate = cb.and(predicate, get);
                    break;
                case 2:
                    Predicate ge = cb.greaterThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusMonths(1)));
                    Predicate l = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today));
                    predicate = cb.and(predicate, ge, l);
                    break;
                case 1:
                    Predicate le = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusMonths(1)));
                    predicate = cb.and(predicate, le);
                    break;
                case 3:
                    Predicate le2weeks = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusWeeks(2)));
                    predicate = cb.and(predicate, le2weeks);
                    break;
                case 4:
                    Predicate ge2weeks = cb.greaterThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusWeeks(2)));
                    predicate = cb.and(predicate, ge2weeks);
                    break;

                case 5:
                    Predicate leMonth = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.plusMonths(1)));
                    predicate = cb.and(predicate, leMonth);
                    break;
                case 6:
                    Predicate letwoweeks = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusWeeks(2)));
                    predicate = cb.and(predicate, letwoweeks);
                    break;
                case 7:
                    Predicate letoneweek = cb.lessThanOrEqualTo(root.get(TFamille_.dtPEREMPTION), java.sql.Date.valueOf(today.minusWeeks(1)));
                    predicate = cb.and(predicate, letoneweek);
                    break;

            }

            cq.multiselect(cb.sum(cb.prod(root.get(TFamille_.intPRICE), fa.get(TFamilleStock_.intNUMBERAVAILABLE))), cb.sum(cb.prod(root.get(TFamille_.intPAF), fa.get(TFamilleStock_.intNUMBERAVAILABLE))), cb.sum(fa.get(TFamilleStock_.intNUMBERAVAILABLE)), root.get("lgZONEGEOID").get("strLIBELLEE"), cb.function("DATE_FORMAT", String.class, root.get(TFamille_.dtPEREMPTION), cb.literal("%Y-%m-%d")));

            cq.where(predicate);
            Query q = em.createQuery(cq);

            List<Object[]> list = q.getResultList();

            list.forEach((t) -> {
                try {

                    ob.putOnce("intPRICE", Integer.valueOf(t[0] + "")).putOnce("intPAF", Integer.valueOf(t[1] + ""))
                            .putOnce("intNUMBERAVAILABLE", Integer.valueOf(t[2] + ""));

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ob;
    }

    public JSONArray getLog(boolean all, String criteria, String dt_start, String dt_end, String cmbobsolete, String user, int start, int limit) {
        EntityManager em = this.getEntityManager();
        JSONArray array = new JSONArray();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TEventLog> cq = cb.createQuery(TEventLog.class);
            Root<TEventLog> root = cq.from(TEventLog.class);

            Predicate predicate = cb.conjunction();
            if (!"".equals(criteria)) {
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TEventLog_.strDESCRIPTION), criteria + "%"), cb.like(root.get(TEventLog_.strCREATEDBY), criteria + "%"), cb.like(root.get(TEventLog_.strMODULECONCERN), criteria + "%"), cb.like(root.get(TEventLog_.strTABLECONCERN), criteria + "%")));
            }

            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TEventLog_.dtCREATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            predicate = cb.and(predicate, btw);
            if (!"".equals(cmbobsolete)) {
                predicate = cb.and(predicate, cb.like(root.get(TEventLog_.strTYPELOG), cmbobsolete.trim() + "%"));
            }
            if (!"".equals(user)) {
                predicate = cb.and(predicate, cb.equal(root.get("lgUSERID").get("lgUSERID"), user));
            }

            cq.select(root)
                    .orderBy(cb.desc(root.get(TEventLog_.dtCREATED)));

            cq.where(predicate);
            Query q = em.createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);

            }

            List<TEventLog> list = q.getResultList();

            list.forEach((t) -> {
                try {

                    JSONObject ob = new JSONObject();

                    ob.putOnce("lgEVENTLOGID", t.getLgEVENTLOGID()).putOnce("strDESCRIPTION", t.getStrDESCRIPTION())
                            .putOnce("strCREATEDBY", (t.getLgUSERID() != null ? t.getLgUSERID().getStrFIRSTNAME() + " " + t.getLgUSERID().getStrLASTNAME() : t.getStrCREATEDBY()))
                            .putOnce("strTYPELOG", (t.getStrTYPELOG() != null ? t.getStrTYPELOG() : "")).
                            putOnce("dtCREATED", date.formatterShort.format(t.getDtCREATED())).
                            putOnce("dtHEURE", date.NomadicUiFormatTime.format(t.getDtCREATED()));

                    array.put(ob);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public int getLogCount(String criteria, String dt_start, String dt_end, String cmbobsolete, String user) {
        EntityManager em = this.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TEventLog> root = cq.from(TEventLog.class);

            Predicate predicate = cb.conjunction();
            if (!"".equals(criteria)) {
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TEventLog_.strDESCRIPTION), criteria + "%"), cb.like(root.get(TEventLog_.strCREATEDBY), criteria + "%"), cb.like(root.get(TEventLog_.strMODULECONCERN), criteria + "%"), cb.like(root.get(TEventLog_.strTABLECONCERN), criteria + "%")));
            }

            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TEventLog_.dtCREATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            predicate = cb.and(predicate, btw);
            if (!"".equals(cmbobsolete)) {
                predicate = cb.and(predicate, cb.like(root.get(TEventLog_.strTYPELOG), cmbobsolete.trim() + "%"));
            }
            if (!"".equals(user)) {
                predicate = cb.and(predicate, cb.equal(root.get("lgUSERID").get("lgUSERID"), user));
            }
            cq.select(cb.count(root));

            cq.where(predicate);

            Query q = em.createQuery(cq);

            return ((Long) q.getSingleResult()).intValue();
        } finally {
            if (em != null) {

            }
        }

    }

    public JSONArray getAllUsers(boolean all, String criteria, String user, int start, int limit) {
        EntityManager em = this.getEntityManager();
        JSONArray array = new JSONArray();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TUser> root = cq.from(TUser.class);

            Predicate predicate = cb.conjunction();
            if (!"".equals(criteria)) {
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TUser_.strLASTNAME), criteria + "%"), cb.like(root.get(TUser_.strLASTNAME), criteria + "%")));
            }
            if (!"".equals(user)) {
                predicate = cb.and(predicate, cb.equal(root.get(TUser_.lgUSERID), user));
            }
            predicate = cb.and(predicate, cb.equal(root.get(TUser_.strSTATUT), "enable"));

            cq.multiselect(root.get(TUser_.lgUSERID), root.get(TUser_.strFIRSTNAME), root.get(TUser_.strLASTNAME))
                    .orderBy(cb.asc(root.get(TUser_.strFIRSTNAME)), cb.asc(root.get(TUser_.strLASTNAME)));

            cq.where(predicate);
            Query q = em.createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);

            }

            List<Object[]> list = q.getResultList();

            list.forEach((t) -> {
                try {

                    JSONObject ob = new JSONObject();

                    ob.putOnce("lgUSERID", t[0]).putOnce("strFIRSTNAME", t[1] + " " + t[2]);

                    array.put(ob);

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public int getAllUsersCount(String criteria, String user) {
        EntityManager em = this.getEntityManager();
        JSONArray array = new JSONArray();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TUser> root = cq.from(TUser.class);

            Predicate predicate = cb.conjunction();
            if (!"".equals(criteria)) {
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TUser_.strLASTNAME), criteria + "%"), cb.like(root.get(TUser_.strLASTNAME), criteria + "%")));
            }
            if (!"".equals(user)) {
                predicate = cb.and(predicate, cb.equal(root.get(TUser_.lgUSERID), user));
            }
            predicate = cb.and(predicate, cb.equal(root.get(TUser_.strSTATUT), "enable"));

            cq.select(cb.count(root));
            cq.where(predicate);
            Query q = em.createQuery(cq);

            return ((Long) q.getSingleResult()).intValue();
        } finally {
            if (em != null) {

            }
        }
    }

    public JSONArray getLog(String criteria, String dt_start, String dt_end, String cmbobsolete, String user) {
        EntityManager em = this.getEntityManager();
        JSONArray array = new JSONArray();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TEventLog> cq = cb.createQuery(TEventLog.class);
            Root<TEventLog> root = cq.from(TEventLog.class);

            Predicate predicate = cb.conjunction();
            if (!"".equals(criteria)) {
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TEventLog_.strDESCRIPTION), criteria + "%"), cb.like(root.get(TEventLog_.strCREATEDBY), criteria + "%"), cb.like(root.get(TEventLog_.strMODULECONCERN), criteria + "%"), cb.like(root.get(TEventLog_.strTABLECONCERN), criteria + "%")));
            }

            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TEventLog_.dtCREATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            predicate = cb.and(predicate, btw);
            if (!"".equals(cmbobsolete)) {
                predicate = cb.and(predicate, cb.equal(root.get(TEventLog_.strTYPELOG), cmbobsolete));
            }
            if (!"".equals(user)) {
                predicate = cb.and(predicate, cb.equal(root.get("lgUSERID").get("lgUSERID"), user));
            }

            cq.select(root)
                    .orderBy(cb.desc(root.get(TEventLog_.dtCREATED)));

            cq.where(predicate);
            Query q = em.createQuery(cq);

            List<TEventLog> list = q.getResultList();

            list.forEach((t) -> {
                try {

                    JSONObject ob = new JSONObject();

                    ob.putOnce("lgEVENTLOGID", t.getLgEVENTLOGID()).putOnce("strDESCRIPTION", t.getStrDESCRIPTION())
                            .putOnce("strCREATEDBY", (t.getLgUSERID() != null ? t.getLgUSERID().getStrFIRSTNAME() + " " + t.getLgUSERID().getStrLASTNAME() : t.getStrCREATEDBY()))
                            .putOnce("strTYPELOG", (t.getStrTYPELOG() != null ? t.getStrTYPELOG() : "")).
                            putOnce("dtCREATED", LocalDateTime.parse(date.formatterMysql.format(t.getDtCREATED()), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).
                            putOnce("dtHEURE", date.NomadicUiFormatTime.format(t.getDtCREATED()));

                    array.put(ob);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    //marge sur produit
    public JSONObject getMargeProduit(boolean all, String search, String dt_start, String dt_end, String emp, int start, int limit) {
        EntityManager em = this.getEntityManager();

        JSONObject _json = new JSONObject();

        try {
            JSONArray data = new JSONArray();
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementDetail, TFamille> prf = root.join("lgFAMILLEID", JoinType.INNER);
//            Join<TFamille, TCodeTva> tva = prf.join("lgCODETVAID", JoinType.INNER);

            Predicate criteria = cb.conjunction();

            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("lgPREENREGISTREMENTID").get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), emp));
            if (!"".equals(search)) {

                criteria = cb.and(criteria, cb.or(cb.like(prf.get(TFamille_.strDESCRIPTION), search + "%"), cb.like(prf.get(TFamille_.intCIP), search + "%"), cb.like(prf.get(TFamille_.intEAN13), search + "%")));
            }
            cq.multiselect(prf.get(TFamille_.lgFAMILLEID), prf.get(TFamille_.intCIP),
                    prf.get(TFamille_.strDESCRIPTION),
                    root.get(TPreenregistrementDetail_.intPRICEUNITAIR), prf.get(TFamille_.intPAF),
                    cb.sum(root.get(TPreenregistrementDetail_.intPRICE)),
                    cb.sum(root.get(TPreenregistrementDetail_.intQUANTITY)))
                    .groupBy(prf.get(TFamille_.lgFAMILLEID))
                    .orderBy(cb.asc(prf.get(TFamille_.strDESCRIPTION)));

            cq.where(criteria, pu, btw);

            Query q = em.createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);

            }
            List<Object[]> oblist = q.getResultList();

            oblist.forEach((t) -> {
                try {
                    String lgFAMILLEID = t[0].toString();
                    TFamille tf = em.find(TFamille.class, lgFAMILLEID);
                    TCodeTva key = tf.getLgCODETVAID();
                    JSONObject json = new JSONObject();
                    Double _montHT2 = (Double.valueOf(t[5] + "") / (1 + (Double.valueOf(key.getIntVALUE()) / 100)));
                    Integer montantAchat = Integer.valueOf(t[6] + "") * Integer.valueOf(t[4] + "");
                    Integer marger = _montHT2.intValue() - montantAchat;
                    json.put("lg_FAMILLE_ID", lgFAMILLEID).put("CIP", t[1] + "")
                            .put("DESC", t[2].toString()).put("INTPU", Integer.valueOf(t[3] + ""))
                            .put("INTPA", Integer.valueOf(t[4] + ""))
                            .put("AMOUNTPA", montantAchat)
                            .put("AMOUNTPV", _montHT2.longValue())
                            .put("MARGE", marger).put("QTY", Integer.valueOf(t[6] + ""))
                            .put("AMOUNTTTC", Integer.valueOf(t[5] + ""));
                    data.put(json);

                } catch (JSONException ex) {
                    Logger.getLogger(GroupeTierspayantController.class.getName()).log(Level.SEVERE, null, ex);
                }

            });
            _json.put("data", data);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return _json;
    }

    //marge sur produit
    public int getMargeProduit(String search, String dt_start, String dt_end, String emp) {
        EntityManager em = this.getEntityManager();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> pr = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementDetail, TFamille> prf = root.join("lgFAMILLEID", JoinType.INNER);
//            Join<TFamille, TCodeTva> tva = prf.join("lgCODETVAID", JoinType.INNER);

            Predicate criteria = cb.conjunction();

            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("lgPREENREGISTREMENTID").get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));

            Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), emp));
            if (!"".equals(search)) {
//                TPreenregistrementDetail_.
                // criteria = cb.and(criteria, cb.or(cb.like(root.get("lgFAMILLEID").get("strDESCRIPTION"), criteria + "%"), cb.like(root.get("lgFAMILLEID").get("intCIP"), criteria + "%"), cb.like(root.get("lgFAMILLEID").get("intEAN13"), criteria + "%")));
                criteria = cb.and(criteria, cb.or(cb.like(prf.get(TFamille_.strDESCRIPTION), search + "%"), cb.like(prf.get(TFamille_.intCIP), search + "%"), cb.like(prf.get(TFamille_.intEAN13), search + "%")));
            }
            cq.multiselect(cb.count(root)).groupBy(prf.get(TFamille_.lgFAMILLEID), prf.get(TFamille_.strDESCRIPTION));

            cq.where(criteria, pu, btw);

            Query q = em.createQuery(cq);

            return (q.getResultList().size());

        } finally {
            if (em != null) {

            }
        }
    }

    public JSONArray findAllCompanies(boolean all, int maxResults, int firstResult, String str_LIBELLE) {

        JSONArray array = new JSONArray();
        try {
            EntityManager em = this.getEntityManager();
            JSONArray data = new JSONArray();
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<TCompany> cq = cb.createQuery(TCompany.class);
            Root<TCompany> root = cq.from(TCompany.class);
            cq.select(root).orderBy(cb.asc(root.get(TCompany_.strRAISONSOCIALE)))
                    .where(cb.like(root.get(TCompany_.strRAISONSOCIALE), str_LIBELLE + "%"));

            Query q = em.createQuery(cq);
            if (!all) {
                q.setFirstResult(firstResult).setMaxResults(maxResults);
            }
            List<TCompany> companys = q.getResultList();
            companys.forEach((t) -> {
                JSONObject ob = new JSONObject();
                try {
                    ob.put("lg_COMPANY_ID", t.getLgCOMPANYID());
                    ob.put("str_RAISONSOCIALE", t.getStrRAISONSOCIALE());

                    ob.put("str_CEL", t.getStrCEL());
                    ob.put("str_ADRESS", t.getStrADRESS());
                    ob.put("str_PHONE", t.getStrPHONE());
                    array.put(ob);
                } catch (JSONException ex) {

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public int findAllCompaniesCount(String str_LIBELLE) {

        JSONArray array = new JSONArray();
        EntityManager em = null;
        try {
            em = this.getEntityManager();
            JSONArray data = new JSONArray();
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TCompany> root = cq.from(TCompany.class);
            cq.select(cb.count(root))
                    .where(cb.like(root.get(TCompany_.strRAISONSOCIALE), str_LIBELLE + "%"));

            Query q = em.createQuery(cq);

            return ((Long) q.getSingleResult()).intValue();
        } finally {
            if (em != null) {

            }

        }

    }

    public boolean addCompany(String str_RAISONSOCIALE, String str_ADRESS, String str_PHONE, String str_CEL) {
        boolean isOk = true;
        EntityManager em = this.getEntityManager();
        try {

            TCompany _new = new TCompany(key.getComplexId());
            _new.setStrADRESS(str_ADRESS);
            _new.setStrCEL(str_CEL);
            _new.setStrPHONE(str_PHONE);
            _new.setStrRAISONSOCIALE(str_RAISONSOCIALE);
            em.getTransaction().begin();
            em.persist(_new);
            em.getTransaction().commit();
        } catch (Exception e) {
            isOk = false;
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();

            }
        }

        return isOk;
    }

    public boolean updateCompany(String lg_COMPANY_ID, String str_RAISONSOCIALE, String str_ADRESS, String str_PHONE, String str_CEL) {
        boolean isOk = true;
        EntityManager em = this.getEntityManager();

        try {
            TCompany _new = em.find(TCompany.class, lg_COMPANY_ID);
            _new.setStrADRESS(str_ADRESS);
            _new.setStrCEL(str_CEL);
            _new.setStrPHONE(str_PHONE);
            _new.setStrRAISONSOCIALE(str_RAISONSOCIALE);
            em.getTransaction().begin();
            em.merge(_new);
            em.getTransaction().commit();
        } catch (Exception e) {
            isOk = false;
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();

            }
            e.printStackTrace();
        }
        return isOk;
    }

    public boolean deleteCompany(String lg_COMPANY_ID) {
        boolean isOk = true;
        EntityManager em = this.getEntityManager();
        try {

            TCompany _new = em.find(TCompany.class, lg_COMPANY_ID);
            em.getTransaction().begin();
            em.remove(_new);
            em.getTransaction().commit();

        } catch (Exception e) {
            isOk = false;
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();

            }
        }
        return isOk;
    }

    public List<TPreenregistrementCompteClientTiersPayent> getDetails(String id) {
        try {
            EntityManager em = getEntityManager();
            return em.createQuery("SELECT p FROM TPreenregistrementCompteClientTiersPayent p, TFactureDetail o WHERE p.lgPREENREGISTREMENTCOMPTECLIENTPAYENTID=o.strREF AND o.lgFACTUREID.lgFACTUREID=?1 ORDER BY p.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTID.lgCLIENTID.strFIRSTNAME,p.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTID.lgCLIENTID.strLASTNAME ASC  ,p.lgCOMPTECLIENTTIERSPAYANTID.intPRIORITY DESC   ")
                    .setParameter(1, id).getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public JSONObject generateInvoices(String lgFACTURE) {

        EntityManager em = getEntityManager();
        TFacture facture = em.find(TFacture.class, lgFACTURE);
        final TTiersPayant payant = em.find(TTiersPayant.class, facture.getStrCUSTOMER());
        JSONObject json = new JSONObject();

        List<TPreenregistrementCompteClientTiersPayent> clientTiersPayents = getDetails(lgFACTURE);

        JSONArray datas = new JSONArray();

        clientTiersPayents.forEach((ps) -> {
            JSONObject line = new JSONObject();

            try {
                TPreenregistrement op = ps.getLgPREENREGISTREMENTID();

                line.put("Date", LocalDate.parse(DATEFORMATYYYY.format(op.getDtUPDATED()), DateTimeFormatter.ISO_DATE));
                TCompteClientTiersPayant cm = ps.getLgCOMPTECLIENTTIERSPAYANTID();

                line.putOnce("Bon", ps.getStrREFBON());
                TClient cl = cm.getLgCOMPTECLIENTID().getLgCLIENTID();
                TCompany cmp = cl.getLgCOMPANYID();

                try {

                    line.putOnce("MontantNet", ps.getIntPRICE());

                    line.putOnce("FIRSTNAME", cl.getStrFIRSTNAME() + " " + cl.getStrLASTNAME());
                    line.putOnce("Montant", op.getIntPRICE());
                    line.putOnce("TAUX", ps.getIntPERCENT());
                    line.putOnce("lg_COMPANY_ID", (cmp != null ? cmp.getLgCOMPANYID() : ""));
                    line.putOnce("str_RAISONSOCIALE", (cmp != null ? cmp.getStrRAISONSOCIALE() : ""));

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            } catch (JSONException ex) {
                ex.printStackTrace();

            }
            datas.put(line);
        });

        try {
            json.put("invoice", datas);

        } catch (JSONException ex) {

        }

        return json;
    }

    public JSONArray getAllTypeVente(String exclude, String lg_TYPE_VENTE_ID) {
        EntityManager em = this.getEntityManager();
        JSONArray data = new JSONArray();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TTypeVente> root = cq.from(TTypeVente.class);
            Predicate criteria = cb.conjunction();
            criteria = cb.and(criteria, cb.equal(root.get(TTypeVente_.strSTATUT), commonparameter.statut_enable));
            criteria = cb.and(criteria, cb.equal(root.get(TTypeVente_.strTYPE), "OFFICINE"));
            if (!"".equals(exclude)) {

                criteria = cb.and(criteria, cb.notLike(root.get(TTypeVente_.lgTYPEVENTEID), exclude));
            }
            cq.multiselect(root.get(TTypeVente_.lgTYPEVENTEID), root.get(TTypeVente_.strNAME),
                    root.get(TTypeVente_.strDESCRIPTION))
                    .orderBy(cb.asc(root.get(TTypeVente_.lgTYPEVENTEID)));

            cq.where(criteria);

            Query q = em.createQuery(cq);

            List<Object[]> oblist = q.getResultList();

            oblist.forEach((t) -> {

                JSONObject json = new JSONObject();

                try {
                    json.put("lg_TYPE_VENTE_ID", t[0]).put("str_DESCRIPTION", t[2] + "")
                            .put("str_NAME", t[1]);
                    data.put(json);

                } catch (JSONException ex) {
                    Logger.getLogger(GroupeTierspayantController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public Integer getBalanceReglTicketZ(String dt_start, String typevente, String lgEmp, String lgUSERID, int amount2, String lgTYPEREGLEMENTID) {

        EntityManager em = getEntityManager();

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<TCashTransaction> root = cq.from(TCashTransaction.class);

            Join<TCashTransaction, TReglement> r = root.join("lgREGLEMENTID", JoinType.INNER);
            Subquery<String> sub = cq.subquery(String.class);
            Root<TPreenregistrement> pr = sub.from(TPreenregistrement.class);
            Join<TPreenregistrement, TUser> pu = pr.join("lgUSERID", JoinType.INNER);
            Predicate predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.equal(pu.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            predicate = cb.and(predicate, cb.notLike(pr.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            predicate = cb.and(predicate, cb.equal(pr.get(TPreenregistrement_.strSTATUT), "is_Closed"));
            predicate = cb.and(predicate, cb.equal(pr.get(TPreenregistrement_.bISCANCEL), false));
            predicate = cb.and(predicate, cb.equal(pr.get(TPreenregistrement_.strTYPEVENTE), typevente));
            predicate = cb.and(predicate, cb.equal(root.get(TCashTransaction_.lgTYPEREGLEMENTID), lgTYPEREGLEMENTID));
            predicate = cb.and(predicate, cb.equal(pu.get(TUser_.lgUSERID), lgUSERID));
            Predicate ge = cb.greaterThan(pr.get(TPreenregistrement_.intPRICE), 0);

            Predicate btw = cb.equal(cb.function("DATE", Date.class, pr.get(TPreenregistrement_.dtUPDATED)), java.sql.Date.valueOf(dt_start));
            sub.select(pr.get(TPreenregistrement_.lgPREENREGISTREMENTID)).where(predicate, btw, ge);

            Predicate ge2 = cb.greaterThan(root.get(TCashTransaction_.intAMOUNT), 0);
            cq.select(cb.sum((amount2 == 1 ? root.get(TCashTransaction_.intAMOUNT2) : root.get(TCashTransaction_.intAMOUNT))));
//          
            cq.where(ge2, cb.in(root.get(TCashTransaction_.strRESSOURCEREF)).value(sub));

            Query q = em.createQuery(cq);

            return (Integer) q.getSingleResult();

        } finally {

        }

    }

    public Integer getTicketDiff(String dt_start, String typevente, String lgEmp, String lgUSERID) {
        EntityManager em = this.getEntityManager();
        Integer diff = 0;

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<TPreenregistrementCompteClient> root = cq.from(TPreenregistrementCompteClient.class);
            Join<TPreenregistrementCompteClient, TPreenregistrement> r = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementCompteClient, TUser> pu = root.join("lgUSERID", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.equal(pu.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            predicate = cb.and(predicate, cb.equal(r.get(TPreenregistrement_.bISCANCEL), Boolean.FALSE));
            predicate = cb.and(predicate, cb.notLike(r.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            predicate = cb.and(predicate, cb.equal(r.get(TPreenregistrement_.strSTATUT), "is_Closed"));
            predicate = cb.and(predicate, cb.equal(pu.get(TUser_.lgUSERID), lgUSERID));
            Predicate ge = cb.greaterThan(r.get(TPreenregistrement_.intPRICE), 0);
            predicate = cb.and(predicate, cb.equal(r.get(TPreenregistrement_.strTYPEVENTE), typevente));
            Predicate btw = cb.equal(cb.function("DATE", Date.class, r.get(TPreenregistrement_.dtUPDATED)), java.sql.Date.valueOf(dt_start));
            cq.select(cb.sum(root.get(TPreenregistrementCompteClient_.intPRICERESTE)));
            cq.where(predicate, btw, ge);
            Query q = em.createQuery(cq);

            diff = (Integer) q.getSingleResult();
            if (diff == null) {
                diff = 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return diff;
    }

    public JSONArray getMVTTicketZ(String dt_start, String emp) {
        JSONArray data = new JSONArray();
        EntityManager em = getEntityManager();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TMvtCaisse> root = cq.from(TMvtCaisse.class);
            Join<TMvtCaisse, TTypeMvtCaisse> tm = root.join("lgTYPEMVTCAISSEID", JoinType.INNER);
            Join<TMvtCaisse, TModeReglement> mvt = root.join("lgMODEREGLEMENTID", JoinType.INNER);
            Subquery<String> sub = cq.subquery(String.class);
            Root<TUser> urs = sub.from(TUser.class);
            Join<TUser, TEmplacement> um = urs.join("lgEMPLACEMENTID", JoinType.INNER);
            sub.select(urs.get(TUser_.lgUSERID)).where(cb.equal(um.get(TEmplacement_.lgEMPLACEMENTID), emp));
            Predicate btw = cb.equal(cb.function("DATE", Date.class, root.get(TMvtCaisse_.dtCREATED)), LocalDate.parse(dt_start));
            Predicate p = cb.conjunction();
            p = cb.and(p, cb.notLike(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "8"), cb.notLike(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "9"), cb.notLike(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "1"), cb.notLike(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "6"), cb.notLike(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "7"));
//            p = cb.and(p, cb.equal(root.get(TMvtCaisse_.lgUSERID), lgUSERID));
//               Predicate tp=cb.conjunction();
//             tp=  cb.and(tp,cb.equal(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "3"),cb.equal(mvt.get("lgTYPEREGLEMENTID").get("lgTYPEREGLEMENTID"), "1"));
////             tp=  cb.and(tp,cb.equal(mvt.get("lgTYPEREGLEMENTID").get("lgTYPEREGLEMENTID"), "1"));
//              Predicate tpch=cb.conjunction();
//             tpch=  cb.and(tpch,cb.equal(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "3"));
//             tpch=  cb.and(tpch,cb.equal(mvt.get("lgTYPEREGLEMENTID").get("lgTYPEREGLEMENTID"), "2"));
            Case caseExp = cb.selectCase();
            cq.multiselect(
                    // reglement 1/3 espece 0
                    caseExp.when(cb.and(cb.equal(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "3"), cb.equal(mvt.get(TModeReglement_.lgMODEREGLEMENTID), "2")),
                            cb.sum(root.get(TMvtCaisse_.intAMOUNT))).otherwise(0),
                    // reglement 1/3 cheque 1
                    caseExp.when(cb.and(cb.equal(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "3"), cb.equal(mvt.get("lgTYPEREGLEMENTID").get("lgTYPEREGLEMENTID"), "1")),
                            cb.sum(root.get(TMvtCaisse_.intAMOUNT))).otherwise(0)
            /*      
                    // reglement 1/3 carte banquaire 2
                    cb.selectCase().when(cb.and(cb.equal(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "3"),
                            cb.equal(mvt.get("lgTYPEREGLEMENTID").get("lgTYPEREGLEMENTID"), "3")),
                            cb.sum(root.get(TMvtCaisse_.intAMOUNT)))
                            .otherwise(0),
                    // reglement 1/3 virement 3
                    cb.selectCase().when(cb.and(cb.equal(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "3"),
                            cb.equal(mvt.get("lgTYPEREGLEMENTID").get("lgTYPEREGLEMENTID"), "6")),
                            cb.sum(root.get(TMvtCaisse_.intAMOUNT)))
                            .otherwise(0),
                    // entree de caisse espece 4
                    cb.selectCase().when(cb.and(cb.or(cb.equal(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "5"),
                            cb.equal(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "2")),
                            cb.equal(mvt.get("lgTYPEREGLEMENTID").get("lgTYPEREGLEMENTID"), "1")),
                            cb.sum(root.get(TMvtCaisse_.intAMOUNT)))
                            .otherwise(0),
                    // entree de caisse cheque 5
                    cb.selectCase().when(cb.and(cb.or(cb.equal(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "5"),
                            cb.equal(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "2")),
                            cb.equal(mvt.get("lgTYPEREGLEMENTID").get("lgTYPEREGLEMENTID"), "2")),
                            cb.sum(root.get(TMvtCaisse_.intAMOUNT)))
                            .otherwise(0),
                    // entree de caisse carte banquaire 6
                    cb.selectCase().when(cb.and(cb.or(cb.equal(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "5"),
                            cb.equal(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "2")),
                            cb.equal(mvt.get("lgTYPEREGLEMENTID").get("lgTYPEREGLEMENTID"), "3")),
                            cb.sum(root.get(TMvtCaisse_.intAMOUNT)))
                            .otherwise(0),
                    // entree de caisse virement 7
                    cb.selectCase().when(cb.and(cb.or(cb.equal(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "5"),
                            cb.equal(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "2")),
                            cb.equal(mvt.get("lgTYPEREGLEMENTID").get("lgTYPEREGLEMENTID"), "6")),
                            cb.sum(root.get(TMvtCaisse_.intAMOUNT)))
                            .otherwise(0),
                    // sortie de caisse  8
                    cb.selectCase().when(cb.equal(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "4"),
                            cb.sum(root.get(TMvtCaisse_.intAMOUNT)))
                            .otherwise(0)*/
            ).groupBy(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID));
            cq.where(p, btw, cb.in(root.get(TMvtCaisse_.lgUSERID)).value(sub));
            Query q = em.createQuery(cq);
            List<Object[]> list = q.getResultList();

            list.forEach((t) -> {
                JSONObject ob = new JSONObject();
                try {
                    System.out.println(" " + t[0] + "\n" + t[1]);
//ob.put("REGLTP", t[0])

                    data.put(ob);
                } catch (Exception ex) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public List<String> getMVTUSERTicketZ(String dt_start, String dt_end) {

        EntityManager em = getEntityManager();
        List<String> list = new ArrayList<>();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cq = cb.createQuery(String.class);
            Root<TMvtCaisse> root = cq.from(TMvtCaisse.class);
            Join<TMvtCaisse, TTypeMvtCaisse> tm = root.join("lgTYPEMVTCAISSEID", JoinType.INNER);

            Predicate btw = cb.equal(cb.function("DATE", Date.class, root.get(TMvtCaisse_.dtCREATED)), java.sql.Date.valueOf(dt_start));
//            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TMvtCaisse_.dtCREATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            Predicate p = cb.conjunction();
            p = cb.and(p, cb.notLike(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "8"), cb.notLike(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "9"), cb.notLike(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "1"), cb.notLike(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "6"), cb.notLike(tm.get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID), "7"));
            cq.select(root.get(TMvtCaisse_.lgUSERID)).distinct(true);
            cq.where(p, btw);
            Query q = em.createQuery(cq);
            list = q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public JSONObject mvtTicketZ(String IDUSER, String dt) {
        JSONObject json = new JSONObject();
        try {
            EntityManager manager = this.getEntityManager();
            StoredProcedureQuery p = manager.createStoredProcedureQuery("proc_mvtTicketZ");
            p.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
            p.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);
            p.registerStoredProcedureParameter(3, Integer.class, ParameterMode.OUT);
            p.registerStoredProcedureParameter(4, Integer.class, ParameterMode.OUT);
            p.registerStoredProcedureParameter(5, Integer.class, ParameterMode.OUT);
            p.registerStoredProcedureParameter(6, Integer.class, ParameterMode.OUT);
            p.registerStoredProcedureParameter(7, Integer.class, ParameterMode.OUT);
            p.registerStoredProcedureParameter(8, Integer.class, ParameterMode.OUT);
            p.registerStoredProcedureParameter(9, Integer.class, ParameterMode.OUT);
            p.registerStoredProcedureParameter(10, Integer.class, ParameterMode.OUT);
            p.registerStoredProcedureParameter(11, Integer.class, ParameterMode.OUT);
            p.registerStoredProcedureParameter(12, Integer.class, ParameterMode.OUT);
            p.registerStoredProcedureParameter(13, Integer.class, ParameterMode.OUT);
            p.registerStoredProcedureParameter(14, Integer.class, ParameterMode.OUT);

            p.setParameter(1, IDUSER).setParameter(2, java.sql.Date.valueOf(dt));
            p.execute();
            Integer count = (Integer) p.getOutputParameterValue(3);
            if (count != null) {
                json.put("ENTRESP", count);
            }

            count = (Integer) p.getOutputParameterValue(4);
            if (count != null) {
                json.put("ENTRECH", count);
            }

            count = (Integer) p.getOutputParameterValue(5);
            if (count != null) {
                json.put("ENTRECB", count);
            }

            count = (Integer) p.getOutputParameterValue(6);
            if (count != null) {
                json.put("ENTREVIR", count);
            }
            count = (Integer) p.getOutputParameterValue(7);
            if (count != null) {
                json.put("REGLTPESP", count);
            }
            count = (Integer) p.getOutputParameterValue(8);
            if (count != null) {
                json.put("REGLTPCH", count);
            }
            count = (Integer) p.getOutputParameterValue(9);
            if (count != null) {
                json.put("REGLTPCB", count);
            }
            count = (Integer) p.getOutputParameterValue(10);
            if (count != null) {
                json.put("REGLTPVIR", count);
            }
            count = (Integer) p.getOutputParameterValue(11);
            if (count != null) {
                json.put("SORTIEESP", count);
            }
            count = (Integer) p.getOutputParameterValue(12);
            if (count != null) {
                json.put("SORTIECH", count);
            }
            count = (Integer) p.getOutputParameterValue(13);
            if (count != null) {
                json.put("SORTIECB", count);
            }
            count = (Integer) p.getOutputParameterValue(14);
            if (count != null) {
                json.put("SORTIEVIR", count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public Integer getBalanceRegl(String dt_start, String dt_end, String typevente, String lgEmp, String lgTYPEREGLEMENTID) {

        EntityManager em = getEntityManager();
        Integer diff = 0;
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<TCashTransaction> root = cq.from(TCashTransaction.class);

//            Join<TCashTransaction, TReglement> r = root.join("lgREGLEMENTID", JoinType.INNER);
            Subquery<String> sub = cq.subquery(String.class);
            Root<TPreenregistrement> pr = sub.from(TPreenregistrement.class);
            Join<TPreenregistrement, TUser> pu = pr.join("lgUSERID", JoinType.INNER);
            Predicate predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.equal(pu.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            predicate = cb.and(predicate, cb.notLike(pr.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            predicate = cb.and(predicate, cb.equal(pr.get(TPreenregistrement_.strSTATUT), "is_Closed"));
            predicate = cb.and(predicate, cb.equal(pr.get(TPreenregistrement_.bISCANCEL), false));
            predicate = cb.and(predicate, cb.equal(pr.get(TPreenregistrement_.strTYPEVENTE), typevente));
            predicate = cb.and(predicate, cb.equal(root.get(TCashTransaction_.lgTYPEREGLEMENTID), lgTYPEREGLEMENTID));
            Predicate ge = cb.greaterThan(pr.get(TPreenregistrement_.intPRICE), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, pr.get(TPreenregistrement_.dtUPDATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            sub.select(pr.get(TPreenregistrement_.lgPREENREGISTREMENTID)).where(predicate, btw, ge);

            Predicate ge2 = cb.greaterThan(root.get(TCashTransaction_.intAMOUNT), 0);

            cq.select(cb.sum(root.get(TCashTransaction_.intAMOUNT)));

            cq.where(ge2, cb.in(root.get(TCashTransaction_.strRESSOURCEREF)).value(sub));

            Query q = em.createQuery(cq);

            diff = (Integer) q.getSingleResult();
            if (diff == null) {
                diff = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return diff;

    }
// list des article à la recherche avec couleur

    public JSONArray getFamillesCouleur(boolean all, String criteria, String lgEmp, int start, int limit) {
        EntityManager em = this.getEntityManager();
        JSONArray array = new JSONArray();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TFamille> root = cq.from(TFamille.class);
            Join<TFamille, TFamilleGrossiste> st = root.join("tFamilleGrossisteCollection", JoinType.INNER);
            Join<TFamille, TFamilleStock> fa = root.join("tFamilleStockCollection", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            if (!"".equals(criteria)) {
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TFamille_.strNAME), criteria + "%"), cb.like(root.get(TFamille_.intCIP), criteria + "%"), cb.like(root.get(TFamille_.intEAN13), criteria + "%"), cb.like(st.get("strCODEARTICLE"), criteria + "%"), cb.like(root.get(TFamille_.lgFAMILLEID), criteria + "%"), cb.like(root.get(TFamille_.strDESCRIPTION), criteria + "%")));
            }
            predicate = cb.and(predicate, cb.equal(root.get(TFamille_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(fa.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            cq.multiselect(root.get(TFamille_.lgFAMILLEID), root.get(TFamille_.strDESCRIPTION), root.get(TFamille_.intCIP), root.get(TFamille_.intPRICE), fa.get(TFamilleStock_.intNUMBER), fa.get(TFamilleStock_.intNUMBERAVAILABLE), root.get("lgZONEGEOID").get("strLIBELLEE")).orderBy(cb.asc(root.get(TFamille_.strDESCRIPTION)));
            cq.where(predicate);
            Query q = em.createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);
            }
            List<Object[]> list = q.getResultList();
            list.forEach((t) -> {
                try {
                    JSONObject ob = new JSONObject();
                    int stock = Integer.valueOf(t[5] + "");
                    String style = "font-weight:800;";
                    if (stock <= 0) {
                        style = "background-color:#B0F2B6;font-weight:800;";
                    }
                    ob.putOnce("lg_FAMILLE_ID", t[0] + "").putOnce("str_DESCRIPTION", "<span style=" + style + " >" + t[1] + "</span>")
                            .putOnce("CIP", "<span style=" + style + " >" + t[2] + "</span>").putOnce("int_PRICE", "<span style=" + style + " >" + t[3] + "</span>").putOnce("int_NUMBER", t[4] + "")
                            .putOnce("int_NUMBER_AVAILABLE", t[5] + "").put("lg_ZONE_GEO_ID", t[6]);
                    array.put(ob);
                } catch (JSONException ex) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public Integer getBalanceReglExclude(String dt_start, String dt_end, String typevente, String lgEmp, int AMOUNT2, String lgTYPEREGLEMENTID) {

        EntityManager em = getEntityManager();
        Integer diff = 0;
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<TCashTransaction> root = cq.from(TCashTransaction.class);
            Join<TCashTransaction, TReglement> r = root.join("lgREGLEMENTID", JoinType.INNER);
            Subquery<String> sub = cq.subquery(String.class);
            Root<TPreenregistrement> pr = sub.from(TPreenregistrement.class);
            Join<TPreenregistrement, TUser> pu = pr.join("lgUSERID", JoinType.INNER);
            Predicate predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.equal(pu.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            predicate = cb.and(predicate, cb.notLike(pr.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            predicate = cb.and(predicate, cb.equal(pr.get(TPreenregistrement_.strSTATUT), "is_Closed"));
            predicate = cb.and(predicate, cb.equal(pr.get(TPreenregistrement_.bISCANCEL), false));
            predicate = cb.and(predicate, cb.equal(pr.get(TPreenregistrement_.strTYPEVENTE), typevente));
            predicate = cb.and(predicate, cb.equal(root.get(TCashTransaction_.lgTYPEREGLEMENTID), lgTYPEREGLEMENTID));
            Predicate ge = cb.greaterThan(pr.get(TPreenregistrement_.intPRICE), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, pr.get(TPreenregistrement_.dtUPDATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            sub.select(pr.get(TPreenregistrement_.lgPREENREGISTREMENTID)).where(predicate, btw, ge);

            Predicate ge2 = cb.greaterThan(root.get(TCashTransaction_.intAMOUNT), 0);

            cq.select(cb.sum((AMOUNT2 == 1 ? root.get(TCashTransaction_.intAMOUNT2) : root.get(TCashTransaction_.intACCOUNT))));

            cq.where(ge2, cb.in(root.get(TCashTransaction_.strRESSOURCEREF)).value(sub));

            Query q = em.createQuery(cq);
            diff = (Integer) q.getSingleResult();
            if (diff == null) {
                diff = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return diff;

    }

    public JSONArray getBalanceExclude(LocalDate dt_start, LocalDate dt_end, String lgEmp, int isOk) {
        EntityManager em = this.getEntityManager();
        JSONArray array = new JSONArray();
        boolean period = true;
        TParameters p;

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

            Root<TPreenregistrement> root = cq.from(TPreenregistrement.class);
            Join<TPreenregistrement, TUser> pu = root.join("lgUSERID", JoinType.INNER);
            Join<TPreenregistrement, TReglement> r = root.join("lgREGLEMENTID", JoinType.INNER);
            Predicate predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.equal(pu.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.bISCANCEL), Boolean.FALSE));
            predicate = cb.and(predicate, cb.notLike(root.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.strSTATUT), "is_Closed"));
            Predicate ge = cb.greaterThan(root.get(TPreenregistrement_.intPRICE), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrement_.dtUPDATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            cq.multiselect(cb.sum((isOk == 1 ? root.get(TPreenregistrement_.intPRICEOTHER) : root.get(TPreenregistrement_.intACCOUNT))),
                    cb.sum(root.get(TPreenregistrement_.intREMISEPARA)),
                    cb.sum(cb.diff((isOk == 1 ? root.get(TPreenregistrement_.intPRICEOTHER) : root.get(TPreenregistrement_.intACCOUNT)), root.get(TPreenregistrement_.intREMISEPARA))),
                    root.get(TPreenregistrement_.strTYPEVENTE),
                    cb.selectCase().when(cb.equal(root.get(TPreenregistrement_.strTYPEVENTE), "VO"),
                            cb.sum(root.get(TPreenregistrement_.intCUSTPART)))
                            .otherwise(cb.sum(cb.diff((isOk == 1 ? root.get(TPreenregistrement_.intPRICEOTHER) : root.get(TPreenregistrement_.intACCOUNT)),
                                    root.get(TPreenregistrement_.intREMISEPARA)))), cb.count(root),
                    cb.quot(cb.sum((isOk == 1 ? root.get(TPreenregistrement_.intPRICEOTHER) : root.get(TPreenregistrement_.intACCOUNT))),
                            cb.count(root)), cb.selectCase()
                    .when(cb.equal(root.get(TPreenregistrement_.strTYPEVENTE), "VO"),
                            cb.sum(cb.diff(root.get(TPreenregistrement_.intACCOUNT), root.get(TPreenregistrement_.intCUSTPART)))).otherwise(0))
                    .groupBy(root.get(TPreenregistrement_.strTYPEVENTE)).orderBy(cb.desc(root.get(TPreenregistrement_.strTYPEVENTE)));
            cq.where(predicate, btw, ge);
            Query q = em.createQuery(cq);

            List<Object[]> list = q.getResultList();

            LocalDate x = dt_start;
            LocalDate y = dt_end;
            double percent = 0, Gbl = 0, vo = 0, vno = 0;
            int nb = 0;
            Integer totalcq = 0, totalcb = 0, totalEs = 0;
            for (Object[] t : list) {
                try {
                    Integer VENTE_BRUT = Integer.valueOf(t[0] + ""),
                            TOTAL_REMISE = Integer.valueOf(t[1] + ""),
                            VENTE_NET = Integer.valueOf(t[2] + ""), TOTAL_CAISSE = Integer.valueOf(t[4] + ""), PANIER_MOYEN = Math.round(Double.valueOf(t[6] + "").intValue()),
                            PART_TIERSPAYANT = Integer.valueOf(t[7] + "");
                    Integer TOTAL_DIFFERE = getBalanceDiff(dt_start.toString(), dt_end.toString(), t[3] + "", lgEmp);
                    Integer montantES = getBalanceReglExclude(dt_start.toString(), dt_end.toString(), t[3] + "", lgEmp, isOk, "1");
                    montantES = (montantES != null ? montantES : 0);
                    montantES += getBalanceReglExclude(dt_start.toString(), dt_end.toString(), t[3] + "", lgEmp, isOk, "4");

                    Integer Cheques = getBalanceReglExclude(dt_start.toString(), dt_end.toString(), t[3] + "", lgEmp, isOk, "2");
                    Cheques = (Cheques != null ? Cheques : 0);
                    Integer cba = getBalanceReglExclude(dt_start.toString(), dt_end.toString(), t[3] + "", lgEmp, isOk, "3");
                    cba = (cba != null ? cba : 0);

                    if (y.compareTo(x) == 0) {
                        JSONObject json = getVenteAnnulerExclude(dt_end.toString(), t[3] + "");
                        VENTE_BRUT -= json.getInt("monantTTC");
                        TOTAL_REMISE -= json.getInt("remise");
                        VENTE_NET -= json.getInt("montant");
                        TOTAL_CAISSE -= json.getInt("montantES");
                        PART_TIERSPAYANT -= json.getInt("tp");
                        montantES -= json.getInt("montantES");
                        Cheques -= json.getInt("cheques");
                        cba -= json.getInt("cb");
                        TOTAL_DIFFERE -= json.getInt("differe");
                        Long _PANIER_MOYEN = Math.round(Double.valueOf(VENTE_BRUT) / Integer.valueOf(t[5] + ""));
                        PANIER_MOYEN = _PANIER_MOYEN.intValue();

                    }
                    Gbl += VENTE_NET;
                    totalcq += Cheques;
                    totalcb += cba;
                    totalEs += montantES;
                    nb += Integer.valueOf(t[5] + "");
                    JSONObject ob = new JSONObject();
                    ob.put("VENTE_BRUT", VENTE_BRUT).put("TOTAL_DIFFERE", TOTAL_DIFFERE)
                            .put("TOTAL_REMISE", TOTAL_REMISE)
                            .put("VENTE_NET", VENTE_NET);
                    ob.put("str_TYPE_VENTE", t[3] + "");
                    ob.put("TOTAL_CAISSE", TOTAL_CAISSE);
                    ob.put("NB", Integer.valueOf(t[5] + ""));
                    ob.put("PANIER_MOYEN", PANIER_MOYEN);
                    ob.put("PART_TIERSPAYANT", PART_TIERSPAYANT);
                    if ("VNO".equals(t[3] + "")) {
                        ob.put("PART_TIERSPAYANT", 0);
                    }

                    ob.put("TOTAL_ESPECE", montantES);
                    ob.put("TOTAL_CHEQUE", Cheques);
                    ob.put("TOTAL_CARTEBANCAIRE", cba);

                    array.put(ob);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            JSONObject totaux = new JSONObject();
            double ration;
            long achat = getMontantAchats(dt_start.toString(), dt_end.toString(), lgEmp);
            long marge = getMarge(dt_start.toString(), dt_end.toString(), lgEmp);

            totaux.put("NB", nb).put("GLOBALESP", totalEs).put("GLOBALCB", totalcb).put("TOTALMARGE", marge)
                    .put("GLOBALCQ", totalcq).put("GLOBAL", Gbl).put("TOTALACHAT", achat);
            array.put(totaux);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public JSONArray getBalanceIntervalEclu(String dt_start, String dt_end, String lgEmp) {
        JSONArray array = new JSONArray();
        String str_TYPE_VENTE = "", str_TYPE_VENTEVO = "";
        Integer NB = 0, NBT = 0, NBVO = 0;
        long PART_TIERSPAYANT = 0, TOTALMARGE = 0, TOTALACHAT = 0, GLOBALESP = 0, GLOBAL = 0, GLOBALCQ = 0, GLOBALCB = 0, PART_TIERSPAYANTVO = 0,
                TOTAL_REMISE = 0,
                VENTE_BRUT = 0,
                TOTAL_ESPECE = 0,
                TOTAL_CHEQUE = 0,
                TOTAL_CARTEBANCAIRE = 0,
                VENTE_NET = 0,
                TOTAL_DIFFERE = 0,
                TOTAL_CAISSE = 0,
                TOTAL_REMISEVO = 0,
                VENTE_BRUTVO = 0,
                TOTAL_ESPECEVO = 0,
                TOTAL_CHEQUEVO = 0,
                TOTAL_CARTEBANCAIREVO = 0,
                VENTE_NETVO = 0,
                TOTAL_DIFFEREVO = 0,
                TOTAL_CAISSEVO = 0;
        int isOk = 0;
        try {
            TParameters p;
            try {
                EntityManager em = this.getEntityManager();
                p = em.getReference(TParameters.class, "KEY_PARAMS");
                if (p != null) {
                    isOk = Integer.valueOf(p.getStrVALUE().trim());
                }
                LocalDate dtstart = LocalDate.parse(dt_start);
                LocalDate dtend = LocalDate.parse(dt_end);
                Period period = Period.between(dtstart, dtend);
                for (int i = 0; i < period.getMonths(); i++) {
                    LocalDate _dtStart = dtstart.plusMonths(i);
                    JSONArray _array = getBalanceExclude(_dtStart, _dtStart.plusDays(_dtStart.lengthOfMonth() - 1), lgEmp, isOk);
                    JSONObject totaux = _array.getJSONObject((_array.length() - 1));
                    NBT += totaux.getInt("NB");
                    TOTALMARGE += totaux.getLong("TOTALMARGE");
                    TOTALACHAT += totaux.getLong("TOTALACHAT");
                    GLOBALESP += totaux.getLong("GLOBALESP");
                    GLOBAL += totaux.getLong("GLOBAL");
                    GLOBALCQ += totaux.getLong("GLOBALCQ");
                    GLOBALCB += totaux.getLong("GLOBALCB");
                    for (int j = 0; j < _array.length() - 1; j++) {
                        JSONObject object = _array.getJSONObject(j);
                        if (object.getString("str_TYPE_VENTE").equals("VNO")) {
                            TOTAL_REMISE += object.getInt("TOTAL_REMISE");
                            NB += object.getInt("NB");
                            VENTE_BRUT += object.getLong("VENTE_BRUT");
                            TOTAL_ESPECE += object.getLong("TOTAL_ESPECE");
                            TOTAL_CHEQUE += object.getLong("TOTAL_CHEQUE");
                            TOTAL_CARTEBANCAIRE += object.getLong("TOTAL_CARTEBANCAIRE");
                            VENTE_NET += object.getLong("VENTE_NET");
                            TOTAL_DIFFERE += object.getLong("TOTAL_DIFFERE");
                            TOTAL_CAISSE += object.getLong("TOTAL_CAISSE");
                            str_TYPE_VENTE = object.getString("str_TYPE_VENTE");
                        } else {
                            PART_TIERSPAYANTVO += object.getLong("PART_TIERSPAYANT");
                            TOTAL_REMISEVO += object.getInt("TOTAL_REMISE");
                            NBVO += object.getInt("NB");
                            VENTE_BRUTVO += object.getLong("VENTE_BRUT");
                            TOTAL_ESPECEVO += object.getLong("TOTAL_ESPECE");
                            TOTAL_CHEQUEVO += object.getLong("TOTAL_CHEQUE");
                            TOTAL_CARTEBANCAIREVO += object.getLong("TOTAL_CARTEBANCAIRE");
                            VENTE_NETVO += object.getLong("VENTE_NET");
                            TOTAL_DIFFEREVO += object.getLong("TOTAL_DIFFERE");
                            TOTAL_CAISSEVO += object.getLong("TOTAL_CAISSE");
                            str_TYPE_VENTEVO = object.getString("str_TYPE_VENTE");
                        }
                    }

                }
                // à revoir pour la date
                LocalDate dd = LocalDate.of(dtend.getYear(), dtend.getMonthValue(), 1);
                JSONArray endarray = getBalanceExclude(dd, dd.plusDays(dd.lengthOfMonth() - 1), lgEmp, isOk);
                JSONObject endtotaux = endarray.getJSONObject((endarray.length() - 1));
                NBT += endtotaux.getInt("NB");
                TOTALMARGE += endtotaux.getLong("TOTALMARGE");
                TOTALACHAT += endtotaux.getLong("TOTALACHAT");
                GLOBALESP += endtotaux.getLong("GLOBALESP");
                GLOBAL += endtotaux.getLong("GLOBAL");
                GLOBALCQ += endtotaux.getLong("GLOBALCQ");
                GLOBALCB += endtotaux.getLong("GLOBALCB");

                for (int j = 0; j < endarray.length() - 1; j++) {
                    JSONObject object = endarray.getJSONObject(j);
                    if (object.getString("str_TYPE_VENTE").equals("VNO")) {
                        TOTAL_REMISE += object.getInt("TOTAL_REMISE");
                        NB += object.getInt("NB");
                        VENTE_BRUT += object.getLong("VENTE_BRUT");
                        //PART_TIERSPAYANT+= object.getLong("PART_TIERSPAYANT");
                        TOTAL_ESPECE += object.getLong("TOTAL_ESPECE");
                        TOTAL_CHEQUE += object.getLong("TOTAL_CHEQUE");
                        TOTAL_CARTEBANCAIRE += object.getLong("TOTAL_CARTEBANCAIRE");
                        VENTE_NET += object.getLong("VENTE_NET");
                        TOTAL_DIFFERE += object.getLong("TOTAL_DIFFERE");
                        TOTAL_CAISSE += object.getLong("TOTAL_CAISSE");
                        str_TYPE_VENTE = object.getString("str_TYPE_VENTE");
                    } else {
                        PART_TIERSPAYANTVO += object.getLong("PART_TIERSPAYANT");
                        TOTAL_REMISEVO += object.getInt("TOTAL_REMISE");
                        NBVO += object.getInt("NB");
                        VENTE_BRUTVO += object.getLong("VENTE_BRUT");
                        TOTAL_ESPECEVO += object.getLong("TOTAL_ESPECE");
                        TOTAL_CHEQUEVO += object.getLong("TOTAL_CHEQUE");
                        TOTAL_CARTEBANCAIREVO += object.getLong("TOTAL_CARTEBANCAIRE");
                        VENTE_NETVO += object.getLong("VENTE_NET");
                        TOTAL_DIFFEREVO += object.getLong("TOTAL_DIFFERE");
                        TOTAL_CAISSEVO += object.getLong("TOTAL_CAISSE");
                        str_TYPE_VENTEVO = object.getString("str_TYPE_VENTE");
                    }
                }

                Long _PANIER_MOYEN = Math.round(Double.valueOf(VENTE_BRUT) / NB);
                JSONObject ob = new JSONObject();
                ob.put("VENTE_BRUT", VENTE_BRUT).put("TOTAL_DIFFERE", TOTAL_DIFFERE)
                        .put("TOTAL_REMISE", TOTAL_REMISE)
                        .put("VENTE_NET", VENTE_NET);
                ob.put("str_TYPE_VENTE", "VNO");
                ob.put("TOTAL_CAISSE", TOTAL_CAISSE);
                ob.put("NB", NB);
                ob.put("PANIER_MOYEN", _PANIER_MOYEN.intValue());
                ob.put("PART_TIERSPAYANT", 0);
                ob.put("TOTAL_ESPECE", TOTAL_ESPECE);
                ob.put("TOTAL_CHEQUE", TOTAL_CHEQUE);
                ob.put("TOTAL_CARTEBANCAIRE", TOTAL_CARTEBANCAIRE);
                array.put(ob);
                Long _PANIER_MOYENVO = Math.round(Double.valueOf(VENTE_BRUTVO) / NBVO);

                JSONObject obvo = new JSONObject();
                obvo.put("VENTE_BRUT", VENTE_BRUTVO).put("TOTAL_DIFFERE", TOTAL_DIFFEREVO)
                        .put("TOTAL_REMISE", TOTAL_REMISEVO)
                        .put("VENTE_NET", VENTE_NETVO);
                obvo.put("str_TYPE_VENTE", "VO");
                obvo.put("TOTAL_CAISSE", TOTAL_CAISSEVO);
                obvo.put("NB", NBVO);
                obvo.put("PANIER_MOYEN", _PANIER_MOYENVO.intValue());
                obvo.put("PART_TIERSPAYANT", PART_TIERSPAYANTVO);
                obvo.put("TOTAL_ESPECE", TOTAL_ESPECEVO);
                obvo.put("TOTAL_CHEQUE", TOTAL_CHEQUEVO);
                obvo.put("TOTAL_CARTEBANCAIRE", TOTAL_CARTEBANCAIREVO);
                array.put(obvo);
                double ration;
                double rat = 0.0;
                if (TOTALACHAT > 0) {
                    ration = (GLOBAL / TOTALACHAT);
                    rat = new BigDecimal(ration).setScale(2, RoundingMode.HALF_UP).doubleValue();
                }
                JSONObject totaux = new JSONObject();
                totaux.put("NB", NBT).put("GLOBALESP", GLOBALESP).put("GLOBALCB", GLOBALCB).put("TOTALMARGE", TOTALMARGE)
                        .put("GLOBALCQ", GLOBALCQ).put("GLOBAL", Double.valueOf(GLOBAL).intValue())
                        .put("TOTALACHAT", TOTALACHAT).put("TOTALRATIO", rat);
                array.put(totaux);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public JSONObject getRecap(String dt_start, String dt_end, String emp) {
        Integer cba = 0, diff = 0, vir = 0, ch = 0, montTTC = 0, montHT = 0, TVA = 0, montAchat = 0, VO = 0, VNO = 0, remiseHT = 0, montantHTCNET = 0;
        Integer marge;
        JSONObject jsono = new JSONObject();
        try {
            long count = getTotal(dt_start, dt_end, emp);
            Integer start = 0;
            int page = 1000;
            Integer limit = page;
            EntityManager em = this.getEntityManager();
            Transaction transaction = new TransactionImpl(em);
            while (count > 0) {
                JSONObject ob = transaction.getDataActivites(dt_start, dt_end, emp, start, limit);
                cba += ob.getInt("cba");
                diff += ob.getInt("diff");
                vir += ob.getInt("vir");
                ch += ob.getInt("ch");
                montTTC += ob.getInt("montTTC");
                montHT += ob.getInt("montHT");
                montAchat += ob.getInt("montAchat");
                VO += ob.getInt("VO");
                VNO += ob.getInt("VNO");
                remiseHT += ob.getInt("remiseHT");
                montantHTCNET += ob.getInt("montantHTCNET");
                TVA += ob.getInt("TVA");
                start += page;
                count -= page;
            }
            Integer Esp = transaction.getBalanceRegl(dt_start, dt_end, emp, "1");

            Double percentVO = (Double.valueOf(VO) * 100) / Math.abs(montTTC);
            Double percentVNO = (Double.valueOf(Esp) * 100) / Math.abs(montTTC);
            JSONArray recettes = new JSONArray();
            JSONArray datatva = new JSONArray();
            JSONObject tva = new JSONObject();
            tva.put("name", "TVA 18").put("montant", Util.getFormattedIntegerValue(TVA)).put("value", 18);
            datatva.put(tva);
            jsono.put("datatva", datatva);
            JSONObject obRe = new JSONObject();
            obRe.put("name", "Espèce").put("montant", Util.getFormattedIntegerValue(Esp));
            recettes.put(obRe);
            obRe = new JSONObject();
            obRe.put("name", "Carte Bancaire").put("montant", Util.getFormattedIntegerValue(cba));
            recettes.put(obRe);
            obRe = new JSONObject();
            obRe.put("name", "Crédit Différé").put("montant", Util.getFormattedIntegerValue(diff));
            recettes.put(obRe);
            obRe = new JSONObject();
            obRe.put("name", "Virement").put("montant", Util.getFormattedIntegerValue(vir));
            recettes.put(obRe);
            obRe = new JSONObject();
            obRe.put("name", "Chèques").put("montant", Util.getFormattedIntegerValue(ch));
            recettes.put(obRe);

            jsono.put("recettes", recettes);
            jsono.put("montTTC", Util.getFormattedIntegerValue(montTTC));
            jsono.put("montHT", Util.getFormattedIntegerValue(montHT));
//            jsono.put("TVA", TVA);
//            jsono.put("montAchat", montAchat);
            jsono.put("VO", Util.getFormattedIntegerValue(VO) + " (" + Math.round(percentVO) + "%)");
            jsono.put("VNO", Util.getFormattedIntegerValue(Esp) + " (" + Math.round(percentVNO) + "%)");
            jsono.put("remiseHT", Util.getFormattedIntegerValue(remiseHT));
            jsono.put("montantHTCNET", Util.getFormattedIntegerValue(montantHTCNET));
            marge = montHT - montAchat;
            count = achats(dt_start, dt_end);
            start = 0;
            page = 1000;
            limit = page;
            Integer th = 0, ttc = 0, tvaAchat = 0;
            while (count > 0) {
                JSONObject js = achat(dt_start, dt_end, start, limit);
                th += js.getInt("th");
                ttc += js.getInt("ttc");
                tvaAchat += js.getInt("tva");
                start += page;
                count -= page;
            }
            JSONObject achats = new JSONObject();
            achats.put("th", Util.getFormattedIntegerValue(th));
            achats.put("ttc", Util.getFormattedIntegerValue(ttc));
            achats.put("tva", Util.getFormattedIntegerValue(tvaAchat));
            achats.put("marge", Util.getFormattedIntegerValue(marge));
            double ratio = 0.0;
            if (ttc > 0) {
                ratio = new BigDecimal(Double.valueOf(ttc) / Double.valueOf(montTTC)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();//Math.round(Double.valueOf(ttc)/Double.valueOf(valueTTC));
            }

            achats.put("ratio", ratio);
            jsono.put("achats", achats);
        } catch (Exception e) {
        }
        return jsono;
    }

    public long achats(String dt_start, String dt_end) {
        EntityManager em = this.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TBonLivraison> root = cq.from(TBonLivraison.class);
            Predicate criteria = cb.conjunction();
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(root.get(TBonLivraison_.strSTATUT), "is_Closed"));
            cq.select(cb.count(root));
            cq.where(btw, criteria);
            Query q = em.createQuery(cq);
            return (long) q.getSingleResult();
        } finally {

        }
    }

    public JSONObject achat(String dt_start, String dt_end, int start, int limit) {
        EntityManager em = this.getEntityManager();
        JSONObject data = new JSONObject();

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TBonLivraison> root = cq.from(TBonLivraison.class);

            Predicate criteria = cb.conjunction();
            System.out.println(" dt " + dt_start + " " + dt_end);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("dtUPDATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, cb.equal(root.get(TBonLivraison_.strSTATUT), "is_Closed"));

            cq.multiselect(cb.sumAsLong(root.get(TBonLivraison_.intHTTC)), cb.sumAsLong(root.get(TBonLivraison_.intMHT)), cb.sumAsLong(root.get(TBonLivraison_.intTVA)));
            cq.where(btw, criteria);

            Query q = em.createQuery(cq);
            q.setFirstResult(start).setMaxResults(limit);
            List<Object[]> list = q.getResultList();

            long valueTTC = 0l, valueHT = 0, valueTVA = 0l;

            for (Object[] objects : list) {

                valueTTC += (objects[0] != null ? Long.valueOf(objects[0] + "") : 0);
                valueHT += (objects[1] != null ? Long.valueOf(objects[1] + "") : 0);
                valueTVA += (objects[2] != null ? Long.valueOf(objects[2] + "") : 0);
            }

            data.put("th", valueHT).put("ttc", valueTTC).put("tva", valueTVA);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return data;
    }

    public JSONObject getGroupeInvoice(String dt_start, String dt_end, Integer idGrp) {
        JSONArray list = new JSONArray();
        JSONObject _json = new JSONObject();
        EntityManager em = null;
        try {
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TGroupeFactures> root = cq.from(TGroupeFactures.class);
            Join<TGroupeFactures, TGroupeTierspayant> pr = root.join("lgGROUPEID", JoinType.INNER);
            Join<TGroupeFactures, TFacture> gf = root.join("lgFACTURESID", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            if (idGrp > 0) {
                criteria = cb.and(criteria, cb.equal(root.get("lgGROUPEID").get("lgGROUPEID"), idGrp));
            }
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get("dtCREATED")), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            criteria = cb.and(criteria, btw);
            cq.multiselect(root.get("lgGROUPEID").get("lgGROUPEID"), root.get("lgGROUPEID").get("strLIBELLE"), cb.count(root), cb.sumAsLong(root.get("lgFACTURESID").get("dblMONTANTRESTANT")), root.get("strCODEFACTURE"), root.get("dtCREATED"), root.get("lgFACTURESID").get("strSTATUT"), cb.sumAsLong(root.get("lgFACTURESID").get("dblMONTANTCMDE")), cb.sumAsLong(root.get("lgFACTURESID").get("dblMONTANTPAYE")))
                    .groupBy(root.get("strCODEFACTURE"));
            cq.where(criteria);
            Query q = em.createQuery(cq);
            List<Object[]> oblist = q.getResultList();
            oblist.forEach((objects) -> {
                try {
                    JSONObject json = new JSONObject();
                    String status = "enable";
                    if (Long.valueOf(objects[3] + "") == 0) {
                        status = "paid";
                    }
                    json.put("lg_GROUPE_ID", objects[0]).put("str_LIB", objects[1]).put("NBFACTURES", objects[2]).put("MONTANTRESTANT", objects[3]).put("CODEFACTURE", objects[4]).put("DATECREATION", date.formatterMysqlShort.format(objects[5])).put("STATUT", status).put("AMOUNT", objects[7]).put("AMOUNTPAYE", objects[8]);
                    list.put(json);
                } catch (JSONException ex) {

                }
            });
            _json.put("invoices", list);
        } catch (JSONException ex) {

        }
        return _json;
    }

    public JSONObject getEmplacement(String search) {
        EntityManager em = this.getEntityManager();
        JSONObject _json = new JSONObject();

        try {
            JSONArray data = new JSONArray();
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TEmplacement> root = cq.from(TEmplacement.class);

            Predicate criteria = cb.conjunction();

            criteria = cb.and(criteria, cb.equal(root.get(TEmplacement_.strSTATUT), commonparameter.statut_enable));
            if (!"".equals(search)) {

                criteria = cb.and(criteria, cb.or(cb.like(root.get(TEmplacement_.strNAME), search + "%")));
            }
            cq.multiselect(root.get(TEmplacement_.strNAME), root.get(TEmplacement_.lgEMPLACEMENTID));

            cq.where(criteria);

            Query q = em.createQuery(cq);

            List<Object[]> oblist = q.getResultList();

            oblist.forEach((t) -> {
                try {
                    JSONObject json = new JSONObject();
                    json.put("lgEMPLACEMENTID", t[1] + "").put("strNAME", t[0] + "");
                    data.put(json);
                } catch (JSONException ex) {

                }

            });
            _json.put("data", data).put("total", data.length());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return _json;
    }

    public JSONArray getListePerimes(String search_value, String str_TYPE_TRANSACTION, String str_TRI, int start, int limit, boolean all) {
        JSONArray array = new JSONArray();
        EntityManager em = this.getEntityManager();
        try {
            LocalDate now = LocalDate.now();

            TParameters OTParameters = em.getReference(TParameters.class, "KEY_MONTH_PERIME");
            int int_NUMBER = 0;
            if (OTParameters != null) {
                int_NUMBER = Integer.parseInt(OTParameters.getStrVALUE());
            }
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TLot> cq = cb.createQuery(TLot.class);
            Root<TLot> root = cq.from(TLot.class);
            Join<TLot, TFamille> pr = root.join("lgFAMILLEID", JoinType.INNER);
            Join<TFamille, TFamilleStock> st = pr.join("tFamilleStockCollection", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            if (!"".equals(search_value)) {
                criteria = cb.and(criteria, cb.or(cb.like(pr.get(TFamille_.intCIP), search_value + "%"), cb.like(pr.get(TFamille_.strNAME), search_value + "%")));
            }
            Predicate gt = cb.greaterThan(st.get(TFamilleStock_.intNUMBERAVAILABLE), 0);
            criteria = cb.and(criteria, gt);
            criteria = cb.and(criteria, cb.equal(st.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), "1"));

            switch (str_TYPE_TRANSACTION) {
                case Parameter.KEY_PARAM_PERIME:
//                    Predicate eq = cb.lessThanOrEqualTo(root.get(TLot_.dtPEREMPTION), java.sql.Date.valueOf(now));
                    criteria = cb.and(criteria, cb.equal(root.get(TLot_.strSTATUT), commonparameter.statut_perime));

                    break;
                case Parameter.KEY_PERIMER_ENCOURS:
//                    Predicate l = cb.lessThan(root.get(TLot_.dtPEREMPTION), java.sql.Date.valueOf(peremption));
                    Predicate ge = cb.greaterThan(root.get(TLot_.dtPEREMPTION), java.sql.Date.valueOf(now));
                    criteria = cb.and(criteria, cb.equal(root.get(TLot_.strSTATUT), Parameter.STATUT_ENCOURS_PEREMPTION));
                    criteria = cb.and(criteria, ge);
                    break;
                default:
//                    Predicate le = cb.lessThanOrEqualTo(root.get(TLot_.dtPEREMPTION), java.sql.Date.valueOf(peremption));
                    criteria = cb.and(criteria, cb.or(cb.equal(root.get(TLot_.strSTATUT), Parameter.STATUT_ENCOURS_PEREMPTION), cb.equal(root.get(TLot_.strSTATUT), commonparameter.statut_perime)));

                    break;
            }
            criteria = cb.and(criteria, cb.equal(pr.get(TFamille_.strSTATUT), commonparameter.statut_enable));

            switch (str_TRI) {
                case "str_CODE_EMPLACEMENT":
                    cq.select(root).orderBy(cb.asc(pr.get("lgZONEGEOID").get("strCODE")), cb.asc(pr.get(TFamille_.strNAME)));
                    break;
                case "str_CODE_FAMILLE":
                    cq.select(root).orderBy(cb.asc(pr.get("lgFAMILLEARTICLEID").get("strCODEFAMILLE")), cb.asc(pr.get(TFamille_.strNAME)));
                    break;
                case "str_CODE_GROSSISTE":
                    cq.select(root).orderBy(cb.asc(pr.get("lgGROSSISTEID").get("strCODE")), cb.asc(pr.get(TFamille_.strNAME)));
                    break;
                default:
                    cq.select(root).orderBy(cb.asc(pr.get(TFamille_.strNAME)));
                    break;
            }
            cq.where(criteria);

            Query q = em.createQuery(cq);

            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);
            }
            List<TLot> list = q.getResultList();
            for (TLot tLot : list) {

                JSONObject json = new JSONObject();
                json.put("lg_LOT_ID", tLot.getLgLOTID());
                json.put("lg_FAMILLE_ID", tLot.getLgUSERID().getStrFIRSTNAME().substring(0, 1) + " " + tLot.getLgUSERID().getStrLASTNAME());
                json.put("str_NAME", tLot.getLgFAMILLEID().getStrNAME());
                json.put("lg_GROSSISTE_ID", tLot.getLgFAMILLEID().getLgGROSSISTEID().getStrLIBELLE());
                json.put("int_CIP", tLot.getLgFAMILLEID().getIntCIP());
//                json.put("int_NUMBER", tLot.getIntNUMBER() - tLot.getIntQTYVENDUE());
                json.put("int_NUMBER", tLot.getIntNUMBER());
                json.put("int_NUM_LOT", tLot.getIntNUMLOT());
                json.put("dt_PEREMPTION", DATEFORMAT.format(tLot.getDtPEREMPTION()));
                if (LocalDate.parse(DATEFORMATYYYY.format(tLot.getDtPEREMPTION()), DateTimeFormatter.ofPattern("yyyy-MM-dd")).isBefore(now) || LocalDate.parse(DATEFORMATYYYY.format(tLot.getDtPEREMPTION()), DateTimeFormatter.ofPattern("yyyy-MM-dd")).isEqual(now)) {
                    json.put("str_STATUT", "Périmé");
                    json.put("etat", "1");
                } else {
                    json.put("str_STATUT", "En cours de péremption");
                    json.put("etat", "0");
                }
                array.put(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public int getPerimesCount(String search_value, String str_TYPE_TRANSACTION, String str_TRI) {
        Long count = 0l;
        EntityManager em = this.getEntityManager();
        try {
            LocalDate now = LocalDate.now();

            TParameters OTParameters = em.getReference(TParameters.class, "KEY_MONTH_PERIME");
            int int_NUMBER = 0;
            if (OTParameters != null) {
                int_NUMBER = Integer.parseInt(OTParameters.getStrVALUE());
            }
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TLot> root = cq.from(TLot.class);
            Join<TLot, TFamille> pr = root.join("lgFAMILLEID", JoinType.INNER);
            Join<TFamille, TFamilleStock> st = pr.join("tFamilleStockCollection", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            if (!"".equals(search_value)) {
                criteria = cb.and(criteria, cb.or(cb.like(pr.get(TFamille_.intCIP), search_value + "%"), cb.like(pr.get(TFamille_.strNAME), search_value + "%")));
            }
            Predicate gt = cb.greaterThan(st.get(TFamilleStock_.intNUMBERAVAILABLE), 0);
            criteria = cb.and(criteria, gt);
            criteria = cb.and(criteria, cb.equal(st.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), "1"));
            switch (str_TYPE_TRANSACTION) {
                case Parameter.KEY_PARAM_PERIME:
                    criteria = cb.and(criteria, cb.equal(root.get(TLot_.strSTATUT), commonparameter.statut_perime));

                    break;
                case Parameter.KEY_PERIMER_ENCOURS:
                    Predicate ge = cb.greaterThan(root.get(TLot_.dtPEREMPTION), java.sql.Date.valueOf(now));
                    criteria = cb.and(criteria, cb.equal(root.get(TLot_.strSTATUT), Parameter.STATUT_ENCOURS_PEREMPTION));
                    criteria = cb.and(criteria, ge);
                    break;
                default:
                    criteria = cb.and(criteria, cb.or(cb.equal(root.get(TLot_.strSTATUT), Parameter.STATUT_ENCOURS_PEREMPTION), cb.equal(root.get(TLot_.strSTATUT), commonparameter.statut_perime)));

                    break;
            }
            criteria = cb.and(criteria, cb.equal(pr.get(TFamille_.strSTATUT), commonparameter.statut_enable));
            cq.select(cb.count(root));
            cq.where(criteria);

            Query q = em.createQuery(cq);

            count = (Long) q.getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count.intValue();
    }

    public JSONObject getListePerimesReport(String search_value, String str_TYPE_TRANSACTION, String str_TRI) {
        JSONArray array = new JSONArray();
        JSONObject _json = new JSONObject();
        EntityManager em = this.getEntityManager();
        try {
            LocalDate now = LocalDate.now();

            TParameters OTParameters = em.getReference(TParameters.class, "KEY_MONTH_PERIME");
            int int_NUMBER = 0;
            if (OTParameters != null) {
                int_NUMBER = Integer.parseInt(OTParameters.getStrVALUE());
            }
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TLot> cq = cb.createQuery(TLot.class);
            Root<TLot> root = cq.from(TLot.class);
            Join<TLot, TFamille> pr = root.join("lgFAMILLEID", JoinType.INNER);
            Join<TFamille, TFamilleStock> st = pr.join("tFamilleStockCollection", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            if (!"".equals(search_value)) {
                criteria = cb.and(criteria, cb.or(cb.like(pr.get(TFamille_.intCIP), search_value + "%"), cb.like(pr.get(TFamille_.strNAME), search_value + "%")));
            }
            Predicate gt = cb.greaterThan(st.get(TFamilleStock_.intNUMBERAVAILABLE), 0);
            criteria = cb.and(criteria, gt);
            criteria = cb.and(criteria, cb.equal(st.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), "1"));
            criteria = cb.and(criteria, cb.equal(pr.get(TFamille_.strSTATUT), commonparameter.statut_enable));
            switch (str_TYPE_TRANSACTION) {
                case Parameter.KEY_PARAM_PERIME:

                    criteria = cb.and(criteria, cb.equal(root.get(TLot_.strSTATUT), commonparameter.statut_perime));

                    break;
                case Parameter.KEY_PERIMER_ENCOURS:

                    Predicate ge = cb.greaterThan(root.get(TLot_.dtPEREMPTION), java.sql.Date.valueOf(now));
                    criteria = cb.and(criteria, cb.equal(root.get(TLot_.strSTATUT), Parameter.STATUT_ENCOURS_PEREMPTION));
                    criteria = cb.and(criteria, ge);
                    break;
                default:
                    criteria = cb.and(criteria, cb.or(cb.equal(root.get(TLot_.strSTATUT), Parameter.STATUT_ENCOURS_PEREMPTION), cb.equal(root.get(TLot_.strSTATUT), commonparameter.statut_perime)));
                    break;
            }

            switch (str_TRI) {
                case "str_CODE_EMPLACEMENT":
                    cq.select(root).orderBy(cb.asc(pr.get("lgZONEGEOID").get("strCODE")), cb.asc(pr.get(TFamille_.strNAME)));
                    break;
                case "str_CODE_FAMILLE":
                    cq.select(root).orderBy(cb.asc(pr.get("lgFAMILLEARTICLEID").get("strCODEFAMILLE")), cb.asc(pr.get(TFamille_.strNAME)));
                    break;
                case "str_CODE_GROSSISTE":
                    cq.select(root).orderBy(cb.asc(pr.get("lgGROSSISTEID").get("strCODE")), cb.asc(pr.get(TFamille_.strNAME)));
                    break;
                default:
                    cq.select(root).orderBy(cb.asc(pr.get(TFamille_.strNAME)));
                    break;
            }
            cq.where(criteria/*, pu2, pu*/);
            Query q = em.createQuery(cq);

            List<TLot> list = q.getResultList();
            list.stream().map(tLot -> {
                JSONObject json = new JSONObject();
                json.put("lg_LOT_ID", tLot.getLgLOTID());
                json.put("lg_FAMILLE_ID", tLot.getLgUSERID().getStrFIRSTNAME().substring(0, 1) + " " + tLot.getLgUSERID().getStrLASTNAME());
                json.put("str_NAME", tLot.getLgFAMILLEID().getStrNAME());
                json.put("lg_GROSSISTE_ID", tLot.getLgFAMILLEID().getLgGROSSISTEID().getStrLIBELLE());
                json.put("int_CIP", tLot.getLgFAMILLEID().getIntCIP());
                json.put("int_NUMBER", tLot.getIntNUMBER());
                json.put("int_NUM_LOT", tLot.getIntNUMLOT());
                json.put("dt_PEREMPTION", DATEFORMAT.format(tLot.getDtPEREMPTION()));
                if (LocalDate.parse(DATEFORMATYYYY.format(tLot.getDtPEREMPTION()), DateTimeFormatter.ofPattern("yyyy-MM-dd")).isBefore(now)) {
                    json.put("str_STATUT", "Périmé");

                } else {
                    json.put("str_STATUT", "En cours de péremption");

                }
                return json;
            }).forEachOrdered(json -> {
                array.put(json);
            });
            _json.put("root", array);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _json;
    }

    public Map<String, LinkedHashSet<TFacture>> generateInvoices(List<TTiersPayant> payants, String dt_start, String dt_end, TUser us) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Map<String, LinkedHashSet<TFacture>> grfact = new HashMap<>();
        LinkedHashSet<TFacture> factures = new LinkedHashSet<>();
        Map<TGroupeTierspayant, List<TTiersPayant>> lgGRPgroupe = payants.stream().collect(Collectors.groupingBy(s -> s.getLgGROUPEID()));
        lgGRPgroupe.forEach((g, u) -> {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }
            TParameters OParameters = em.find(TParameters.class, Parameter.KEY_CODE_FACTURE);
            String CODEFACTURE = OParameters.getStrVALUE();
              OParameters.setStrVALUE((Integer.valueOf(CODEFACTURE) + 1) + "");
             boolean numerationFacture=getParametreFacturation();
            final String codeFacture=numerationFacture?LocalDate.now().format(DateTimeFormatter.ofPattern("yy")).concat("_").concat(CODEFACTURE):CODEFACTURE;
            em.merge(OParameters);
            u.forEach((p) -> {
                List<TPreenregistrementCompteClientTiersPayent> finalTp = this.getGroupeBons(true, dt_start, dt_end, -1, -1, p.getLgTIERSPAYANTID(), g.getLgGROUPEID(), "");
                switch (getCase(p)) {
                    case 1:
                        long montantFact = finalTp.stream().mapToLong((_qty) -> {
                            return _qty.getIntPRICE();
                        }).sum();

                        if (p.getIntMONTANTFAC() < montantFact) {
                            Integer virtualAmont = 0;
                            int myCount = 0;
                            int volatilecount = 0;

                            for (TPreenregistrementCompteClientTiersPayent op : finalTp) {

                                if (virtualAmont > p.getIntMONTANTFAC()) {
                                    try {
                                        if (myCount < finalTp.size()) {

                                            TFacture of = this.createInvoices(finalTp.subList(volatilecount, myCount - 1), date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, us);

                                            factures.add(of);

                                            createGroupeFacture(g, of, codeFacture, em);

                                        } else if (myCount == (finalTp.size() - 1)) {

                                            TFacture of = this.createInvoices(finalTp.subList(volatilecount, finalTp.size()), date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, us);

                                            factures.add(of);
                                            createGroupeFacture(g, of, codeFacture, em);
                                        }

                                        volatilecount = (myCount - 1);
                                        virtualAmont = (finalTp.get(volatilecount).getIntPRICE()) + (finalTp.get(myCount).getIntPRICE());

                                    } catch (ParseException ex) {

                                    }
                                } else if ((virtualAmont <= p.getIntMONTANTFAC()) && (myCount == (finalTp.size() - 1))) {
                                    try {

                                        TFacture of = this.createInvoices(finalTp.subList(volatilecount, finalTp.size()), date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, us);

                                        factures.add(of);

                                        createGroupeFacture(g, of, codeFacture, em);
                                    } catch (ParseException ex) {

                                    }

                                }
                                virtualAmont += op.getIntPRICE();
                                myCount++;

                            }

                        } else {
                            try {
                                TFacture of = this.createInvoices(finalTp, date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, us);
                                factures.add(of);
                                createGroupeFacture(g, of, codeFacture, em);
                            } catch (ParseException ex) {

                            }

                        }

                        break;
                    case 2:

                        int count = p.getIntNBREBONS();
                        int decrementCount = finalTp.size();
                        int _count = p.getIntNBREBONS();
                        int virtualCnt = 0;
                        Date dtstart = null;
                        Date dtend = null;
                        try {
                            dtstart = date.formatterMysqlShort.parse(dt_start);
                            dtend = date.formatterMysqlShort.parse(dt_end);
                        } catch (Exception e) {
                        }

                        if (finalTp.size() > _count) {
                            while (decrementCount > 0) {

                                if (count < finalTp.size()) {
                                    TFacture of = this.createInvoices(finalTp.subList(virtualCnt, count), dtstart, dtend, p, em, us);

                                    factures.add(of);
                                    createGroupeFacture(g, of, codeFacture, em);

                                } else {
                                    TFacture of = this.createInvoices(finalTp.subList(virtualCnt, finalTp.size()), dtstart, dtend, p, em, us);

                                    factures.add(of);
                                    createGroupeFacture(g, of, codeFacture, em);

                                }
                                virtualCnt += _count;
                                count += _count;
                                decrementCount -= (_count);
                            }

                        } else {

                            TFacture of = this.createInvoices(finalTp.subList(virtualCnt, finalTp.size()), dtstart, dtend, p, em, us);

                            factures.add(of);
                            createGroupeFacture(g, of, codeFacture, em);

                        }
                        break;
                    default:

                        if (finalTp.size() > 0) {
                            try {
                                TFacture of = this.createInvoices(finalTp, date.formatterMysqlShort.parse(dt_start), date.formatterMysqlShort.parse(dt_end), p, em, us);

                                factures.add(of);
                                createGroupeFacture(g, of, codeFacture, em);
                            } catch (Exception e) {
                            }
                        }
                        break;
                }
            });
            grfact.put(codeFacture, factures);
            em.getTransaction().commit();
        });

        return grfact;
    }

    public List<TFacture> getGroupeInvoiceDetails(String codeFacture) {
        EntityManager em;
        try {
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TFacture> cq = cb.createQuery(TFacture.class);
            Root<TFacture> root = cq.from(TFacture.class);
            Join< TGroupeFactures, TFacture> pr = root.join("tGroupeFacturesList", JoinType.INNER);
            cq.orderBy(cb.asc(pr.get("strCODEFACTURE")));
            cq.where(cb.and(cb.notEqual(root.get(TFacture_.strSTATUT), commonparameter.statut_paid), cb.equal(pr.get("strCODEFACTURE"), codeFacture)));
            Query q = em.createQuery(cq);
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }

    }

}
