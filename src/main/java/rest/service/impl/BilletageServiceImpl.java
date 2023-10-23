package rest.service.impl;

import dal.TBilletage;
import dal.TBilletageDetails;
import dal.TCaisse;
import dal.TCoffreCaisse;
import dal.TResumeCaisse;
import dal.TUser;
import dal.enumeration.TypeLog;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import rest.service.BilletageService;
import rest.service.LogService;
import rest.service.dto.BilletageDTO;
import rest.service.dto.CoffreCaisseDTO;
import rest.service.dto.UserCaisseDataDTO;
import rest.service.exception.CaisseNotFoundExeception;
import rest.service.exception.CaisseUsingExeception;
import rest.service.exception.CashFundNotFoundExeception;
import util.Constant;
import util.DateUtil;

/**
 *
 * @author koben
 */
@Stateless
public class BilletageServiceImpl implements BilletageService {

    private static final String SOLDE_SQL = "SELECT SUM(m.`montantRegle`) AS montantRegle FROM t_preenregistrement p,mvttransaction m,t_user u WHERE p.`lg_USER_CAISSIER_ID`=u.`lg_USER_ID` AND p.`lg_PREENREGISTREMENT_ID`=m.vente_id"
            + " AND p.`str_STATUT`='is_Closed' AND p.`lg_TYPE_VENTE_ID` <> ?1 AND  p.`lg_USER_CAISSIER_ID`= ?2 AND p.`dt_UPDATED` BETWEEN ?3 AND ?4 AND m.`typeReglementId` =?5 ";
    private static final String SOLDE_SQL_OTHERS = "SELECT SUM(m.int_AMOUNT) AS montant  FROM t_mvt_caisse m,t_user u WHERE m.`lg_USER_ID`=u.`lg_USER_ID` AND u.`lg_USER_ID`=?1 AND m.`dt_CREATED` BETWEEN ?2 AND ?3 AND  m.`lg_MODE_REGLEMENT_ID`=?4 AND m.`lg_TYPE_MVT_CAISSE_ID` <> '1'  ";

    private static final Logger LOG = Logger.getLogger(BilletageServiceImpl.class.getName());
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @EJB
    private LogService logService;

    private TCaisse getUserCaisse(TUser user) {
        try {
            TypedQuery<TCaisse> q = em
                    .createQuery("SELECT o FROM TCaisse o WHERE o.lgUSERID.lgUSERID=?1", TCaisse.class)
                    .setParameter(1, user.getLgUSERID());
            q.setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
            // return createCaisse(user,0.0);
        }

    }

    private TCaisse createCaisse(TUser user, double amount) {
        TCaisse oCaisse = new TCaisse();
        oCaisse.setLgUSERID(user);
        oCaisse.setIntSOLDE(amount);
        oCaisse.setDtCREATED(new Date());
        oCaisse.setDtUPDATED(oCaisse.getDtCREATED());
        oCaisse.setLgUPDATEDBY(user.getLgUSERID());
        oCaisse.setLgCREATEDBY(user.getLgUSERID());
        oCaisse.setLgCAISSEID(UUID.randomUUID().toString());

        this.em.persist(oCaisse);
        return oCaisse;
    }

    private Optional<TResumeCaisse> getUserResumeCaisse(LocalDateTime dtStart, LocalDateTime dtEnd, TUser user,
            String status) {

        try {
            TypedQuery<TResumeCaisse> q = this.em.createQuery(
                    "SELECT o FROM TResumeCaisse o WHERE o.lgUSERID.lgUSERID=?1 AND  o.dtCREATED BETWEEN ?2 AND ?3 AND o.strSTATUT=?4",
                    TResumeCaisse.class).setParameter(1, user.getLgUSERID())
                    .setParameter(2, Timestamp.valueOf(dtStart), TemporalType.TIMESTAMP)
                    .setParameter(3, Timestamp.valueOf(dtEnd), TemporalType.TIMESTAMP).setParameter(4, status);

            return Optional.ofNullable(q.getSingleResult());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Optional.empty();

        }
    }

    private Optional<TResumeCaisse> getUserResumeCaisse(TUser user, String status) {

        try {
            TypedQuery<TResumeCaisse> q = this.em.createQuery(
                    "SELECT o FROM TResumeCaisse o WHERE o.lgUSERID.lgUSERID=?1 AND o.strSTATUT=?2 ORDER BY o.dtCREATED DESC",
                    TResumeCaisse.class).setParameter(1, user.getLgUSERID()).setParameter(2, status);
            q.setMaxResults(1);
            return Optional.ofNullable(q.getSingleResult());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Optional.empty();

        }
    }

    private Pair<LocalDateTime, LocalDateTime> buildDateParam(String dtStart, String dtEnd, String hStart,
            String hEnd) {
        LocalDate dateStart = LocalDate.now();

        if (StringUtils.isNotEmpty(dtStart)) {
            dateStart = LocalDate.parse(dtStart);
        }
        LocalDateTime start = dateStart.atStartOfDay();
        LocalDate dateEnd = dateStart;
        if (StringUtils.isNotEmpty(dtEnd)) {
            dateEnd = LocalDate.parse(dtEnd);
        }
        LocalDateTime end = dateEnd.atTime(LocalTime.MAX);
        if (StringUtils.isNotEmpty(hStart)) {
            start = dateStart.atTime(LocalTime.parse(hStart));
        }
        if (StringUtils.isNotEmpty(hEnd)) {
            end = dateStart.atTime(LocalTime.parse(hEnd));
        }
        return Pair.of(start, end);
    }

    @Override
    public UserCaisseDataDTO getUserCaisseData(String dtStart, String dtEnd, String hStart, String hEnd, TUser user) {

        TCaisse caisse = getUserCaisse(user);
        if (caisse == null) {
            caisse = createCaisse(user, 0);
        }
        Optional<TResumeCaisse> o = getUserResumeCaisse(user, Constant.STATUT_IS_USING);
        if (o.isEmpty()) {
            throw new CaisseNotFoundExeception(
                    "Resumé de caisse not found for user " + user.getStrFIRSTNAME() + " " + user.getStrLASTNAME());
        }
        TResumeCaisse resumeCaisse = o.get();
        return UserCaisseDataDTO.builder().userId(user.getLgUSERID()).caisseId(caisse.getLgCAISSEID())
                .solde(caisse.getIntSOLDE().longValue())
                .cashFund(Objects.nonNull(resumeCaisse.getIntSOLDEMATIN()) ? resumeCaisse.getIntSOLDEMATIN() : 0)
                .display(true).userFullName(user.getStrFIRSTNAME() + " " + user.getStrLASTNAME())
                .createAt(DateUtil.convertDateToDD_MM_YYYY_HH_mm(caisse.getDtCREATED()))
                .updateAt(DateUtil.convertDateToDD_MM_YYYY_HH_mm(caisse.getDtUPDATED()))
                .resumeCaisseId(resumeCaisse.getLdCAISSEID()).build();
    }

    private TCoffreCaisse getCoffreCaisse(LocalDateTime dtStart, LocalDateTime dtEnd, TUser user, String status) {
        try {
            TypedQuery<TCoffreCaisse> q = this.em.createQuery(
                    "SELECT o FROM TCoffreCaisse o WHERE o.lgUSERID.lgUSERID=?1 AND  o.dtCREATED BETWEEN ?2 AND ?3 AND o.strSTATUT=?4",
                    TCoffreCaisse.class).setParameter(1, user.getLgUSERID())
                    .setParameter(2, Timestamp.valueOf(dtStart), TemporalType.TIMESTAMP)
                    .setParameter(3, Timestamp.valueOf(dtEnd), TemporalType.TIMESTAMP).setParameter(4, status);

            return q.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public CoffreCaisseDTO getUserCoffreCaisseDTO(String dtStart, String dtEnd, String hStart, String hEnd, TUser user)
            throws CashFundNotFoundExeception {
        Pair<LocalDateTime, LocalDateTime> pair = buildDateParam(dtStart, dtEnd, hStart, hEnd);
        LocalDateTime start = pair.getLeft();
        LocalDateTime end = pair.getRight();
        TCoffreCaisse caisse = getCoffreCaisse(start, end, user, Constant.STATUT_IS_WAITING_VALIDATION);
        if (Objects.isNull(caisse)) {
            throw new CashFundNotFoundExeception("Not cash fund");
        }
        TUser createdBy = em.find(TUser.class, caisse.getLdCREATEDBY());
        return CoffreCaisseDTO.builder().userId(user.getLgUSERID()).id(caisse.getIdCoffreCaisse())
                .amount(caisse.getIntAMOUNT().intValue()).display(true)
                .userFullName(user.getStrFIRSTNAME() + " " + user.getStrLASTNAME())
                .createdByFullName(createdBy.getStrFIRSTNAME() + " " + createdBy.getStrLASTNAME())
                .createAt(DateUtil.convertDateToDD_MM_YYYY_HH_mm(caisse.getDtCREATED()))
                .updateAt(DateUtil.convertDateToDD_MM_YYYY_HH_mm(caisse.getDtUPDATED())).statut(caisse.getStrSTATUT())
                .build();

    }

    @Override
    public void cloturerCaisse(BilletageDTO billetage, TUser user) throws CaisseUsingExeception {
        TResumeCaisse oTResumeCaisse = this.em.find(TResumeCaisse.class, billetage.getResumeCaisseId());
        if (oTResumeCaisse == null) {
            throw new CaisseUsingExeception("Erreur de cloture de la caisse [" + billetage.getResumeCaisseId() + "]");
        }
        if (oTResumeCaisse.getStrSTATUT().equals(Constant.STATUT_IS_PROGRESS)) {
            throw new CaisseUsingExeception("Impossible de cloturer cette caisse [" + billetage.getResumeCaisseId()
                    + "]. Elle est déjà fermée");
        }
        double amount = caisseAmount(oTResumeCaisse) + caisseAmountOtherMvts(oTResumeCaisse);
        TCaisse caisse = getUserCaisse(user);
        Date now = new Date();
        if (caisse == null) {
            createCaisse(user, amount);
        } else {
            caisse.setIntSOLDE(amount);
            caisse.setDtUPDATED(now);
            caisse.setLgUPDATEDBY(user.getLgUSERID());
            this.em.merge(caisse);
        }

        oTResumeCaisse.setLgUPDATEDBY(user.getLgUSERID());
        oTResumeCaisse.setIntSOLDESOIR((int) amount);
        oTResumeCaisse.setStrSTATUT(Constant.STATUT_IS_PROGRESS);
        oTResumeCaisse.setDtUPDATED(now);
        doBilletage(oTResumeCaisse, billetage, user);
        String description = "Cloture de la caisse de  " + user.getStrFIRSTNAME() + " " + user.getStrLASTNAME()
                + " avec succès avec un montant de: " + amount + " Billetage " + billetage;
        logService.updateItem(user, oTResumeCaisse.getLdCAISSEID(), description, TypeLog.CLOTURE_CAISSE,
                oTResumeCaisse);
    }

    private TBilletage createBilletage(TResumeCaisse caisse, TUser user, double billetageAmount) {
        TBilletage billetage = new TBilletage();
        billetage.setLgBILLETAGEID(UUID.randomUUID().toString());
        billetage.setLdCAISSEID(caisse.getLdCAISSEID());
        billetage.setIntAMOUNT(billetageAmount);
        billetage.setLgUSERID(user);
        billetage.setDtCREATED(new Date());
        billetage.setDtUPDATED(billetage.getDtCREATED());
        this.em.persist(billetage);
        return billetage;
    }

    private void doBilletage(TResumeCaisse caisse, BilletageDTO billetageDTO, TUser user) {
        int total = billetageDTO.getTotal();
        TBilletage billetage = createBilletage(caisse, user, total);
        createBilletageItem(billetage, billetageDTO);
    }

    private void createBilletageItem(TBilletage billetage, BilletageDTO billetageDTO) {

        TBilletageDetails billetageDetails = new TBilletageDetails();
        billetageDetails.setLgBILLETAGEDETAILSID(UUID.randomUUID().toString());
        billetageDetails.setLgBILLETAGEID(billetage);
        billetageDetails.setIntNBDIXMIL(billetageDTO.getDixMille());
        billetageDetails.setIntNBCINQMIL(billetageDTO.getCinqMille());
        billetageDetails.setIntNBDEUXMIL(billetageDTO.getDeuxMille());
        billetageDetails.setIntNBMIL(billetageDTO.getMille());
        billetageDetails.setIntNBCINQCENT(billetageDTO.getCinqCent());
        billetageDetails.setIntAUTRE(billetageDTO.getAutre());
        billetageDetails.setDtCREATED(new Date());
        billetageDetails.setLgCREATEDBY(billetage.getLgUSERID().getStrLOGIN());
        this.em.persist(billetageDetails);
    }

    private double caisseAmount(TResumeCaisse caisse) {

        try {
            Query query = this.em.createNativeQuery(SOLDE_SQL).setParameter(1, Constant.DEPOT_EXTENSION)
                    .setParameter(2, caisse.getLgUSERID().getLgUSERID())
                    .setParameter(3, caisse.getDtCREATED(), TemporalType.TIMESTAMP)
                    .setParameter(4, new Date(), TemporalType.TIMESTAMP).setParameter(5, Constant.MODE_ESP);
            Object result = query.getSingleResult();
            if (result != null) {
                return ((BigDecimal) result).doubleValue();
            }

            return 0;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;

        }

    }

    private double caisseAmountOtherMvts(TResumeCaisse caisse) {
        try {
            Query query = this.em.createNativeQuery(SOLDE_SQL_OTHERS)
                    .setParameter(1, caisse.getLgUSERID().getLgUSERID())
                    .setParameter(2, caisse.getDtCREATED(), TemporalType.TIMESTAMP)
                    .setParameter(3, new Date(), TemporalType.TIMESTAMP).setParameter(4, Constant.MODE_ESP);
            Object result = query.getSingleResult();
            if (result != null) {
                return (Double) result;
            }
            return 0;

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }

    }
}
