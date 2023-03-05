package rest.service.impl;

import commonTasks.dto.BalanceDTO;
import commonTasks.dto.GenericDTO;
import commonTasks.dto.SummaryDTO;
import dal.TPreenregistrementDetail;
import dal.enumeration.TypeTransaction;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import org.json.JSONObject;
import rest.service.BalanceService;
import rest.service.dto.BalanceParamsDTO;
import rest.service.dto.BalanceVenteItemDTO;
import util.DateConverter;
import util.FunctionUtils;

/**
 *
 * @author koben
 */
@Stateless
public class BalanceServiceImpl implements BalanceService {

    private static final Logger LOG = Logger.getLogger(BalanceServiceImpl.class.getName());
    private final String balanceSqlQuery = "SELECT %s %s m.`typeTransaction`  AS typeVente, m.`typeReglementId` AS typeReglement ,SUM(m.montant) AS montantTTC,\n"
            + "SUM(m.`montantNet`) AS montantNet,SUM(m.`montantCredit`) AS montantCredit,SUM(m.`montantRemise`) AS montantRemise,\n"
            + "SUM(m.`montantRegle`) AS montantRegle,\n"
            + "SUM(m.`montantPaye`) AS montantPaye\n"
            + ",SUM(m.`montantRestant`) AS montantDiffere,SUM(sqlQ.montantTTCDetatil) AS montantTTCDetatil,SUM(sqlQ.montantAChat) AS montantAChat,\n"
            + "SUM(sqlQ.montantUg) AS montantUg,SUM(sqlQ.montantTva) AS montantTva,SUM(sqlQ.montantAchatUg) AS montantAchatUg,SUM(sqlQ.montantRemiseDetail) AS montantRemiseDetail,\n"
            + " COUNT(CASE p.`lg_PREENREGISTREMENT_ID`  WHEN p.`int_PRICE` <0 THEN - 1 ELSE 1 END) AS totalVente,SUM(m.`avoidAmount`) AS avoidAmount,SUM(m.`montantAcc`) AS montantAcc "
            + " FROM  mvttransaction m,t_preenregistrement p,\n"
            + "  (SELECT d.`lg_PREENREGISTREMENT_ID` AS idVente, \n"
            + "   SUM(d.`int_PRICE`) AS montantTTCDetatil,SUM(d.`int_QUANTITY`*d.`prixAchat`) AS montantAChat,SUM(d.`int_UG`*d.`int_PRICE_UNITAIR`) AS montantUg,SUM(d.`montantTva`) AS montantTva\n"
            + ",SUM(d.`int_UG`*d.`prixAchat`) AS montantAchatUg,SUM(d.`int_PRICE_REMISE`) AS montantRemiseDetail\n"
            + " FROM t_preenregistrement_detail d GROUP BY d.`lg_PREENREGISTREMENT_ID`  ) AS sqlQ  WHERE  sqlQ.idVente=p.`lg_PREENREGISTREMENT_ID` AND m.pkey=p.lg_PREENREGISTREMENT_ID AND  DATE(p.`dt_UPDATED`) BETWEEN "
            + " ?3 AND ?4 AND p.`str_STATUT`='is_Closed' AND p.`lg_TYPE_VENTE_ID` <> ?1 AND m.`lg_EMPLACEMENT_ID` =?2 GROUP BY typeVente,typeReglement %s %s";
    private final String remiseSqlQuery = "SELECT %s p.`lg_TYPE_VENTE_ID` AS typeVente,SUM(d.`int_PRICE_REMISE`) AS montantRemise FROM  t_preenregistrement_detail d,t_preenregistrement p,t_user u,t_famille f WHERE   d.`lg_PREENREGISTREMENT_ID`=p.`lg_PREENREGISTREMENT_ID`  AND d.`lg_FAMILLE_ID`=f.`lg_FAMILLE_ID` AND f.`bool_ACCOUNT`=?1 "
            + "  AND p.`lg_USER_ID`=u.`lg_USER_ID` AND u.`lg_EMPLACEMENT_ID`=?2  AND p.`str_STATUT`='is_Closed' AND %s  AND p.`lg_TYPE_VENTE_ID` <> ?3 AND DATE(p.`dt_UPDATED`) BETWEEN ?4 AND ?5 AND d.`int_PRICE_REMISE` <>0 GROUP BY typeVente %s";

    private final String otherMvtSql = "SELECT m.`typeMvtCaisseId` AS typeMvtCaisse, SUM(m.montant) AS montantTTC FROM  mvttransaction m WHERE DATE(m.mvtdate) BETWEEN ?1 AND ?2 AND m.`typeTransaction` >2  AND m.`lg_EMPLACEMENT_ID` =?3  GROUP BY m.`typeMvtCaisseId` ";

    private final String bonsSql = "SELECT  SUM(m.montant) AS montant FROM  mvttransaction m WHERE DATE(m.mvtdate) BETWEEN ?1 AND ?2 AND m.`typeTransaction` =2  AND m.`lg_EMPLACEMENT_ID` =?3 ";
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public List<BalanceDTO> buildBalanceFromPreenregistrement(BalanceParamsDTO balanceParams) {

        List<BalanceDTO> balances = new ArrayList<>();
        List<BalanceVenteItemDTO> dataVentes;
// List<BalanceVenteItemDTO> dataVentes = fetchPreenregistrements(balanceParams, " DATE_FORMAT(p.`dt_UPDATED`,'%Y-%m-%d' ) AS mvtDate , ", " AND p.`int_PRICE`>?0 ", ",mvtDate");
        if (!balanceParams.isToPrint()) {
            dataVentes = fetchPreenregistrements(balanceParams, "", "", "", "")
                    .stream().map(this::buildFromTuple).collect(Collectors.toList());
        } else {
            dataVentes = fetchPreenregistrements(balanceParams, "", "", "m.`typeMvtCaisseId` AS typeMvtCaisse,", ",typeMvtCaisse")
                    .stream().map(this::buildPdfDataFromTuple).collect(Collectors.toList());
        }

        Map<TypeTransaction, List<BalanceVenteItemDTO>> groupByTypeVente = dataVentes.stream().collect(Collectors.groupingBy(BalanceVenteItemDTO::getTypeTransaction));
        if (groupByTypeVente.containsKey(TypeTransaction.VENTE_COMPTANT)) {

            List<BalanceVenteItemDTO> vnoData = groupByTypeVente.remove(TypeTransaction.VENTE_COMPTANT);
            BalanceDTO balanceVno = buildVenteBalance(vnoData);
            balanceVno.setTypeVente(DateConverter.VENTE_COMPTANT);
            balanceVno.setBalanceId(balanceVno.getTypeVente());
            balances.add(balanceVno);
        }
        if (groupByTypeVente.containsKey(TypeTransaction.VENTE_CREDIT)) {
            List<BalanceVenteItemDTO> vnoData = groupByTypeVente.remove(TypeTransaction.VENTE_CREDIT);
            BalanceDTO balanceVo = buildVenteBalance(vnoData);
            balanceVo.setTypeVente(DateConverter.VENTE_ASSURANCE);
            balanceVo.setBalanceId(balanceVo.getTypeVente());
            balances.add(balanceVo);
        }
        return updatePourcent(balances);
    }

    @Override
    public GenericDTO getBalanceVenteCaisseData(BalanceParamsDTO balanceParams) {
        List<BalanceDTO> balances = buildBalanceFromPreenregistrement(balanceParams);
        long montantAchat = bonLivraisonsAmount(balanceParams);
        List<BalanceVenteItemDTO> othersTypeMvts = othersTypeMvts(balanceParams);
        return buildBalance(balances, othersTypeMvts, montantAchat);
    }

    @Override
    public JSONObject getBalanceVenteCaisseDataView(BalanceParamsDTO balanceParams) {
        GenericDTO generic = this.getBalanceVenteCaisseData(balanceParams);
        SummaryDTO summary = generic.getSummary();
        List<BalanceDTO> balances = generic.getBalances();
        return FunctionUtils.returnData(balances, balances.size(), summary);
    }

    private BalanceDTO buildVenteBalance(List<BalanceVenteItemDTO> venteData) {
        long montantTTC = 0;
        long montantNet = 0;
        long montantEsp = 0;
        long montantCheque = 0;
        long montantVirement = 0;
        long montantCB = 0;
        long montantMobilePayment = 0;
        long montantTva = 0;
        long marge = 0;
        long montantAchat = 0;
        long montantRemise = 0;
        long montantDiff = 0;
        long nbreVente = 0;
        long panierMoyen = 0;
        long montantCredit = 0;
        long montantFlag = 0;
        Map<String, List<BalanceVenteItemDTO>> mapByTypeReglemement = venteData.stream().collect(Collectors.groupingBy(BalanceVenteItemDTO::getTypeReglement));
        for (Map.Entry<String, List<BalanceVenteItemDTO>> entry : mapByTypeReglemement.entrySet()) {
            String typeReglemement = entry.getKey();
            List<BalanceVenteItemDTO> values = entry.getValue();
            for (BalanceVenteItemDTO balanceVenteItem : values) {
                montantTTC += balanceVenteItem.getMontantTTCDetatil().longValue();
                montantTva += balanceVenteItem.getMontantTva().longValue();
                montantAchat += balanceVenteItem.getMontantAchat().longValue();
                montantRemise += balanceVenteItem.getMontantRemiseDetail().longValue();
                montantDiff += balanceVenteItem.getMontantDiffere().longValue();
                nbreVente += balanceVenteItem.getTotalVente();
                montantCredit += balanceVenteItem.getMontantCredit().longValue();
                switch (typeReglemement) {
                    case DateConverter.MODE_ESP:
                        montantEsp += balanceVenteItem.getMontantRegle().longValue();
                        break;
                    case DateConverter.MODE_CHEQUE:
                        montantCheque += balanceVenteItem.getMontantRegle().longValue();
                        break;
                    case DateConverter.MODE_CB:
                        montantCB += balanceVenteItem.getMontantRegle().longValue();
                        break;
                    case DateConverter.MODE_VIREMENT:
                        montantVirement += balanceVenteItem.getMontantRegle().longValue();
                        break;
                    case DateConverter.MODE_MOOV:
                    case DateConverter.TYPE_REGLEMENT_ORANGE:
                    case DateConverter.MODE_MTN:
                    case DateConverter.MODE_WAVE:
                        montantMobilePayment += balanceVenteItem.getMontantRegle().longValue();
                        break;
                    default:
                        break;

                }

            }

        }
        BalanceDTO balance = new BalanceDTO();

        if (nbreVente > 0) {
            panierMoyen = montantTTC / nbreVente;
            balance.setNbreVente(nbreVente);
        }
        montantNet += (montantTTC - montantRemise);
        marge += (montantNet - montantTva) - montantAchat;

        balance.setMarge(marge);
        balance.setMontantDiff(montantDiff);
        balance.setPanierMoyen(panierMoyen);
        balance.setMontantCB(montantCB);
        balance.setMontantCheque(montantCheque);
        balance.setMontantEsp(montantEsp);
        balance.setMontantMobilePayment(montantMobilePayment);
        balance.setMontantTTC(montantTTC);
        balance.setMontantNet(montantNet);
        balance.setMontantRemise(montantRemise);
        balance.setMontantAchat(montantAchat);
        balance.setMontantVirement(montantVirement);
        balance.setMontantTva(montantTva);
        balance.setMontantTp(montantCredit);
        return balance;
    }

    private List<Tuple> fetchPreenregistrements(BalanceParamsDTO balanceParams, String subQueryMvtDate, String subQueryGroupBy, String typeMvtCaisse, String typeMvtCaisseGroupBy) { //AND p.`int_PRICE`>?0

        String sql = String.format(balanceSqlQuery, subQueryMvtDate, typeMvtCaisse, subQueryGroupBy, typeMvtCaisseGroupBy);
        LOG.log(Level.INFO, "sql--- balance vente {0}", sql);
        try {
            Query query = em.createNativeQuery(sql, Tuple.class)
                    .setParameter(1, DateConverter.DEPOT_EXTENSION)
                    .setParameter(2, balanceParams.getEmplacementId())
                    .setParameter(3, java.sql.Date.valueOf(balanceParams.getDtStart()))
                    .setParameter(4, java.sql.Date.valueOf(balanceParams.getDtEnd()));
            return query.getResultList();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new ArrayList<>();
        }
    }

    private List<BalanceVenteItemDTO> fetchRemises(BalanceParamsDTO balanceParams, String subQueryMvtDate, String subQuery, String subQueryGroupBy, boolean remisePara) { //AND p.`int_PRICE`>?0

        String sql = String.format(remiseSqlQuery, subQueryMvtDate, subQuery, subQueryGroupBy);
        LOG.log(Level.INFO, "sql--- remiseSqlQuery  {0}", sql);
        try {
            Query query = em.createNativeQuery(sql, Tuple.class)
                    .setParameter(1, remisePara)
                    .setParameter(2, balanceParams.getEmplacementId())
                    .setParameter(3, DateConverter.DEPOT_EXTENSION)
                    .setParameter(4, java.sql.Date.valueOf(balanceParams.getDtStart()))
                    .setParameter(5, java.sql.Date.valueOf(balanceParams.getDtEnd()));
            List<Tuple> list = query.getResultList();
            return list.stream().map(this::buildRemiseFromTuple).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new ArrayList<>();
        }
    }

    private List<BalanceVenteItemDTO> othersTypeMvts(BalanceParamsDTO balanceParams) {

        LOG.log(Level.INFO, "sql--- balance othersTypeMvts {0}", otherMvtSql);
        try {
            Query query = em.createNativeQuery(otherMvtSql, Tuple.class)
                    .setParameter(1, java.sql.Date.valueOf(balanceParams.getDtStart()))
                    .setParameter(2, java.sql.Date.valueOf(balanceParams.getDtEnd()))
                    .setParameter(3, balanceParams.getEmplacementId());
            List<Tuple> list = query.getResultList();
            return list.stream().map(this::buildFromTupleOtherMvt).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new ArrayList<>();
        }
    }

    private long bonLivraisonsAmount(BalanceParamsDTO balanceParams) {

        LOG.log(Level.INFO, "sql--- bonLivraison {0}", bonsSql);
        try {
            Query query = em.createNativeQuery(bonsSql, Tuple.class)
                    .setParameter(1, java.sql.Date.valueOf(balanceParams.getDtStart()))
                    .setParameter(2, java.sql.Date.valueOf(balanceParams.getDtEnd()))
                    .setParameter(3, balanceParams.getEmplacementId());
            Tuple tuple = (Tuple) query.getSingleResult();
            return tuple.get("montant", BigDecimal.class).longValue();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    private BalanceVenteItemDTO buildRemiseFromTuple(Tuple tuple) {
        return BalanceVenteItemDTO.builder()
                .typeVente(tuple.get("typeVente", String.class))
                .montantRemise(tuple.get("montantRemise", BigDecimal.class))
                .build();
    }

    private BalanceVenteItemDTO buildFromTuple(Tuple tuple) {
        TypeTransaction typeVente = tuple.get("typeVente", Integer.class) == 0 ? TypeTransaction.VENTE_COMPTANT : TypeTransaction.VENTE_CREDIT;
        return BalanceVenteItemDTO.builder()
                .typeTransaction(typeVente)
                .typeReglement(tuple.get("typeReglement", String.class))
                .montantAcc(tuple.get("montantAcc", BigDecimal.class))
                .montantAchat(tuple.get("montantAchat", BigDecimal.class))
                .montantTva(tuple.get("montantTva", BigDecimal.class))
                .montantUg(tuple.get("montantUg", BigDecimal.class))
                .montantAchatUg(tuple.get("montantAchatUg", BigDecimal.class))
                .montantTTC(tuple.get("montantTTC", BigDecimal.class))
                .montantNet(tuple.get("montantNet", BigDecimal.class))
                .montantCredit(tuple.get("montantCredit", BigDecimal.class))
                .montantRemise(tuple.get("montantRemise", BigDecimal.class))
                .montantRegle(tuple.get("montantRegle", BigDecimal.class))
                .montantPaye(tuple.get("montantPaye", BigDecimal.class))
                .montantDiffere(tuple.get("montantDiffere", BigDecimal.class))
                .montantTTCDetatil(tuple.get("montantTTCDetatil", BigDecimal.class))
                .montantAchat(tuple.get("montantAChat", BigDecimal.class))
                .totalVente(tuple.get("totalVente", BigInteger.class).intValue())
                .montantRemiseDetail(tuple.get("montantRemiseDetail", BigDecimal.class))
                .build();
    }

    private BalanceVenteItemDTO buildPdfDataFromTuple(Tuple tuple) {
        BalanceVenteItemDTO balanceVenteItem = buildFromTuple(tuple);
        balanceVenteItem.setTypeMvtCaisse(tuple.get("typeMvtCaisse", String.class));
        return balanceVenteItem;
    }

    private BalanceVenteItemDTO buildFromTupleOtherMvt(Tuple tuple) {
        return BalanceVenteItemDTO.builder()
                .typeMvtCaisse(tuple.get("typeMvtCaisse", String.class))
                .montantTTC(tuple.get("montantTTC", BigDecimal.class))
                .build();
    }

    private int computePercent(long amount, long totalAmount) {
        return (int) Math.round((Double.valueOf(amount) * 100) / Math.abs(totalAmount));
    }

    private List<BalanceDTO> updatePourcent(List<BalanceDTO> balances) {
        List<BalanceDTO> list = new ArrayList<>();
        long totalMontantNet = 0;
        BalanceDTO balanceVNO = null;
        BalanceDTO balanceVO = null;
        for (BalanceDTO balance : balances) {
            totalMontantNet += balance.getMontantNet();
            if (balance.getTypeVente().equals(DateConverter.VENTE_COMPTANT)) {
                balanceVNO = balance;
            } else if (balance.getTypeVente().equals(DateConverter.VENTE_ASSURANCE)) {
                balanceVO = balance;
            }
        }
        if (balanceVNO != null) {
            balanceVNO.setPourcentage(computePercent(balanceVNO.getMontantNet(), totalMontantNet));
            list.add(balanceVNO);
        }
        if (balanceVO != null) {
            balanceVO.setPourcentage(computePercent(balanceVO.getMontantNet(), totalMontantNet));
            list.add(balanceVO);
        }
        return list;
    }

    private GenericDTO buildBalance(List<BalanceDTO> balances, List<BalanceVenteItemDTO> othersTypeMvts, long montantAchat) {
        long montantTTC = 0;
        long montantNet = 0;
        long montantRemise = 0;
        long panierMoyen = 0;
        long montantEsp = 0;
        long montantCheque = 0;
        long montantVirement = 0;
        long montantCB = 0;
        long montantTp = 0;
        long montantDiff = 0;
        int nbreVente = 0;
        long fondCaisse = 0;
        long montantRegDiff = 0;
        long montantMobilePayment = 0;
        long montantRegleTp = 0;
        long montantEntre = 0;
        long montantSortie = 0;
        long marge = 0;
        long montantTva = 0;
        double ratioVA = 0.0;
        double rationAV = 0.0;
        for (BalanceDTO balance : balances) {
            montantTTC += balance.getMontantTTC();
            montantNet += balance.getMontantNet();
            montantRemise += balance.getMontantRemise();
            montantEsp += balance.getMontantEsp();
            montantCheque += balance.getMontantCheque();
            montantVirement += balance.getMontantVirement();
            montantCB += balance.getMontantCB();
            montantTp += balance.getMontantTp();
            montantDiff += balance.getMontantDiff();
            nbreVente += balance.getNbreVente();
            montantMobilePayment += balance.getMontantMobilePayment();
            marge += balance.getMarge();
            montantTva += balance.getMontantTva();

        }
        long montantHT = montantTTC - montantTva;
        for (BalanceVenteItemDTO o : othersTypeMvts) {
            long amount = o.getMontantTTC().longValue();
            switch (o.getTypeMvtCaisse()) {
                case DateConverter.MVT_FOND_CAISSE:
                    fondCaisse += amount;
                    break;
                case DateConverter.MVT_SORTIE_CAISSE:
                    montantSortie += amount;
                    break;
                case DateConverter.MVT_ENTREE_CAISSE:
                    montantEntre += amount;
                    break;
                case DateConverter.MVT_REGLE_DIFF:
                    montantRegDiff += amount;
                    break;
                case DateConverter.MVT_REGLE_TP:
                    montantRegleTp += amount;
                    break;
                default:
                    break;
            }
        }
        if (montantAchat > 0) {

            ratioVA = new BigDecimal(Double.valueOf(montantTTC) / montantAchat).setScale(2, RoundingMode.HALF_UP).doubleValue();
            rationAV = new BigDecimal(Double.valueOf(montantAchat) / montantTTC).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }
        if (nbreVente > 0) {
            panierMoyen = montantTTC / nbreVente;

        }
        SummaryDTO summary = new SummaryDTO();
        summary.setFondCaisse(fondCaisse);
        summary.setMarge(marge);
        summary.setMontantAchat(montantAchat);
        summary.setMontantCB(montantCB);
        summary.setMontantCheque(montantCheque);
        summary.setMontantDiff(montantDiff);
        summary.setMontantRegDiff(montantRegDiff);
        summary.setMontantEntre(montantEntre);
        summary.setMontantEsp(montantEsp);
        summary.setMontantNet(montantNet);
        summary.setMontantSortie(montantSortie);
        summary.setMontantVirement(montantVirement);
        summary.setMontantHT(montantHT);
        summary.setMontantRegleTp(montantRegleTp);
        summary.setMontantRemise(montantRemise);
        summary.setMontantTva(montantTva);
        summary.setNbreVente(nbreVente);
        summary.setMontantTTC(montantTTC);
        summary.setMontantMobilePayment(montantMobilePayment);
        summary.setPanierMoyen(panierMoyen);
        summary.setRatioVA(ratioVA);
        summary.setRationAV(rationAV);
        summary.setMontantTp(montantTp);
        GenericDTO generic = new GenericDTO();
        generic.setSummary(summary);
        generic.setBalances(balances);
        return generic;

    }

}
