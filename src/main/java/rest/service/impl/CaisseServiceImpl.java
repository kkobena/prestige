/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import bll.common.Parameter;
import commonTasks.dto.BalanceDTO;
import commonTasks.dto.CaisseParamsDTO;
import commonTasks.dto.GenericDTO;
import commonTasks.dto.MvtCaisseDTO;
import commonTasks.dto.Params;
import commonTasks.dto.RapportDTO;
import commonTasks.dto.ResumeCaisseDTO;
import commonTasks.dto.SumCaisseDTO;
import commonTasks.dto.SummaryDTO;
import commonTasks.dto.TableauBaordPhDTO;
import commonTasks.dto.TableauBaordSummary;
import commonTasks.dto.VenteDetailsDTO;
import commonTasks.dto.VisualisationCaisseDTO;
import dal.AnnulationRecette;
import dal.AnnulationRecette_;
import dal.AnnulationSnapshot;
import dal.AnnulationSnapshot_;
import dal.Groupefournisseur;
import dal.HMvtProduit;
import dal.MvtTransaction;
import dal.MvtTransaction_;
import dal.Notification;
import dal.TBilletage;
import dal.TBilletageDetails;
import dal.TBonLivraison;
import dal.TBonLivraison_;
import dal.TCaisse;
import dal.TCashTransaction;
import dal.TCashTransaction_;
import dal.TClient;
import dal.TCoffreCaisse;
import dal.TEmplacement;
import dal.TEmplacement_;
import dal.TFamille_;
import dal.TModeReglement;
import dal.TMotifReglement;
import dal.TMvtCaisse;
import dal.TOfficine;
import dal.TParameters;
import dal.TPreenregistrement;
import dal.TPreenregistrementDetail;
import dal.TPreenregistrementDetail_;
import dal.TPreenregistrement_;
import dal.TReglement;
import dal.TResumeCaisse;
import dal.TResumeCaisse_;
import dal.TTiersPayant;
import dal.TTypeMvtCaisse;
import dal.TTypeMvtCaisse_;
import dal.TTypeReglement;
import dal.TTypeReglement_;
import dal.TUser;
import dal.TUser_;
import dal.Typemvtproduit;
import dal.enumeration.Canal;
import dal.enumeration.CategorieTypeMvt;
import dal.enumeration.CategoryTransaction;
import dal.enumeration.TypeLog;
import dal.enumeration.TypeNotification;
import dal.enumeration.TypeTransaction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.CaisseService;
import rest.service.LogService;
import rest.service.NotificationService;
import rest.service.TransactionService;
import toolkits.parameters.commonparameter;
import util.DateConverter;

/**
 *
 * @author Kobena
 */
@Stateless
public class CaisseServiceImpl implements CaisseService {

    private static final Logger LOG = Logger.getLogger(CaisseServiceImpl.class.getName());
    @EJB
    TransactionService transactionService;
    @EJB
    LogService logService;
    @EJB
    NotificationService notificationService;
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    public boolean checkParameterByKey(String key) {
        try {
            TParameters parameters = getEntityManager().find(TParameters.class, key);
            return (Integer.parseInt(parameters.getStrVALUE().trim()) == 1);
        } catch (Exception e) {
            return false;
        }
    }

    public void createNotification(String msg, TypeNotification typeNotification, TUser user) {
        try {
            notificationService.save(
                    new Notification().canal(Canal.SMS).typeNotification(typeNotification).message(msg).addUser(user));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public JSONObject donneeCaisses(CaisseParamsDTO caisseParams, boolean all) {
        JSONObject json = new JSONObject();
        try {
            Long count = findAllsTransaction(caisseParams);
            if (count == 0) {
                return json.put("total", 0).put("data", new JSONArray());
            }
            SumCaisseDTO caisse = cumul(caisseParams, all);
            List<SumCaisseDTO> os = caisse.getSummary();
            List<VisualisationCaisseDTO> data = caisse.getCaisses();
            json.put("total", count).put("data", new JSONArray(data)).put("metaData", new JSONArray(os));
        } catch (JSONException e) {
            LOG.log(Level.SEVERE, null, e);

        }
        return json;
    }

    @Override
    public SumCaisseDTO cumul(CaisseParamsDTO caisseParams, boolean all) {
        SumCaisseDTO dTO = new SumCaisseDTO();
        try {
            List<SumCaisseDTO> os = new ArrayList<>();
            List<VisualisationCaisseDTO> caisses;
            List<VisualisationCaisseDTO> sum;
            if (!all) {
                caisses = findAllsTransaction(caisseParams, all);
                dTO.setCaisses(caisses);
                sum = findAllsTransaction(caisseParams, true);
            } else {
                caisses = findAllsTransaction(caisseParams, all);
                dTO.setCaisses(caisses);
                sum = caisses;
            }

            Map<String, List<VisualisationCaisseDTO>> map = sum.stream()
                    .collect(Collectors.groupingBy(s -> s.getModeRegle()));
            LongAdder montantEspec = new LongAdder();
            LongAdder montantCredit = new LongAdder();
            LongAdder montantAnnulation = new LongAdder();
            LongAdder montantSortie = new LongAdder();
            LongAdder montantEntree = new LongAdder();
            LongAdder montantVir = new LongAdder();
            LongAdder montantCb = new LongAdder();
            LongAdder montantcheque = new LongAdder();
            LongAdder montantCaisse = new LongAdder();
            LongAdder montantFondCaisse = new LongAdder();
            LongAdder montantMobileMoney = new LongAdder();

            map.forEach((k, v) -> {
                switch (k) {
                case DateConverter.MODE_ESP:
                    v.forEach(b -> {
                        montantCaisse.add(b.getMontantCaisse());
                        if (b.getTypeMvt().equals(DateConverter.MVT_FOND_CAISSE)) {
                            montantFondCaisse.add(b.getMontant());
                        } else {
                            montantEspec.add(b.getMontant());

                            if (b.getTypeMvt().equals(DateConverter.MVT_REGLE_VO)
                                    || b.getTypeMvt().equals(DateConverter.MVT_REGLE_VNO)) {
                                montantCredit.add(b.getMontantCredit());
                                if (b.getMontant() < 0) {
                                    montantAnnulation.add(b.getMontant());
                                }
                            } else {
                                if (b.getMontant() < 0) {
                                    montantSortie.add(b.getMontant());
                                } else {
                                    montantEntree.add(b.getMontant());
                                }
                            }
                        }

                    });
                    break;
                case DateConverter.MODE_VIREMENT:
                    v.forEach(b -> {
                        montantVir.add(b.getMontant());
                        if (b.getTypeMvt().equals(DateConverter.MVT_REGLE_VO)
                                || b.getTypeMvt().equals(DateConverter.MVT_REGLE_VNO)) {
                            montantCredit.add(b.getMontantCredit());
                            if (b.getMontant() < 0) {
                                montantAnnulation.add(b.getMontant());
                            }
                        } else {
                            if (b.getMontant() < 0) {
                                montantSortie.add(b.getMontant());
                            } else {
                                montantEntree.add(b.getMontant());
                            }
                        }

                    });
                    break;
                case DateConverter.MODE_CB:
                    v.forEach(b -> {
                        montantCb.add(b.getMontant());
                        if (b.getTypeMvt().equals(DateConverter.MVT_REGLE_VO)
                                || b.getTypeMvt().equals(DateConverter.MVT_REGLE_VNO)) {
                            montantCredit.add(b.getMontantCredit());
                            if (b.getMontant() < 0) {
                                montantAnnulation.add(b.getMontant());
                            }
                        } else {
                            if (b.getMontant() < 0) {
                                montantSortie.add(b.getMontant());
                            } else {
                                montantEntree.add(b.getMontant());
                            }
                        }

                    });
                    break;
                case DateConverter.MODE_CHEQUE:
                    v.forEach(b -> {
                        montantcheque.add(b.getMontant());
                        if (b.getTypeMvt().equals(DateConverter.MVT_REGLE_VO)
                                || b.getTypeMvt().equals(DateConverter.MVT_REGLE_VNO)) {
                            montantCredit.add(b.getMontantCredit());
                            if (b.getMontant() < 0) {
                                montantAnnulation.add(b.getMontant());
                            }
                        } else {
                            if (b.getMontant() < 0) {
                                montantSortie.add(b.getMontant());
                            } else {
                                montantEntree.add(b.getMontant());
                            }
                        }
                    });
                    break;
                case DateConverter.MODE_MTN:
                case DateConverter.MODE_MOOV:
                case DateConverter.TYPE_REGLEMENT_ORANGE:
                case DateConverter.MODE_WAVE:
                    v.forEach(b -> {
                        montantMobileMoney.add(b.getMontant());
                        if (b.getTypeMvt().equals(DateConverter.MVT_REGLE_VO)
                                || b.getTypeMvt().equals(DateConverter.MVT_REGLE_VNO)) {
                            montantCredit.add(b.getMontantCredit());
                            if (b.getMontant() < 0) {
                                montantAnnulation.add(b.getMontant());
                            }
                        } else {
                            if (b.getMontant() < 0) {
                                montantSortie.add(b.getMontant());
                            } else {
                                montantEntree.add(b.getMontant());
                            }
                        }

                    });
                    break;
                default:
                    break;
                }
            });

            Integer _montantEspec = montantEspec.intValue();
            Integer _montantCredit = montantCredit.intValue();
            Integer _montantAnnulation = montantAnnulation.intValue();
            Integer _montantSortie = montantSortie.intValue();
            Integer _montantEntree = montantEntree.intValue();
            Integer _montantVir = montantVir.intValue();
            Integer _montantCb = montantCb.intValue();
            Integer _montantcheque = montantcheque.intValue();
            Integer _montantFondCaisse = montantFondCaisse.intValue();
            int _montantMobileMoney = montantMobileMoney.intValue();
            if (_montantFondCaisse.compareTo(0) != 0) {
                os.add(new SumCaisseDTO(_montantFondCaisse, "Fond.Caisse"));
            }

            if (_montantcheque.compareTo(0) != 0) {
                os.add(new SumCaisseDTO(_montantcheque, "Chèque"));
            }
            if (_montantCb.compareTo(0) != 0) {
                os.add(new SumCaisseDTO(_montantCb, "Carte.Bancaire"));
            }
            if (_montantVir.compareTo(0) != 0) {
                os.add(new SumCaisseDTO(_montantVir, "Virement"));
            }
            if (_montantCredit.compareTo(0) != 0) {
                os.add(new SumCaisseDTO(_montantCredit, "Différé"));
            }
            if (_montantSortie.compareTo(0) != 0) {
                os.add(new SumCaisseDTO(_montantSortie, "Sortie.Caisse"));
            }
            if (_montantEntree.compareTo(0) != 0) {
                os.add(new SumCaisseDTO(_montantEntree, "Entrée.Caisse"));
            }
            if (_montantMobileMoney != 0) {
                os.add(new SumCaisseDTO(_montantMobileMoney, "Paiement.Mobile"));
            }
            os.add(new SumCaisseDTO(_montantEspec, "Espèce"));
            if (_montantAnnulation.compareTo(0) != 0) {
                os.add(new SumCaisseDTO(_montantAnnulation, "Annulation"));
            }
            dTO.setSummary(os);
        } catch (Exception e) {

            LOG.log(Level.SEVERE, "--------------->>> cumul", e);
        }

        return dTO;
    }

    @Override
    public List<SumCaisseDTO> montantCaisseAnnule(CaisseParamsDTO caisseParams) {
        List<SumCaisseDTO> lis = new ArrayList<>();
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TCashTransaction> root = cq.from(TCashTransaction.class);
            cq.multiselect(cb.sum(root.get(TCashTransaction_.intAMOUNT)), root.get(TCashTransaction_.lgTYPEREGLEMENTID))
                    .groupBy(root.get(TCashTransaction_.lgTYPEREGLEMENTID));
            predicates.add(cb.and(cb.equal(root.get(TCashTransaction_.strTRANSACTIONREF), "D")));
            predicates.add(cb.and(cb.equal(root.get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"),
                    caisseParams.getEmplacementId())));
            if (caisseParams.getStartHour() != null && caisseParams.getStartEnd() == null) {
                LocalDateTime debut = LocalDateTime.of(caisseParams.getStartDate(), caisseParams.getStartHour());
                LocalTime hfin = LocalTime.now();
                LocalDateTime fin = LocalDateTime.of(caisseParams.getEnd(), hfin);
                Predicate btw = cb.between(
                        cb.function("TIMESTAMP", Timestamp.class, root.get(TCashTransaction_.dtUPDATED)),
                        java.sql.Timestamp.valueOf(debut), java.sql.Timestamp.valueOf(fin));
                predicates.add(cb.and(btw));
            } else if (caisseParams.getStartHour() != null && caisseParams.getStartEnd() != null) {
                LocalDateTime debut = LocalDateTime.of(caisseParams.getStartDate(), caisseParams.getStartHour());
                LocalDateTime fin = LocalDateTime.of(caisseParams.getEnd(), caisseParams.getStartEnd());
                Predicate btw = cb.between(
                        cb.function("TIMESTAMP", Timestamp.class, root.get(TCashTransaction_.dtUPDATED)),
                        java.sql.Timestamp.valueOf(debut), java.sql.Timestamp.valueOf(fin));
                predicates.add(cb.and(btw));
            } else if (caisseParams.getStartHour() == null && caisseParams.getStartEnd() != null) {
                LocalDateTime debut = LocalDateTime.of(caisseParams.getStartDate(), LocalTime.of(0, 0, 0));
                LocalDateTime fin = LocalDateTime.of(caisseParams.getEnd(), caisseParams.getStartEnd());
                Predicate btw = cb.between(
                        cb.function("TIMESTAMP", Timestamp.class, root.get(TCashTransaction_.dtUPDATED)),
                        java.sql.Timestamp.valueOf(debut), java.sql.Timestamp.valueOf(fin));
                predicates.add(cb.and(btw));
            } else if (caisseParams.getStartHour() == null && caisseParams.getStartEnd() == null) {
                Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TCashTransaction_.dtUPDATED)),
                        java.sql.Date.valueOf(caisseParams.getStartDate()),
                        java.sql.Date.valueOf(caisseParams.getEnd()));
                predicates.add(cb.and(btw));
            }
            if (caisseParams.getTypeReglementId() != null) {
                predicates.add(cb.and(
                        cb.equal(root.get(TCashTransaction_.lgTYPEREGLEMENTID), caisseParams.getTypeReglementId())));
            }
            if (caisseParams.getUtilisateurId() != null) {
                predicates.add(cb.and(cb.equal(root.get("lgUSERID").get("lgUSERID"), caisseParams.getUtilisateurId())));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = getEntityManager().createQuery(cq);
            List<Object[]> data = q.getResultList();
            data.forEach((objects) -> {
                lis.add(new SumCaisseDTO(Long.valueOf(objects[0] + ""), objects[1] + ""));
            });
            return lis;

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<SumCaisseDTO> getCaisse(CaisseParamsDTO caisseParams) {
        List<SumCaisseDTO> lis = new ArrayList<>();
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TCashTransaction> root = cq.from(TCashTransaction.class);
            cq.multiselect(cb.sum(root.get(TCashTransaction_.intAMOUNT)), root.get(TCashTransaction_.lgTYPEREGLEMENTID))
                    .groupBy(root.get(TCashTransaction_.lgTYPEREGLEMENTID));
            predicates.add(cb.and(cb.equal(root.get(TCashTransaction_.strTRANSACTIONREF), "C")));
            predicates.add(cb.and(cb.ge(root.get(TCashTransaction_.intAMOUNT), 0)));
            predicates.add(cb.and(cb.notEqual(root.get(TCashTransaction_.strTASK), "ANNULE_VENTE")));
            predicates.add(cb.and(cb.equal(root.get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"),
                    caisseParams.getEmplacementId())));
            if (caisseParams.getStartHour() != null && caisseParams.getStartEnd() == null) {
                LocalDateTime debut = LocalDateTime.of(caisseParams.getStartDate(), caisseParams.getStartHour());
                LocalTime hfin = LocalTime.now();
                LocalDateTime fin = LocalDateTime.of(caisseParams.getEnd(), hfin);
                Predicate btw = cb.between(
                        cb.function("TIMESTAMP", Timestamp.class, root.get(TCashTransaction_.dtUPDATED)),
                        java.sql.Timestamp.valueOf(debut), java.sql.Timestamp.valueOf(fin));
                predicates.add(cb.and(btw));
            } else if (caisseParams.getStartHour() != null && caisseParams.getStartEnd() != null) {
                LocalDateTime debut = LocalDateTime.of(caisseParams.getStartDate(), caisseParams.getStartHour());
                LocalDateTime fin = LocalDateTime.of(caisseParams.getEnd(), caisseParams.getStartEnd());
                Predicate btw = cb.between(
                        cb.function("TIMESTAMP", Timestamp.class, root.get(TCashTransaction_.dtUPDATED)),
                        java.sql.Timestamp.valueOf(debut), java.sql.Timestamp.valueOf(fin));
                predicates.add(cb.and(btw));
            } else if (caisseParams.getStartHour() == null && caisseParams.getStartEnd() != null) {
                LocalDateTime debut = LocalDateTime.of(caisseParams.getStartDate(), LocalTime.of(0, 0, 0));
                LocalDateTime fin = LocalDateTime.of(caisseParams.getEnd(), caisseParams.getStartEnd());
                Predicate btw = cb.between(
                        cb.function("TIMESTAMP", Timestamp.class, root.get(TCashTransaction_.dtUPDATED)),
                        java.sql.Timestamp.valueOf(debut), java.sql.Timestamp.valueOf(fin));
                predicates.add(cb.and(btw));
            } else if (caisseParams.getStartHour() == null && caisseParams.getStartEnd() == null) {
                Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TCashTransaction_.dtUPDATED)),
                        java.sql.Date.valueOf(caisseParams.getStartDate()),
                        java.sql.Date.valueOf(caisseParams.getEnd()));
                predicates.add(cb.and(btw));
            }
            if (caisseParams.getTypeReglementId() != null) {
                predicates.add(cb.and(
                        cb.equal(root.get(TCashTransaction_.lgTYPEREGLEMENTID), caisseParams.getTypeReglementId())));
            }
            if (caisseParams.getUtilisateurId() != null) {
                predicates.add(cb.and(cb.equal(root.get("lgUSERID").get("lgUSERID"), caisseParams.getUtilisateurId())));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            Query q = getEntityManager().createQuery(cq);
            List<Object[]> data = q.getResultList();
            data.forEach((objects) -> {
                lis.add(new SumCaisseDTO(Long.valueOf(objects[0] + ""), objects[1] + ""));
            });
            return lis;

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }

    }

    @Override
    public long countcashTransactions(CaisseParamsDTO caisseParams) {
        List<Predicate> predicates = new ArrayList<>();
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TCashTransaction> root = cq.from(TCashTransaction.class);
            cq.select(cb.countDistinct(root.get(TCashTransaction_.strRESSOURCEREF)));
            predicates.add(cb.and(cb.equal(root.get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"),
                    caisseParams.getEmplacementId())));
            if (caisseParams.getStartHour() != null && caisseParams.getStartEnd() == null) {
                LocalDateTime debut = LocalDateTime.of(caisseParams.getStartDate(), caisseParams.getStartHour());
                LocalTime hfin = LocalTime.now();
                LocalDateTime fin = LocalDateTime.of(caisseParams.getEnd(), hfin);
                Predicate btw = cb.between(
                        cb.function("TIMESTAMP", Timestamp.class, root.get(TCashTransaction_.dtUPDATED)),
                        java.sql.Timestamp.valueOf(debut), java.sql.Timestamp.valueOf(fin));
                predicates.add(cb.and(btw));
            } else if (caisseParams.getStartHour() != null && caisseParams.getStartEnd() != null) {
                LocalDateTime debut = LocalDateTime.of(caisseParams.getStartDate(), caisseParams.getStartHour());
                LocalDateTime fin = LocalDateTime.of(caisseParams.getEnd(), caisseParams.getStartEnd());
                Predicate btw = cb.between(
                        cb.function("TIMESTAMP", Timestamp.class, root.get(TCashTransaction_.dtUPDATED)),
                        java.sql.Timestamp.valueOf(debut), java.sql.Timestamp.valueOf(fin));
                predicates.add(cb.and(btw));
            } else if (caisseParams.getStartHour() == null && caisseParams.getStartEnd() != null) {
                LocalDateTime debut = LocalDateTime.of(caisseParams.getStartDate(), LocalTime.of(0, 0, 0));
                LocalDateTime fin = LocalDateTime.of(caisseParams.getEnd(), caisseParams.getStartEnd());
                Predicate btw = cb.between(
                        cb.function("TIMESTAMP", Timestamp.class, root.get(TCashTransaction_.dtUPDATED)),
                        java.sql.Timestamp.valueOf(debut), java.sql.Timestamp.valueOf(fin));
                predicates.add(cb.and(btw));
            } else if (caisseParams.getStartHour() == null && caisseParams.getStartEnd() == null) {
                Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TCashTransaction_.dtUPDATED)),
                        java.sql.Date.valueOf(caisseParams.getStartDate()),
                        java.sql.Date.valueOf(caisseParams.getEnd()));
                predicates.add(cb.and(btw));
            }
            if (caisseParams.getTypeReglementId() != null) {
                predicates.add(cb.and(
                        cb.equal(root.get(TCashTransaction_.lgTYPEREGLEMENTID), caisseParams.getTypeReglementId())));
            }
            if (caisseParams.getUtilisateurId() != null) {
                predicates.add(cb.and(cb.equal(root.get("lgUSERID").get("lgUSERID"), caisseParams.getUtilisateurId())));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = getEntityManager().createQuery(cq);

            return ((Long) q.getSingleResult()).intValue();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    @Override
    public JSONObject resumeCaisse(LocalDate dtStart, LocalDate dtEnd, TUser u, boolean cancel, Boolean allActivite,
            int start, int limit, boolean all, String userId) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            long total = caisseSummaryCount(dtStart, dtEnd, u, allActivite, userId);
            if (total == 0) {
                json.put("total", 0);
                json.put("data", new JSONArray());
                return json;
            }
            List<ResumeCaisseDTO> os = resumeCaisse(dtStart, dtEnd, u, allActivite, start, limit, cancel, userId, all);
            Integer summary = caisseSummary(dtStart, dtEnd, u, allActivite, userId);
            json.put("total", total);
            json.put("data", new JSONArray(os));
            json.put("metaData", new JSONObject().put("summary", summary));
            return json;
        } catch (Exception e) {
            return json;
        }
    }

    private Integer getBilletageByCaisse(String ld_CAISSE_ID) {
        Integer montant = 0;
        try {
            TBilletage OTBilletage = (TBilletage) getEntityManager()
                    .createQuery("SELECT t FROM TBilletage t WHERE t.ldCAISSEID = ?1 ").setParameter(1, ld_CAISSE_ID)
                    .getSingleResult();
            if (OTBilletage != null) {
                montant = OTBilletage.getIntAMOUNT().intValue();
            }
        } catch (Exception e) {
            return 0;

        }
        return montant;

    }

    private Integer getBilletageByCaisse(String ld_CAISSE_ID, String lg_USER_ID) {
        Integer montant = 0;
        try {
            TBilletage OTBilletage = (TBilletage) getEntityManager()
                    .createQuery("SELECT t FROM TBilletage t WHERE t.ldCAISSEID = ?1 AND t.lgUSERID.lgUSERID = ?2")
                    .setParameter(1, ld_CAISSE_ID).setParameter(2, lg_USER_ID).getSingleResult();
            if (OTBilletage != null) {
                montant = OTBilletage.getIntAMOUNT().intValue();
            }
        } catch (Exception e) {
            return 0;

        }
        return montant;

    }

    @Override
    public List<ResumeCaisseDTO> resumeCaisse(LocalDate dtStart, LocalDate dtEnd, TUser u, Boolean allActivite,
            int start, int limit, boolean cancel, String userId, boolean all) {
        List<Predicate> predicates = new ArrayList<>();
        try {
            TEmplacement emp = u.getLgEMPLACEMENTID();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TResumeCaisse> cq = cb.createQuery(TResumeCaisse.class);
            Root<TResumeCaisse> root = cq.from(TResumeCaisse.class);
            cq.select(root).orderBy(cb.desc(root.get(TResumeCaisse_.dtCREATED)));
            if (!allActivite) {
                predicates.add(cb.and(cb.equal(root.get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"),
                        emp.getLgEMPLACEMENTID())));
            }
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TResumeCaisse_.dtCREATED)),
                    java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
            predicates.add(cb.and(btw));
            if (userId != null && !"".equals(userId)) {
                predicates.add(cb.and(cb.equal(root.get("lgUSERID").get("lgUSERID"), userId)));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            TypedQuery<TResumeCaisse> q = getEntityManager().createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);
            }
            if (!isTrue(DateConverter.KEY_PRENDRE_EN_COMPTE_FOND_CAISSE)) {
                return q.getResultList().stream()
                        .map(o -> new ResumeCaisseDTO(o,
                                getBilletageByCaisse(o.getLdCAISSEID(), o.getLgUSERID().getLgUSERID()),
                                montantAnnuleGestionCaisse(o.getLgUSERID().getLgUSERID(),
                                        DateConverter.convertDateToLocalDateTime(o.getDtCREATED()),
                                        DateConverter.convertDateToLocalDateTime(o.getDtUPDATED()), false)
                                        + montantAnnuleRecette(o.getLgUSERID().getLgUSERID(),
                                                DateConverter.convertDateToLocalDate(o.getDtCREATED()),
                                                DateConverter.convertDateToLocalDate(o.getDtCREATED())),
                                cancel))
                        .collect(Collectors.toList());
            } else {
                return q.getResultList().stream()
                        .map(o -> new ResumeCaisseDTO(o,
                                getBilletageByCaisse(o.getLdCAISSEID(), o.getLgUSERID().getLgUSERID()),
                                montantAnnuleGestionCaisse(o.getLgUSERID().getLgUSERID(),
                                        DateConverter.convertDateToLocalDateTime(o.getDtCREATED()),
                                        DateConverter.convertDateToLocalDateTime(o.getDtUPDATED()), false)
                                        + montantAnnuleRecette(o.getLgUSERID().getLgUSERID(),
                                                DateConverter.convertDateToLocalDate(o.getDtCREATED()),
                                                DateConverter.convertDateToLocalDate(o.getDtCREATED())),
                                cancel, true))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public GenericDTO balanceVenteCaisseReport(LocalDate dtStart, LocalDate dtEnd, boolean checked,
            String emplacementId, Boolean excludeSome) {
        List<MvtTransaction> transactions = balanceVenteCaisseList(dtStart, dtEnd, checked, emplacementId, excludeSome);
        GenericDTO generic;
        if (key_Take_Into_Account() || key_Params()) {
            generic = balanceFormat0(transactions);
        } else {
            generic = balanceFormat(transactions);
        }
        return generic;
    }

    @Override
    public JSONObject balanceVenteCaisse(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId,
            Boolean excludeSome) throws JSONException {

        GenericDTO generic;
        long interval = ChronoUnit.DAYS.between(dtStart, dtEnd);
        if (key_Take_Into_Account() || key_Params()) {
            if (interval == 0) {
                List<MvtTransaction> transactions = balanceVenteCaisseList(dtStart, dtEnd, checked, emplacementId,
                        excludeSome);
                generic = balanceFormat0(transactions);
            } else {
                generic = balanceFormat0(dtStart, interval, checked, emplacementId);
            }

        } else {
            List<MvtTransaction> transactions = balanceVenteCaisseList(dtStart, dtEnd, checked, emplacementId,
                    excludeSome);
            generic = balanceFormat(transactions);
        }
        JSONObject json = new JSONObject();
        List<BalanceDTO> balances = generic.getBalances();
        SummaryDTO summary = generic.getSummary();
        json.put("total", balances.size());
        json.put("data", balances);
        json.put("metaData", new JSONObject(summary));
        return json;
    }

    private List<MvtTransaction> balanceVenteCaisseList(LocalDate dtStart, LocalDate dtEnd, boolean checked,
            String emplacementId, Boolean excludeSome) {
        try {
            String sql = "SELECT o FROM MvtTransaction o WHERE o.mvtDate BETWEEN ?1 AND ?2 AND o.magasin.lgEMPLACEMENTID=?3 AND o.checked=?4";
            if (excludeSome != null && excludeSome) {
                sql = "SELECT o FROM MvtTransaction o WHERE o.mvtDate BETWEEN ?1 AND ?2 AND o.magasin.lgEMPLACEMENTID=?3 AND o.checked=?4 AND o.uuid NOT IN (SELECT m.mvtTransactionKey FROM VenteExclus m)";
            }
            TypedQuery<MvtTransaction> query = getEntityManager().createQuery(sql, MvtTransaction.class);
            query.setParameter(1, dtStart);
            query.setParameter(2, dtEnd);
            query.setParameter(3, emplacementId);
            query.setParameter(4, checked);
            return query.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }

    }

    @Override
    public List<VisualisationCaisseDTO> findAllMvtCaisse(LocalDate dtStart, LocalDate dtEnd, boolean checked,
            String emplacementId) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<VisualisationCaisseDTO> cq = cb.createQuery(VisualisationCaisseDTO.class);
            Root<MvtTransaction> root = cq.from(MvtTransaction.class);
            cq.select(cb.construct(VisualisationCaisseDTO.class,
                    root.get(MvtTransaction_.tTypeMvtCaisse).get(TTypeMvtCaisse_.strNAME),
                    root.get(MvtTransaction_.tTypeMvtCaisse).get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID),
                    root.get(MvtTransaction_.reglement).get(TTypeReglement_.lgTYPEREGLEMENTID),
                    root.get(MvtTransaction_.reglement).get(TTypeReglement_.strNAME),
                    cb.sum(root.get(MvtTransaction_.montant))))
                    .groupBy(root.get(MvtTransaction_.tTypeMvtCaisse), root.get(MvtTransaction_.reglement));
            predicates.add(cb.and(
                    cb.equal(root.get(MvtTransaction_.magasin).get(TEmplacement_.lgEMPLACEMENTID), emplacementId)));
            Predicate btw = cb.between(root.get(MvtTransaction_.mvtDate), dtStart, dtEnd);
            predicates.add(btw);
            predicates.add(cb.isTrue(root.get(MvtTransaction_.checked)));
            predicates.add(cb.notEqual(root.get(MvtTransaction_.typeTransaction), TypeTransaction.ACHAT));
            predicates.add(root.get(MvtTransaction_.tTypeMvtCaisse).get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID)
                    .in(Arrays.asList(DateConverter.MVT_FOND_CAISSE, DateConverter.MVT_ENTREE_CAISSE,
                            DateConverter.MVT_SORTIE_CAISSE, DateConverter.MVT_REGLE_TP,
                            DateConverter.MVT_REGLE_DIFF)));
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            TypedQuery<VisualisationCaisseDTO> q = getEntityManager().createQuery(cq);
            return q.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    private GenericDTO balanceFormat(List<MvtTransaction> mvtTransactions) {
        List<BalanceDTO> balances = new ArrayList<>();
        GenericDTO generic = new GenericDTO();
        SummaryDTO summary = new SummaryDTO();

        if (!mvtTransactions.isEmpty()) {
            Map<TypeTransaction, List<MvtTransaction>> map = mvtTransactions.stream()
                    .collect(Collectors.groupingBy(o -> o.getTypeTransaction()));
            List<MvtTransaction> venteVNO = map.get(TypeTransaction.VENTE_COMPTANT);
            List<MvtTransaction> venteVO = map.get(TypeTransaction.VENTE_CREDIT);
            List<MvtTransaction> achats = map.get(TypeTransaction.ACHAT);
            List<MvtTransaction> entreesCaisse = map.get(TypeTransaction.ENTREE);
            List<MvtTransaction> sortieCaisse = map.get(TypeTransaction.SORTIE);
            BalanceDTO vno = null;
            int pourcentageVo;
            long _montantTTC = 0, _montantNet = 0, _montantRemise = 0, _montantEsp = 0, _montantCheque = 0,
                    _MontantVirement = 0, _montantCB = 0, _montantDiff = 0, _nbreVente = 0, montantAchat = 0,
                    montantSortie = 0, marge = 0, fondCaisse = 0, montantReglDiff = 0, montantRegleTp = 0,
                    montantEntre = 0, montantTva = 0, montantTp = 0, _montantMobilePayment = 0;
            double ratioVA = 0.0;

            if (venteVNO != null) {
                vno = new BalanceDTO();
                vno.setTypeVente("VNO");
                long montantTTC = 0, montantNet = 0, montantRemise = 0, panierMoyen = 0, montantEsp = 0,
                        montantCheque = 0, MontantVirement = 0, montantCB = 0, montantDiff = 0, nbreVente = 0,
                        montantMobilePayment = 0;
                for (MvtTransaction mvt : venteVNO) {
                    montantTTC += mvt.getMontant();
                    montantNet += mvt.getMontantNet();
                    montantRemise += mvt.getMontantRemise();
                    montantTva += mvt.getMontantTva();
                    marge += mvt.getMarge();
                    montantDiff += mvt.getMontantRestant();
                    if (mvt.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                        nbreVente++;

                    }
                    switch (mvt.getReglement().getLgTYPEREGLEMENTID()) {
                    case DateConverter.MODE_ESP:
                        montantEsp += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_CHEQUE:
                        montantCheque += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_CB:
                        montantCB += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_VIREMENT:
                        MontantVirement += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_MOOV:
                    case DateConverter.TYPE_REGLEMENT_ORANGE:
                    case DateConverter.MODE_MTN:
                    case DateConverter.MODE_WAVE:
                        montantMobilePayment += mvt.getMontantRegle();
                        break;

                    }

                }
                _montantTTC += montantTTC;
                _montantNet += montantNet;
                _MontantVirement += MontantVirement;
                _montantCB += montantCB;
                _montantCheque += montantCheque;
                _montantEsp += montantEsp;
                _montantRemise += montantRemise;
                _montantDiff += montantDiff;
                _montantMobilePayment += montantMobilePayment;
                _nbreVente += nbreVente;
                if (nbreVente > 0) {
                    panierMoyen = montantTTC / nbreVente;
                }

                vno.setMontantCB(montantCB);
                vno.setMontantCheque(montantCheque);
                vno.setMontantEsp(montantEsp);
                vno.setMontantDiff(montantDiff);
                vno.setMontantNet(montantNet);
                vno.setMontantTTC(montantTTC);
                vno.setMontantVirement(MontantVirement);
                vno.setNbreVente(nbreVente);
                vno.setMontantRemise(montantRemise);
                vno.setMontantTp(0);
                vno.setPanierMoyen(panierMoyen);
                vno.setMontantMobilePayment(montantMobilePayment);

            }
            BalanceDTO vo = null;
            if (venteVO != null) {
                vo = new BalanceDTO();
                vo.setTypeVente("VO");
                long montantTTC = 0, montantNet = 0, montantRemise = 0, panierMoyen = 0, montantEsp = 0,
                        montantCheque = 0, MontantVirement = 0, montantCB = 0, montantDiff = 0,
                        montantMobilePayment = 0, nbreVente = 0;

                for (MvtTransaction mvt : venteVO) {

                    montantTTC += mvt.getMontant();
                    montantNet += mvt.getMontantNet();
                    montantRemise += mvt.getMontantRemise();
                    montantTva += mvt.getMontantTva();
                    marge += mvt.getMarge();
                    montantDiff += mvt.getMontantRestant();
                    montantTp += mvt.getMontantCredit();
                    if (mvt.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                        nbreVente++;

                    }
                    switch (mvt.getReglement().getLgTYPEREGLEMENTID()) {
                    case DateConverter.MODE_ESP:
                        montantEsp += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_CHEQUE:
                        montantCheque += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_CB:
                        montantCB += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_VIREMENT:
                        MontantVirement += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_MOOV:
                    case DateConverter.TYPE_REGLEMENT_ORANGE:
                    case DateConverter.MODE_MTN:
                    case DateConverter.MODE_WAVE:
                        montantMobilePayment += mvt.getMontantRegle();
                        break;

                    }

                }
                if (nbreVente > 0) {
                    panierMoyen = montantTTC / nbreVente;
                }

                vo.setMontantCB(montantCB);
                vo.setMontantCheque(montantCheque);
                vo.setMontantEsp(montantEsp);
                vo.setMontantDiff(montantDiff);
                vo.setMontantNet(montantNet);
                vo.setMontantTTC(montantTTC);
                vo.setMontantVirement(MontantVirement);
                vo.setNbreVente(nbreVente);
                vo.setMontantRemise(montantRemise);
                vo.setMontantTp(montantTp);
                vo.setPanierMoyen(panierMoyen);
                vo.setMontantMobilePayment(montantMobilePayment);
                _montantMobilePayment += montantMobilePayment;
                _montantTTC += montantTTC;
                _montantNet += montantNet;
                _MontantVirement += MontantVirement;
                _montantCB += montantCB;
                _montantCheque += montantCheque;
                _montantEsp += montantEsp;
                _montantRemise += montantRemise;
                _montantDiff += montantDiff;
                _nbreVente += nbreVente;

            }
            if (vo != null) {
                pourcentageVo = (int) Math.round((Double.valueOf(vo.getMontantNet()) * 100) / Math.abs(_montantNet));
                vo.setPourcentage(pourcentageVo);
                balances.add(vo);
            }
            if (vno != null) {
                int pourcentageVno = (int) Math
                        .round((Double.valueOf(vno.getMontantNet()) * 100) / Math.abs(_montantNet));
                vno.setPourcentage(pourcentageVno);
                balances.add(vno);
            }

            if (achats != null) {
                montantAchat = achats.stream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);

            }
            if (sortieCaisse != null) {
                Map<String, List<MvtTransaction>> typeMvt = sortieCaisse.parallelStream()
                        .collect(Collectors.groupingBy(o -> o.gettTypeMvtCaisse().getLgTYPEMVTCAISSEID()));
                List<MvtTransaction> fond = typeMvt.get(DateConverter.MVT_FOND_CAISSE);
                List<MvtTransaction> sortie = typeMvt.get(DateConverter.MVT_SORTIE_CAISSE);
                if (fond != null) {
                    fondCaisse = fond.stream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }
                if (sortie != null) {
                    montantSortie = sortie.stream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }

            }
            if (entreesCaisse != null) {
                Map<String, List<MvtTransaction>> typeMvtEntree = entreesCaisse.parallelStream()
                        .collect(Collectors.groupingBy(o -> o.gettTypeMvtCaisse().getLgTYPEMVTCAISSEID()));
                List<MvtTransaction> entree = typeMvtEntree.get(DateConverter.MVT_ENTREE_CAISSE);
                List<MvtTransaction> diff = typeMvtEntree.get(DateConverter.MVT_REGLE_DIFF);
                List<MvtTransaction> reglementTp = typeMvtEntree.get(DateConverter.MVT_REGLE_TP);
                if (entree != null) {
                    montantEntre = entree.stream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }
                if (diff != null) {
                    montantReglDiff = diff.stream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }
                if (reglementTp != null) {
                    montantRegleTp = reglementTp.stream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }
            }
            if (montantAchat > 0) {
                ratioVA = Double.valueOf(_montantTTC) / montantAchat;
                ratioVA = new BigDecimal(ratioVA).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }

            // marge = (_montantTTC - montantTva) - margeAchatVente;
            summary.setFondCaisse(fondCaisse);
            summary.setMarge(marge);
            summary.setMontantAchat(montantAchat);
            summary.setMontantCB(_montantCB);
            summary.setMontantCheque(_montantCheque);
            summary.setMontantDiff(_montantDiff);
            summary.setMontantRegDiff(montantReglDiff);
            summary.setMontantEntre(montantEntre);
            summary.setMontantEsp(_montantEsp);
            summary.setMontantNet(_montantNet);
            summary.setMontantSortie(montantSortie);
            summary.setMontantVirement(_MontantVirement);
            summary.setMontantHT((_montantTTC - montantTva));
            summary.setMontantRegleTp(montantRegleTp);
            summary.setMontantRemise(_montantRemise);
            summary.setMontantTva(montantTva);
            summary.setNbreVente(_nbreVente);
            summary.setMontantTTC(_montantTTC);
            summary.setMontantMobilePayment(_montantMobilePayment);
            if (_nbreVente > 0) {
                summary.setPanierMoyen(_montantTTC / _nbreVente);
            }
            summary.setRatioVA(ratioVA);
            summary.setMontantTp(montantTp);

        }
        generic.setBalances(balances);
        generic.setSummary(summary);
        return generic;
    }

    private TUser findUser(String idUser) {
        return getEntityManager().find(TUser.class, idUser);
    }

    public TCaisse getTCaisse(String lg_USER_ID) {

        try {
            TypedQuery<TCaisse> query = getEntityManager()
                    .createQuery("SELECT t FROM TCaisse t WHERE t.lgUSERID.lgUSERID = ?1 ", TCaisse.class)
                    .setParameter(1, lg_USER_ID).setMaxResults(1);
            TCaisse o = query.getSingleResult();
            getEntityManager().refresh(o);

            return o;
        } catch (Exception e) {
            TUser tu = findUser(lg_USER_ID);
            TCaisse OTCaisse = new TCaisse();
            OTCaisse.setLgUSERID(tu);
            OTCaisse.setIntSOLDE(0.0);
            OTCaisse.setDtCREATED(new Date());
            OTCaisse.setLgCREATEDBY(tu.getStrLOGIN());
            OTCaisse.setLgCAISSEID(UUID.randomUUID().toString());
            getEntityManager().persist(OTCaisse);
            return OTCaisse;
        }
    }

    public TBilletageDetails getTBilletageDetails(String lg_CAISSE_ID) {

        try {
            TypedQuery<TBilletageDetails> query = getEntityManager()
                    .createQuery("SELECT t FROM TBilletageDetails t WHERE t.lgBILLETAGEID.ldCAISSEID = ?1",
                            TBilletageDetails.class)
                    .setParameter(1, lg_CAISSE_ID);
            query.setMaxResults(1);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }

    }

    public TResumeCaisse getTResumeCaisse(String lg_USER_ID, String str_STATUT) {
        TResumeCaisse OTResumeCaisse = null;
        try {
            TypedQuery<TResumeCaisse> query = getEntityManager().createQuery(
                    "SELECT t FROM TResumeCaisse t WHERE t.lgUSERID.lgUSERID = ?1 AND t.strSTATUT = ?2 ORDER BY t.dtCREATED DESC",
                    TResumeCaisse.class).setParameter(1, lg_USER_ID).setParameter(2, str_STATUT);
            OTResumeCaisse = query.setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return OTResumeCaisse;
    }

    @Override
    public JSONObject rollbackcloseCaisse(TUser o, String idCaisse) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            TResumeCaisse OTResumeCaisse = getEntityManager().find(TResumeCaisse.class, idCaisse);
            getEntityManager().refresh(OTResumeCaisse);
            if (OTResumeCaisse == null) {
                json.put("success", false).put("msg",
                        " Impossible de cloturer la caisse. Ref Inconnu de la caisse inconnu ");
                return json;

            }
            if (OTResumeCaisse.getStrSTATUT().equals(commonparameter.statut_is_Using)) {
                json.put("success", false).put("msg",
                        " Impossible de cloturer cette caisse ;La caisse specifiée est déjà  en cours d'utilisation");
                return json;
            }
            TResumeCaisse OTResumeCaisseCurrent = this.getTResumeCaisse(OTResumeCaisse.getLgUSERID().getLgUSERID(),
                    commonparameter.statut_is_Using);
            if (OTResumeCaisseCurrent != null) {
                json.put("success", false).put("msg",
                        " Impossible d'annuler la clôture cette caisse ;Cet utilisateur a deja une caisse en cours d'utilisation");
                return json;
            }
            TBilletageDetails OTBilletageDetails = this.getTBilletageDetails(idCaisse);
            OTResumeCaisse.setLgUPDATEDBY(o.getStrLOGIN());
            OTResumeCaisse.setIntSOLDESOIR(0);
            OTResumeCaisse.setStrSTATUT(commonparameter.statut_is_Using);
            OTResumeCaisse.setDtUPDATED(new Date());
            if (OTBilletageDetails != null) {
                TBilletage tb = OTBilletageDetails.getLgBILLETAGEID();
                getEntityManager().remove(OTBilletageDetails);
                getEntityManager().remove(tb);
            }
            getEntityManager().merge(OTResumeCaisse);
            String Description = "Annulation de la clôture de la caisse de "
                    + OTResumeCaisse.getLgUSERID().getStrFIRSTNAME() + " "
                    + OTResumeCaisse.getLgUSERID().getStrLASTNAME() + " par " + o.getStrFIRSTNAME() + " "
                    + o.getStrLASTNAME() + " effectuée avec succès";
            logService.updateItem(o, idCaisse, Description, TypeLog.ANNULATION_DE_CAISSE, OTResumeCaisse);
            createNotification(Description, TypeNotification.ANNULATION_CLOTURE_DE_CAISSE, o);

            json.put("success", true).put("msg", "Opération effectuée avec succes ");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false).put("msg", "Impossible d'annuler la cloture de cette caisse");

        }
        return json;
    }

    @Override
    public JSONObject closeCaisse(TUser o, String idCaisse) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            TResumeCaisse OTResumeCaisse = getEntityManager().find(TResumeCaisse.class, idCaisse);
            getEntityManager().refresh(OTResumeCaisse);
            if (OTResumeCaisse.getStrSTATUT().equals(commonparameter.statut_is_Using)) {
                json.put("success", false).put("msg", " La caisse est en cours d utilisation ");
                return json;
            } else if (OTResumeCaisse.getStrSTATUT().equals(commonparameter.statut_is_Closed)) {
                json.put("success", false).put("msg", " La caisse a déjà été fermée ");
                return json;
            }
            Integer billetage = getBilletageByCaisse(idCaisse);
            OTResumeCaisse.setStrSTATUT(commonparameter.statut_is_Closed);
            TCaisse OTCaisse = getTCaisse(OTResumeCaisse.getLdCAISSEID());
            String Description = "Validation de la Cloture de la caisse de  " + o.getStrLOGIN() + " avec un montant de "
                    + DateConverter.amountFormat(billetage, '.');
            OTCaisse.setIntSOLDE(0.0);
            OTCaisse.setLgUPDATEDBY(o.getStrLOGIN());
            OTCaisse.setDtUPDATED(new Date());
            getEntityManager().merge(OTCaisse);
            logService.updateItem(o, idCaisse, Description, TypeLog.VALIDATION_DE_CAISSE, OTResumeCaisse);
            json.put("success", true).put("msg", " Validation de cloture de caisse effectuée avec succes ");
            createNotification(Description, TypeNotification.VALIDATION_DE_CAISSE, o);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false).put("msg", " Echec de validation de cloture de caisse");

        }
        return json;
    }

    Comparator<TableauBaordPhDTO> comparator = Comparator.comparing(TableauBaordPhDTO::getMvtDate);

    @Override
    public JSONObject tableauBoardDatas(LocalDate dtStart, LocalDate dtEnd, Boolean checked, TUser user, int start,
            int limit, boolean all) throws JSONException {
        TEmplacement emp = user.getLgEMPLACEMENTID();
        JSONObject json = new JSONObject();
        Map<TableauBaordSummary, List<TableauBaordPhDTO>> map;
        if (key_Take_Into_Account() || key_Params()) {
            List<MvtTransaction> l = donneestableauboard(dtStart, dtEnd, checked, emp.getLgEMPLACEMENTID(), start,
                    limit, all);

            map = buillTableauBoardData0(l);
        } else {
            map = buillTableauBoardData(
                    donneestableauboard(dtStart, dtEnd, checked, emp.getLgEMPLACEMENTID(), start, limit, all));
        }

        if (map.isEmpty()) {
            json.put("total", 0);
            json.put("data", new JSONArray());

        }
        map.forEach((k, v) -> {
            json.put("total", v.size());
            json.put("data", new JSONArray(v));
            json.put("metaData", new JSONObject(k));

        });
        return json;

    }

    private Integer avoirFournisseur(LocalDate date) {
        try {
            Query q = getEntityManager().createQuery(
                    "SELECT SUM(o.dlAMOUNT) FROM TRetourFournisseur o WHERE FUNCTION('DATE',o.dtUPDATED)=?1 AND o.strREPONSEFRS <>'' AND o.strSTATUT='enable' ")
                    .setParameter(1, java.sql.Date.valueOf(date));
            return ((Double) q.getSingleResult()).intValue();
        } catch (Exception e) {
            // e.printStackTrace(System.err);
            return 0;
        }

    }

    private Integer avoirFournisseur(String dateString) {
        try {
            Query q = getEntityManager().createNativeQuery(
                    "SELECT COALESCE(SUM(o.dl_AMOUNT),0) FROM  t_retour_fournisseur o where DATE_FORMAT(o.dt_UPDATED,'%Y%m')= ?1 AND o.str_REPONSE_FRS <>'' AND o.str_STATUT='enable'")
                    .setParameter(1, dateString);
            return ((Number) q.getSingleResult()).intValue();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }

    }

    private Map<TableauBaordSummary, List<TableauBaordPhDTO>> buillTableauBoardData(List<MvtTransaction> transactions) {
        if (transactions.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<TableauBaordSummary, List<TableauBaordPhDTO>> summ = new HashMap<>();
        List<TableauBaordPhDTO> tableauBaords = new ArrayList<>();
        TableauBaordSummary summary = new TableauBaordSummary();
        Map<LocalDate, List<MvtTransaction>> map = transactions.stream()
                .collect(Collectors.groupingBy(o -> o.getMvtDate()));
        LongAdder _summontantTTC = new LongAdder(), _summontantNet = new LongAdder(),
                _summontantRemise = new LongAdder(), _summontantEsp = new LongAdder(),
                _summontantCredit = new LongAdder(), _sumnbreVente = new LongAdder(),
                _summontantAchatOne = new LongAdder(), _summontantAchatTwo = new LongAdder(),
                _summontantAchatThree = new LongAdder(), _summontantAchatFour = new LongAdder(),
                _summontantAchatFive = new LongAdder(), _summontantAvoir = new LongAdder(),
                _summontantAchat = new LongAdder();
        DoubleAdder _sumratioVA = new DoubleAdder(), _sumrationAV = new DoubleAdder();
        map.forEach((k, v) -> {
            TableauBaordPhDTO baordPh = new TableauBaordPhDTO();
            baordPh.setMvtDate(k);
            LongAdder montantTTC = new LongAdder(), montantNet = new LongAdder(), montantRemise = new LongAdder(),
                    montantEsp = new LongAdder(), montantCredit = new LongAdder(), nbreVente = new LongAdder(),
                    montantAchatOne = new LongAdder(), montantAchatTwo = new LongAdder(),
                    montantAchatThree = new LongAdder(), montantAchatFour = new LongAdder(),
                    montantAchatFive = new LongAdder(), montantAchat = new LongAdder(), montantAvoir = new LongAdder();
            DoubleAdder ratioVA = new DoubleAdder(), rationAV = new DoubleAdder();
            int avoir = avoirFournisseur(k);
            montantAvoir.add(avoir);

            v.forEach(op -> {
                switch (op.getTypeTransaction()) {
                case VENTE_COMPTANT:
                case VENTE_CREDIT: {
                    montantTTC.add(op.getMontant() - op.getMontantttcug());
                    montantNet.add(op.getMontantNet() - op.getMontantnetug());
                    montantRemise.add(op.getMontantRemise());
                    montantEsp.add(op.getMontantRegle() - op.getMontantnetug());
                    montantCredit.add(op.getMontantCredit());
                    montantCredit.add(op.getMontantRestant());
                    if (op.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                        nbreVente.increment();
                    }
                }
                    break;
                case ACHAT: {
                    montantAchat.add(op.getMontant());
                    try {
                        Groupefournisseur g = op.getGrossiste().getGroupeId();
                        switch (g.getLibelle()) {
                        case DateConverter.LABOREXCI:
                            montantAchatOne.add(op.getMontant());
                            break;
                        case DateConverter.DPCI:
                            montantAchatTwo.add(op.getMontant());
                            break;
                        case DateConverter.COPHARMED:
                            montantAchatThree.add(op.getMontant());
                            break;
                        case DateConverter.TEDIS:
                            montantAchatFour.add(op.getMontant());
                            break;
                        case DateConverter.AUTRES:
                            montantAchatFive.add(op.getMontant());
                            break;
                        default:
                            break;
                        }

                    } catch (Exception e) {
                    }
                }
                    break;
                default:
                    break;

                }
            });
            Integer _montantNet = montantNet.intValue();
            Integer _montantAchat = montantAchat.intValue() - avoir;
            if (_montantAchat.compareTo(0) > 0) {
                ratioVA.add(new BigDecimal(Double.valueOf(_montantNet) / _montantAchat).setScale(2, RoundingMode.FLOOR)
                        .doubleValue());
            }
            if (_montantNet.compareTo(0) > 0) {
                rationAV.add(new BigDecimal(Double.valueOf(_montantAchat) / _montantNet).setScale(2, RoundingMode.FLOOR)
                        .doubleValue());
            }
            baordPh.setMontantAchatFive(montantAchatFive.intValue());
            baordPh.setMontantAchatFour(montantAchatFour.intValue());
            baordPh.setMontantAchatThree(montantAchatThree.intValue());
            baordPh.setNbreVente(nbreVente.intValue());
            baordPh.setMontantAchatTwo(montantAchatTwo.intValue());
            baordPh.setMontantAchatOne(montantAchatOne.intValue());
            baordPh.setMontantAchat(_montantAchat);
            baordPh.setRatioVA(ratioVA.doubleValue());
            baordPh.setRationAV(rationAV.doubleValue());
            baordPh.setMontantTTC(montantTTC.intValue());
            baordPh.setMontantNet(_montantNet);
            baordPh.setMontantEsp(montantEsp.intValue());
            baordPh.setMontantRemise(montantRemise.intValue());
            baordPh.setMontantCredit(montantCredit.intValue());
            baordPh.setMontantAvoir(montantAvoir.intValue());

            /**
             * ** ***************
             */
            _summontantAchatFive.add(baordPh.getMontantAchatFive());
            _summontantAchatFour.add(baordPh.getMontantAchatFour());
            _summontantAchatThree.add(baordPh.getMontantAchatThree());
            _sumnbreVente.add(baordPh.getNbreVente());
            _summontantAchatTwo.add(baordPh.getMontantAchatTwo());
            _summontantAchatOne.add(baordPh.getMontantAchatOne());
            _summontantAchat.add(baordPh.getMontantAchat());
            _summontantTTC.add(baordPh.getMontantTTC());
            _summontantNet.add(baordPh.getMontantNet());
            _summontantEsp.add(baordPh.getMontantEsp());
            _summontantRemise.add(baordPh.getMontantRemise());
            _summontantCredit.add(baordPh.getMontantCredit());
            _summontantAvoir.add(baordPh.getMontantAvoir());
            tableauBaords.add(baordPh);
        });
        Long _montantNet = _summontantNet.longValue();
        Long _montantAchat = _summontantAchat.longValue();
        if (_montantAchat.compareTo(0l) > 0) {
            _sumratioVA.add(new BigDecimal(Double.valueOf(_montantNet) / _montantAchat).setScale(2, RoundingMode.FLOOR)
                    .doubleValue());
        }
        if (_montantNet.compareTo(0l) > 0) {
            _sumrationAV.add(new BigDecimal(Double.valueOf(_montantAchat) / _montantNet).setScale(2, RoundingMode.FLOOR)
                    .doubleValue());
        }
        summary.setMontantAchatFive(_summontantAchatFive.longValue());
        summary.setMontantAchatFour(_summontantAchatFour.longValue());
        summary.setMontantAchatThree(_summontantAchatThree.longValue());
        summary.setNbreVente(_sumnbreVente.longValue());
        summary.setMontantAchatTwo(_summontantAchatTwo.longValue());
        summary.setMontantAchatOne(_summontantAchatOne.longValue());
        summary.setMontantAchat(_summontantAchat.longValue());
        summary.setRatioVA(_sumratioVA.doubleValue());
        summary.setRationAV(_sumrationAV.doubleValue());
        summary.setMontantTTC(_summontantTTC.longValue());
        summary.setMontantNet(_summontantNet.longValue());
        summary.setMontantEsp(_summontantEsp.longValue());
        summary.setMontantRemise(_summontantRemise.longValue());
        summary.setMontantCredit(_summontantCredit.longValue());
        summary.setMontantAvoir(_summontantAvoir.longValue());
        summ.put(summary, tableauBaords.stream().sorted(comparator).collect(Collectors.toList()));

        return summ;
    }

    @Override
    public List<Typemvtproduit> findAllTypeMvtProduit() {
        TypedQuery<Typemvtproduit> query = getEntityManager().createQuery("SELECT o FROM Typemvtproduit o",
                Typemvtproduit.class);
        return query.getResultList();
    }

    private List<MvtTransaction> donneestableauboard(LocalDate dtStart, LocalDate dtEnd, boolean checked,
            String emplacementId, int start, int limit, boolean all) {
        try {
            TypedQuery<MvtTransaction> query = getEntityManager().createQuery(
                    "SELECT o FROM MvtTransaction o WHERE o.mvtDate BETWEEN :dtStart AND :dtEnd AND o.magasin.lgEMPLACEMENTID=:empl AND o.checked=:checked AND o.typeTransaction IN :typetransac AND o.uuid NOT IN (SELECT m.mvtTransactionKey FROM VenteExclus m)",
                    MvtTransaction.class);
            query.setParameter("dtStart", dtStart);
            query.setParameter("dtEnd", dtEnd);
            query.setParameter("empl", emplacementId);
            query.setParameter("checked", checked);
            query.setParameter("typetransac",
                    EnumSet.of(TypeTransaction.VENTE_COMPTANT, TypeTransaction.VENTE_CREDIT, TypeTransaction.ACHAT));
            if (!all) {
                query.setFirstResult(start);
                query.setMaxResults(limit);

            }
            return query.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }

    }

    @Override
    public JSONObject createMvt(MvtCaisseDTO caisseDTO, TUser user) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = getEntityManager();
        try {
            if (!checkCaisse(user, emg)) {
                return json.put("success", false).put("msg", "Votre caisse est fermée.");
            }
            TTypeMvtCaisse OTTypeMvtCaisse = emg.find(TTypeMvtCaisse.class, caisseDTO.getIdTypeMvt());
            TModeReglement modeReglement = findModeByIdOrName(caisseDTO.getIdModeRegle(), emg);
            TTypeReglement tTypeReglement = findTypeRegByIdOrName(caisseDTO.getIdTypeRegl(), emg);
            if (modeReglement == null) {
                return json.put("success", false).put("msg", "Echec d'encaissement. Mode de règlement inexistant.");
            }
            TMotifReglement motifReglement = emg.find(TMotifReglement.class, "2");

            String transac = DateConverter.TRANSACTION_CREDIT;
            if (OTTypeMvtCaisse.getLgTYPEMVTCAISSEID().equals(DateConverter.MVT_SORTIE_CAISSE)) {
                transac = DateConverter.TRANSACTION_DEBIT;
                caisseDTO.setAmount(caisseDTO.getAmount() * (-1));
                motifReglement = emg.find(TMotifReglement.class, "3");
            }
            TMvtCaisse mvtCaisse = addTMvtCaisse(OTTypeMvtCaisse, caisseDTO, emg, modeReglement, user);
            String Description = "Mouvement d'une somme de  " + mvtCaisse.getIntAMOUNT().intValue()
                    + " Type de mouvement " + OTTypeMvtCaisse.getStrDESCRIPTION() + " par " + user.getStrFIRSTNAME()
                    + " " + user.getStrLASTNAME();
            TReglement OTReglement = createReglement(caisseDTO, user, mvtCaisse, modeReglement, emg);
            transactionService.addTransaction(user, user, mvtCaisse.getLgMVTCAISSEID(),
                    mvtCaisse.getIntAMOUNT().intValue(), mvtCaisse.getIntAMOUNT().intValue(),
                    mvtCaisse.getIntAMOUNT().intValue(), 0, caisseDTO.getAmount() > 0 ? caisseDTO.getAmount() : 0,
                    Boolean.TRUE, caisseDTO.getAmount() > 0 ? CategoryTransaction.CREDIT : CategoryTransaction.DEBIT,
                    caisseDTO.getAmount() > 0 ? TypeTransaction.ENTREE : TypeTransaction.SORTIE, tTypeReglement,
                    OTTypeMvtCaisse, 0, emg, caisseDTO.getAmount() > 0 ? caisseDTO.getAmount() : 0, 0, 0,
                    mvtCaisse.getStrREFTICKET());
            addtransactionComptant(OTTypeMvtCaisse, Description, motifReglement, transac, mvtCaisse, tTypeReglement,
                    OTReglement, user, emg);
            logService.updateItem(user, mvtCaisse.getStrREFTICKET(), Description, TypeLog.MVT_DE_CAISSE, mvtCaisse);
            createNotification(Description, TypeNotification.MVT_DE_CAISSE, user);
            return json.put("success", true).put("msg", "Opération effectuée .").put("mvtId",
                    mvtCaisse.getLgMVTCAISSEID());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return json.put("success", false).put("msg", "L'opération a échouée.");
        }

    }

    private TModeReglement findModeByIdOrName(String id, EntityManager emg) {
        try {
            TypedQuery<TModeReglement> tq = emg.createQuery(
                    "SELECT o FROM TModeReglement o WHERE o.lgMODEREGLEMENTID LIKE ?1 OR o.strNAME LIKE ?1 OR o.strDESCRIPTION LIKE ?1",
                    TModeReglement.class);
            tq.setParameter(1, id + "%");
            tq.setMaxResults(1);
            return tq.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }

    private TModeReglement findModeById(String id) {
        try {
            return getEntityManager().find(TModeReglement.class, id);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }

    private TTypeReglement findTypeRegByIdOrName(String id, EntityManager emg) {
        try {
            TypedQuery<TTypeReglement> tq = emg.createQuery(
                    "SELECT o FROM TTypeReglement o WHERE o.lgTYPEREGLEMENTID LIKE ?1 OR o.strNAME LIKE ?1 OR o.strDESCRIPTION LIKE ?1",
                    TTypeReglement.class);
            tq.setParameter(1, id + "%");
            tq.setMaxResults(1);
            return tq.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public JSONObject removeMvt(MvtCaisseDTO caisseDTO, TUser user) throws JSONException {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    public TMvtCaisse addTMvtCaisse(TTypeMvtCaisse OTTypeMvtCaisse, MvtCaisseDTO caisseDTO, EntityManager emg,
            TModeReglement OTModeReglement, TUser user) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        TMvtCaisse OTMvtCaisse = new TMvtCaisse(UUID.randomUUID().toString());
        OTMvtCaisse.setBoolCHECKED(Boolean.TRUE);
        OTMvtCaisse.setDtCREATED(new Date());
        OTMvtCaisse.setLgTYPEMVTCAISSEID(OTTypeMvtCaisse);
        OTMvtCaisse.setLgMODEREGLEMENTID(OTModeReglement);
        OTMvtCaisse.setStrNUMCOMPTE(OTTypeMvtCaisse.getStrCODECOMPTABLE());
        OTMvtCaisse.setStrNUMPIECECOMPTABLE(caisseDTO.getNumPieceComptable());
        OTMvtCaisse.setIntAMOUNT(caisseDTO.getAmount().doubleValue());
        OTMvtCaisse.setStrCOMMENTAIRE(caisseDTO.getCommentaire());
        OTMvtCaisse.setStrSTATUT(commonparameter.statut_enable);
        OTMvtCaisse.setDtDATEMVT(dateFormat.parse(caisseDTO.getDateMvt()));
        OTMvtCaisse.setStrCREATEDBY(user);
        OTMvtCaisse.setPKey(user.getLgUSERID());
        OTMvtCaisse.setDtUPDATED(new Date());
        OTMvtCaisse.setStrREFTICKET(DateConverter.getShortId(10));
        OTMvtCaisse.setLgUSERID(user.getLgUSERID());
        emg.persist(OTMvtCaisse);
        return OTMvtCaisse;

    }

    private TReglement createReglement(MvtCaisseDTO caisseDTO, TUser user, TMvtCaisse mvtCaisse,
            TModeReglement OTModeReglement, EntityManager emg) throws Exception {
        TReglement OTReglement = new TReglement();
        OTReglement.setLgREGLEMENTID(UUID.randomUUID().toString());
        OTReglement.setStrBANQUE(caisseDTO.getBanque());
        OTReglement.setStrCODEMONNAIE(caisseDTO.getCodeMonnaie());
        OTReglement.setStrCOMMENTAIRE(caisseDTO.getCommentaire());
        OTReglement.setStrLIEU(caisseDTO.getLieux());
        OTReglement.setStrFIRSTLASTNAME(user.getStrFIRSTNAME() + " " + user.getStrLASTNAME());
        OTReglement.setStrREFRESSOURCE(mvtCaisse.getLgMVTCAISSEID());
        OTReglement.setIntTAUX(0);
        OTReglement.setDtCREATED(new Date());
        OTReglement.setDtUPDATED(new Date());
        OTReglement.setLgMODEREGLEMENTID(OTModeReglement);
        OTReglement.setDtREGLEMENT(mvtCaisse.getDtDATEMVT());
        OTReglement.setLgUSERID(user);
        OTReglement.setBoolCHECKED(mvtCaisse.getBoolCHECKED());
        emg.persist(OTReglement);
        return OTReglement;
    }

    public void addtransactionComptant(TTypeMvtCaisse optionalCaisse, String Description,
            TMotifReglement motifReglement, String debitOrcredit, TMvtCaisse op, TTypeReglement ttr,
            TReglement OTReglement, TUser user, EntityManager emg) {
        Integer amount = op.getIntAMOUNT().intValue();
        TCashTransaction cashTransaction = new TCashTransaction(UUID.randomUUID().toString());
        cashTransaction.setBoolCHECKED(Boolean.TRUE);
        cashTransaction.setDtCREATED(new Date());
        cashTransaction.setDtUPDATED(new Date());
        cashTransaction.setIntAMOUNT(amount);
        cashTransaction.setIntACCOUNT(amount);
        cashTransaction.setStrTYPE(Boolean.TRUE);
        cashTransaction.setStrDESCRIPTION(Description);
        cashTransaction.setLgTYPEREGLEMENTID(ttr.getLgTYPEREGLEMENTID());
        cashTransaction.setLgUSERID(user);
        cashTransaction.setIntAMOUNT2(cashTransaction.getIntAMOUNT());
        cashTransaction.setStrTRANSACTIONREF(debitOrcredit);
        cashTransaction.setStrTASK("OTHER");
        cashTransaction.setStrNUMEROCOMPTE(optionalCaisse.getStrCODECOMPTABLE());
        cashTransaction.setLgREGLEMENTID(OTReglement);
        cashTransaction.setLgMOTIFREGLEMENTID(motifReglement);
        cashTransaction.setStrREFFACTURE(op.getStrREFTICKET());
        cashTransaction.setStrRESSOURCEREF(op.getLgMVTCAISSEID());
        cashTransaction.setStrTYPEVENTE("OTHER");
        cashTransaction.setIntAMOUNTRECU(amount < 0 ? 0 : amount);
        cashTransaction.setIntAMOUNTCREDIT(amount);
        if (optionalCaisse.getLgTYPEMVTCAISSEID().equals(DateConverter.MVT_FOND_CAISSE)) {
            cashTransaction.setIntAMOUNTDEBIT(amount);
            cashTransaction.setStrTRANSACTIONREF("C");
        } else {
            cashTransaction.setIntAMOUNTDEBIT(amount > 0 ? 0 : (-1) * amount);
        }

        cashTransaction.setIntAMOUNTREMIS(0);
        cashTransaction.setCaissier(user);
        cashTransaction.setStrREFCOMPTECLIENT("");
        emg.persist(cashTransaction);

    }

    @Override
    public boolean checkCaisse(TUser ooTUser, EntityManager emg) {
        try {
            TypedQuery<TResumeCaisse> q = emg.createQuery(
                    "SELECT t FROM TResumeCaisse t WHERE t.lgUSERID.lgUSERID = ?1  AND t.strSTATUT = ?2 ",
                    TResumeCaisse.class);
            q.setParameter(1, ooTUser.getLgUSERID()).setParameter(2, DateConverter.STATUT_IS_IN_USE).setMaxResults(1);
            return (q.getSingleResult() != null);
        } catch (Exception e) {
            // LOG.log(Level.SEVERE, null, e);
            return false;
        }
    }

    private TCoffreCaisse getStatutCoffre(String userId, EntityManager emg) {

        try {
            TypedQuery<TCoffreCaisse> q = emg.createQuery(
                    "SELECT t FROM TCoffreCaisse t WHERE t.lgUSERID.lgUSERID = ?1 AND  t.strSTATUT = ?2  AND   FUNCTION('DATE', t.dtCREATED)=CURRENT_DATE ",
                    TCoffreCaisse.class).setParameter(1, userId)
                    .setParameter(2, DateConverter.STATUT_IS_WAITING_VALIDATION);
            return q.getSingleResult();

        } catch (Exception e) {
            // e.printStackTrace(System.err);
            return null;

        }

    }

    @Override
    public JSONObject attribuerFondDeCaisse(String idUser, TUser operateur, Integer amount) throws JSONException {
        EntityManager emg = getEntityManager();
        JSONObject json = new JSONObject();

        try {
            TUser user = emg.find(TUser.class, idUser);
            emg.refresh(user);

            if (user.getBIsConnected() == null || user.getBIsConnected().equals(false)) {
                return json.put("success", false).put("msg", "Cet utilisateur n'est pas connecté");

            }
            if (checkCaisse(user, emg)) {
                return json.put("success", false).put("msg", "La caisse de cet utilisateur est en cours d'utilisation");
            }
            if (getStatutCoffre(idUser, emg) != null) {
                return json.put("success", false).put("msg", "Cet utilisateur a déjà reçu un fond de caisse");
            }
            createCoffreCaisse(user, operateur, amount.doubleValue(), emg);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return json.put("success", false).put("msg", "L'opération a échoué");
        }
        return json.put("success", true).put("msg", "L'opération  effectuée ");
    }

    private void createCoffreCaisse(TUser OTUser, TUser operateur, double dbl_AMOUNT, EntityManager emg)
            throws Exception {
        TCoffreCaisse OTCoffreCaisse = new TCoffreCaisse();
        OTCoffreCaisse.setIdCoffreCaisse(UUID.randomUUID().toString());
        OTCoffreCaisse.setLgUSERID(OTUser);
        OTCoffreCaisse.setIntAMOUNT(dbl_AMOUNT);
        OTCoffreCaisse.setDtCREATED(new Date());
        OTCoffreCaisse.setStrSTATUT(DateConverter.STATUT_IS_WAITING_VALIDATION);
        OTCoffreCaisse.setLdCREATEDBY(operateur.getLgUSERID());
        emg.persist(OTCoffreCaisse);
        String Description = "Reaprovisionement de la caisse de " + OTUser.getStrLOGIN() + " d'un montant de "
                + OTCoffreCaisse.getIntAMOUNT().intValue() + " par " + operateur.getStrFIRSTNAME() + " "
                + operateur.getStrLASTNAME();
        logService.updateItem(operateur, OTCoffreCaisse.getIdCoffreCaisse(), Description,
                TypeLog.ATTRIBUTION_DE_FOND_DE_CAISSE, OTCoffreCaisse);
        createNotification(Description, TypeNotification.MVT_DE_CAISSE, operateur);
    }

    private TCaisse findByUser(String userId) {
        try {
            TypedQuery<TCaisse> q = getEntityManager()
                    .createQuery("SELECT o FROM TCaisse o WHERE o.lgUSERID.lgUSERID=?1", TCaisse.class);
            q.setParameter(1, userId);
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private TReglement createReglement(String commenatire, TUser user, TMvtCaisse mvtCaisse,
            TModeReglement OTModeReglement) {
        TReglement OTReglement = new TReglement();
        OTReglement.setLgREGLEMENTID(UUID.randomUUID().toString());
        OTReglement.setStrBANQUE("");
        OTReglement.setStrCODEMONNAIE("");
        OTReglement.setStrCOMMENTAIRE(commenatire);
        OTReglement.setStrLIEU("");
        OTReglement.setStrFIRSTLASTNAME(user.getStrFIRSTNAME() + " " + user.getStrLASTNAME());
        OTReglement.setStrREFRESSOURCE(mvtCaisse.getLgMVTCAISSEID());
        OTReglement.setIntTAUX(0);
        OTReglement.setDtCREATED(new Date());
        OTReglement.setDtUPDATED(new Date());
        OTReglement.setLgMODEREGLEMENTID(OTModeReglement);
        OTReglement.setDtREGLEMENT(mvtCaisse.getDtDATEMVT());
        OTReglement.setLgUSERID(user);
        OTReglement.setBoolCHECKED(mvtCaisse.getBoolCHECKED());
        getEntityManager().persist(OTReglement);
        return OTReglement;
    }

    public TMvtCaisse addTMvtCaisse(TUser user, TTypeMvtCaisse OTTypeMvtCaisse, TTypeReglement type,
            String str_NUM_PIECE_COMPTABLE, String lg_MODE_REGLEMENT_ID, double int_AMOUNT) {
        TModeReglement OTModeReglement = findModeById(lg_MODE_REGLEMENT_ID);
        TMvtCaisse OTMvtCaisse = new TMvtCaisse();
        OTMvtCaisse.setLgMVTCAISSEID(UUID.randomUUID().toString());
        OTMvtCaisse.setLgTYPEMVTCAISSEID(OTTypeMvtCaisse);
        OTMvtCaisse.setLgMODEREGLEMENTID(OTModeReglement);
        OTMvtCaisse.setStrNUMCOMPTE(OTTypeMvtCaisse.getStrCODECOMPTABLE());
        OTMvtCaisse.setStrNUMPIECECOMPTABLE(str_NUM_PIECE_COMPTABLE);
        OTMvtCaisse.setIntAMOUNT(int_AMOUNT);
        OTMvtCaisse.setStrCOMMENTAIRE("Attribution de fond de caisse");
        OTMvtCaisse.setStrSTATUT(commonparameter.statut_enable);
        OTMvtCaisse.setDtDATEMVT(new Date());
        OTMvtCaisse.setStrCREATEDBY(user);
        OTMvtCaisse.setDtCREATED(new Date());
        OTMvtCaisse.setPKey(user.getLgUSERID());
        OTMvtCaisse.setDtUPDATED(new Date());
        OTMvtCaisse.setStrREFTICKET(DateConverter.getShortId(10));
        OTMvtCaisse.setLgUSERID(user.getLgUSERID());
        OTMvtCaisse.setBoolCHECKED(true);
        getEntityManager().persist(OTMvtCaisse);
        TReglement OTReglement = createReglement("Attribution de fond de caisse", user, OTMvtCaisse, OTModeReglement);
        TMotifReglement motifReglement = getEntityManager().find(TMotifReglement.class, "2");
        addtransactionComptant(OTTypeMvtCaisse, lg_MODE_REGLEMENT_ID, motifReglement, lg_MODE_REGLEMENT_ID, OTMvtCaisse,
                type, OTReglement, user, em);
        return OTMvtCaisse;
    }

    @Override
    public JSONObject validerFondDeCaisse(String id, TUser user) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            TTypeMvtCaisse typeMvtCaisse = getEntityManager().find(TTypeMvtCaisse.class, DateConverter.MVT_FOND_CAISSE);
            TCoffreCaisse OTCoffreCaisse = getEntityManager().find(TCoffreCaisse.class, id);
            TTypeReglement reglement = getEntityManager().find(TTypeReglement.class, DateConverter.MODE_ESP);
            if (OTCoffreCaisse == null) {
                return json.put("success", false).put("msg",
                        "Aucune attribution de fond de caisse en cours pour cet utilisateur");
            }
            TResumeCaisse OTResumeCaisse = new TResumeCaisse();
            TCaisse oOTCaisse = findByUser(user.getLgUSERID());
            if (oOTCaisse == null) {
                oOTCaisse = new TCaisse();
                oOTCaisse.setLgCAISSEID(UUID.randomUUID().toString());
                oOTCaisse.setDtCREATED(new Date());
            }
            oOTCaisse.setDtUPDATED(new Date());
            oOTCaisse.setLgUPDATEDBY(user.getStrLOGIN());
            oOTCaisse.setIntSOLDE(0.0);
            oOTCaisse.setLgUSERID(user);
            oOTCaisse.setLgCREATEDBY(user.getStrLOGIN());
            OTResumeCaisse.setLdCAISSEID(UUID.randomUUID().toString());
            OTResumeCaisse.setIntSOLDEMATIN(OTCoffreCaisse.getIntAMOUNT().intValue());
            OTResumeCaisse.setLgUSERID(user);
            OTResumeCaisse.setDtCREATED(new Date());
            OTCoffreCaisse.setDtUPDATED(new Date());
            OTResumeCaisse.setLgCREATEDBY(user.getStrLOGIN());
            OTResumeCaisse.setIdCoffreCaisse(OTCoffreCaisse);
            OTResumeCaisse.setIntSOLDESOIR(0);
            OTResumeCaisse.setStrSTATUT(DateConverter.STATUT_IS_IN_USE);
            OTCoffreCaisse.setStrSTATUT(DateConverter.STATUT_IS_ASSIGN);
            OTCoffreCaisse.setLdUPDATEDBY(user.getStrLOGIN());
            getEntityManager().merge(oOTCaisse);
            getEntityManager().persist(OTResumeCaisse);
            String Description = "Validation de fond de caisse " + user.getStrLOGIN() + " d'un montant de "
                    + OTCoffreCaisse.getIntAMOUNT().intValue() + " par " + user.getStrFIRSTNAME() + " "
                    + user.getStrLASTNAME();
            TMvtCaisse OTMvtCaisse = addTMvtCaisse(user, typeMvtCaisse, reglement, Description, "1",
                    OTCoffreCaisse.getIntAMOUNT());
            logService.updateItem(user, OTMvtCaisse.getStrREFTICKET(), Description,
                    TypeLog.VALIDATION_DE_FOND_DE_CAISSE, OTCoffreCaisse);
            transactionService.addTransaction(user, user, OTMvtCaisse.getLgMVTCAISSEID(),
                    OTCoffreCaisse.getIntAMOUNT().intValue(), OTMvtCaisse.getIntAMOUNT().intValue(),
                    OTCoffreCaisse.getIntAMOUNT().intValue(), 0, Boolean.TRUE, CategoryTransaction.DEBIT,
                    TypeTransaction.SORTIE, reglement, typeMvtCaisse, getEntityManager(), 0, 0, 0,
                    OTMvtCaisse.getStrREFTICKET());
            createNotification(Description, TypeNotification.MVT_DE_CAISSE, user);
            return json.put("success", true).put("msg", "Opération effectuée ").accumulate("mvtId",
                    OTMvtCaisse.getLgMVTCAISSEID());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return json.put("success", false).put("msg", "Errreur:::: Echec de  validation ");
        }

    }

    Comparator<RapportDTO> comparatorReport = Comparator.comparingInt(RapportDTO::getOder);

    @Override
    public JSONObject rapportGestionViewData(Params params) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            Map<Params, List<RapportDTO>> map = rapportGestion(params);
            List<RapportDTO> data = new ArrayList<>();
            LongAdder montantDep = new LongAdder(), montantRg = new LongAdder();
            map.forEach((k, v) -> {
                if (k.getRef().equals(DateConverter.DEPENSES)) {
                    montantDep.add(k.getValue());
                } else {
                    montantRg.add(k.getValue());
                }
                if (v != null) {
                    data.addAll(v);
                }

            });
            data.sort(comparatorReport);
            json.put("total", data.size()).put("data", new JSONArray(data)).put("metaData", new JSONObject()
                    .put("montantCaisse", montantRg.intValue()).put("montantDepense", montantDep.intValue()));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return json;
    }

    @Override
    public Map<Params, List<RapportDTO>> rapportGestion(Params params) {
        List<RapportDTO> rapports = new ArrayList<>();
        Map<Params, List<RapportDTO>> _myMap = new HashMap<>();
        try {
            List<MvtTransaction> transactions = findTransaction(LocalDate.parse(params.getDtStart()),
                    LocalDate.parse(params.getDtEnd()), true,
                    params.getOperateur().getLgEMPLACEMENTID().getLgEMPLACEMENTID(), 0, 0, true);
            if (!transactions.isEmpty()) {
                Map<TypeTransaction, List<MvtTransaction>> groupe = transactions.stream()
                        .collect(Collectors.groupingBy(MvtTransaction::getTypeTransaction));
                LongAdder montantDepense = new LongAdder();
                LongAdder montantReglement = new LongAdder();
                LongAdder montantTTC = new LongAdder();
                LongAdder marge = new LongAdder();
                LongAdder montantACHAT = new LongAdder();
                LongAdder counter = new LongAdder();
                LongAdder counterSortie = new LongAdder();
                counter.add(5);
                counterSortie.add(4);
                RapportDTO rapportTTC = new RapportDTO();
                RapportDTO rapportHT = new RapportDTO();
                rapportTTC.setOder(0);
                rapportTTC.setDisplay(0);
                rapportHT.setDisplay(0);
                rapportHT.setOder(1);
                rapportTTC.setLibelle(DateConverter.CA_TTC);
                rapportHT.setLibelle(DateConverter.CA_HT);
                rapportTTC.setCategorie(DateConverter.CA);
                rapportHT.setCategorie(DateConverter.CA);
                groupe.forEach((k, v) -> {
                    switch (k) {
                    case VENTE_COMPTANT:
                    case VENTE_CREDIT:
                        v.forEach(o -> {
                            rapportTTC.setMontant(rapportTTC.getMontant() + o.getMontantNet());
                            marge.add(o.getMarge());
                            rapportHT.setMontant(rapportHT.getMontant() + (o.getMontantNet() - o.getMontantTva()));
                        });
                        montantTTC.add(rapportTTC.getMontant());
                        break;
                    case ACHAT:
                        RapportDTO rapportAcht = new RapportDTO();
                        Integer montantAchat = v.stream().mapToInt(MvtTransaction::getMontantNet).sum();
                        rapportAcht.setMontant(montantAchat);
                        montantACHAT.add(montantAchat);
                        rapportAcht.setOder(2);
                        rapportAcht.setDisplay(1);
                        rapportAcht.setLibelle(DateConverter.ACHATS);
                        rapportAcht.setCategorie(DateConverter.ACHATS);
                        rapports.add(rapportAcht);
                        break;
                    case ENTREE:
                        v.forEach(r -> {
                            RapportDTO rapportEntree = new RapportDTO();
                            rapportEntree.setMontant(r.getMontant());
                            montantReglement.add(r.getMontant());
                            counter.increment();
                            rapportEntree.setDisplay(5);
                            rapportEntree.setOder(counter.intValue());
                            rapportEntree.setLibelle(r.gettTypeMvtCaisse().getStrNAME());
                            rapportEntree.setCategorie(DateConverter.ENTREE_CAISSE);
                            rapports.add(rapportEntree);
                        });
                        break;
                    case SORTIE:
                        v.forEach(r -> {
                            RapportDTO rapportSorie = new RapportDTO();
                            rapportSorie.setMontant(r.getMontant());
                            montantDepense.add(r.getMontant());
                            rapportSorie.setDisplay(4);
                            counterSortie.increment();
                            rapportSorie.setOder(counter.intValue());
                            rapportSorie.setLibelle(r.gettTypeMvtCaisse().getStrNAME());
                            rapportSorie.setCategorie(DateConverter.DEPENSES);
                            rapports.add(rapportSorie);
                        });
                        break;
                    default:
                        break;
                    }
                });
                rapports.add(rapportTTC);
                rapports.add(rapportHT);
                RapportDTO rapportMarge = new RapportDTO();
                rapportMarge.setOder(3);
                rapportMarge.setDisplay(2);
                rapportMarge.setCategorie(DateConverter.MARGE);
                rapportMarge.setLibelle(DateConverter.MARGE);
                // Integer marge = montantTTC.intValue() - montantACHAT.intValue();
                rapportMarge.setMontant(marge.intValue());
                rapports.add(rapportMarge);
                Params p = new Params();
                p.setRef(DateConverter.DEPENSES);
                p.setValue(montantDepense.intValue());
                _myMap.put(p, rapports);
                p = new Params();
                p.setRef(DateConverter.ENTREE_CAISSE);
                p.setValue(montantReglement.intValue());
                _myMap.put(p, null);
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return _myMap;
    }

    private List<MvtTransaction> findTransaction(LocalDate dtStart, LocalDate dtEnd, boolean checked,
            String emplacementId, int start, int limit, boolean all) {
        try {
            TypedQuery<MvtTransaction> query = getEntityManager().createQuery(
                    "SELECT o FROM MvtTransaction o WHERE o.mvtDate BETWEEN :dtStart AND :dtEnd AND o.magasin.lgEMPLACEMENTID=:empl AND o.checked=:checked",
                    MvtTransaction.class);
            query.setParameter("dtStart", dtStart);
            query.setParameter("dtEnd", dtEnd);
            query.setParameter("empl", emplacementId);
            query.setParameter("checked", checked);

            if (!all) {
                query.setFirstResult(start);
                query.setMaxResults(limit);

            }
            return query.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }

    }

    private List<HMvtProduit> mouvementsVente(LocalDate dtStart, LocalDate dtEnd, TUser user, String empl) {
        try {
            TypedQuery<HMvtProduit> query = getEntityManager().createQuery(
                    "SELECT o FROM HMvtProduit o WHERE o.mvtDate BETWEEN :dtStart AND :dtEnd AND o.emplacement.lgEMPLACEMENTID =:empl AND o.typemvtproduit.categorieTypeMvt =:catmvt",
                    HMvtProduit.class);
            query.setParameter("dtStart", dtStart);
            query.setParameter("dtEnd", dtEnd);
            query.setParameter("empl", empl);
            query.setParameter("catmvt", CategorieTypeMvt.VENTE);
            return query.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }

    }

    BiFunction<Integer, Integer, Integer> valeurAchat = (prixAchat, qtyVendue) -> prixAchat * qtyVendue;

    @Override
    public Integer margeAchatVente(LocalDate dtStart, LocalDate dtEnd, TUser user, String empl) {
        return mouvementsVente(dtStart, dtEnd, user, empl).stream()
                .mapToInt(x -> valeurAchat.apply(x.getPrixAchat(), x.getQteMvt())).sum();
    }

    private TClient findClientByVenteId(String id) {
        try {
            TClient tp = getEntityManager().find(TClient.class, id);
            return tp;
        } catch (Exception e) {
            return null;
        }
    }

    Comparator<VisualisationCaisseDTO> comparatorCaisse = Comparator
            .comparing(VisualisationCaisseDTO::getDateOperation);

    private List<VisualisationCaisseDTO> findAllsTransaction(CaisseParamsDTO caisseParams, boolean all) {

        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<MvtTransaction> cq = cb.createQuery(MvtTransaction.class);
            Root<MvtTransaction> root = cq.from(MvtTransaction.class);
            cq.select(root).orderBy(cb.asc(root.get(MvtTransaction_.createdAt)));

            predicates.add(cb.and(cb.equal(root.get(MvtTransaction_.magasin).get(TEmplacement_.lgEMPLACEMENTID),
                    caisseParams.getEmplacementId())));

            predicates.add(cb.and(cb.notEqual(root.get(MvtTransaction_.typeTransaction), TypeTransaction.ACHAT)));
            if (caisseParams.getStartHour() != null && caisseParams.getStartEnd() != null) {
                LocalDateTime debut = LocalDateTime.of(caisseParams.getStartDate(), caisseParams.getStartHour());
                LocalDateTime fin = LocalDateTime.of(caisseParams.getEnd(), caisseParams.getStartEnd());
                Predicate btw = cb.between(
                        cb.function("TIMESTAMP", Timestamp.class, root.get(MvtTransaction_.createdAt)),
                        java.sql.Timestamp.valueOf(debut), java.sql.Timestamp.valueOf(fin));
                predicates.add(cb.and(btw));
            } else if (caisseParams.getStartHour() == null && caisseParams.getStartEnd() == null) {
                Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(MvtTransaction_.mvtDate)),
                        java.sql.Date.valueOf(caisseParams.getStartDate()),
                        java.sql.Date.valueOf(caisseParams.getEnd()));
                predicates.add(cb.and(btw));
            }
            if (caisseParams.getTypeReglementId() != null) {
                predicates
                        .add(cb.and(cb.equal(root.get(MvtTransaction_.reglement).get(TTypeReglement_.lgTYPEREGLEMENTID),
                                caisseParams.getTypeReglementId())));
            }
            if (caisseParams.getUtilisateurId() != null) {
                predicates.add(cb.and(cb.equal(root.get(MvtTransaction_.caisse).get(TUser_.lgUSERID),
                        caisseParams.getUtilisateurId())));
            }

            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            TypedQuery<MvtTransaction> q = getEntityManager().createQuery(cq);
            if (!all) {
                q.setFirstResult(caisseParams.getStart());
                q.setMaxResults(caisseParams.getLimit());
            }

            return q.getResultList().stream()
                    .map(x -> new VisualisationCaisseDTO(x,
                            caisseParams.isFindClient() ? findClientByVenteId(x.getOrganisme()) : null))
                    /* .filter(w->Objects.nonNull(w.getTypeMouvement()) ) */.sorted(comparatorCaisse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }

    }

    private Long findAllsTransaction(CaisseParamsDTO caisseParams) {

        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<MvtTransaction> root = cq.from(MvtTransaction.class);
            cq.select(cb.count(root));
            predicates.add(cb.and(cb.equal(root.get(MvtTransaction_.magasin).get(TEmplacement_.lgEMPLACEMENTID),
                    caisseParams.getEmplacementId())));

            predicates.add(cb.and(cb.notEqual(root.get(MvtTransaction_.typeTransaction), TypeTransaction.ACHAT)));
            if (caisseParams.getStartHour() != null && caisseParams.getStartEnd() != null) {
                LocalDateTime debut = LocalDateTime.of(caisseParams.getStartDate(), caisseParams.getStartHour());
                LocalDateTime fin = LocalDateTime.of(caisseParams.getEnd(), caisseParams.getStartEnd());
                Predicate btw = cb.between(
                        cb.function("TIMESTAMP", Timestamp.class, root.get(MvtTransaction_.createdAt)),
                        java.sql.Timestamp.valueOf(debut), java.sql.Timestamp.valueOf(fin));
                predicates.add(cb.and(btw));
            } else if (caisseParams.getStartHour() == null && caisseParams.getStartEnd() == null) {
                Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(MvtTransaction_.mvtDate)),
                        java.sql.Date.valueOf(caisseParams.getStartDate()),
                        java.sql.Date.valueOf(caisseParams.getEnd()));
                predicates.add(cb.and(btw));
            }
            if (caisseParams.getTypeReglementId() != null) {
                predicates
                        .add(cb.and(cb.equal(root.get(MvtTransaction_.reglement).get(TTypeReglement_.lgTYPEREGLEMENTID),
                                caisseParams.getTypeReglementId())));
            }
            if (caisseParams.getUtilisateurId() != null) {
                predicates.add(cb.and(cb.equal(root.get(MvtTransaction_.caisse).get(TUser_.lgUSERID),
                        caisseParams.getUtilisateurId())));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = getEntityManager().createQuery(cq);
            return (Long) q.getSingleResult();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0l;
        }

    }

    @Override
    public List<VisualisationCaisseDTO> listCaisses(CaisseParamsDTO caisseParams, boolean all) {
        return findAllsTransaction(caisseParams, all);
    }

    @Override
    public List<TCashTransaction> cashTransactions(CaisseParamsDTO caisseParams, boolean all) {
        List<Predicate> predicates = new ArrayList<>();
        List<TCashTransaction> data = new ArrayList<>();
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TCashTransaction> cq = cb.createQuery(TCashTransaction.class);
            Root<TCashTransaction> root = cq.from(TCashTransaction.class);// groupBy(root.get(TCashTransaction_.strRESSOURCEREF))
            cq.select(root).orderBy(cb.desc(root.get(TCashTransaction_.dtUPDATED)));
            predicates.add(cb.and(cb.equal(root.get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"),
                    caisseParams.getEmplacementId())));
            if (caisseParams.getStartHour() != null && caisseParams.getStartEnd() == null) {
                LocalDateTime debut = LocalDateTime.of(caisseParams.getStartDate(), caisseParams.getStartHour());
                LocalTime hfin = LocalTime.now();
                LocalDateTime fin = LocalDateTime.of(caisseParams.getEnd(), hfin);
                Predicate btw = cb.between(
                        cb.function("TIMESTAMP", Timestamp.class, root.get(TCashTransaction_.dtUPDATED)),
                        java.sql.Timestamp.valueOf(debut), java.sql.Timestamp.valueOf(fin));
                predicates.add(cb.and(btw));
            } else if (caisseParams.getStartHour() != null && caisseParams.getStartEnd() != null) {
                LocalDateTime debut = LocalDateTime.of(caisseParams.getStartDate(), caisseParams.getStartHour());
                LocalDateTime fin = LocalDateTime.of(caisseParams.getEnd(), caisseParams.getStartEnd());
                Predicate btw = cb.between(
                        cb.function("TIMESTAMP", Timestamp.class, root.get(TCashTransaction_.dtUPDATED)),
                        java.sql.Timestamp.valueOf(debut), java.sql.Timestamp.valueOf(fin));
                predicates.add(cb.and(btw));
            } else if (caisseParams.getStartHour() == null && caisseParams.getStartEnd() != null) {
                LocalDateTime debut = LocalDateTime.of(caisseParams.getStartDate(), LocalTime.of(0, 0, 0));
                LocalDateTime fin = LocalDateTime.of(caisseParams.getEnd(), caisseParams.getStartEnd());
                Predicate btw = cb.between(
                        cb.function("TIMESTAMP", Timestamp.class, root.get(TCashTransaction_.dtUPDATED)),
                        java.sql.Timestamp.valueOf(debut), java.sql.Timestamp.valueOf(fin));
                predicates.add(cb.and(btw));
            } else if (caisseParams.getStartHour() == null && caisseParams.getStartEnd() == null) {
                Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TCashTransaction_.dtUPDATED)),
                        java.sql.Date.valueOf(caisseParams.getStartDate()),
                        java.sql.Date.valueOf(caisseParams.getEnd()));
                predicates.add(cb.and(btw));
            }
            if (caisseParams.getTypeReglementId() != null) {
                predicates.add(cb.and(
                        cb.equal(root.get(TCashTransaction_.lgTYPEREGLEMENTID), caisseParams.getTypeReglementId())));
            }
            if (caisseParams.getUtilisateurId() != null) {
                predicates.add(cb.and(cb.equal(root.get("lgUSERID").get("lgUSERID"), caisseParams.getUtilisateurId())));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<TCashTransaction> q = getEntityManager().createQuery(cq);
            if (!all) {
                q.setFirstResult(caisseParams.getStart());
                q.setMaxResults(caisseParams.getLimit());
            }
            return q.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return data;
    }

    @Override
    public Integer totalVenteDepot(LocalDate dtStart, LocalDate dtEnd, String empl) {
        try {
            Query q = getEntityManager().createQuery(
                    "SELECT SUM(o.intPRICE) FROM TPreenregistrement o WHERE o.intPRICE >0 AND o.bISCANCEL=FALSE AND FUNCTION('DATE',o.dtUPDATED) BETWEEN ?1 AND ?2 AND o.lgTYPEVENTEID.lgTYPEVENTEID=?3 AND o.lgUSERID.lgEMPLACEMENTID.lgEMPLACEMENTID=?4 ");
            q.setParameter(1, java.sql.Date.valueOf(dtStart), TemporalType.DATE);
            q.setParameter(2, java.sql.Date.valueOf(dtEnd), TemporalType.DATE);
            q.setParameter(3, DateConverter.DEPOT_EXTENSION);
            q.setParameter(4, empl);
            Long t = (Long) q.getSingleResult();
            if (t != null) {
                return t.intValue();
            }
            return 0;

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    @Override
    public List<MvtTransaction> venteDepot(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId) {
        try {
            TypedQuery<MvtTransaction> query = getEntityManager().createQuery(
                    "SELECT o FROM MvtTransaction o WHERE o.mvtDate BETWEEN :dtStart AND :dtEnd AND o.magasin.lgEMPLACEMENTID <>:empl AND o.checked=:checked AND o.typeTransaction IN :typetransac ",
                    MvtTransaction.class);
            query.setParameter("dtStart", dtStart);
            query.setParameter("dtEnd", dtEnd);
            query.setParameter("empl", emplacementId);
            query.setParameter("checked", checked);
            query.setParameter("typetransac", EnumSet.of(TypeTransaction.VENTE_COMPTANT, TypeTransaction.VENTE_CREDIT));
            return query.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }

    }

    private List<TBonLivraison> findBonLivraisons(LocalDate dtStart, LocalDate dtEnd, int start, int limit,
            boolean all) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TBonLivraison> cq = cb.createQuery(TBonLivraison.class);
            Root<TBonLivraison> root = cq.from(TBonLivraison.class);
            cq.select(root);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TBonLivraison_.dtDATELIVRAISON)),
                    java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
            predicates.add(cb.and(btw));
            predicates.add(cb.and(cb.equal(root.get(TBonLivraison_.strSTATUT), DateConverter.STATUT_IS_CLOSED)));
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            Query q = getEntityManager().createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);
            }

            return q.getResultList();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<TableauBaordSummary, List<TableauBaordPhDTO>> tableauBoardDatas(LocalDate dtStart, LocalDate dtEnd,
            Boolean checked, TUser user, int ration, int start, int limit, boolean all) {
        TEmplacement emp = user.getLgEMPLACEMENTID();
        List<MvtTransaction> transactions = donneestableauboard(dtStart, dtEnd, checked, emp.getLgEMPLACEMENTID(),
                start, limit, all);
        if (key_Params() || key_Take_Into_Account()) {
            return buillTableauBoardData0(transactions);
        } else {
            return buillTableauBoardData(transactions);
        }

    }

    private List<TableauBaordPhDTO> buillTableauBoardData(List<TableauBaordPhDTO> tps,
            List<TableauBaordPhDTO> especesCl, List<TableauBaordPhDTO> annulationCl,
            List<TableauBaordPhDTO> annulationAnterieurCl, List<TBonLivraison> bonLivraisons) {
        Stream<TableauBaordPhDTO> donneecChaVentes = Stream.concat(tps.parallelStream(), especesCl.parallelStream());
        Stream<TableauBaordPhDTO> annulationcChaVentes = Stream.concat(annulationCl.parallelStream(),
                annulationAnterieurCl.parallelStream());
        Map<LocalDate, List<TableauBaordPhDTO>> liste = Stream.concat(donneecChaVentes, annulationcChaVentes)
                .collect(Collectors.groupingBy(TableauBaordPhDTO::getMvtDate));

        List<TableauBaordPhDTO> tableauBaords = new ArrayList<>();
        List<TableauBaordPhDTO> tableauBaordsAchat = new ArrayList<>();

        Map<LocalDate, List<TBonLivraison>> mapbl = bonLivraisons.parallelStream()
                .collect(Collectors.groupingBy(o -> DateConverter.convertDateToLocalDate(o.getDtDATELIVRAISON())));
        // boolean avoir = liste.isEmpty();
        liste.forEach((k, v) -> {
            TableauBaordPhDTO baordPh = new TableauBaordPhDTO();
            baordPh.setMvtDate(k);
            LongAdder montantEsp = new LongAdder(), montantCredit = new LongAdder(), montantNet = new LongAdder(),
                    montantRemise = new LongAdder(), nb = new LongAdder();
            // montantAvoir = new LongAdder();
            // montantAvoir.add(avoirFournisseur(k));
            v.stream().forEach(b -> {
                montantEsp.add(b.getMontantEsp());
                montantCredit.add(b.getMontantCredit());
                nb.add(b.getNbreVente());

                montantRemise.add(b.getMontantRemise());
                montantNet.add(b.getMontantNet());
            });

            Integer net = montantNet.intValue();
            Integer esp = montantEsp.intValue();
            // Integer ce = net - Math.abs(esp)+montantCredit.intValue();
            // System.out.println("net "+net+" "+esp+" "+);
            baordPh.setMontantCredit(montantCredit.intValue());
            baordPh.setMontantRemise(montantRemise.intValue());
            baordPh.setMontantNet(net);
            baordPh.setMontantEsp(esp);
            baordPh.setNbreVente(nb.intValue());
            tableauBaords.add(baordPh);
            // System.out.println("montantAvoir.intValue() --------- " + montantAvoir.intValue());
            // baordPh.setMontantAvoir(montantAvoir.intValue());

        });

        tableauBaords.sort(comparator);
        mapbl.forEach((k, v) -> {
            TableauBaordPhDTO baordPh = new TableauBaordPhDTO();
            baordPh.setMvtDate(k);

            LongAdder montantAchatOne = new LongAdder(), montantAchatTwo = new LongAdder(),
                    montantAchatThree = new LongAdder(), montantAchatFour = new LongAdder(),
                    montantAchatFive = new LongAdder(), montantAchat = new LongAdder();

            // if (avoir) {
            // LongAdder montantAvoir = new LongAdder();
            // montantAvoir.add(avoirFournisseur(k));
            // baordPh.setMontantAvoir(montantAvoir.intValue());
            // }
            v.stream().forEach(bl -> {
                Groupefournisseur g = bl.getLgORDERID().getLgGROSSISTEID().getGroupeId();
                switch (g.getLibelle()) {
                case DateConverter.LABOREXCI:
                    montantAchatOne.add(bl.getIntMHT());
                    montantAchat.add(bl.getIntMHT());
                    break;
                case DateConverter.DPCI:
                    montantAchatTwo.add(bl.getIntMHT());
                    montantAchat.add(bl.getIntMHT());
                    break;
                case DateConverter.COPHARMED:
                    montantAchatThree.add(bl.getIntMHT());
                    montantAchat.add(bl.getIntMHT());
                    break;
                case DateConverter.TEDIS:
                    montantAchatFour.add(bl.getIntMHT());
                    montantAchat.add(bl.getIntMHT());
                    break;
                case DateConverter.AUTRES:
                    montantAchatFive.add(bl.getIntMHT());
                    montantAchat.add(bl.getIntMHT());
                    break;
                default:
                    break;
                }
            });

            baordPh.setMontantAchatFive(montantAchatFive.intValue());
            baordPh.setMontantAchatTwo(montantAchatTwo.intValue());
            baordPh.setMontantAchatOne(montantAchatOne.intValue());
            baordPh.setMontantAchatThree(montantAchatThree.intValue());
            baordPh.setMontantAchatFour(montantAchatFour.intValue());
            baordPh.setMontantAchat(montantAchat.intValue());
            tableauBaordsAchat.add(baordPh);
        });
        tableauBaordsAchat.sort(comparator);
        List<TableauBaordPhDTO> finaltableauBaords = Stream
                .concat(tableauBaords.parallelStream(), tableauBaordsAchat.parallelStream())
                .collect(Collectors.toList());
        return finaltableauBaords;
    }

    private Map<TableauBaordSummary, List<TableauBaordPhDTO>> buillTableauBoardDataOld(
            List<TableauBaordPhDTO> tableauBaord) {
        if (tableauBaord.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<TableauBaordSummary, List<TableauBaordPhDTO>> summ = new HashMap<>();
        List<TableauBaordPhDTO> tableauBaords = new ArrayList<>();
        TableauBaordSummary summary = new TableauBaordSummary();
        Map<LocalDate, List<TableauBaordPhDTO>> map = tableauBaord.parallelStream()
                .collect(Collectors.groupingBy(o -> o.getMvtDate()));
        LongAdder _summontantTTC = new LongAdder(), _summontantNet = new LongAdder(),
                _summontantRemise = new LongAdder(), _summontantEsp = new LongAdder(),
                _summontantCredit = new LongAdder(), _sumnbreVente = new LongAdder(),
                _summontantAchatOne = new LongAdder(), _summontantAchatTwo = new LongAdder(),
                _summontantAchatThree = new LongAdder(), _summontantAchatFour = new LongAdder(),
                _summontantAchatFive = new LongAdder(), _summontantAvoir = new LongAdder(),
                _summontantAchat = new LongAdder();
        DoubleAdder _sumratioVA = new DoubleAdder(), _sumrationAV = new DoubleAdder();
        map.forEach((k, v) -> {
            TableauBaordPhDTO baordPh = new TableauBaordPhDTO();
            baordPh.setMvtDate(k);
            LongAdder montantTTC = new LongAdder(), montantNet = new LongAdder(), montantRemise = new LongAdder(),
                    montantEsp = new LongAdder(), montantCredit = new LongAdder(), nbreVente = new LongAdder(),
                    montantAchatOne = new LongAdder(), montantAchatTwo = new LongAdder(),
                    montantAchatThree = new LongAdder(), montantAchatFour = new LongAdder(),
                    montantAchatFive = new LongAdder(), montantAchat = new LongAdder(), montantAvoir = new LongAdder();
            DoubleAdder ratioVA = new DoubleAdder(), rationAV = new DoubleAdder();

            montantAvoir.add(avoirFournisseur(k));
            v.forEach(s -> {
                montantEsp.add(s.getMontantEsp());
                montantNet.add(s.getMontantNet());
                montantCredit.add(s.getMontantCredit());
                montantRemise.add(s.getMontantRemise());
                nbreVente.add(s.getNbreVente());
                montantAchatThree.add(s.getMontantAchatThree());
                montantAchatOne.add(s.getMontantAchatOne());
                montantAchatTwo.add(s.getMontantAchatTwo());
                montantAchatFive.add(s.getMontantAchatFive());
                montantAchatFour.add(s.getMontantAchatFour());
                montantAchat.add(s.getMontantAchat());

            });

            Integer _montantNet = montantNet.intValue();
            Integer _montantAchat = montantAchat.intValue();
            if (_montantAchat.compareTo(0) > 0) {
                ratioVA.add(new BigDecimal(Double.valueOf(_montantNet) / _montantAchat).setScale(1, RoundingMode.FLOOR)
                        .doubleValue());
            }
            if (_montantNet.compareTo(0) > 0) {
                rationAV.add(new BigDecimal(Double.valueOf(_montantAchat) / _montantNet).setScale(1, RoundingMode.FLOOR)
                        .doubleValue());
            }
            baordPh.setMontantAchatFive(montantAchatFive.intValue());
            baordPh.setMontantAchatFour(montantAchatFour.intValue());
            baordPh.setMontantAchatThree(montantAchatThree.intValue());
            baordPh.setNbreVente(nbreVente.intValue());
            baordPh.setMontantAchatTwo(montantAchatTwo.intValue());
            baordPh.setMontantAchatOne(montantAchatOne.intValue());
            baordPh.setMontantAchat(montantAchat.intValue());
            baordPh.setRatioVA(ratioVA.doubleValue());
            baordPh.setRationAV(rationAV.doubleValue());
            baordPh.setMontantTTC(montantTTC.intValue());
            baordPh.setMontantNet(montantNet.intValue());
            baordPh.setMontantEsp(montantEsp.intValue());
            baordPh.setMontantRemise(montantRemise.intValue());
            baordPh.setMontantCredit(montantCredit.intValue());
            baordPh.setMontantAvoir(montantAvoir.intValue());

            /**
             * ** ***************
             */
            _summontantAchatFive.add(baordPh.getMontantAchatFive());
            _summontantAchatFour.add(baordPh.getMontantAchatFour());
            _summontantAchatThree.add(baordPh.getMontantAchatThree());
            _sumnbreVente.add(baordPh.getNbreVente());
            _summontantAchatTwo.add(baordPh.getMontantAchatTwo());
            _summontantAchatOne.add(baordPh.getMontantAchatOne());
            _summontantAchat.add(baordPh.getMontantAchat());
            _summontantTTC.add(baordPh.getMontantTTC());
            _summontantNet.add(baordPh.getMontantNet());
            _summontantEsp.add(baordPh.getMontantEsp());
            _summontantRemise.add(baordPh.getMontantRemise());
            _summontantCredit.add(baordPh.getMontantCredit());
            _summontantAvoir.add(baordPh.getMontantAvoir());
            tableauBaords.add(baordPh);
        });
        Integer _montantNet = _summontantNet.intValue();
        Integer _montantAchat = _summontantAchat.intValue();
        if (_montantAchat.compareTo(0) > 0) {
            _sumratioVA.add(new BigDecimal(Double.valueOf(_montantNet) / _montantAchat).setScale(1, RoundingMode.FLOOR)
                    .doubleValue());
        }
        if (_montantNet.compareTo(0) > 0) {
            _sumrationAV.add(new BigDecimal(Double.valueOf(_montantAchat) / _montantNet).setScale(1, RoundingMode.FLOOR)
                    .doubleValue());
        }
        summary.setMontantAchatFive(_summontantAchatFive.intValue());
        summary.setMontantAchatFour(_summontantAchatFour.intValue());
        summary.setMontantAchatThree(_summontantAchatThree.intValue());
        summary.setNbreVente(_sumnbreVente.intValue());
        summary.setMontantAchatTwo(_summontantAchatTwo.intValue());
        summary.setMontantAchatOne(_summontantAchatOne.intValue());
        summary.setMontantAchat(_summontantAchat.intValue());
        summary.setRatioVA(_sumratioVA.doubleValue());
        summary.setRationAV(_sumrationAV.doubleValue());
        summary.setMontantTTC(_summontantTTC.intValue());
        summary.setMontantNet(_summontantNet.intValue());
        summary.setMontantEsp(_summontantEsp.intValue());
        summary.setMontantRemise(_summontantRemise.intValue());
        summary.setMontantCredit(_summontantCredit.intValue());
        summary.setMontantAvoir(_summontantAvoir.intValue());

        // .sorted(comparator) sort(comparator);
        summ.put(summary, tableauBaords.stream().sorted(comparator).collect(Collectors.toList()));

        return summ;
    }

    @Override
    public Map<TableauBaordSummary, List<TableauBaordPhDTO>> tableauBoardDatasOld(LocalDate dtStart, LocalDate dtEnd,
            Boolean checked, TUser user, int ration, int start, int limit, boolean all) {
        TEmplacement emp = user.getLgEMPLACEMENTID();
        List<TableauBaordPhDTO> especesCl = totalEspTableaudebord(dtStart, dtEnd, emp.getLgEMPLACEMENTID());
        List<TableauBaordPhDTO> annulationCl = annulerEspTableaudebord(dtStart, dtEnd, emp.getLgEMPLACEMENTID());
        List<TableauBaordPhDTO> annulationAnterieurCl = annulerTableaudebord(dtStart, dtEnd, emp.getLgEMPLACEMENTID());
        List<TableauBaordPhDTO> transactions = buillTableauBoardData(findPreenregistrements(dtStart, dtEnd, emp),
                especesCl, annulationCl, annulationAnterieurCl, findBonLivraisons(dtStart, dtEnd, start, limit, all));
        return buillTableauBoardDataOld(transactions);
    }

    @Override
    public JSONObject tableauBoardDatasOld(LocalDate dtStart, LocalDate dtEnd, Boolean checked, TUser user, int start,
            int limit, boolean all) throws JSONException {
        TEmplacement emp = user.getLgEMPLACEMENTID();
        JSONObject json = new JSONObject();
        List<TableauBaordPhDTO> especesCl = totalEspTableaudebord(dtStart, dtEnd, emp.getLgEMPLACEMENTID());
        List<TableauBaordPhDTO> annulationCl = annulerEspTableaudebord(dtStart, dtEnd, emp.getLgEMPLACEMENTID());
        List<TableauBaordPhDTO> annulationAnterieurCl = annulerTableaudebord(dtStart, dtEnd, emp.getLgEMPLACEMENTID());
        List<TableauBaordPhDTO> transactions = buillTableauBoardData(findPreenregistrements(dtStart, dtEnd, emp),
                especesCl, annulationCl, annulationAnterieurCl, findBonLivraisons(dtStart, dtEnd, start, limit, all));
        Map<TableauBaordSummary, List<TableauBaordPhDTO>> map = buillTableauBoardDataOld(transactions);

        if (map.isEmpty()) {
            json.put("total", 0);
            json.put("data", new JSONArray());

        }
        map.forEach((k, v) -> {
            json.put("total", v.size());
            json.put("data", new JSONArray(v));
            json.put("metaData", new JSONObject(k));

        });
        return json;
    }

    private List<TableauBaordPhDTO> findPreenregistrements(LocalDate dtStart, LocalDate dtEnd, TEmplacement empl) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TableauBaordPhDTO> cq = cb.createQuery(TableauBaordPhDTO.class);
            Root<TPreenregistrement> root = cq.from(TPreenregistrement.class);
            cq.select(cb.construct(TableauBaordPhDTO.class, cb.sum(root.get(TPreenregistrement_.intPRICE)),
                    cb.sum(root.get(TPreenregistrement_.intPRICEREMISE)), root.get(TPreenregistrement_.dtUPDATED),
                    cb.count(root), cb.sum(root.get(TPreenregistrement_.intCUSTPART)),
                    root.get(TPreenregistrement_.strTYPEVENTE)))
                    .groupBy(cb.function("DATE", Date.class, root.get(TPreenregistrement_.dtUPDATED)),
                            root.get(TPreenregistrement_.strTYPEVENTE));
            predicates.add(cb.and(cb.isFalse(root.get(TPreenregistrement_.bISCANCEL))));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrement_.dtUPDATED)),
                    java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
            predicates.add(cb.and(btw));
            predicates.add(cb.and(cb.greaterThan(root.get(TPreenregistrement_.intPRICE), 0)));
            predicates.add(cb.and(cb.equal(root.get(TPreenregistrement_.strSTATUT), DateConverter.STATUT_IS_CLOSED)));
            predicates.add(cb.and(
                    cb.equal(root.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID).get("lgEMPLACEMENTID"),
                            empl.getLgEMPLACEMENTID())));
            predicates.add(cb.and(cb.notEqual(root.get(TPreenregistrement_.lgTYPEVENTEID).get("lgTYPEVENTEID"),
                    DateConverter.DEPOT_EXTENSION)));
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            Query q = getEntityManager().createQuery(cq);
            return q.getResultList();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    public List<TableauBaordPhDTO> totalEspTableaudebord(LocalDate dtStart, LocalDate dtEnd, String empla) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TableauBaordPhDTO> cq = cb.createQuery(TableauBaordPhDTO.class);
            Root<TCashTransaction> root = cq.from(TCashTransaction.class);
            cq.select(cb.construct(TableauBaordPhDTO.class, cb.sum(root.get(TCashTransaction_.intAMOUNT)),
                    root.get(TCashTransaction_.dtUPDATED)))
                    .groupBy(cb.function("DATE", Date.class, root.get(TCashTransaction_.dtUPDATED)));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TCashTransaction_.dtUPDATED)),
                    java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
            predicates.add(cb.and(btw));
            predicates.add(cb.and(cb.greaterThan(root.get(TCashTransaction_.intAMOUNT), 0)));
            predicates.add(cb.equal(
                    root.get(TCashTransaction_.lgUSERID).get(TUser_.lgEMPLACEMENTID).get(TEmplacement_.lgEMPLACEMENTID),
                    empla));
            predicates.add(cb.equal(root.get(TCashTransaction_.strTASK), DateConverter.TYPE_ACTION_VENTE));
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            Query q = getEntityManager().createQuery(cq);
            return q.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    public List<TableauBaordPhDTO> annulerEspTableaudebord(LocalDate dtStart, LocalDate dtEnd, String empla) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TableauBaordPhDTO> cq = cb.createQuery(TableauBaordPhDTO.class);
            Root<TCashTransaction> root = cq.from(TCashTransaction.class);
            cq.select(cb.construct(TableauBaordPhDTO.class, root.get(TCashTransaction_.dtUPDATED),
                    cb.sum(root.get(TCashTransaction_.intAMOUNTDEBIT))))
                    .groupBy(cb.function("DATE", Date.class, root.get(TCashTransaction_.dtUPDATED)));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TCashTransaction_.dtUPDATED)),
                    java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
            predicates.add(cb.and(btw));
            predicates.add(cb.equal(root.get(TCashTransaction_.strTASK), DateConverter.ACTION_ANNULATION_VENTE));
            predicates.add(cb.equal(
                    root.get(TCashTransaction_.lgUSERID).get(TUser_.lgEMPLACEMENTID).get(TEmplacement_.lgEMPLACEMENTID),
                    empla));
            predicates.add(cb.and(cb.isTrue(root.get(TCashTransaction_.boolCHECKED))));
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            TypedQuery<TableauBaordPhDTO> q = getEntityManager().createQuery(cq);
            return q.getResultList();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }

    public List<TableauBaordPhDTO> annulerTableaudebord(LocalDate dtStart, LocalDate dtEnd, String empla) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TableauBaordPhDTO> cq = cb.createQuery(TableauBaordPhDTO.class);
            Root<AnnulationSnapshot> root = cq.from(AnnulationSnapshot.class);
            cq.select(cb.construct(TableauBaordPhDTO.class, root.get(AnnulationSnapshot_.createdAt),
                    cb.sum(root.get(AnnulationSnapshot_.montant)), cb.sum(root.get(AnnulationSnapshot_.remise)),
                    cb.sum(root.get(AnnulationSnapshot_.montantPaye)), cb.sum(root.get(AnnulationSnapshot_.montantTP)),
                    cb.sum(root.get(AnnulationSnapshot_.montantRestant))))
                    .groupBy(cb.function("DATE", Date.class, root.get(AnnulationSnapshot_.dateOp)));

            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(AnnulationSnapshot_.dateOp)),
                    java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
            predicates.add(cb.and(btw));
            predicates
                    .add(cb.equal(root.get(AnnulationSnapshot_.emplacement).get(TEmplacement_.lgEMPLACEMENTID), empla));
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            TypedQuery<TableauBaordPhDTO> q = getEntityManager().createQuery(cq);
            return q.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    private Integer montantAnnuleGestionCaisse(String userId, LocalDateTime dtStart, LocalDateTime dtEnd,
            boolean isSummary) {
        List<Predicate> predicates = new ArrayList<>();
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<MvtTransaction> cq = cb.createQuery(MvtTransaction.class);
            Root<MvtTransaction> root = cq.from(MvtTransaction.class);
            cq.select(root);
            if (isSummary) {
                predicates
                        .add(cb.between(root.get(MvtTransaction_.mvtDate), dtStart.toLocalDate(), dtEnd.toLocalDate()));
            } else {
                predicates.add(cb.between(root.get(MvtTransaction_.createdAt), dtStart, dtEnd));
            }
            predicates.add(cb.equal(root.get(MvtTransaction_.caisse).get(TUser_.lgUSERID), userId));
            predicates.add(cb.equal(root.get(MvtTransaction_.categoryTransaction), CategoryTransaction.DEBIT));
            predicates.add(root.get(MvtTransaction_.typeTransaction).in(TypeTransaction.VENTE_CREDIT,
                    TypeTransaction.VENTE_COMPTANT));
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            TypedQuery<MvtTransaction> q = getEntityManager().createQuery(cq);
            List<MvtTransaction> list = q.getResultList();
            return list.stream().map(x -> Math.abs(x.getMontantRegle())).reduce(0, Integer::sum);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    // la valeur des annulations pour une caisse fermee pendant l'annulation de la vente
    private Integer montantAnnuleRecette(String userId, LocalDate dtStart, LocalDate dtEnd) {
        List<Predicate> predicates = new ArrayList<>();
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<AnnulationRecette> cq = cb.createQuery(AnnulationRecette.class);
            Root<AnnulationRecette> root = cq.from(AnnulationRecette.class);
            cq.select(root);

            predicates.add(cb.between(root.get(AnnulationRecette_.mvtDate), dtStart, dtEnd));
            if (userId != null && !userId.isEmpty()) {
                predicates.add(cb.equal(root.get(AnnulationRecette_.caissier).get(TUser_.lgUSERID), userId));
            }

            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            TypedQuery<AnnulationRecette> q = getEntityManager().createQuery(cq);
            List<AnnulationRecette> list = q.getResultList();
            return list.stream().map(x -> Math.abs(x.getMontantRegle())).reduce(0, Integer::sum);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    private Integer caisseSummary(LocalDate dtStart, LocalDate dtEnd, TUser u, Boolean allActivite, String userId)
            throws JSONException {
        List<Predicate> predicates = new ArrayList<>();
        try {
            TEmplacement emp = u.getLgEMPLACEMENTID();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<TResumeCaisse> root = cq.from(TResumeCaisse.class);
            cq.select(cb.sum(root.get(TResumeCaisse_.intSOLDESOIR)));
            if (!allActivite) {
                predicates.add(cb.and(cb.equal(root.get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"),
                        emp.getLgEMPLACEMENTID())));
            }
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TResumeCaisse_.dtCREATED)),
                    java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
            predicates.add(cb.and(btw));
            if (userId != null && !"".equals(userId)) {
                predicates.add(cb.and(cb.equal(root.get("lgUSERID").get("lgUSERID"), userId)));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            Query q = getEntityManager().createQuery(cq);
            Integer total = (Integer) q.getSingleResult();
            return total != null
                    ? total - (montantAnnuleGestionCaisse(null, dtStart.atStartOfDay(), dtEnd.atStartOfDay(), true)
                            + montantAnnuleRecette(null, dtStart, dtEnd))
                    : (-1) * montantAnnuleGestionCaisse(null, dtStart.atStartOfDay(), dtEnd.atStartOfDay(), true)
                            + montantAnnuleRecette(null, dtStart, dtEnd);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }

    }

    private long caisseSummaryCount(LocalDate dtStart, LocalDate dtEnd, TUser u, Boolean allActivite, String userId) {
        List<Predicate> predicates = new ArrayList<>();
        try {
            TEmplacement emp = u.getLgEMPLACEMENTID();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TResumeCaisse> root = cq.from(TResumeCaisse.class);
            cq.select(cb.count(root));
            if (!allActivite) {
                predicates.add(cb.and(cb.equal(root.get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"),
                        emp.getLgEMPLACEMENTID())));
            }
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TResumeCaisse_.dtCREATED)),
                    java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
            predicates.add(cb.and(btw));
            if (userId != null && !"".equals(userId)) {
                predicates.add(cb.and(cb.equal(root.get("lgUSERID").get("lgUSERID"), userId)));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            Query q = getEntityManager().createQuery(cq);
            return (long) q.getSingleResult();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    private List<Predicate> mouvementCaissesPredicats(CriteriaBuilder cb, Root<MvtTransaction> root,
            CaisseParamsDTO caisseParams) {
        List<Predicate> predicates = new ArrayList<>();
        Predicate btw = cb.between(root.get(MvtTransaction_.mvtDate), caisseParams.getStartDate(),
                caisseParams.getEnd());
        predicates.add(btw);
        predicates.add(cb.equal(root.get(MvtTransaction_.magasin).get(TEmplacement_.lgEMPLACEMENTID),
                caisseParams.getEmplacementId()));
        predicates.add(root.get(MvtTransaction_.tTypeMvtCaisse).get(TTypeMvtCaisse_.lgTYPEMVTCAISSEID).in(
                DateConverter.MVT_FOND_CAISSE, DateConverter.MVT_REGLE_TP, DateConverter.MVT_REGLE_DIFF,
                DateConverter.MVT_ENTREE_CAISSE, DateConverter.MVT_SORTIE_CAISSE));
        if (caisseParams.getUtilisateurId() != null) {
            predicates.add(cb.and(
                    cb.equal(root.get(MvtTransaction_.caisse).get(TUser_.lgUSERID), caisseParams.getUtilisateurId())));
        }

        return predicates;
    }

    @Override
    public List<VisualisationCaisseDTO> mouvementCaisses(CaisseParamsDTO caisseParams, boolean all) {
        try {
            List<Predicate> predicates;
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<MvtTransaction> cq = cb.createQuery(MvtTransaction.class);
            Root<MvtTransaction> root = cq.from(MvtTransaction.class);
            cq.select(root).orderBy(cb.asc(root.get(MvtTransaction_.createdAt)));
            predicates = mouvementCaissesPredicats(cb, root, caisseParams);
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            TypedQuery<MvtTransaction> q = getEntityManager().createQuery(cq);
            if (!all) {
                q.setFirstResult(caisseParams.getStart());
                q.setMaxResults(caisseParams.getLimit());
            }
            return q.getResultList().stream()
                    .map(x -> new VisualisationCaisseDTO(x,
                            isReglementTierspayant(x.gettTypeMvtCaisse().getLgTYPEMVTCAISSEID())
                                    ? findClientByVenteId(x.getOrganisme()) : null,
                            isReglementTierspayant(x.gettTypeMvtCaisse().getLgTYPEMVTCAISSEID())
                                    ? findTiersPayantId(x.getOrganisme()) : null))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    private Long countMouvementCaisses(CaisseParamsDTO caisseParams) {
        try {
            List<Predicate> predicates;
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<MvtTransaction> root = cq.from(MvtTransaction.class);
            cq.select(cb.count(root));
            predicates = mouvementCaissesPredicats(cb, root, caisseParams);
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            TypedQuery<Long> q = getEntityManager().createQuery(cq);
            return q.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return 0l;
        }
    }

    private boolean isReglementTierspayant(String typeReglement) {
        return typeReglement.equals(DateConverter.MVT_REGLE_TP);
    }

    private TTiersPayant findTiersPayantId(String id) {
        try {
            return getEntityManager().find(TTiersPayant.class, id);

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public JSONObject mouvementCaisses(CaisseParamsDTO caisseParams) throws JSONException {
        JSONObject json = new JSONObject();
        long total = countMouvementCaisses(caisseParams);
        if (total == 0) {
            return json.put("total", 0).put("data", new JSONArray());
        }
        List<SumCaisseDTO> summary = summaryMouvementCaisses(caisseParams);
        List<VisualisationCaisseDTO> data = mouvementCaisses(caisseParams, false);
        return json.put("total", total).put("data", new JSONArray(data)).put("metaData", new JSONArray(summary));

    }

    private List<SumCaisseDTO> summaryMouvementCaisses(CaisseParamsDTO caisseParams) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<SumCaisseDTO> cq = cb.createQuery(SumCaisseDTO.class);
            Root<MvtTransaction> root = cq.from(MvtTransaction.class);
            cq.select(cb.construct(SumCaisseDTO.class, root.get(MvtTransaction_.reglement).get(TTypeReglement_.strNAME),
                    cb.sumAsLong(root.get(MvtTransaction_.montant)))).groupBy(root.get(MvtTransaction_.reglement));
            predicates = mouvementCaissesPredicats(cb, root, caisseParams);
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            Query q = getEntityManager().createQuery(cq);
            return q.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public Integer montantCa(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId,
            TypeTransaction transaction, String typrReglement) {
        try {
            TypedQuery<Long> query = getEntityManager().createQuery(
                    "SELECT COALESCE(SUM(o.montantPaye),0) FROM MvtTransaction o WHERE o.mvtDate BETWEEN ?1 AND ?2 AND o.magasin.lgEMPLACEMENTID=?3 AND o.checked=?4 AND o.typeTransaction =?5 AND o.reglement.lgTYPEREGLEMENTID=?6",
                    Long.class);
            query.setParameter(1, dtStart);
            query.setParameter(2, dtEnd);
            query.setParameter(3, emplacementId);
            query.setParameter(4, checked);
            query.setParameter(5, transaction);
            query.setParameter(6, typrReglement);
            return query.getSingleResult().intValue();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    @Override
    public List<TPreenregistrement> getTtVente(String dt_start, String dt_end, String lgEmp) {

        try {

            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TPreenregistrement> cq = cb.createQuery(TPreenregistrement.class);
            Root<TPreenregistrement> root = cq.from(TPreenregistrement.class);
            Join<TPreenregistrement, TUser> pu = root.join("lgUSERID", JoinType.INNER);
            Join<TPreenregistrement, TReglement> pr = root.join("lgREGLEMENTID", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.equal(pu.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.bISCANCEL), Boolean.FALSE));
            predicate = cb.and(predicate,
                    cb.notLike(root.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), Parameter.VENTE_DEPOT_EXTENSION));
            predicate = cb.and(predicate,
                    cb.equal(root.get(TPreenregistrement_.strSTATUT), DateConverter.STATUT_IS_CLOSED));
            predicate = cb.and(predicate,
                    cb.equal(root.get(TPreenregistrement_.strTYPEVENTE), DateConverter.VENTE_COMPTANT));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.intPRICEREMISE), 0));
            predicate = cb.and(predicate,
                    cb.equal(pr.get("lgMODEREGLEMENTID").get("lgMODEREGLEMENTID"), DateConverter.MODE_ESP));
            Predicate ge = cb.greaterThan(root.get(TPreenregistrement_.intPRICE), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrement_.dtUPDATED)),
                    java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            cq.select(root).orderBy(cb.desc(root.get(TPreenregistrement_.intPRICE)));
            cq.where(predicate, btw, ge);
            Query q = em.createQuery(cq);

            return q.getResultList();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }

    }

    @Override
    public MvtTransaction findByVenteId(String venteId) {
        try {
            TypedQuery<MvtTransaction> q = getEntityManager()
                    .createQuery("SELECT o FROM  MvtTransaction o WHERE o.pkey=?1", MvtTransaction.class);
            q.setMaxResults(1);
            q.setParameter(1, venteId);
            return q.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }

    private GenericDTO balanceFormat__(List<MvtTransaction> mvtTransactions) {
        List<BalanceDTO> balances = new ArrayList<>();
        GenericDTO generic = new GenericDTO();
        SummaryDTO summary = new SummaryDTO();

        if (!mvtTransactions.isEmpty()) {
            Map<TypeTransaction, List<MvtTransaction>> map = mvtTransactions.stream()
                    .collect(Collectors.groupingBy(o -> o.getTypeTransaction()));
            List<MvtTransaction> venteVNO = map.get(TypeTransaction.VENTE_COMPTANT);
            List<MvtTransaction> venteVO = map.get(TypeTransaction.VENTE_CREDIT);
            List<MvtTransaction> achats = map.get(TypeTransaction.ACHAT);
            List<MvtTransaction> entreesCaisse = map.get(TypeTransaction.ENTREE);
            List<MvtTransaction> sortieCaisse = map.get(TypeTransaction.SORTIE);
            BalanceDTO vno = null;
            Integer pourcentageVo;
            long _montantTTC = 0, _montantNet = 0, _montantRemise = 0, _montantEsp = 0, _montantCheque = 0,
                    _MontantVirement = 0, _montantCB = 0, _montantDiff = 0, _nbreVente = 0, montantAchat = 0,
                    montantSortie = 0, marge = 0, fondCaisse = 0, montantReglDiff = 0, montantRegleTp = 0,
                    montantEntre = 0, montantTva = 0, montantTp = 0, _montantMobilePayment = 0;
            double ratioVA = 0.0;
            if (venteVNO != null) {
                vno = new BalanceDTO();
                vno.setTypeVente("VNO");
                int montantTTC = 0, montantNet = 0, montantRemise = 0, panierMoyen = 0, montantEsp = 0,
                        montantCheque = 0, MontantVirement = 0, montantCB = 0, montantDiff = 0, nbreVente = 0,
                        montantMobilePayment = 0;

                for (MvtTransaction mvt : venteVNO) {
                    // montantRemise += mvt.getMontantRemise();
                    int remiseNonPara = 0;
                    if (Math.abs(mvt.getMontantRemise()) > 0) {
                        remiseNonPara = remiseNonPara(mvt.getPkey());
                    }

                    int newAmount = (mvt.getTypeTransaction() == TypeTransaction.VENTE_COMPTANT
                            && mvt.getMontantAcc().compareTo(mvt.getMontant()) == 0) ? mvt.getMontant()
                                    : mvt.getMontant() - mvt.getMontantAcc();
                    montantRemise += remiseNonPara;
                    montantTTC += (newAmount /*- mvt.getMontantttcug()*/);
                    long montantNonPara = ((newAmount - remiseNonPara) /*- mvt.getMontantnetug()*/);
                    montantNet += montantNonPara;
                    montantTva += (mvt.getMontantTva()/* - mvt.getMontantTvaUg() */);
                    marge += (mvt.getMarge() /*- mvt.getMargeug()*/);
                    montantDiff += mvt.getMontantRestant();
                    if (mvt.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                        nbreVente++;
                    }
                    switch (mvt.getReglement().getLgTYPEREGLEMENTID()) {
                    case DateConverter.MODE_ESP:
                        montantEsp += montantNonPara;
                        break;
                    case DateConverter.MODE_CHEQUE:
                        montantCheque += montantNonPara;
                        break;
                    case DateConverter.MODE_CB:
                        montantCB += montantNonPara;
                        break;
                    case DateConverter.MODE_VIREMENT:
                        MontantVirement += montantNonPara;
                        break;
                    case DateConverter.MODE_MOOV:
                    case DateConverter.TYPE_REGLEMENT_ORANGE:
                    case DateConverter.MODE_MTN:
                    case DateConverter.MODE_WAVE:
                        montantMobilePayment += montantNonPara;
                        break;
                    }

                }
                _montantTTC += montantTTC;
                _montantNet += montantNet;
                _MontantVirement += MontantVirement;
                _montantCB += montantCB;
                _montantCheque += montantCheque;
                _montantEsp += montantEsp;
                _montantRemise += montantRemise;
                _montantDiff += montantDiff;
                _nbreVente += nbreVente;
                _montantMobilePayment += montantMobilePayment;
                if (nbreVente > 0) {
                    panierMoyen = montantTTC / nbreVente;
                }

                vno.setMontantCB(montantCB);
                vno.setMontantCheque(montantCheque);
                vno.setMontantEsp(montantEsp);
                vno.setMontantDiff(montantDiff);
                vno.setMontantNet(montantNet);
                vno.setMontantTTC(montantTTC);
                vno.setMontantVirement(MontantVirement);
                vno.setNbreVente(nbreVente);
                vno.setMontantRemise(montantRemise);
                vno.setMontantTp(0);
                vno.setMontantMobilePayment(montantMobilePayment);
                vno.setPanierMoyen(panierMoyen);

            }

            BalanceDTO vo = null;
            if (venteVO != null) {
                vo = new BalanceDTO();
                vo.setTypeVente("VO");
                long montantTTC = 0, montantNet = 0, montantRemise = 0, panierMoyen = 0, montantEsp = 0,
                        montantCheque = 0, MontantVirement = 0, montantCB = 0, montantDiff = 0, nbreVente = 0,
                        montantMobilePayment = 0;

                for (MvtTransaction mvt : venteVO) {
                    montantTTC += mvt.getMontant();
                    montantNet += mvt.getMontantNet();
                    montantRemise += mvt.getMontantRemise();
                    montantTva += mvt.getMontantTva();
                    marge += mvt.getMarge();
                    montantDiff += mvt.getMontantRestant();
                    montantTp += mvt.getMontantCredit();
                    if (mvt.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                        nbreVente++;

                    }
                    switch (mvt.getReglement().getLgTYPEREGLEMENTID()) {
                    case DateConverter.MODE_ESP:
                        montantEsp += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_CHEQUE:
                        montantCheque += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_CB:
                        montantCB += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_VIREMENT:
                        MontantVirement += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_MOOV:
                    case DateConverter.TYPE_REGLEMENT_ORANGE:
                    case DateConverter.MODE_MTN:
                    case DateConverter.MODE_WAVE:
                        montantMobilePayment += mvt.getMontantRegle();
                        break;
                    }

                }
                if (nbreVente > 0) {
                    panierMoyen = montantTTC / nbreVente;
                }

                vo.setMontantCB(montantCB);
                vo.setMontantCheque(montantCheque);
                vo.setMontantEsp(montantEsp);
                vo.setMontantDiff(montantDiff);
                vo.setMontantNet(montantNet);
                vo.setMontantTTC(montantTTC);
                vo.setMontantVirement(MontantVirement);
                vo.setNbreVente(nbreVente);
                vo.setMontantRemise(montantRemise);
                vo.setMontantTp(montantTp);
                vo.setPanierMoyen(panierMoyen);
                vo.setMontantMobilePayment(montantMobilePayment);
                _montantTTC += montantTTC;
                _montantMobilePayment += montantMobilePayment;
                _montantNet += montantNet;
                _MontantVirement += MontantVirement;
                _montantCB += montantCB;
                _montantCheque += montantCheque;
                _montantEsp += montantEsp;
                _montantRemise += montantRemise;
                _montantDiff += montantDiff;
                _nbreVente += nbreVente;

            }
            if (vo != null) {
                pourcentageVo = (int) Math.round((Double.valueOf(vo.getMontantNet()) * 100) / Math.abs(_montantNet));
                vo.setPourcentage(pourcentageVo);
                balances.add(vo);
            }
            if (vno != null) {
                int pourcentageVno = (int) Math
                        .round((Double.valueOf(vno.getMontantNet()) * 100) / Math.abs(_montantNet));
                vno.setPourcentage(pourcentageVno);
                balances.add(vno);
            }

            if (achats != null) {
                montantAchat = achats.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);

            }
            if (sortieCaisse != null) {
                Map<String, List<MvtTransaction>> typeMvt = sortieCaisse.parallelStream()
                        .collect(Collectors.groupingBy(o -> o.gettTypeMvtCaisse().getLgTYPEMVTCAISSEID()));
                List<MvtTransaction> fond = typeMvt.get(DateConverter.MVT_FOND_CAISSE);
                List<MvtTransaction> sortie = typeMvt.get(DateConverter.MVT_SORTIE_CAISSE);
                if (fond != null) {
                    fondCaisse = fond.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }
                if (sortie != null) {
                    montantSortie = sortie.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }

            }
            if (entreesCaisse != null) {
                Map<String, List<MvtTransaction>> typeMvtEntree = entreesCaisse.parallelStream()
                        .collect(Collectors.groupingBy(o -> o.gettTypeMvtCaisse().getLgTYPEMVTCAISSEID()));
                List<MvtTransaction> entree = typeMvtEntree.get(DateConverter.MVT_ENTREE_CAISSE);
                List<MvtTransaction> diff = typeMvtEntree.get(DateConverter.MVT_REGLE_DIFF);
                List<MvtTransaction> reglementTp = typeMvtEntree.get(DateConverter.MVT_REGLE_TP);
                if (entree != null) {
                    montantEntre = entree.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }
                if (diff != null) {
                    montantReglDiff = diff.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }
                if (reglementTp != null) {
                    montantRegleTp = reglementTp.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0,
                            Long::sum);
                }
            }
            if (montantAchat > 0) {
                ratioVA = Double.valueOf(_montantTTC) / montantAchat;
                ratioVA = new BigDecimal(ratioVA).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
            summary.setFondCaisse(fondCaisse);
            summary.setMarge(marge);
            summary.setMontantAchat(montantAchat);
            summary.setMontantCB(_montantCB);
            summary.setMontantCheque(_montantCheque);
            summary.setMontantDiff(_montantDiff);
            summary.setMontantRegDiff(montantReglDiff);
            summary.setMontantEntre(montantEntre);
            summary.setMontantEsp(_montantEsp);
            summary.setMontantNet(_montantNet);
            summary.setMontantSortie(montantSortie);
            summary.setMontantVirement(_MontantVirement);
            summary.setMontantMobilePayment(_montantMobilePayment);
            summary.setMontantHT((_montantTTC - montantTva));
            summary.setMontantRegleTp(montantRegleTp);
            summary.setMontantRemise(_montantRemise);
            summary.setMontantTva(montantTva);
            summary.setNbreVente(_nbreVente);
            summary.setMontantTTC(_montantTTC);
            if (_nbreVente > 0) {
                summary.setPanierMoyen(_montantTTC / _nbreVente);
            }
            summary.setRatioVA(ratioVA);
            summary.setMontantTp(montantTp);

        }
        generic.setBalances(balances);
        generic.setSummary(summary);
        return generic;
    }

    @Override
    public boolean key_Take_Into_Account() {
        try {

            TParameters tp = getEntityManager().find(TParameters.class, DateConverter.KEY_TAKE_INTO_ACCOUNT);
            return (Integer.parseInt(tp.getStrVALUE()) == 1);

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean key_Params() {
        try {

            TParameters tp = getEntityManager().find(TParameters.class, DateConverter.KEY_PARAMS);
            return (Integer.parseInt(tp.getStrVALUE()) == 1);

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long montantAccount(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId,
            TypeTransaction transaction, String typrReglement) {
        try {
            TypedQuery<Long> query = getEntityManager().createQuery(
                    "SELECT COALESCE(SUM(o.montantAcc),0) FROM MvtTransaction o WHERE o.mvtDate BETWEEN ?1 AND ?2 AND o.magasin.lgEMPLACEMENTID=?3 AND o.checked=?4 AND o.typeTransaction =?5 AND o.reglement.lgTYPEREGLEMENTID=?6",
                    Long.class);
            query.setParameter(1, dtStart);
            query.setParameter(2, dtEnd);
            query.setParameter(3, emplacementId);
            query.setParameter(4, checked);
            query.setParameter(5, transaction);
            query.setParameter(6, typrReglement);
            return query.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    @Override
    public long montantAccount(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId,
            TypeTransaction transaction, String typrReglement, String typeMvtCaisse) {
        try {
            TypedQuery<Long> query = getEntityManager().createQuery(
                    "SELECT COALESCE(SUM(o.montantAcc),0) FROM MvtTransaction o WHERE o.mvtDate BETWEEN ?1 AND ?2 AND o.magasin.lgEMPLACEMENTID=?3 AND o.checked=?4 AND o.typeTransaction =?5 AND o.reglement.lgTYPEREGLEMENTID=?6 AND o.tTypeMvtCaisse.lgTYPEMVTCAISSEID=?7 AND o.flaged=TRUE",
                    Long.class);
            query.setParameter(1, dtStart);
            query.setParameter(2, dtEnd);
            query.setParameter(3, emplacementId);
            query.setParameter(4, checked);
            query.setParameter(5, transaction);
            query.setParameter(6, typrReglement);
            query.setParameter(7, typeMvtCaisse);
            return query.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    @Override
    public long montantAccount(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TypeTransaction transaction,
            String typrReglement, String typeMvtCaisse) {
        try {
            TypedQuery<Long> query = getEntityManager().createQuery(
                    "SELECT COALESCE(SUM(o.montant),0)- COALESCE(SUM(o.montantAcc),0) AS montant FROM MvtTransaction o WHERE o.mvtDate BETWEEN ?1 AND ?2 AND o.magasin.lgEMPLACEMENTID=?3 AND o.checked=?4 AND o.typeTransaction =?5 AND o.flaged=TRUE",
                    Long.class);
            query.setParameter(1, dtStart);
            query.setParameter(2, dtEnd);
            query.setParameter(3, emplacementId);
            query.setParameter(4, true);
            query.setParameter(5, transaction);
            return query.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    @Override
    public TOfficine findOfficine() {
        return getEntityManager().find(TOfficine.class, "1");
    }

    private GenericDTO balanceFormatter(List<MvtTransaction> mvtTransactions) {
        List<BalanceDTO> balances = new ArrayList<>();
        GenericDTO generic = new GenericDTO();
        SummaryDTO summary = new SummaryDTO();
        if (!mvtTransactions.isEmpty()) {
            Map<TypeTransaction, List<MvtTransaction>> map = mvtTransactions.parallelStream()
                    .collect(Collectors.groupingBy(o -> o.getTypeTransaction()));
            List<MvtTransaction> venteVNO = map.get(TypeTransaction.VENTE_COMPTANT);
            List<MvtTransaction> venteVO = map.get(TypeTransaction.VENTE_CREDIT);
            List<MvtTransaction> achats = map.get(TypeTransaction.ACHAT);
            List<MvtTransaction> entreesCaisse = map.get(TypeTransaction.ENTREE);
            List<MvtTransaction> sortieCaisse = map.get(TypeTransaction.SORTIE);
            BalanceDTO vno = null;
            int pourcentageVo;
            int _montantTTC = 0, _montantNet = 0, _montantRemise = 0, _montantEsp = 0, _montantCheque = 0,
                    _MontantVirement = 0, _montantCB = 0, _montantDiff = 0, _nbreVente = 0, montantAchat = 0,
                    montantSortie = 0, marge = 0, fondCaisse = 0, montantReglDiff = 0, montantRegleTp = 0,
                    montantEntre = 0, montantTva = 0, montantTp = 0, _montantMobilePayment = 0;
            double ratioVA = 0.0;
            if (venteVNO != null) {
                vno = new BalanceDTO();
                vno.setTypeVente("VNO");
                int montantTTC = 0, montantNet = 0, montantRemise = 0, panierMoyen = 0, montantEsp = 0,
                        montantCheque = 0, MontantVirement = 0, montantCB = 0, montantDiff = 0, nbreVente = 0,
                        montantMobilePayment = 0;
                for (MvtTransaction mvt : venteVNO) {
                    montantTTC += (mvt.getMontant() - mvt.getMontantttcug());
                    montantNet += (mvt.getMontantNet() - mvt.getMontantnetug());
                    montantRemise += mvt.getMontantRemise();
                    montantTva += (mvt.getMontantTva() - mvt.getMontantTvaUg());
                    marge += (mvt.getMarge() - mvt.getMargeug());
                    montantDiff += mvt.getMontantRestant();

                    if (mvt.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                        nbreVente++;

                    }
                    switch (mvt.getReglement().getLgTYPEREGLEMENTID()) {
                    case DateConverter.MODE_ESP:
                        montantEsp += (mvt.getMontantRegle() - mvt.getMontantnetug());
                        break;
                    case DateConverter.MODE_CHEQUE:
                        montantCheque += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_CB:
                        montantCB += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_VIREMENT:
                        MontantVirement += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_MOOV:
                    case DateConverter.TYPE_REGLEMENT_ORANGE:
                    case DateConverter.MODE_MTN:
                    case DateConverter.MODE_WAVE:
                        montantMobilePayment += (mvt.getMontantRegle() - mvt.getMontantnetug());
                        break;

                    }
                }
                _montantTTC += montantTTC;
                _montantNet += montantNet;
                _MontantVirement += MontantVirement;
                _montantCB += montantCB;
                _montantCheque += montantCheque;
                _montantEsp += montantEsp;
                _montantRemise += montantRemise;
                _montantDiff += montantDiff;
                _montantMobilePayment += montantMobilePayment;
                _nbreVente += nbreVente;
                if (nbreVente > 0) {
                    panierMoyen = montantTTC / nbreVente;
                }
                vno.setMontantCB(montantCB);
                vno.setMontantCheque(montantCheque);
                vno.setMontantEsp(montantEsp);
                vno.setMontantDiff(montantDiff);
                vno.setMontantNet(montantNet);
                vno.setMontantTTC(montantTTC);
                vno.setMontantVirement(MontantVirement);
                vno.setNbreVente(nbreVente);
                vno.setMontantRemise(montantRemise);
                vno.setMontantTp(0);
                vno.setPanierMoyen(panierMoyen);
                vno.setMontantMobilePayment(montantMobilePayment);

            }
            BalanceDTO vo = null;
            if (venteVO != null) {
                vo = new BalanceDTO();
                vo.setTypeVente("VO");
                int montantTTC = 0, montantNet = 0, montantRemise = 0, panierMoyen = 0, montantEsp = 0,
                        montantCheque = 0, MontantVirement = 0, montantCB = 0, montantDiff = 0,
                        montantMobilePayment = 0, nbreVente = 0;
                for (MvtTransaction mvt : venteVO) {
                    montantTTC += mvt.getMontant();
                    montantNet += mvt.getMontantNet();
                    montantRemise += mvt.getMontantRemise();
                    montantTva += mvt.getMontantTva();
                    marge += mvt.getMarge();
                    montantDiff += mvt.getMontantRestant();
                    montantTp += mvt.getMontantCredit();
                    if (mvt.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                        nbreVente++;

                    }
                    switch (mvt.getReglement().getLgTYPEREGLEMENTID()) {
                    case DateConverter.MODE_ESP:
                        montantEsp += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_CHEQUE:
                        montantCheque += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_CB:
                        montantCB += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_VIREMENT:
                        MontantVirement += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_MOOV:
                    case DateConverter.TYPE_REGLEMENT_ORANGE:
                    case DateConverter.MODE_MTN:
                    case DateConverter.MODE_WAVE:
                        montantMobilePayment += mvt.getMontantRegle();
                        break;

                    }
                }
                if (nbreVente > 0) {
                    panierMoyen = montantTTC / nbreVente;
                }
                vo.setMontantCB(montantCB);
                vo.setMontantCheque(montantCheque);
                vo.setMontantEsp(montantEsp);
                vo.setMontantDiff(montantDiff);
                vo.setMontantNet(montantNet);
                vo.setMontantTTC(montantTTC);
                vo.setMontantVirement(MontantVirement);
                vo.setNbreVente(nbreVente);
                vo.setMontantRemise(montantRemise);
                vo.setMontantTp(montantTp);
                vo.setPanierMoyen(panierMoyen);
                vo.setMontantMobilePayment(montantMobilePayment);
                _montantMobilePayment += montantMobilePayment;
                _montantTTC += montantTTC;
                _montantNet += montantNet;
                _MontantVirement += MontantVirement;
                _montantCB += montantCB;
                _montantCheque += montantCheque;
                _montantEsp += montantEsp;
                _montantRemise += montantRemise;
                _montantDiff += montantDiff;
                _nbreVente += nbreVente;

            }
            if (vo != null) {
                pourcentageVo = (int) Math.round((Double.valueOf(vo.getMontantNet()) * 100) / Math.abs(_montantNet));
                vo.setPourcentage(pourcentageVo);
                balances.add(vo);
            }
            if (vno != null) {
                int pourcentageVno = (int) Math
                        .round((Double.valueOf(vno.getMontantNet()) * 100) / Math.abs(_montantNet));
                vno.setPourcentage(pourcentageVno);
                balances.add(vno);
            }

            if (achats != null) {
                montantAchat = achats.parallelStream().map(MvtTransaction::getMontant).reduce(0, Integer::sum);

            }
            if (sortieCaisse != null) {
                Map<String, List<MvtTransaction>> typeMvt = sortieCaisse.parallelStream()
                        .collect(Collectors.groupingBy(o -> o.gettTypeMvtCaisse().getLgTYPEMVTCAISSEID()));
                List<MvtTransaction> fond = typeMvt.get(DateConverter.MVT_FOND_CAISSE);
                List<MvtTransaction> sortie = typeMvt.get(DateConverter.MVT_SORTIE_CAISSE);
                if (fond != null) {
                    fondCaisse = fond.parallelStream().map(MvtTransaction::getMontant).reduce(0, Integer::sum);
                }
                if (sortie != null) {
                    montantSortie = sortie.parallelStream().map(MvtTransaction::getMontant).reduce(0, Integer::sum);
                }

            }
            if (entreesCaisse != null) {
                Map<String, List<MvtTransaction>> typeMvtEntree = entreesCaisse.parallelStream()
                        .collect(Collectors.groupingBy(o -> o.gettTypeMvtCaisse().getLgTYPEMVTCAISSEID()));
                List<MvtTransaction> entree = typeMvtEntree.get(DateConverter.MVT_ENTREE_CAISSE);
                List<MvtTransaction> diff = typeMvtEntree.get(DateConverter.MVT_REGLE_DIFF);
                List<MvtTransaction> reglementTp = typeMvtEntree.get(DateConverter.MVT_REGLE_TP);
                if (entree != null) {
                    montantEntre = entree.parallelStream().map(MvtTransaction::getMontant).reduce(0, Integer::sum);
                }
                if (diff != null) {
                    montantReglDiff = diff.parallelStream().map(MvtTransaction::getMontant).reduce(0, Integer::sum);
                }
                if (reglementTp != null) {
                    montantRegleTp = reglementTp.parallelStream().map(MvtTransaction::getMontant).reduce(0,
                            Integer::sum);
                }
            }
            if (montantAchat > 0) {
                ratioVA = Double.valueOf(_montantTTC) / montantAchat;
                ratioVA = BigDecimal.valueOf(ratioVA).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
            summary.setFondCaisse(fondCaisse);
            summary.setMarge(marge);
            summary.setMontantAchat(montantAchat);
            summary.setMontantCB(_montantCB);
            summary.setMontantCheque(_montantCheque);
            summary.setMontantDiff(_montantDiff);
            summary.setMontantRegDiff(montantReglDiff);
            summary.setMontantEntre(montantEntre);
            summary.setMontantEsp(_montantEsp);
            summary.setMontantNet(_montantNet);
            summary.setMontantSortie(montantSortie);
            summary.setMontantVirement(_MontantVirement);
            summary.setMontantHT((_montantTTC - montantTva));
            summary.setMontantRegleTp(montantRegleTp);
            summary.setMontantRemise(_montantRemise);
            summary.setMontantTva(montantTva);
            summary.setNbreVente(_nbreVente);
            summary.setMontantTTC(_montantTTC);
            summary.setMontantMobilePayment(_montantMobilePayment);
            if (_nbreVente > 0) {
                summary.setPanierMoyen(_montantTTC / _nbreVente);
            }
            summary.setRatioVA(ratioVA);
            summary.setMontantTp(montantTp);

        }
        generic.setBalances(balances);
        generic.setSummary(summary);
        return generic;
    }

    @Override
    public JSONObject balanceVenteCaisseVersion2(LocalDate dtStart, LocalDate dtEnd, boolean checked,
            String emplacementId, Boolean excludeSome) throws JSONException {
        long interval = ChronoUnit.DAYS.between(dtStart, dtEnd);

        GenericDTO generic;
        if (key_Take_Into_Account() || key_Params()) {
            if (interval == 0) {
                List<MvtTransaction> transactions = balanceVenteCaisseList(dtStart, dtEnd, checked, emplacementId,
                        excludeSome);
                generic = balanceFormat0(transactions);
            } else {
                generic = balanceFormat0(dtStart, interval, checked, emplacementId);
            }

        } else {
            List<MvtTransaction> transactions = balanceVenteCaisseList(dtStart, dtEnd, checked, emplacementId,
                    excludeSome);
            generic = balanceFormatter(transactions);
        }
        JSONObject json = new JSONObject();
        List<BalanceDTO> balances = generic.getBalances();
        SummaryDTO summary = generic.getSummary();
        json.put("total", balances.size());
        json.put("data", balances);
        json.put("metaData", new JSONObject(summary));
        return json;
    }

    @Override
    public GenericDTO balanceVenteCaisseReportVersion2(LocalDate dtStart, LocalDate dtEnd, boolean checked,
            String emplacementId, Boolean excludeSome) {
        long interval = ChronoUnit.DAYS.between(dtStart, dtEnd);
        LOG.info("=============================================>>>>>balanceVenteCaisseReportVersion2");
        GenericDTO generic;
        if (key_Take_Into_Account() || key_Params()) {
            if (interval == 0) {
                List<MvtTransaction> transactions = balanceVenteCaisseList(dtStart, dtEnd, checked, emplacementId,
                        excludeSome);
                generic = balanceFormat0(transactions);
            } else {
                generic = balanceFormat0(dtStart, interval, checked, emplacementId);
            }

        } else {
            List<MvtTransaction> transactions = balanceVenteCaisseList(dtStart, dtEnd, checked, emplacementId,
                    excludeSome);
            generic = balanceFormatter(transactions);
        }
        return generic;
    }

    public JSONObject tableauBoardData(LocalDate dtStart, LocalDate dtEnd, Boolean checked, TUser user, int start,
            int limit, boolean all) throws JSONException {
        TEmplacement emp = user.getLgEMPLACEMENTID();
        JSONObject json = new JSONObject();
        Map<TableauBaordSummary, List<TableauBaordPhDTO>> map;
        if (key_Take_Into_Account() || key_Params()) {
            map = buillTableauBoardData0(
                    donneestableauboard(dtStart, dtEnd, checked, emp.getLgEMPLACEMENTID(), start, limit, all));
        } else {
            map = buillTableauBoardData(
                    donneestableauboard(dtStart, dtEnd, checked, emp.getLgEMPLACEMENTID(), start, limit, all));
        }

        if (map.isEmpty()) {
            json.put("total", 0);
            json.put("data", new JSONArray());
        }
        map.forEach((k, v) -> {
            json.put("total", v.size());
            json.put("data", new JSONArray(v));
            json.put("metaData", new JSONObject(k));

        });
        return json;

    }

    private int stockUg(String idProduit, String emplacementId) {
        try {
            Query q = this.getEntityManager().createNamedQuery("TFamilleStock.findStockUg");
            q.setParameter("lgFAMILLEID", idProduit);
            q.setParameter("lgEMPLACEMENTID", emplacementId);
            return (Integer) q.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return 0;
        }
    }

    @Override
    public JSONObject venteUg(LocalDate dtStart, LocalDate dtEnd, String query) {
        int montant = 0, nbreVente = 0;
        List<VenteDetailsDTO> datas = venteUgDTO(dtStart, dtEnd, query);

        for (VenteDetailsDTO e : datas) {
            montant += e.getMontantUg();
            nbreVente += e.getUniteGratuite();

        }
        return new JSONObject().put("total", true).put("data", new JSONArray(datas)).put("metaData",
                new JSONObject().put("montantAchat", montant).put("nbreVente", nbreVente));

    }

    @Override
    public List<VenteDetailsDTO> venteUgDTO(LocalDate dtStart, LocalDate dtEnd, String query) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TPreenregistrementDetail> cq = cb.createQuery(TPreenregistrementDetail.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> st = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            cq.select(root).orderBy(cb.asc(st.get(TPreenregistrement_.dtUPDATED)));
            Predicate btw = cb.between(cb.function("DATE", Date.class, st.get(TPreenregistrement_.dtUPDATED)),
                    java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
            predicates.add(btw);
            predicates.add(cb.greaterThan(root.get(TPreenregistrementDetail_.intUG), 0));
            predicates.add(cb.greaterThan(st.get(TPreenregistrement_.intPRICE), 0));
            predicates.add(cb.isFalse(st.get(TPreenregistrement_.bISCANCEL)));
            predicates.add(cb.equal(st.get(TPreenregistrement_.strSTATUT), commonparameter.statut_is_Closed));
            predicates.add(cb.equal(st.get(TPreenregistrement_.strTYPEVENTE), DateConverter.VENTE_COMPTANT));
            if (!StringUtils.isEmpty(query)) {
                Predicate predicate = cb.and(cb.or(
                        cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.intCIP), query + "%"),
                        cb.like(st.get(TPreenregistrement_.strREFTICKET), query + "%"),
                        cb.like(st.get(TPreenregistrement_.strREF), query + "%"),
                        cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.strNAME), query + "%"),
                        cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.intEAN13), query + "%")));
                predicates.add(predicate);
            }
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            TypedQuery<TPreenregistrementDetail> q = getEntityManager().createQuery(cq);
            return q.getResultList().stream()
                    .map(e -> new VenteDetailsDTO(e)
                            .stockUg(stockUg(e.getLgFAMILLEID().getLgFAMILLEID(), DateConverter.OFFICINE)))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    private List<MvtTransaction> balanceVenteCaisse(LocalDate dtStart, LocalDate dtEnd, String emplacementId) {
        try {
            TypedQuery<MvtTransaction> query = getEntityManager().createQuery(
                    "SELECT o FROM MvtTransaction o WHERE o.mvtDate BETWEEN ?1 AND ?2 AND o.magasin.lgEMPLACEMENTID=?3 AND o.checked=?4 AND o.tTypeMvtCaisse.lgTYPEMVTCAISSEID=?5 AND o.montant <> o.montantAcc",
                    MvtTransaction.class);
            query.setParameter(1, dtStart);
            query.setParameter(2, dtEnd);
            query.setParameter(3, emplacementId);
            query.setParameter(4, true);
            query.setParameter(5, DateConverter.MVT_REGLE_VNO);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }

    }

    private int remiseNonPara(String idVente) {
        try {
            TypedQuery<TPreenregistrementDetail> q = getEntityManager().createQuery(
                    "SELECT o FROM TPreenregistrementDetail o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1 AND o.lgFAMILLEID.boolACCOUNT=TRUE",
                    TPreenregistrementDetail.class);
            q.setParameter(1, idVente);
            return q.getResultList().stream().map(TPreenregistrementDetail::getIntPRICEREMISE).reduce(0, Integer::sum);
        } catch (Exception e) {
            return 0;
        }
    }

    private int remisePara(String idVente) {
        try {
            TypedQuery<TPreenregistrementDetail> q = getEntityManager().createQuery(
                    "SELECT o FROM TPreenregistrementDetail o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1 AND o.lgFAMILLEID.boolACCOUNT=FALSE",
                    TPreenregistrementDetail.class);
            q.setParameter(1, idVente);
            return q.getResultList().stream().map(TPreenregistrementDetail::getIntPRICEREMISE).reduce(0, Integer::sum);
        } catch (Exception e) {
            return 0;
        }
    }

    private GenericDTO balanceFormatPara(List<MvtTransaction> venteVNO) {
        List<BalanceDTO> balances = new ArrayList<>();
        GenericDTO generic = new GenericDTO();
        SummaryDTO summary = new SummaryDTO();
        if (!venteVNO.isEmpty()) {
            BalanceDTO vno = new BalanceDTO();
            vno.setTypeVente("VNO");

            int _montantTTC = 0, _montantNet = 0, _montantRemise = 0, _montantEsp = 0, _montantCheque = 0,
                    _MontantVirement = 0, _montantCB = 0, _montantDiff = 0, _nbreVente = 0, montantAchat = 0,
                    montantSortie = 0, marge = 0, fondCaisse = 0, montantReglDiff = 0, montantRegleTp = 0,
                    montantEntre = 0, montantTva = 0, montantTp = 0, _montantMobilePayment = 0;
            double ratioVA = 0.0;
            int montantTTC = 0, montantNet = 0, montantRemise = 0, panierMoyen = 0, montantEsp = 0, montantCheque = 0,
                    MontantVirement = 0, montantCB = 0, montantDiff = 0, nbreVente = 0, montantMobilePayment = 0;

            for (MvtTransaction mvt : venteVNO) {
                int remise = remisePara(mvt.getPkey());
                montantRemise += remise;
                montantNet += (((mvt.getMontant() - mvt.getMontantAcc()) - remise) - mvt.getMontantttcug());
                montantTTC += ((mvt.getMontant() - mvt.getMontantAcc()) - mvt.getMontantttcug());

                montantTva += (mvt.getMontantTva() - mvt.getMontantTvaUg());
                marge += (mvt.getMarge() - mvt.getMargeug());
                montantDiff += mvt.getMontantRestant();
                if (mvt.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                    nbreVente++;

                }
                int montantPara = ((mvt.getMontant() - mvt.getMontantAcc() - remise) - mvt.getMontantnetug());
                switch (mvt.getReglement().getLgTYPEREGLEMENTID()) {

                case DateConverter.MODE_ESP:

                    montantEsp += montantPara;
                    break;
                case DateConverter.MODE_CHEQUE:
                    montantCheque += montantPara;
                    break;
                case DateConverter.MODE_CB:
                    montantCB += montantPara;
                    break;
                case DateConverter.MODE_VIREMENT:
                    MontantVirement += montantPara;
                    break;
                case DateConverter.MODE_MOOV:
                case DateConverter.TYPE_REGLEMENT_ORANGE:
                case DateConverter.MODE_MTN:
                case DateConverter.MODE_WAVE:
                    montantMobilePayment += montantPara;
                    break;
                }

            }
            _montantTTC += montantTTC;
            _montantNet += montantNet;
            _MontantVirement += MontantVirement;
            _montantCB += montantCB;
            _montantCheque += montantCheque;
            _montantEsp += montantEsp;
            _montantRemise += montantRemise;
            _montantDiff += montantDiff;
            _nbreVente += nbreVente;
            _montantMobilePayment += montantMobilePayment;
            if (nbreVente > 0) {
                panierMoyen = montantTTC / nbreVente;
            }

            vno.setMontantCB(montantCB);
            vno.setMontantCheque(montantCheque);
            vno.setMontantEsp(montantEsp);
            vno.setMontantDiff(montantDiff);
            vno.setMontantNet(montantNet);
            vno.setMontantTTC(montantTTC);
            vno.setMontantVirement(MontantVirement);
            vno.setNbreVente(nbreVente);
            vno.setMontantRemise(montantRemise);
            vno.setMontantTp(0);
            vno.setMontantMobilePayment(montantMobilePayment);
            vno.setPanierMoyen(panierMoyen);

            int pourcentageVno = (int) Math.round((Double.valueOf(vno.getMontantNet()) * 100) / Math.abs(_montantNet));
            vno.setPourcentage(pourcentageVno);
            balances.add(vno);

            if (montantAchat > 0) {
                ratioVA = Double.valueOf(_montantTTC) / montantAchat;
                ratioVA = new BigDecimal(ratioVA).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
            summary.setFondCaisse(fondCaisse);
            summary.setMarge(marge);
            summary.setMontantAchat(montantAchat);
            summary.setMontantCB(_montantCB);
            summary.setMontantCheque(_montantCheque);
            summary.setMontantDiff(_montantDiff);
            summary.setMontantRegDiff(montantReglDiff);
            summary.setMontantEntre(montantEntre);
            summary.setMontantEsp(_montantEsp);
            summary.setMontantNet(_montantNet);
            summary.setMontantSortie(montantSortie);
            summary.setMontantVirement(_MontantVirement);
            summary.setMontantMobilePayment(_montantMobilePayment);
            summary.setMontantHT((_montantTTC - montantTva));
            summary.setMontantRegleTp(montantRegleTp);
            summary.setMontantRemise(_montantRemise);
            summary.setMontantTva(montantTva);
            summary.setNbreVente(_nbreVente);
            summary.setMontantTTC(_montantTTC);
            if (_nbreVente > 0) {
                summary.setPanierMoyen(_montantTTC / _nbreVente);
            }
            summary.setRatioVA(ratioVA);
            summary.setMontantTp(montantTp);

        }
        generic.setBalances(balances);
        generic.setSummary(summary);
        return generic;
    }

    @Override
    public GenericDTO balanceVenteCaisseReportPara(LocalDate dtStart, LocalDate dtEnd, String emplacementId) {

        List<MvtTransaction> transactions = balanceVenteCaisse(dtStart, dtEnd, emplacementId);

        GenericDTO generic = balanceFormatPara(transactions);

        return generic;
    }

    @Override
    public JSONObject balancePara(LocalDate dtStart, LocalDate dtEnd, String emplacementId) throws JSONException {

        List<MvtTransaction> transactions = balanceVenteCaisse(dtStart, dtEnd, emplacementId);

        GenericDTO generic = balanceFormatPara(transactions);

        JSONObject json = new JSONObject();
        List<BalanceDTO> balances = generic.getBalances();
        SummaryDTO summary = generic.getSummary();
        json.put("total", balances.size());
        json.put("data", balances);
        json.put("metaData", new JSONObject(summary));
        return json;
    }

    @Override
    public JSONObject tableauBoardDatasGroupByMonth(LocalDate dtStart, LocalDate dtEnd, Boolean checked, TUser user,
            int start, int limit, boolean all) throws JSONException {
        TEmplacement emp = user.getLgEMPLACEMENTID();
        JSONObject json = new JSONObject();
        Map<TableauBaordSummary, List<TableauBaordPhDTO>> map;
        if (key_Take_Into_Account() || key_Params()) {
            map = buillTableauBoardDataMonthly(
                    donneestableauboard(dtStart, dtEnd, checked, emp.getLgEMPLACEMENTID(), start, limit, all));
        } else {
            map = buillTableauBoardDataGroupByMonth(
                    donneestableauboard(dtStart, dtEnd, checked, emp.getLgEMPLACEMENTID(), start, limit, all));
        }

        if (map.isEmpty()) {
            json.put("total", 0);
            json.put("data", new JSONArray());

        }
        map.forEach((k, v) -> {
            json.put("total", v.size());
            json.put("data", new JSONArray(v));
            json.put("metaData", new JSONObject(k));

        });
        return json;

    }

    private Map<TableauBaordSummary, List<TableauBaordPhDTO>> buillTableauBoardDataGroupByMonth(
            List<MvtTransaction> transactions) {
        if (transactions.isEmpty()) {
            return Collections.emptyMap();
        }
        DateTimeFormatter DD_MM_YYY = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MM/yyyy");
        Map<TableauBaordSummary, List<TableauBaordPhDTO>> summ = new HashMap<>();
        List<TableauBaordPhDTO> tableauBaords = new ArrayList<>();
        TableauBaordSummary summary = new TableauBaordSummary();
        Map<Integer, List<MvtTransaction>> map = transactions.stream()
                .collect(Collectors.groupingBy(o -> Integer.valueOf(o.getMvtDate().format(formatter))));
        LongAdder _summontantTTC = new LongAdder(), _summontantNet = new LongAdder(),
                _summontantRemise = new LongAdder(), _summontantEsp = new LongAdder(),
                _summontantCredit = new LongAdder(), _sumnbreVente = new LongAdder(),
                _summontantAchatOne = new LongAdder(), _summontantAchatTwo = new LongAdder(),
                _summontantAchatThree = new LongAdder(), _summontantAchatFour = new LongAdder(),
                _summontantAchatFive = new LongAdder(), _summontantAvoir = new LongAdder(),
                _summontantAchat = new LongAdder();
        DoubleAdder _sumratioVA = new DoubleAdder(), _sumrationAV = new DoubleAdder();
        map.forEach((k, v) -> {
            TableauBaordPhDTO baordPh = new TableauBaordPhDTO();
            baordPh.setMvtDateInt(k);
            String dateMvt = String.valueOf(k);
            dateMvt = dateMvt.concat("01");
            LocalDate perode = LocalDate.parse(dateMvt, DD_MM_YYY);
            perode = LocalDate.of(perode.getYear(), perode.getMonth(), perode.lengthOfMonth());
            baordPh.setDateOperation(perode.format(formatter2));
            baordPh.setMvtDate(perode);
            LongAdder montantTTC = new LongAdder(), montantNet = new LongAdder(), montantRemise = new LongAdder(),
                    montantEsp = new LongAdder(), montantCredit = new LongAdder(), nbreVente = new LongAdder(),
                    montantAchatOne = new LongAdder(), montantAchatTwo = new LongAdder(),
                    montantAchatThree = new LongAdder(), montantAchatFour = new LongAdder(),
                    montantAchatFive = new LongAdder(), montantAchat = new LongAdder(), montantAvoir = new LongAdder();
            DoubleAdder ratioVA = new DoubleAdder(), rationAV = new DoubleAdder();
            int avoir = avoirFournisseur(k.toString());
            montantAvoir.add(avoir);

            v.forEach(op -> {
                switch (op.getTypeTransaction()) {
                case VENTE_COMPTANT:
                case VENTE_CREDIT: {
                    montantTTC.add(op.getMontant() - op.getMontantttcug());
                    montantNet.add(op.getMontantNet() - op.getMontantnetug());
                    montantRemise.add(op.getMontantRemise());
                    montantEsp.add(op.getMontantRegle() - op.getMontantnetug());
                    montantCredit.add(op.getMontantCredit());
                    montantCredit.add(op.getMontantRestant());
                    if (op.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                        nbreVente.increment();
                    }
                }
                    break;
                case ACHAT: {
                    montantAchat.add(op.getMontant());
                    try {
                        Groupefournisseur g = op.getGrossiste().getGroupeId();
                        switch (g.getLibelle()) {
                        case DateConverter.LABOREXCI:
                            montantAchatOne.add(op.getMontant());
                            break;
                        case DateConverter.DPCI:
                            montantAchatTwo.add(op.getMontant());
                            break;
                        case DateConverter.COPHARMED:
                            montantAchatThree.add(op.getMontant());
                            break;
                        case DateConverter.TEDIS:
                            montantAchatFour.add(op.getMontant());
                            break;
                        case DateConverter.AUTRES:
                            montantAchatFive.add(op.getMontant());
                            break;
                        default:
                            break;
                        }

                    } catch (Exception e) {
                    }
                }
                    break;
                default:
                    break;

                }
            });
            Integer _montantNet = montantNet.intValue();
            Integer _montantAchat = montantAchat.intValue() - avoir;
            if (_montantAchat.compareTo(0) > 0) {
                ratioVA.add(new BigDecimal(Double.valueOf(_montantNet) / _montantAchat).setScale(2, RoundingMode.FLOOR)
                        .doubleValue());
            }
            if (_montantNet.compareTo(0) > 0) {
                rationAV.add(new BigDecimal(Double.valueOf(_montantAchat) / _montantNet).setScale(2, RoundingMode.FLOOR)
                        .doubleValue());
            }
            baordPh.setMontantAchatFive(montantAchatFive.intValue());
            baordPh.setMontantAchatFour(montantAchatFour.intValue());
            baordPh.setMontantAchatThree(montantAchatThree.intValue());
            baordPh.setNbreVente(nbreVente.intValue());
            baordPh.setMontantAchatTwo(montantAchatTwo.intValue());
            baordPh.setMontantAchatOne(montantAchatOne.intValue());
            baordPh.setMontantAchat(_montantAchat);
            baordPh.setRatioVA(ratioVA.doubleValue());
            baordPh.setRationAV(rationAV.doubleValue());
            baordPh.setMontantTTC(montantTTC.intValue());
            baordPh.setMontantNet(_montantNet);
            baordPh.setMontantEsp(montantEsp.intValue());
            baordPh.setMontantRemise(montantRemise.intValue());
            baordPh.setMontantCredit(montantCredit.intValue());
            baordPh.setMontantAvoir(montantAvoir.intValue());

            /**
             * ** ***************
             */
            _summontantAchatFive.add(baordPh.getMontantAchatFive());
            _summontantAchatFour.add(baordPh.getMontantAchatFour());
            _summontantAchatThree.add(baordPh.getMontantAchatThree());
            _sumnbreVente.add(baordPh.getNbreVente());
            _summontantAchatTwo.add(baordPh.getMontantAchatTwo());
            _summontantAchatOne.add(baordPh.getMontantAchatOne());
            _summontantAchat.add(baordPh.getMontantAchat());
            _summontantTTC.add(baordPh.getMontantTTC());
            _summontantNet.add(baordPh.getMontantNet());
            _summontantEsp.add(baordPh.getMontantEsp());
            _summontantRemise.add(baordPh.getMontantRemise());
            _summontantCredit.add(baordPh.getMontantCredit());
            _summontantAvoir.add(baordPh.getMontantAvoir());
            tableauBaords.add(baordPh);
        });
        Long _montantNet = _summontantNet.longValue();
        Long _montantAchat = _summontantAchat.longValue();
        if (_montantAchat.compareTo(0l) > 0) {
            _sumratioVA.add(new BigDecimal(Double.valueOf(_montantNet) / _montantAchat).setScale(2, RoundingMode.FLOOR)
                    .doubleValue());
        }
        if (_montantNet.compareTo(0l) > 0) {
            _sumrationAV.add(new BigDecimal(Double.valueOf(_montantAchat) / _montantNet).setScale(2, RoundingMode.FLOOR)
                    .doubleValue());
        }
        summary.setMontantAchatFive(_summontantAchatFive.longValue());
        summary.setMontantAchatFour(_summontantAchatFour.longValue());
        summary.setMontantAchatThree(_summontantAchatThree.longValue());
        summary.setNbreVente(_sumnbreVente.longValue());
        summary.setMontantAchatTwo(_summontantAchatTwo.longValue());
        summary.setMontantAchatOne(_summontantAchatOne.longValue());
        summary.setMontantAchat(_summontantAchat.longValue());
        summary.setRatioVA(_sumratioVA.doubleValue());
        summary.setRationAV(_sumrationAV.doubleValue());
        summary.setMontantTTC(_summontantTTC.longValue());
        summary.setMontantNet(_summontantNet.longValue());
        summary.setMontantEsp(_summontantEsp.longValue());
        summary.setMontantRemise(_summontantRemise.longValue());
        summary.setMontantCredit(_summontantCredit.longValue());
        summary.setMontantAvoir(_summontantAvoir.longValue());
        summ.put(summary, tableauBaords.stream().sorted(comparator).collect(Collectors.toList()));

        return summ;
    }

    @Override
    public Map<TableauBaordSummary, List<TableauBaordPhDTO>> tableauBoardDatasMonthly(LocalDate dtStart,
            LocalDate dtEnd, Boolean checked, TUser user, int ration, int start, int limit, boolean all) {
        TEmplacement emp = user.getLgEMPLACEMENTID();
        List<MvtTransaction> transactions = donneestableauboard(dtStart, dtEnd, checked, emp.getLgEMPLACEMENTID(),
                start, limit, all);
        if (key_Params() || key_Take_Into_Account()) {
            return buillTableauBoardDataMonthly(transactions);
        } else {
            return buillTableauBoardDataGroupByMonth(transactions);
        }

    }

    private Map<TableauBaordSummary, List<TableauBaordPhDTO>> buillTableauBoardDataMonthly(
            List<MvtTransaction> transactions) {
        if (transactions.isEmpty()) {
            return Collections.emptyMap();
        }
        DateTimeFormatter DD_MM_YYY = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MM/yyyy");
        Map<TableauBaordSummary, List<TableauBaordPhDTO>> summ = new HashMap<>();
        List<TableauBaordPhDTO> tableauBaords = new ArrayList<>();
        TableauBaordSummary summary = new TableauBaordSummary();
        Map<Integer, List<MvtTransaction>> map = transactions.stream()
                .collect(Collectors.groupingBy(o -> Integer.valueOf(o.getMvtDate().format(formatter))));
        LongAdder _summontantTTC = new LongAdder(), _summontantNet = new LongAdder(),
                _summontantRemise = new LongAdder(), _summontantEsp = new LongAdder(),
                _summontantCredit = new LongAdder(), _sumnbreVente = new LongAdder(),
                _summontantAchatOne = new LongAdder(), _summontantAchatTwo = new LongAdder(),
                _summontantAchatThree = new LongAdder(), _summontantAchatFour = new LongAdder(),
                _summontantAchatFive = new LongAdder(), _summontantAvoir = new LongAdder(),
                _summontantAchat = new LongAdder();
        DoubleAdder _sumratioVA = new DoubleAdder(), _sumrationAV = new DoubleAdder();
        map.forEach((k, v) -> {
            TableauBaordPhDTO baordPh = new TableauBaordPhDTO();
            baordPh.setMvtDateInt(k);
            String dateMvt = String.valueOf(k);
            dateMvt = dateMvt.concat("01");
            LocalDate perode = LocalDate.parse(dateMvt, DD_MM_YYY);
            perode = LocalDate.of(perode.getYear(), perode.getMonth(), perode.lengthOfMonth());
            baordPh.setDateOperation(perode.format(formatter2));
            baordPh.setMvtDate(perode);
            LongAdder montantTTC = new LongAdder(), montantNet = new LongAdder(), montantRemise = new LongAdder(),
                    montantEsp = new LongAdder(), montantCredit = new LongAdder(), nbreVente = new LongAdder(),
                    montantAchatOne = new LongAdder(), montantAchatTwo = new LongAdder(),
                    montantAchatThree = new LongAdder(), montantAchatFour = new LongAdder(),
                    montantAchatFive = new LongAdder(), montantAchat = new LongAdder(), montantAvoir = new LongAdder();
            DoubleAdder ratioVA = new DoubleAdder(), rationAV = new DoubleAdder();
            int avoir = avoirFournisseur(k.toString());
            montantAvoir.add(avoir);
            v.forEach(op -> {
                switch (op.getTypeTransaction()) {
                case VENTE_COMPTANT: {
                    int remiseNonPara = 0;
                    if (Math.abs(op.getMontantRemise()) > 0) {
                        remiseNonPara = remiseNonPara(op.getPkey());
                    }

                    // montantRemise += remiseNonPara;
                    // int remise = remisePara(op.getPkey());
                    int montantNet_ = op.getMontantAcc() - remiseNonPara - op.getMontantttcug();
                    int montantTTC_ = op.getMontantAcc() - op.getMontantttcug();

                    montantNet.add(montantNet_);
                    montantTTC.add(montantTTC_);
                    montantEsp.add(montantNet_);
                    /*
                     * montantTTC.add(op.getMontant()); montantNet.add(op.getMontantNet());
                     * montantEsp.add(op.getMontantRegle());
                     */
                    montantRemise.add(remiseNonPara);
                    montantCredit.add(op.getMontantCredit());
                    montantCredit.add(op.getMontantRestant());
                    if (op.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                        nbreVente.increment();
                    }
                }

                    break;
                case VENTE_CREDIT: {
                    montantTTC.add(op.getMontant());
                    montantNet.add(op.getMontantNet());
                    montantRemise.add(op.getMontantRemise());
                    montantEsp.add(op.getMontantRegle());
                    montantCredit.add(op.getMontantCredit());
                    montantCredit.add(op.getMontantRestant());
                    if (op.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                        nbreVente.increment();
                    }
                }
                    break;
                case ACHAT: {
                    montantAchat.add(op.getMontant());
                    try {
                        Groupefournisseur g = op.getGrossiste().getGroupeId();
                        switch (g.getLibelle()) {
                        case DateConverter.LABOREXCI:
                            montantAchatOne.add(op.getMontant());
                            break;
                        case DateConverter.DPCI:
                            montantAchatTwo.add(op.getMontant());
                            break;
                        case DateConverter.COPHARMED:
                            montantAchatThree.add(op.getMontant());
                            break;
                        case DateConverter.TEDIS:
                            montantAchatFour.add(op.getMontant());
                            break;
                        case DateConverter.AUTRES:
                            montantAchatFive.add(op.getMontant());
                            break;
                        default:
                            break;
                        }

                    } catch (Exception e) {
                    }

                }
                    break;
                default:
                    break;

                }
            });
            Integer _montantNet = montantNet.intValue();
            Integer _montantAchat = montantAchat.intValue() - avoir;
            if (_montantAchat.compareTo(0) > 0) {
                ratioVA.add(new BigDecimal(Double.valueOf(_montantNet) / _montantAchat).setScale(2, RoundingMode.FLOOR)
                        .doubleValue());
            }
            if (_montantNet.compareTo(0) > 0) {
                rationAV.add(new BigDecimal(Double.valueOf(_montantAchat) / _montantNet).setScale(2, RoundingMode.FLOOR)
                        .doubleValue());
            }
            baordPh.setMontantAchatFive(montantAchatFive.intValue());
            baordPh.setMontantAchatFour(montantAchatFour.intValue());
            baordPh.setMontantAchatThree(montantAchatThree.intValue());
            baordPh.setNbreVente(nbreVente.intValue());
            baordPh.setMontantAchatTwo(montantAchatTwo.intValue());
            baordPh.setMontantAchatOne(montantAchatOne.intValue());
            baordPh.setMontantAchat(_montantAchat);
            baordPh.setRatioVA(ratioVA.doubleValue());
            baordPh.setRationAV(rationAV.doubleValue());
            baordPh.setMontantTTC(montantTTC.intValue());
            baordPh.setMontantNet(_montantNet);
            baordPh.setMontantEsp(montantEsp.intValue());
            baordPh.setMontantRemise(montantRemise.intValue());
            baordPh.setMontantCredit(montantCredit.intValue());
            baordPh.setMontantAvoir(montantAvoir.intValue());

            /**
             * ** ***************
             */
            _summontantAchatFive.add(baordPh.getMontantAchatFive());
            _summontantAchatFour.add(baordPh.getMontantAchatFour());
            _summontantAchatThree.add(baordPh.getMontantAchatThree());
            _sumnbreVente.add(baordPh.getNbreVente());
            _summontantAchatTwo.add(baordPh.getMontantAchatTwo());
            _summontantAchatOne.add(baordPh.getMontantAchatOne());
            _summontantAchat.add(baordPh.getMontantAchat());
            _summontantTTC.add(baordPh.getMontantTTC());
            _summontantNet.add(baordPh.getMontantNet());
            _summontantEsp.add(baordPh.getMontantEsp());
            _summontantRemise.add(baordPh.getMontantRemise());
            _summontantCredit.add(baordPh.getMontantCredit());
            _summontantAvoir.add(baordPh.getMontantAvoir());
            tableauBaords.add(baordPh);
        });
        Long _montantNet = _summontantNet.longValue();
        Long _montantAchat = _summontantAchat.longValue();
        if (_montantAchat.compareTo(0l) > 0) {
            _sumratioVA.add(BigDecimal.valueOf(Double.valueOf(_montantNet) / _montantAchat)
                    .setScale(2, RoundingMode.FLOOR).doubleValue());
        }
        if (_montantNet.compareTo(0l) > 0) {
            _sumrationAV.add(BigDecimal.valueOf(Double.valueOf(_montantAchat) / _montantNet)
                    .setScale(2, RoundingMode.FLOOR).doubleValue());
        }
        summary.setMontantAchatFive(_summontantAchatFive.longValue());
        summary.setMontantAchatFour(_summontantAchatFour.longValue());
        summary.setMontantAchatThree(_summontantAchatThree.longValue());
        summary.setNbreVente(_sumnbreVente.longValue());
        summary.setMontantAchatTwo(_summontantAchatTwo.longValue());
        summary.setMontantAchatOne(_summontantAchatOne.longValue());
        summary.setMontantAchat(_summontantAchat.longValue());
        summary.setRatioVA(_sumratioVA.doubleValue());
        summary.setRationAV(_sumrationAV.doubleValue());
        summary.setMontantTTC(_summontantTTC.longValue());
        summary.setMontantNet(_summontantNet.longValue());
        summary.setMontantEsp(_summontantEsp.longValue());
        summary.setMontantRemise(_summontantRemise.longValue());
        summary.setMontantCredit(_summontantCredit.longValue());
        summary.setMontantAvoir(_summontantAvoir.longValue());
        summ.put(summary, tableauBaords.stream().sorted(comparator).collect(Collectors.toList()));
        return summ;
    }

    public JSONObject balanceCaisse(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId,
            Boolean excludeSome) throws JSONException {
        List<MvtTransaction> transactions = balanceVenteCaisseList(dtStart, dtEnd, checked, emplacementId, excludeSome);
        GenericDTO generic;

        if (key_Take_Into_Account() || key_Params()) {
            generic = balanceFormat0(transactions);
        } else {
            generic = balanceFormat(transactions);
        }
        JSONObject json = new JSONObject();
        List<BalanceDTO> balances = generic.getBalances();
        SummaryDTO summary = generic.getSummary();
        json.put("total", balances.size());
        json.put("data", balances);
        json.put("metaData", new JSONObject(summary));
        return json;
    }

    private boolean isTrue(String parameterKey) {
        try {
            TParameters tp = getEntityManager().find(TParameters.class, parameterKey);
            return (Integer.parseInt(tp.getStrVALUE()) == 1);

        } catch (Exception e) {
            return false;
        }
    }

    private int montantFlag(MvtTransaction mvt) {
        if (mvt.getFlaged()) {

            return mvt.getMontant() - mvt.getMontantAcc();
        }
        return mvt.getMontant();

    }

    private GenericDTO balanceFormat0(List<MvtTransaction> mvtTransactions) {

        List<BalanceDTO> balances = new ArrayList<>();
        GenericDTO generic = new GenericDTO();
        SummaryDTO summary = new SummaryDTO();

        if (!mvtTransactions.isEmpty()) {
            Map<TypeTransaction, List<MvtTransaction>> map = mvtTransactions.stream()
                    .collect(Collectors.groupingBy(o -> o.getTypeTransaction()));
            List<MvtTransaction> venteVNO = map.get(TypeTransaction.VENTE_COMPTANT);
            List<MvtTransaction> venteVO = map.get(TypeTransaction.VENTE_CREDIT);
            List<MvtTransaction> achats = map.get(TypeTransaction.ACHAT);
            List<MvtTransaction> entreesCaisse = map.get(TypeTransaction.ENTREE);
            List<MvtTransaction> sortieCaisse = map.get(TypeTransaction.SORTIE);
            BalanceDTO vno = null;
            Integer pourcentageVo;

            long _montantTTC = 0, _montantNet = 0, _montantRemise = 0, _montantEsp = 0, _montantCheque = 0,
                    _MontantVirement = 0, _montantCB = 0, _montantDiff = 0, _nbreVente = 0, montantAchat = 0,
                    montantSortie = 0, marge = 0, fondCaisse = 0, montantReglDiff = 0, montantRegleTp = 0,
                    montantEntre = 0, montantTva = 0, montantTp = 0, _montantMobilePayment = 0;
            double ratioVA = 0.0;
            if (venteVNO != null) {
                vno = new BalanceDTO();
                vno.setTypeVente("VNO");
                int montantTTC = 0, montantNet = 0, montantRemise = 0, panierMoyen = 0, montantEsp = 0,
                        montantCheque = 0, MontantVirement = 0, montantCB = 0, montantDiff = 0, nbreVente = 0,
                        montantMobilePayment = 0;

                for (MvtTransaction mvt : venteVNO) {

                    // montantRemise += mvt.getMontantRemise();
                    int remiseNonPara = 0;
                    if (Math.abs(mvt.getMontantRemise()) > 0) {
                        remiseNonPara = remiseNonPara(mvt.getPkey());
                    }
                    int montantFlag = montantFlag(mvt);

                    // int newAmount = (mvt.getTypeTransaction() == TypeTransaction.VENTE_COMPTANT &&
                    // mvt.getMontantAcc().compareTo(mvt.getMontant()) == 0) ? mvt.getMontant() : mvt.getMontantAcc();
                    int newAmount = montantFlag;
                    montantRemise += remiseNonPara;
                    montantTTC += (newAmount - mvt.getMontantttcug());
                    long montantNonPara = ((newAmount - remiseNonPara) - mvt.getMontantnetug());
                    montantNet += montantNonPara;
                    montantTva += (mvt.getMontantTva() - mvt.getMontantTvaUg());
                    marge += (mvt.getMarge() - mvt.getMargeug());
                    montantDiff += mvt.getMontantRestant();
                    if (mvt.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                        nbreVente++;
                    }
                    switch (mvt.getReglement().getLgTYPEREGLEMENTID()) {
                    case DateConverter.MODE_ESP:
                        montantEsp += montantNonPara;
                        break;
                    case DateConverter.MODE_CHEQUE:
                        montantCheque += montantNonPara;
                        break;
                    case DateConverter.MODE_CB:
                        montantCB += montantNonPara;
                        break;
                    case DateConverter.MODE_VIREMENT:
                        MontantVirement += montantNonPara;
                        break;
                    case DateConverter.MODE_MOOV:
                    case DateConverter.TYPE_REGLEMENT_ORANGE:
                    case DateConverter.MODE_MTN:
                    case DateConverter.MODE_WAVE:
                        montantMobilePayment += montantNonPara;
                        break;
                    }

                }

                _montantTTC += montantTTC;
                _montantNet += montantNet;
                _MontantVirement += MontantVirement;
                _montantCB += montantCB;
                _montantCheque += montantCheque;
                _montantEsp += montantEsp;
                // _montantEsp-=montantFlag;
                _montantRemise += montantRemise;
                _montantDiff += montantDiff;
                _nbreVente += nbreVente;
                _montantMobilePayment += montantMobilePayment;
                if (nbreVente > 0) {
                    panierMoyen = montantTTC / nbreVente;
                }

                vno.setMontantCB(montantCB);
                vno.setMontantCheque(montantCheque);
                vno.setMontantEsp(montantEsp);
                vno.setMontantDiff(montantDiff);
                vno.setMontantNet(montantNet);
                vno.setMontantTTC(montantTTC);
                vno.setMontantVirement(MontantVirement);
                vno.setNbreVente(nbreVente);
                vno.setMontantRemise(montantRemise);
                vno.setMontantTp(0);
                vno.setMontantMobilePayment(montantMobilePayment);
                vno.setPanierMoyen(panierMoyen);

            }

            BalanceDTO vo = null;
            if (venteVO != null) {
                vo = new BalanceDTO();
                vo.setTypeVente("VO");
                long montantTTC = 0, montantNet = 0, montantRemise = 0, panierMoyen = 0, montantEsp = 0,
                        montantCheque = 0, MontantVirement = 0, montantCB = 0, montantDiff = 0, nbreVente = 0,
                        montantMobilePayment = 0;

                for (MvtTransaction mvt : venteVO) {
                    montantTTC += mvt.getMontant();
                    montantNet += mvt.getMontantNet();
                    montantRemise += mvt.getMontantRemise();
                    montantTva += mvt.getMontantTva();
                    marge += mvt.getMarge();
                    montantDiff += mvt.getMontantRestant();
                    montantTp += mvt.getMontantCredit();
                    if (mvt.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                        nbreVente++;

                    }
                    switch (mvt.getReglement().getLgTYPEREGLEMENTID()) {
                    case DateConverter.MODE_ESP:
                        montantEsp += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_CHEQUE:
                        montantCheque += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_CB:
                        montantCB += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_VIREMENT:
                        MontantVirement += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_MOOV:
                    case DateConverter.TYPE_REGLEMENT_ORANGE:
                    case DateConverter.MODE_MTN:
                    case DateConverter.MODE_WAVE:
                        montantMobilePayment += mvt.getMontantRegle();
                        break;
                    }

                }
                if (nbreVente > 0) {
                    panierMoyen = montantTTC / nbreVente;
                }

                vo.setMontantCB(montantCB);
                vo.setMontantCheque(montantCheque);
                vo.setMontantEsp(montantEsp);
                vo.setMontantDiff(montantDiff);
                vo.setMontantNet(montantNet);
                vo.setMontantTTC(montantTTC);
                vo.setMontantVirement(MontantVirement);
                vo.setNbreVente(nbreVente);
                vo.setMontantRemise(montantRemise);
                vo.setMontantTp(montantTp);
                vo.setPanierMoyen(panierMoyen);
                vo.setMontantMobilePayment(montantMobilePayment);
                _montantTTC += montantTTC;
                _montantMobilePayment += montantMobilePayment;
                _montantNet += montantNet;
                _MontantVirement += MontantVirement;
                _montantCB += montantCB;
                _montantCheque += montantCheque;
                _montantEsp += montantEsp;
                _montantRemise += montantRemise;
                _montantDiff += montantDiff;
                _nbreVente += nbreVente;

            }
            if (vo != null) {
                pourcentageVo = (int) Math.round((Double.valueOf(vo.getMontantNet()) * 100) / Math.abs(_montantNet));
                vo.setPourcentage(pourcentageVo);
                balances.add(vo);
            }
            if (vno != null) {
                int pourcentageVno = (int) Math
                        .round((Double.valueOf(vno.getMontantNet()) * 100) / Math.abs(_montantNet));
                vno.setPourcentage(pourcentageVno);
                balances.add(vno);
            }

            if (achats != null) {
                montantAchat = achats.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);

            }
            if (sortieCaisse != null) {
                Map<String, List<MvtTransaction>> typeMvt = sortieCaisse.parallelStream()
                        .collect(Collectors.groupingBy(o -> o.gettTypeMvtCaisse().getLgTYPEMVTCAISSEID()));
                List<MvtTransaction> fond = typeMvt.get(DateConverter.MVT_FOND_CAISSE);
                List<MvtTransaction> sortie = typeMvt.get(DateConverter.MVT_SORTIE_CAISSE);
                if (fond != null) {
                    fondCaisse = fond.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }
                if (sortie != null) {
                    montantSortie = sortie.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }

            }
            if (entreesCaisse != null) {
                Map<String, List<MvtTransaction>> typeMvtEntree = entreesCaisse.parallelStream()
                        .collect(Collectors.groupingBy(o -> o.gettTypeMvtCaisse().getLgTYPEMVTCAISSEID()));
                List<MvtTransaction> entree = typeMvtEntree.get(DateConverter.MVT_ENTREE_CAISSE);
                List<MvtTransaction> diff = typeMvtEntree.get(DateConverter.MVT_REGLE_DIFF);
                List<MvtTransaction> reglementTp = typeMvtEntree.get(DateConverter.MVT_REGLE_TP);
                if (entree != null) {
                    montantEntre = entree.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }
                if (diff != null) {
                    montantReglDiff = diff.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }
                if (reglementTp != null) {
                    montantRegleTp = reglementTp.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0,
                            Long::sum);
                }
            }
            if (montantAchat > 0) {
                ratioVA = Double.valueOf(_montantTTC) / montantAchat;
                ratioVA = BigDecimal.valueOf(ratioVA).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
            summary.setFondCaisse(fondCaisse);
            summary.setMarge(marge);
            summary.setMontantAchat(montantAchat);
            summary.setMontantCB(_montantCB);
            summary.setMontantCheque(_montantCheque);
            summary.setMontantDiff(_montantDiff);
            summary.setMontantRegDiff(montantReglDiff);
            summary.setMontantEntre(montantEntre);
            summary.setMontantEsp(_montantEsp);
            summary.setMontantNet(_montantNet);
            summary.setMontantSortie(montantSortie);
            summary.setMontantVirement(_MontantVirement);
            summary.setMontantMobilePayment(_montantMobilePayment);
            summary.setMontantHT((_montantTTC - montantTva));
            summary.setMontantRegleTp(montantRegleTp);
            summary.setMontantRemise(_montantRemise);
            summary.setMontantTva(montantTva);
            summary.setNbreVente(_nbreVente);
            summary.setMontantTTC(_montantTTC);
            if (_nbreVente > 0) {
                summary.setPanierMoyen(_montantTTC / _nbreVente);
            }
            summary.setRatioVA(ratioVA);
            summary.setMontantTp(montantTp);

        }

        generic.setBalances(balances);
        generic.setSummary(summary);
        return generic;
    }

    private Map<TableauBaordSummary, List<TableauBaordPhDTO>> buillTableauBoardData0(
            List<MvtTransaction> transactions) {
        if (transactions.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<TableauBaordSummary, List<TableauBaordPhDTO>> summ = new HashMap<>();
        List<TableauBaordPhDTO> tableauBaords = new ArrayList<>();
        TableauBaordSummary summary = new TableauBaordSummary();
        Map<LocalDate, List<MvtTransaction>> map = transactions.stream()
                .collect(Collectors.groupingBy(o -> o.getMvtDate()));
        LongAdder _summontantTTC = new LongAdder(), _summontantNet = new LongAdder(),
                _summontantRemise = new LongAdder(), _summontantEsp = new LongAdder(),
                _summontantCredit = new LongAdder(), _sumnbreVente = new LongAdder(),
                _summontantAchatOne = new LongAdder(), _summontantAchatTwo = new LongAdder(),
                _summontantAchatThree = new LongAdder(), _summontantAchatFour = new LongAdder(),
                _summontantAchatFive = new LongAdder(), _summontantAvoir = new LongAdder(),
                _summontantAchat = new LongAdder();
        DoubleAdder _sumratioVA = new DoubleAdder(), _sumrationAV = new DoubleAdder();

        map.forEach((k, v) -> {
            TableauBaordPhDTO baordPh = new TableauBaordPhDTO();
            baordPh.setMvtDate(k);
            LongAdder montantTTC = new LongAdder(), montantNet = new LongAdder(), montantRemise = new LongAdder(),
                    montantEsp = new LongAdder(), montantCredit = new LongAdder(), nbreVente = new LongAdder(),
                    montantAchatOne = new LongAdder(), montantAchatTwo = new LongAdder(),
                    montantAchatThree = new LongAdder(), montantAchatFour = new LongAdder(),
                    montantAchatFive = new LongAdder(), montantAchat = new LongAdder(), montantAvoir = new LongAdder();
            DoubleAdder ratioVA = new DoubleAdder(), rationAV = new DoubleAdder();

            int avoir = avoirFournisseur(k);

            montantAvoir.add(avoir);
            v.forEach(op -> {
                switch (op.getTypeTransaction()) {
                case VENTE_COMPTANT: {

                    int remiseNonPara = 0;
                    if (Math.abs(op.getMontantRemise()) > 0) {
                        remiseNonPara = remiseNonPara(op.getPkey());
                    }

                    // montantRemise += remiseNonPara;
                    // int remise = remisePara(op.getPkey());
                    // int newAmount = op.getMontantAcc().compareTo(op.getMontant()) == 0 ? op.getMontant() :
                    // op.getMontantAcc();
                    int newAmount = montantFlag(op);
                    System.out.println("newAmount ===" + newAmount);

                    /*
                     * int montantNet_ = op.getMontantAcc() - remiseNonPara - op.getMontantttcug(); int montantTTC_ =
                     * op.getMontantAcc() - op.getMontantttcug();
                     */
                    int montantNet_ = (newAmount - remiseNonPara - op.getMontantttcug());
                    int montantTTC_ = newAmount - op.getMontantttcug();
                    montantNet.add(montantNet_);
                    montantTTC.add(montantTTC_);
                    montantEsp.add(montantNet_);
                    /*
                     * montantTTC.add(op.getMontant()); montantNet.add(op.getMontantNet());
                     * montantEsp.add(op.getMontantRegle());
                     */
                    montantRemise.add(remiseNonPara);
                    montantCredit.add(op.getMontantCredit());
                    montantCredit.add(op.getMontantRestant());
                    if (op.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                        nbreVente.increment();
                    }

                }

                    break;
                case VENTE_CREDIT: {

                    montantTTC.add(op.getMontant());
                    montantNet.add(op.getMontantNet());
                    montantRemise.add(op.getMontantRemise());
                    montantEsp.add(op.getMontantRegle());
                    montantCredit.add(op.getMontantCredit());
                    montantCredit.add(op.getMontantRestant());
                    if (op.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                        nbreVente.increment();
                    }

                }
                    break;
                case ACHAT: {
                    montantAchat.add(op.getMontant());
                    try {
                        Groupefournisseur g = op.getGrossiste().getGroupeId();
                        switch (g.getLibelle()) {
                        case DateConverter.LABOREXCI:
                            montantAchatOne.add(op.getMontant());
                            break;
                        case DateConverter.DPCI:
                            montantAchatTwo.add(op.getMontant());
                            break;
                        case DateConverter.COPHARMED:
                            montantAchatThree.add(op.getMontant());
                            break;
                        case DateConverter.TEDIS:
                            montantAchatFour.add(op.getMontant());
                            break;
                        case DateConverter.AUTRES:
                            montantAchatFive.add(op.getMontant());
                            break;
                        default:
                            break;
                        }

                    } catch (Exception e) {
                    }

                }
                    break;
                default:
                    break;

                }
            });

            Integer _montantNet = montantNet.intValue();
            Integer _montantAchat = montantAchat.intValue() - avoir;
            if (_montantAchat.compareTo(0) > 0) {
                ratioVA.add(new BigDecimal(Double.valueOf(_montantNet) / _montantAchat).setScale(2, RoundingMode.FLOOR)
                        .doubleValue());
            }
            if (_montantNet.compareTo(0) > 0) {
                rationAV.add(new BigDecimal(Double.valueOf(_montantAchat) / _montantNet).setScale(2, RoundingMode.FLOOR)
                        .doubleValue());
            }
            baordPh.setMontantAchatFive(montantAchatFive.intValue());
            baordPh.setMontantAchatFour(montantAchatFour.intValue());
            baordPh.setMontantAchatThree(montantAchatThree.intValue());
            baordPh.setNbreVente(nbreVente.intValue());
            baordPh.setMontantAchatTwo(montantAchatTwo.intValue());
            baordPh.setMontantAchatOne(montantAchatOne.intValue());
            baordPh.setMontantAchat(_montantAchat);
            baordPh.setRatioVA(ratioVA.doubleValue());
            baordPh.setRationAV(rationAV.doubleValue());
            baordPh.setMontantTTC(montantTTC.intValue());
            baordPh.setMontantNet(_montantNet);
            baordPh.setMontantEsp(montantEsp.intValue());
            baordPh.setMontantRemise(montantRemise.intValue());
            baordPh.setMontantCredit(montantCredit.intValue());
            baordPh.setMontantAvoir(montantAvoir.intValue());

            /**
             * ** ***************
             */
            _summontantAchatFive.add(baordPh.getMontantAchatFive());
            _summontantAchatFour.add(baordPh.getMontantAchatFour());
            _summontantAchatThree.add(baordPh.getMontantAchatThree());
            _sumnbreVente.add(baordPh.getNbreVente());
            _summontantAchatTwo.add(baordPh.getMontantAchatTwo());
            _summontantAchatOne.add(baordPh.getMontantAchatOne());
            _summontantAchat.add(baordPh.getMontantAchat());
            _summontantTTC.add(baordPh.getMontantTTC());
            _summontantNet.add(baordPh.getMontantNet());
            _summontantEsp.add(baordPh.getMontantEsp());
            _summontantRemise.add(baordPh.getMontantRemise());
            _summontantCredit.add(baordPh.getMontantCredit());
            _summontantAvoir.add(baordPh.getMontantAvoir());
            tableauBaords.add(baordPh);
        });

        Long _montantNet = _summontantNet.longValue();
        Long _montantAchat = _summontantAchat.longValue();
        if (_montantAchat.compareTo(0l) > 0) {
            _sumratioVA.add(BigDecimal.valueOf(Double.valueOf(_montantNet) / _montantAchat)
                    .setScale(2, RoundingMode.FLOOR).doubleValue());
        }
        if (_montantNet.compareTo(0l) > 0) {
            _sumrationAV.add(BigDecimal.valueOf(Double.valueOf(_montantAchat) / _montantNet)
                    .setScale(2, RoundingMode.FLOOR).doubleValue());
        }
        summary.setMontantAchatFive(_summontantAchatFive.longValue());
        summary.setMontantAchatFour(_summontantAchatFour.longValue());
        summary.setMontantAchatThree(_summontantAchatThree.longValue());
        summary.setNbreVente(_sumnbreVente.longValue());
        summary.setMontantAchatTwo(_summontantAchatTwo.longValue());
        summary.setMontantAchatOne(_summontantAchatOne.longValue());
        summary.setMontantAchat(_summontantAchat.longValue());
        summary.setRatioVA(_sumratioVA.doubleValue());
        summary.setRationAV(_sumrationAV.doubleValue());
        summary.setMontantTTC(_summontantTTC.longValue());
        summary.setMontantNet(_summontantNet.longValue());
        summary.setMontantEsp(_summontantEsp.longValue());
        summary.setMontantRemise(_summontantRemise.longValue());
        summary.setMontantCredit(_summontantCredit.longValue());
        summary.setMontantAvoir(_summontantAvoir.longValue());
        summ.put(summary, tableauBaords.stream().sorted(comparator).collect(Collectors.toList()));
        return summ;
    }

    @Override
    public List<MvtTransaction> balanceVenteCaisse(LocalDate dtStart, boolean checked, String emplacementId) {
        try {
            TypedQuery<MvtTransaction> query = getEntityManager().createQuery(
                    "SELECT o FROM MvtTransaction o WHERE o.mvtDate = ?1  AND o.magasin.lgEMPLACEMENTID=?2 AND o.checked=?3 ",
                    MvtTransaction.class);
            query.setParameter(1, dtStart);

            query.setParameter(2, emplacementId);
            query.setParameter(3, checked);
            return query.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }

    }

    private GenericDTO buildBalanceData(LocalDate dtStart, long interval, boolean checked, String emplacementId) {
        List<BalanceDTO> balancesTmp = new ArrayList<>();
        GenericDTO generic = new GenericDTO();
        SummaryDTO summary = new SummaryDTO();
        //
        long counter = 0;
        while (counter <= interval) {
            List<MvtTransaction> mvtTransactions = balanceVenteCaisse(dtStart, checked, emplacementId);
            Optional<GenericDTO> optional = buildBalanceItem(mvtTransactions);
            if (optional.isPresent()) {
                GenericDTO genericDTO = optional.get();
                SummaryDTO summaryDTO = genericDTO.getSummary();
                balancesTmp.addAll(genericDTO.getBalances());
                updateSummaryDTO(summary, summaryDTO);
            }

            dtStart = dtStart.plusDays(1);
            counter++;
        }
        generic.setBalances(buildBalanceDTO(balancesTmp, summary));
        double ratioVA = Double.valueOf(summary.getMontantTTC()) / summary.getMontantAchat();
        ratioVA = BigDecimal.valueOf(ratioVA).setScale(2, RoundingMode.HALF_UP).doubleValue();
        summary.setRatioVA(ratioVA);
        generic.setSummary(summary);
        return generic;
    }

    private GenericDTO balanceFormat0(LocalDate dtStart, long interval, boolean checked, String emplacementId) {
        return buildBalanceData(dtStart, interval, checked, emplacementId);
    }

    private List<BalanceDTO> buildBalanceDTO(List<BalanceDTO> balancesTmp, SummaryDTO summary) {
        List<BalanceDTO> balances = new ArrayList<>();
        Map<String, List<BalanceDTO>> map = balancesTmp.stream().filter(Objects::nonNull)
                .collect(Collectors.groupingBy(BalanceDTO::getTypeVente));
        map.forEach((type, values) -> {
            BalanceDTO balance = new BalanceDTO();
            balance.setTypeVente(type);
            values.forEach(balanceDTO -> {
                updateBalanceDTO(balance, balanceDTO);
            });
            long panierMoyen = balance.getMontantTTC() / balance.getNbreVente();
            balance.setPanierMoyen(panierMoyen);
            int pourcentageVno = (int) Math
                    .round((Double.valueOf(balance.getMontantNet()) * 100) / Math.abs(summary.getMontantNet()));
            balance.setPourcentage(pourcentageVno);

            balances.add(balance);
        });
        return balances;
    }

    private void updateBalanceDTO(BalanceDTO balance, BalanceDTO balanceDTO) {
        balance.setMontantNet(balance.getMontantNet() + balanceDTO.getMontantNet());
        balance.setMontantTTC(balance.getMontantTTC() + balanceDTO.getMontantTTC());
        balance.setMontantCB(balance.getMontantCB() + balanceDTO.getMontantCB());
        balance.setMontantCheque(balance.getMontantCheque() + balanceDTO.getMontantCheque());
        balance.setMontantEsp(balance.getMontantEsp() + balanceDTO.getMontantEsp());
        balance.setMontantDiff(balance.getMontantDiff() + balanceDTO.getMontantDiff());
        balance.setMontantVirement(balance.getMontantVirement() + balanceDTO.getMontantVirement());
        balance.setNbreVente(balance.getNbreVente() + balanceDTO.getNbreVente());
        balance.setMontantRemise(balance.getMontantRemise() + balanceDTO.getMontantRemise());
        balance.setMontantTp(balance.getMontantTp() + balanceDTO.getMontantTp());
        balance.setMontantMobilePayment(balance.getMontantMobilePayment() + balanceDTO.getMontantMobilePayment());

    }

    private void updateSummaryDTO(SummaryDTO summary, SummaryDTO summaryDTO) {

        summary.setFondCaisse(summary.getFondCaisse() + summaryDTO.getFondCaisse());
        summary.setMarge(summary.getMarge() + summaryDTO.getMarge());
        summary.setMontantTTC(summary.getMontantTTC() + summaryDTO.getMontantTTC());
        summary.setMontantNet(summary.getMontantNet() + summaryDTO.getMontantNet());
        summary.setMontantRemise(summary.getMontantRemise() + summaryDTO.getMontantRemise());
        summary.setPourcentage(summary.getPourcentage() + summaryDTO.getPourcentage());
        summary.setPanierMoyen(summary.getPanierMoyen() + summaryDTO.getPanierMoyen());
        summary.setMontantEsp(summary.getMontantEsp() + summaryDTO.getMontantEsp());
        summary.setMontantCheque(summary.getMontantCheque() + summaryDTO.getMontantCheque());
        summary.setMontantVirement(summary.getMontantVirement() + summaryDTO.getMontantVirement());
        summary.setRatioVA(summary.getRatioVA() + summaryDTO.getRatioVA());
        // summary.setRationAV(summary.getRationAV() + summaryDTO.getRationAV());
        summary.setMontantCB(summary.getMontantCB() + summaryDTO.getMontantCB());
        summary.setMontantTp(summary.getMontantTp() + summaryDTO.getMontantTp());
        summary.setMontantDiff(summary.getMontantDiff() + summaryDTO.getMontantDiff());
        summary.setNbreVente(summary.getNbreVente() + summaryDTO.getNbreVente());
        summary.setMontantRegDiff(summary.getMontantRegDiff() + summaryDTO.getMontantRegDiff());
        summary.setMontantMobilePayment(summary.getMontantMobilePayment() + summaryDTO.getMontantMobilePayment());
        summary.setMontantRegleTp(summary.getMontantRegleTp() + summaryDTO.getMontantRegleTp());
        summary.setMontantEntre(summary.getMontantEntre() + summaryDTO.getMontantEntre());
        summary.setMontantAchat(summary.getMontantAchat() + summaryDTO.getMontantAchat());
        summary.setMontantSortie(summary.getMontantSortie() + summaryDTO.getMontantSortie());
        summary.setMontantTva(summary.getMontantTva() + summaryDTO.getMontantTva());
        summary.setMontantHT(summary.getMontantHT() + summaryDTO.getMontantHT());
    }

    private Optional<GenericDTO> buildBalanceItem(List<MvtTransaction> mvtTransactions) {
        if (!mvtTransactions.isEmpty()) {
            List<BalanceDTO> balances = new ArrayList<>();
            GenericDTO generic = new GenericDTO();
            SummaryDTO summary = new SummaryDTO();

            Map<TypeTransaction, List<MvtTransaction>> map = mvtTransactions.stream()
                    .collect(Collectors.groupingBy(o -> o.getTypeTransaction()));
            List<MvtTransaction> venteVNO = map.get(TypeTransaction.VENTE_COMPTANT);
            List<MvtTransaction> venteVO = map.get(TypeTransaction.VENTE_CREDIT);
            List<MvtTransaction> achats = map.get(TypeTransaction.ACHAT);
            List<MvtTransaction> entreesCaisse = map.get(TypeTransaction.ENTREE);
            List<MvtTransaction> sortieCaisse = map.get(TypeTransaction.SORTIE);
            BalanceDTO vno = null;
            Integer pourcentageVo;

            long _montantTTC = 0, _montantNet = 0, _montantRemise = 0, _montantEsp = 0, _montantCheque = 0,
                    _MontantVirement = 0, _montantCB = 0, _montantDiff = 0, _nbreVente = 0, montantAchat = 0,
                    montantSortie = 0, marge = 0, fondCaisse = 0, montantReglDiff = 0, montantRegleTp = 0,
                    montantEntre = 0, montantTva = 0, montantTp = 0, _montantMobilePayment = 0;
            double ratioVA = 0.0;
            if (venteVNO != null) {
                vno = new BalanceDTO();
                vno.setTypeVente("VNO");
                int montantTTC = 0, montantNet = 0, montantRemise = 0, panierMoyen = 0, montantEsp = 0,
                        montantCheque = 0, MontantVirement = 0, montantCB = 0, montantDiff = 0, nbreVente = 0,
                        montantMobilePayment = 0;

                for (MvtTransaction mvt : venteVNO) {

                    // montantRemise += mvt.getMontantRemise();
                    int remiseNonPara = 0;
                    if (Math.abs(mvt.getMontantRemise()) > 0) {
                        remiseNonPara = remiseNonPara(mvt.getPkey());
                    }
                    int montantFlag = montantFlag(mvt);

                    // int newAmount = (mvt.getTypeTransaction() == TypeTransaction.VENTE_COMPTANT &&
                    // mvt.getMontantAcc().compareTo(mvt.getMontant()) == 0) ? mvt.getMontant() : mvt.getMontantAcc();
                    int newAmount = montantFlag;
                    montantRemise += remiseNonPara;
                    montantTTC += (newAmount - mvt.getMontantttcug());
                    long montantNonPara = ((newAmount - remiseNonPara) - mvt.getMontantnetug());
                    montantNet += montantNonPara;
                    montantTva += (mvt.getMontantTva() - mvt.getMontantTvaUg());
                    marge += (mvt.getMarge() - mvt.getMargeug());
                    montantDiff += mvt.getMontantRestant();
                    if (mvt.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                        nbreVente++;
                    }
                    switch (mvt.getReglement().getLgTYPEREGLEMENTID()) {
                    case DateConverter.MODE_ESP:
                        montantEsp += montantNonPara;
                        break;
                    case DateConverter.MODE_CHEQUE:
                        montantCheque += montantNonPara;
                        break;
                    case DateConverter.MODE_CB:
                        montantCB += montantNonPara;
                        break;
                    case DateConverter.MODE_VIREMENT:
                        MontantVirement += montantNonPara;
                        break;
                    case DateConverter.MODE_MOOV:
                    case DateConverter.TYPE_REGLEMENT_ORANGE:
                    case DateConverter.MODE_MTN:
                    case DateConverter.MODE_WAVE:
                        montantMobilePayment += montantNonPara;
                        break;
                    }

                }

                _montantTTC += montantTTC;
                // _montantTTC-=montantFlag;
                _montantNet += montantNet;
                _MontantVirement += MontantVirement;
                _montantCB += montantCB;
                _montantCheque += montantCheque;
                _montantEsp += montantEsp;
                // _montantEsp-=montantFlag;
                _montantRemise += montantRemise;
                _montantDiff += montantDiff;
                _nbreVente += nbreVente;
                _montantMobilePayment += montantMobilePayment;
                if (nbreVente > 0) {
                    panierMoyen = montantTTC / nbreVente;
                }

                vno.setMontantCB(montantCB);
                vno.setMontantCheque(montantCheque);
                vno.setMontantEsp(montantEsp);
                vno.setMontantDiff(montantDiff);
                vno.setMontantNet(montantNet);
                vno.setMontantTTC(montantTTC);
                vno.setMontantVirement(MontantVirement);
                vno.setNbreVente(nbreVente);
                vno.setMontantRemise(montantRemise);
                vno.setMontantTp(0);
                vno.setMontantMobilePayment(montantMobilePayment);
                vno.setPanierMoyen(panierMoyen);

            }

            BalanceDTO vo = null;
            if (venteVO != null) {
                vo = new BalanceDTO();
                vo.setTypeVente("VO");
                long montantTTC = 0, montantNet = 0, montantRemise = 0, panierMoyen = 0, montantEsp = 0,
                        montantCheque = 0, MontantVirement = 0, montantCB = 0, montantDiff = 0, nbreVente = 0,
                        montantMobilePayment = 0;

                for (MvtTransaction mvt : venteVO) {

                    montantTTC += mvt.getMontant();
                    montantNet += mvt.getMontantNet();
                    montantRemise += mvt.getMontantRemise();
                    montantTva += mvt.getMontantTva();
                    marge += mvt.getMarge();
                    montantDiff += mvt.getMontantRestant();
                    montantTp += mvt.getMontantCredit();
                    if (mvt.getCategoryTransaction().equals(CategoryTransaction.CREDIT)) {
                        nbreVente++;

                    }
                    switch (mvt.getReglement().getLgTYPEREGLEMENTID()) {
                    case DateConverter.MODE_ESP:
                        montantEsp += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_CHEQUE:
                        montantCheque += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_CB:
                        montantCB += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_VIREMENT:
                        MontantVirement += mvt.getMontantRegle();
                        break;
                    case DateConverter.MODE_MOOV:
                    case DateConverter.TYPE_REGLEMENT_ORANGE:
                    case DateConverter.MODE_MTN:
                    case DateConverter.MODE_WAVE:
                        montantMobilePayment += mvt.getMontantRegle();
                        break;
                    }

                }
                if (nbreVente > 0) {
                    panierMoyen = montantTTC / nbreVente;
                }

                vo.setMontantCB(montantCB);
                vo.setMontantCheque(montantCheque);
                vo.setMontantEsp(montantEsp);
                vo.setMontantDiff(montantDiff);
                vo.setMontantNet(montantNet);
                vo.setMontantTTC(montantTTC);
                vo.setMontantVirement(MontantVirement);
                vo.setNbreVente(nbreVente);
                vo.setMontantRemise(montantRemise);
                vo.setMontantTp(montantTp);
                vo.setPanierMoyen(panierMoyen);
                vo.setMontantMobilePayment(montantMobilePayment);
                _montantTTC += montantTTC;
                _montantMobilePayment += montantMobilePayment;
                _montantNet += montantNet;
                _MontantVirement += MontantVirement;
                _montantCB += montantCB;
                _montantCheque += montantCheque;
                _montantEsp += montantEsp;
                _montantRemise += montantRemise;
                _montantDiff += montantDiff;
                _nbreVente += nbreVente;

            }
            if (vo != null) {
                pourcentageVo = (int) Math.round((Double.valueOf(vo.getMontantNet()) * 100) / Math.abs(_montantNet));
                vo.setPourcentage(pourcentageVo);
                balances.add(vo);
            }
            if (vno != null) {
                int pourcentageVno = (int) Math
                        .round((Double.valueOf(vno.getMontantNet()) * 100) / Math.abs(_montantNet));
                vno.setPourcentage(pourcentageVno);
                balances.add(vno);
            }

            if (achats != null) {
                montantAchat = achats.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);

            }
            if (sortieCaisse != null) {
                Map<String, List<MvtTransaction>> typeMvt = sortieCaisse.parallelStream()
                        .collect(Collectors.groupingBy(o -> o.gettTypeMvtCaisse().getLgTYPEMVTCAISSEID()));
                List<MvtTransaction> fond = typeMvt.get(DateConverter.MVT_FOND_CAISSE);
                List<MvtTransaction> sortie = typeMvt.get(DateConverter.MVT_SORTIE_CAISSE);
                if (fond != null) {
                    fondCaisse = fond.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }
                if (sortie != null) {
                    montantSortie = sortie.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }

            }
            if (entreesCaisse != null) {
                Map<String, List<MvtTransaction>> typeMvtEntree = entreesCaisse.parallelStream()
                        .collect(Collectors.groupingBy(o -> o.gettTypeMvtCaisse().getLgTYPEMVTCAISSEID()));
                List<MvtTransaction> entree = typeMvtEntree.get(DateConverter.MVT_ENTREE_CAISSE);
                List<MvtTransaction> diff = typeMvtEntree.get(DateConverter.MVT_REGLE_DIFF);
                List<MvtTransaction> reglementTp = typeMvtEntree.get(DateConverter.MVT_REGLE_TP);
                if (entree != null) {
                    montantEntre = entree.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }
                if (diff != null) {
                    montantReglDiff = diff.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0, Long::sum);
                }
                if (reglementTp != null) {
                    montantRegleTp = reglementTp.parallelStream().mapToLong(MvtTransaction::getMontant).reduce(0,
                            Long::sum);
                }
            }
            if (montantAchat > 0) {
                ratioVA = Double.valueOf(_montantTTC) / montantAchat;
                ratioVA = BigDecimal.valueOf(ratioVA).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
            summary.setFondCaisse(fondCaisse);
            summary.setMarge(marge);
            summary.setMontantAchat(montantAchat);
            summary.setMontantCB(_montantCB);
            summary.setMontantCheque(_montantCheque);
            summary.setMontantDiff(_montantDiff);
            summary.setMontantRegDiff(montantReglDiff);
            summary.setMontantEntre(montantEntre);
            summary.setMontantEsp(_montantEsp);
            summary.setMontantNet(_montantNet);
            summary.setMontantSortie(montantSortie);
            summary.setMontantVirement(_MontantVirement);
            summary.setMontantMobilePayment(_montantMobilePayment);
            summary.setMontantHT((_montantTTC - montantTva));
            summary.setMontantRegleTp(montantRegleTp);
            summary.setMontantRemise(_montantRemise);
            summary.setMontantTva(montantTva);
            summary.setNbreVente(_nbreVente);
            summary.setMontantTTC(_montantTTC);
            if (_nbreVente > 0) {
                summary.setPanierMoyen(_montantTTC / _nbreVente);
            }
            summary.setRatioVA(ratioVA);
            summary.setMontantTp(montantTp);
            generic.setBalances(balances);
            generic.setSummary(summary);
            return Optional.of(generic);
        }
        return Optional.empty();

    }
}
