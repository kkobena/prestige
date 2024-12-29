package rest.service.impl;

import commonTasks.dto.VenteDTO;
import dal.Caution;
import dal.CautionHistorique;
import dal.Caution_;
import dal.TClient;
import dal.TPreenregistrement;
import dal.TTiersPayant;
import dal.TTiersPayant_;
import dal.TUser;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import rest.service.CautionTiersPayantService;
import rest.service.SessionHelperService;
import rest.service.dto.AddCautionDTO;
import rest.service.dto.CautionDTO;
import rest.service.dto.CautionHistoriqueDTO;
import util.DateUtil;
import util.FunctionUtils;

/**
 *
 * @author koben
 */
@Stateless
public class CautionTiersPayantServiceImpl implements CautionTiersPayantService {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat heureFormat = new SimpleDateFormat("HH:mm");
    private static final Logger LOG = Logger.getLogger(CautionTiersPayantServiceImpl.class.getName());
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @EJB
    private SessionHelperService sessionHelperService;

    @Override
    public void addCaution(AddCautionDTO addCaution) {

        TTiersPayant tTiersPayant = this.em.find(TTiersPayant.class, addCaution.getTiersPayantId());

        Caution caution = tTiersPayant.getCaution();
        int amount = addCaution.getMontant();
        if (Objects.isNull(caution)) {
            createNewCaution(tTiersPayant, amount);
        } else {
            addCautionHistorique(caution, amount);
            caution.setMontant(caution.getMontant() + amount);
            caution.setUpdatedAt(LocalDateTime.now());
            em.merge(caution);
        }

    }

    @Override
    public JSONObject update(AddCautionDTO addCaution) {
        JSONObject json = new JSONObject();
        int amount = addCaution.getMontant();
        if (amount == 0) {
            return json.put("success", false).put("msg", "Le montant 0 n'est pas permis");
        }
        Caution caution = this.em.find(Caution.class, addCaution.getIdCaution());
        int montant = caution.getMontant() + amount;

        if (!canModifyCaution(caution.getMontant(), amount)) {
            return json.put("success", false).put("msg", "Impossible de reduire le montant");

        }

        addCautionHistorique(caution, amount);
        caution.setMontant(montant);
        caution.setUpdatedAt(LocalDateTime.now());
        em.merge(caution);
        return json.put("success", true).put("msg", "Opération effectuée");
    }

    private boolean canModifyCaution(int montant, int newAmount) {
        if (newAmount < 0) {
            return montant >= 0;
        }
        return newAmount < montant;

    }

    private void addCautionHistorique(Caution caution, int amount) {
        CautionHistorique cautionHistorique = new CautionHistorique();
        cautionHistorique.setCaution(caution);
        cautionHistorique.setMontant(amount);
        cautionHistorique.setUser(sessionHelperService.getCurrentUser());
        em.persist(cautionHistorique);
    }

    private void createNewCaution(TTiersPayant tTiersPayant, int amount) {
        if (amount > 0) {
            Caution caution = new Caution();
            caution.setId(tTiersPayant.getLgTIERSPAYANTID());
            caution.setTiersPayant(tTiersPayant);
            caution.setMontant(amount);
            caution.setConso(0);
            caution.setUser(sessionHelperService.getCurrentUser());

            CautionHistorique cautionHistorique = new CautionHistorique();
            cautionHistorique.setCaution(caution);
            cautionHistorique.setUser(caution.getUser());
            cautionHistorique.setMontant(caution.getMontant());
            caution.getHistoriques().add(cautionHistorique);
            em.persist(caution);
        }

    }

    @Override
    public JSONObject supprimerCaution(String idCaution) {
        JSONObject json = new JSONObject();
        Caution caution = this.em.find(Caution.class, idCaution);
        if (caution.getConso() > 0) {
            return json.put("success", false).put("msg", "Impossible de supprimer il y a des ventes liées");
        }
        em.remove(caution);
        return json.put("success", true).put("msg", "Opération effectuée");

    }

    @Override
    public void updateCaution(Caution caution, int saleAmount) {
        caution.setMontant(caution.getMontant() - saleAmount);
        caution.setConso(caution.getConso() + saleAmount);
        caution.setUpdatedAt(LocalDateTime.now());
        em.merge(caution);
    }

    @Override
    public List<CautionDTO> fetch(String tiersPayantId, int start, int limit, boolean all) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Caution> cq = cb.createQuery(Caution.class);
        Root<Caution> root = cq.from(Caution.class);
        cq.select(root).orderBy(cb.desc(root.get(Caution_.updatedAt)));
        List<Predicate> predicates = buidPredicates(cb, root, tiersPayantId);
        cq.where(cb.and(predicates.toArray(Predicate[]::new)));

        TypedQuery<Caution> q = em.createQuery(cq);
        if (!all) {
            q.setFirstResult(start);
            q.setMaxResults(limit);
        }
        return q.getResultStream().map(this::buildFromEntity).collect(Collectors.toList());
    }

    @Override
    public JSONObject fetch(String tiersPayantId, int start, int limit) {
        return FunctionUtils.returnData(fetch(tiersPayantId, start, limit, true), fetchCount(tiersPayantId));
    }

    private List<Predicate> buidPredicates(CriteriaBuilder cb, Root<Caution> root, String tiersPayantId) {
        List<Predicate> predicates = new ArrayList<>();
        if (!StringUtils.isEmpty(tiersPayantId)) {
            predicates.add(cb.equal(root.get(Caution_.tiersPayant).get(TTiersPayant_.lgTIERSPAYANTID), tiersPayantId));
        }

        /*
         * predicates.add(cb.between( cb.function("DATE", Date.class, root.get(Caution_.mvtDate)),
         * java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd)));
         */
        return predicates;
    }

    private long fetchCount(String tiersPayantId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Caution> root = cq.from(Caution.class);
        cq.select(cb.count(root));
        List<Predicate> predicates = buidPredicates(cb, root, tiersPayantId);
        cq.where(cb.and(predicates.toArray(Predicate[]::new)));

        TypedQuery<Long> q = em.createQuery(cq);

        return q.getSingleResult();
    }

    private CautionDTO buildFromEntity(Caution c) {
        TTiersPayant payant = c.getTiersPayant();
        TUser user = c.getUser();
        CautionDTO caution = new CautionDTO();
        caution.setId(c.getId());
        caution.setConso(c.getConso());
        caution.setMontant(c.getMontant());
        caution.setTiersPayantName(payant.getStrNAME());
        caution.setMvtDate(c.getMvtDate().format(DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm")));
        caution.setUpdatedAt(c.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm")));
        caution.setUser(user.getStrFIRSTNAME().concat(" ").concat(user.getStrLASTNAME()));
        caution.setCautionHistoriques(
                c.getHistoriques().stream().map(this::buildCautionHistoriqueFromEntity).collect(Collectors.toList()));
        return caution;

    }

    private CautionHistoriqueDTO buildCautionHistoriqueFromEntity(CautionHistorique c) {
        TUser user = c.getUser();
        CautionHistoriqueDTO caution = new CautionHistoriqueDTO();
        caution.setMontant(c.getMontant());
        caution.setMvtDate(c.getMvtDate().format(DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm")));
        caution.setUser(user.getStrFIRSTNAME().concat(" ").concat(user.getStrLASTNAME()));
        return caution;

    }

    @Override
    public List<VenteDTO> getVentes(String idCaution) {
        TypedQuery<TPreenregistrement> query = em
                .createQuery("SELECT o FROM TPreenregistrement o WHERE o.caution.id=?1", TPreenregistrement.class);
        query.setParameter(1, idCaution);
        return query.getResultStream().map(this::buildVente).collect(Collectors.toList());
    }

    @Override
    public JSONObject getVentesView(String idCaution) {
        return FunctionUtils.returnData(getVentes(idCaution));
    }

    private VenteDTO buildVente(TPreenregistrement tp) {
        VenteDTO vente = new VenteDTO();
        vente.setLgPREENREGISTREMENTID(tp.getLgPREENREGISTREMENTID());
        vente.setStrREF(tp.getStrREF());
        vente.setStrREFTICKET(tp.getStrREFTICKET());
        vente.setIntPRICE(tp.getIntPRICE());
        vente.setIntPRICEREMISE(tp.getIntPRICEREMISE());
        vente.setStrTYPEVENTE(tp.getStrTYPEVENTE());
        vente.setIntCUSTPART(tp.getIntCUSTPART());
        vente.setDtUPDATED(dateFormat.format(tp.getDtUPDATED()));
        vente.setHeure(heureFormat.format(tp.getDtUPDATED()));
        vente.setDtCREATED(dateFormat.format(tp.getDtUPDATED()));
        vente.setHEUREVENTE(heureFormat.format(tp.getDtUPDATED()));
        vente.setStrSTATUT(tp.getStrSTATUT());
        vente.setAvoir(tp.getBISAVOIR());
        vente.setCancel(tp.getBISCANCEL());
        vente.setSansbon(tp.getBWITHOUTBON());
        vente.setLgTYPEVENTEID(tp.getLgTYPEVENTEID().getLgTYPEVENTEID());
        vente.setCopy(tp.getCopy());
        vente.setMvdate(DateUtil.convertDateToDD_MM_YYYY(tp.getDtUPDATED()));
        TClient cl = tp.getClient();
        if (cl != null) {
            vente.setClientFullName(cl.getStrFIRSTNAME() + " " + cl.getStrLASTNAME());
        }
        try {

            vente.setDateAnnulation(dateFormat.format(tp.getDtANNULER()));
            vente.setHeureAnnulation(heureFormat.format(tp.getDtANNULER()));
        } catch (Exception e) {
        }
        return vente;

    }

    private CautionHistoriqueDTO buildHistoriques(CautionHistorique cautionHistorique) {
        CautionHistoriqueDTO historique = new CautionHistoriqueDTO();
        historique.setId(cautionHistorique.getId());
        historique.setMontant(cautionHistorique.getMontant());
        historique.setMvtDate(cautionHistorique.getMvtDate().format(DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm")));
        TUser user = cautionHistorique.getUser();
        historique.setUser(user.getStrFIRSTNAME() + " " + user.getStrLASTNAME());
        return historique;
    }

    @Override
    public List<CautionHistoriqueDTO> getHistoriques(String idCaution) {
        TypedQuery<CautionHistorique> query = em.createQuery("SELECT o FROM CautionHistorique o WHERE o.caution.id=?1",
                CautionHistorique.class);
        query.setParameter(1, idCaution);
        return query.getResultStream().map(this::buildHistoriques).collect(Collectors.toList());
    }

    @Override
    public JSONObject getHistoriquesView(String idCaution) {
        return FunctionUtils.returnData(getHistoriques(idCaution));
    }
}
