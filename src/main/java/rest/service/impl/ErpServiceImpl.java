/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.ErpAchatFournisseurDTO;
import commonTasks.dto.ErpCaComptant;
import commonTasks.dto.ErpFactureDTO;
import commonTasks.dto.ErpFournisseur;
import commonTasks.dto.ErpReglementDTO;
import commonTasks.dto.ErpTiersPayant;
import commonTasks.dto.ErpTiersPayantDTO;
import commonTasks.dto.StockDailyValueDTO;
import dal.StockDailyValue;
import dal.TDossierReglement;
import dal.TFacture;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TReglement;
import dal.TTiersPayant;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import rest.service.ErpService;
import util.DateConverter;

/**
 *
 * @author koben
 */
@Stateless
public class ErpServiceImpl implements ErpService {

    private static final Logger LOG = Logger.getLogger(ErpServiceImpl.class.getName());
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public StockDailyValueDTO valorisation(String day) {
        try {
            StockDailyValue sdv = getEntityManager().find(StockDailyValue.class, Integer.valueOf(LocalDate.parse(day).format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
            return new StockDailyValueDTO(sdv);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "valorisation =====>>", e);
            return new StockDailyValueDTO();
        }
    }

    @Override
    public List<ErpCaComptant> caComptant(String dtStart, String dtEnd) {
        try {
            List<Tuple> list = getEntityManager().createNativeQuery("SELECT SUM(m.montantRegle) AS MONTANTREGLE,SUM(m.montantPaye) AS MONTANTPAYE, SUM(m.montantRemise) AS montantRemise, SUM(m.montantTva) as montantTva,m.mvtdate,m.typeReglementId FROM mvttransaction m where m.checked=1 AND (m.typeTransaction=0 OR m.typeTransaction=1) AND m.lg_EMPLACEMENT_ID='1' AND DATE(m.mvtdate) BETWEEN ?1 AND ?2 GROUP BY m.mvtdate,m.typeReglementId", Tuple.class)
                    .setParameter(1, LocalDate.parse(dtStart))
                    .setParameter(2, LocalDate.parse(dtEnd))
                    .getResultList();
            List<ErpCaComptant> caComptants = new ArrayList<>();
            list.stream().map(t -> {
                // long montantRegle = t.get(0, BigDecimal.class).longValue();
                long montantPaye = t.get(1, BigDecimal.class).longValue();
                long montantRemise = t.get(2, BigDecimal.class).longValue();
                long montantTva = t.get(3, BigDecimal.class).longValue();
                LocalDate mvtDate = t.get(4, java.sql.Date.class).toLocalDate();
                String typeReglementId = t.get(5, String.class);
                ErpCaComptant caComptant = new ErpCaComptant();
                caComptant.setMode(typeReglementId);
                caComptant.setTotEsp(montantPaye);
                caComptant.setRemiseSurCA(montantRemise);
                caComptant.setTotTVA(montantTva);
                caComptant.setMvtDate(mvtDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                return caComptant;
            }).forEachOrdered(caComptant -> {
                caComptants.add(caComptant);
            });
            List<ErpCaComptant> caComptants2 = new ArrayList<>();
            Map<String, List<ErpCaComptant>> map = caComptants.stream().collect(Collectors.groupingBy(ErpCaComptant::getMvtDate));
            map.forEach((k, v) -> {
                ErpCaComptant caComptant = new ErpCaComptant();
                caComptant.setMvtDate(k);
                v.forEach(e -> {
                    caComptant.setRemiseSurCA(caComptant.getRemiseSurCA() + e.getRemiseSurCA());
                    caComptant.setTotTVA(caComptant.getTotTVA() + e.getTotTVA());
                    switch (e.getMode()) {
                        case DateConverter.MODE_ESP:
                            caComptant.setTotEsp(caComptant.getTotEsp() + e.getTotEsp());
                            break;
                        case DateConverter.MODE_CHEQUE:
                            caComptant.setTotChq(caComptant.getTotChq() + e.getTotEsp());
                            break;
                        case DateConverter.MODE_CB:
                            caComptant.setTotCB(caComptant.getTotCB() + e.getTotEsp());
                            break;
                        case DateConverter.MODE_VIREMENT:
                            caComptant.setTotVirement(caComptant.getTotVirement() + e.getTotEsp());
                            break;
                        case DateConverter.MODE_MOOV:
                        case DateConverter.TYPE_REGLEMENT_ORANGE:
                        case DateConverter.MODE_MTN:
                            caComptant.setTotMobile(caComptant.getTotMobile() + e.getTotEsp());
                            break;
                        default:
                            break;
                    }
                });

                caComptants2.add(caComptant);
            });
            return caComptants2;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "caComptant =====>>", e);
            return Collections.emptyList();
        }
    }

    private List<TPreenregistrementCompteClientTiersPayent> nonFactures(String dtStart, String dtEnd) {
        try {
            TypedQuery<TPreenregistrementCompteClientTiersPayent> q = getEntityManager().createQuery("SELECT o FROM TPreenregistrementCompteClientTiersPayent o WHERE o.lgPREENREGISTREMENTID.strSTATUT='is_Closed' AND o.lgPREENREGISTREMENTID.bISCANCEL=FALSE AND o.lgPREENREGISTREMENTID.intPRICE >0 AND FUNCTION('DATE',o.lgPREENREGISTREMENTID.dtCREATED) BETWEEN ?1 AND ?2 AND o.lgPREENREGISTREMENTCOMPTECLIENTPAYENTID NOT IN (SELECT s.strREF FROM TFactureDetail s  )", TPreenregistrementCompteClientTiersPayent.class);
            q.setParameter(1, java.sql.Date.valueOf(dtStart), TemporalType.DATE);
            q.setParameter(2, java.sql.Date.valueOf(dtEnd), TemporalType.DATE);
            return q.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "nonFactures =====>>", e);
            return Collections.emptyList();
        }
    }

    private List<Tuple> factures(String dtStart, String dtEnd) {
        try {
            return getEntityManager().createQuery("SELECT o,f.lgFACTUREID FROM TPreenregistrementCompteClientTiersPayent o,TFactureDetail f WHERE o.lgPREENREGISTREMENTID.strSTATUT='is_Closed' AND o.lgPREENREGISTREMENTID.bISCANCEL=FALSE AND o.lgPREENREGISTREMENTID.intPRICE >0 AND FUNCTION('DATE',o.lgPREENREGISTREMENTID.dtCREATED) BETWEEN ?1 AND ?2 AND o.lgPREENREGISTREMENTCOMPTECLIENTPAYENTID =f.strREF", Tuple.class)
                    .setParameter(1, java.sql.Date.valueOf(dtStart), TemporalType.DATE)
                    .setParameter(2, java.sql.Date.valueOf(dtEnd), TemporalType.DATE)
                    .getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "factures =====>>", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ErpTiersPayantDTO> rrpTiersPayant(String dtStart, String dtEnd) {
        List<ErpTiersPayantDTO> datas = new ArrayList<>();
        datas.addAll(nonFactures(dtStart, dtEnd).stream().map(ErpTiersPayantDTO::new).collect(Collectors.toList()));
        List<Tuple> list = factures(dtStart, dtEnd);
        list.forEach(t -> {
            TPreenregistrementCompteClientTiersPayent p = t.get(0, TPreenregistrementCompteClientTiersPayent.class);
            TFacture facture = t.get(1, TFacture.class);
            datas.add(new ErpTiersPayantDTO(p, facture));
        });
        return datas;

    }

    @Override
    public List<ErpReglementDTO> erpReglements(String dtStart, String dtEnd) {
        List<ErpReglementDTO> datas = new ArrayList<>();
        List<Tuple> list = reglements(dtStart, dtEnd);
        list.forEach(t -> {
            TDossierReglement dossierReglement = t.get(0, TDossierReglement.class);
            TReglement reglement = t.get(1, TReglement.class);
            datas.add(new ErpReglementDTO(dossierReglement, reglement, t.get(2, TTiersPayant.class)));
        });
        return datas;
    }

    private List<Tuple> reglements(String dtStart, String dtEnd) {
        try {
            return getEntityManager().createQuery("SELECT o,f,p FROM TDossierReglement o,TReglement f,TTiersPayant p WHERE  FUNCTION('DATE',o.dtCREATED) BETWEEN ?1 AND ?2 AND o.lgDOSSIERREGLEMENTID =f.strREFRESSOURCE AND o.strORGANISMEID=p.lgTIERSPAYANTID", Tuple.class)
                    .setParameter(1, java.sql.Date.valueOf(dtStart), TemporalType.DATE)
                    .setParameter(2, java.sql.Date.valueOf(dtEnd), TemporalType.DATE)
                    .getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "reglements =====>>", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ErpFactureDTO> erpFactures(String dtStart, String dtEnd) {
        List<ErpFactureDTO> datas = new ArrayList<>();
        List<Tuple> list = facturePeriode(dtStart, dtEnd);
        list.forEach(t -> {
            TFacture facture = t.get(0, TFacture.class);
            datas.add(new ErpFactureDTO(facture, t.get(1, TTiersPayant.class)));
        });
        return datas;
    }

    private List<Tuple> facturePeriode(String dtStart, String dtEnd) {
        try {
            return getEntityManager().createQuery("SELECT f,p FROM TFacture f,TTiersPayant p WHERE  FUNCTION('DATE',f.dtCREATED) BETWEEN ?1 AND ?2 AND f.strCUSTOMER =p.lgTIERSPAYANTID ", Tuple.class)
                    .setParameter(1, java.sql.Date.valueOf(dtStart), TemporalType.DATE)
                    .setParameter(2, java.sql.Date.valueOf(dtEnd), TemporalType.DATE)
                    .getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "facturePeriode =====>>", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ErpFournisseur> fournisseurs() {
        return getEntityManager().createQuery("SELECT new commonTasks.dto.ErpFournisseur(o) FROM TGrossiste o", ErpFournisseur.class).getResultList();
    }

    @Override
    public List<ErpAchatFournisseurDTO> achatsFournisseurs(String dtStart, String dtEnd) {
        return getEntityManager().createQuery("SELECT new commonTasks.dto.ErpAchatFournisseurDTO(o) FROM TBonLivraison  o WHERE o.strSTATUT='is_Closed' AND FUNCTION('DATE',o.dtCREATED) BETWEEN ?1 AND ?2", ErpAchatFournisseurDTO.class)
                .setParameter(1, java.sql.Date.valueOf(dtStart), TemporalType.DATE)
                .setParameter(2, java.sql.Date.valueOf(dtEnd), TemporalType.DATE)
                .getResultList();
    }

    @Override
    public List<ErpTiersPayant> allTiersPayants() {
        return getEntityManager().createQuery("SELECT new commonTasks.dto.ErpTiersPayant(o) FROM TTiersPayant o", ErpTiersPayant.class).getResultList();
    }
}
