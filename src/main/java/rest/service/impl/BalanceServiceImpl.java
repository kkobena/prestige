package rest.service.impl;

import commonTasks.dto.BalanceDTO;
import commonTasks.dto.GenericDTO;
import commonTasks.dto.SummaryDTO;
import commonTasks.dto.TableauBaordPhDTO;
import commonTasks.dto.TableauBaordSummary;
import commonTasks.dto.TvaDTO;
import dal.TParameters;
import dal.enumeration.TypeTransaction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
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
    private final String balanceSqlQuery = "SELECT %s %s m.`typeTransaction`  AS typeVente, m.`typeReglementId` AS typeReglement ,SUM(m.montant) AS montantTTC,"
            + "SUM(m.`montantNet`) AS montantNet,SUM(m.`montantCredit`) AS montantCredit,SUM(m.`montantRemise`) AS montantRemise,"
            + " SUM(m.`montantRegle`) AS montantRegle, SUM(m.`montantPaye`) AS montantPaye,SUM(m.`montantRestant`) AS montantDiffere,SUM(sqlQ.montantTTCDetatil) AS montantTTCDetatil,SUM(sqlQ.montantAChat) AS montantAChat, "
            + " SUM(sqlQ.montantUg) AS montantUg,SUM(sqlQ.montantTva) AS montantTva,SUM(sqlQ.montantAchatUg) AS montantAchatUg,SUM(sqlQ.montantRemiseDetail) AS montantRemiseDetail, SUM(sqlQ.montantTvaUg) AS montantTvaUg,"
            + " SUM(CASE p.`lg_PREENREGISTREMENT_ID`  WHEN p.`int_PRICE` <0 || p.`b_IS_CANCEL`=1 THEN 0 ELSE 1 END) AS totalVente,SUM(m.`avoidAmount`) AS avoidAmount,SUM(m.`montantAcc`) AS montantAcc "
            + " FROM  mvttransaction m,t_preenregistrement p,(SELECT d.`lg_PREENREGISTREMENT_ID` AS idVente,"
            + " SUM(d.`int_PRICE`) AS montantTTCDetatil,SUM(d.`int_QUANTITY`*d.`prixAchat`) AS montantAChat,SUM(d.`int_UG`*d.`int_PRICE_UNITAIR`) AS montantUg,SUM(d.`montantTva`) AS montantTva\n"
            + ",SUM(d.`int_UG`*d.`prixAchat`) AS montantAchatUg,SUM(d.`int_PRICE_REMISE`) AS montantRemiseDetail,SUM(d.montanttvaug) AS montantTvaUg "
            + " FROM t_preenregistrement_detail d GROUP BY d.`lg_PREENREGISTREMENT_ID`  ) AS sqlQ  WHERE  sqlQ.idVente=p.`lg_PREENREGISTREMENT_ID` AND m.pkey=p.lg_PREENREGISTREMENT_ID AND  DATE(p.`dt_UPDATED`) BETWEEN "
            + " ?3 AND ?4 AND p.`str_STATUT`='is_Closed' AND p.`lg_TYPE_VENTE_ID` <> ?1 AND m.`lg_EMPLACEMENT_ID` =?2 {excludeStatement} {flagStatement} GROUP BY typeVente,typeReglement %s %s";
    private final String remiseSqlQuery = "SELECT %s p.`lg_TYPE_VENTE_ID` AS typeVente,SUM(d.`int_PRICE_REMISE`) AS montantRemise FROM  t_preenregistrement_detail d,t_preenregistrement p,t_user u,t_famille f WHERE   d.`lg_PREENREGISTREMENT_ID`=p.`lg_PREENREGISTREMENT_ID`  AND d.`lg_FAMILLE_ID`=f.`lg_FAMILLE_ID` AND f.`bool_ACCOUNT`=?1 "
            + "  AND p.`lg_USER_ID`=u.`lg_USER_ID` AND u.`lg_EMPLACEMENT_ID`=?2  AND p.`str_STATUT`='is_Closed' AND %s  AND p.`lg_TYPE_VENTE_ID` <> ?3 AND DATE(p.`dt_UPDATED`) BETWEEN ?4 AND ?5 AND d.`int_PRICE_REMISE` <>0 GROUP BY typeVente %s";

    private final String otherMvtSql = "SELECT m.`typeMvtCaisseId` AS typeMvtCaisse, SUM(m.montant) AS montantTTC FROM  mvttransaction m WHERE DATE(m.mvtdate) BETWEEN ?1 AND ?2 AND m.`typeTransaction` >2  AND m.`lg_EMPLACEMENT_ID` =?3  GROUP BY m.`typeMvtCaisseId` ";

    private final String bonsSql = "SELECT  SUM(m.montant) AS montant FROM  mvttransaction m WHERE DATE(m.mvtdate) BETWEEN ?1 AND ?2 AND m.`typeTransaction` =2  AND m.`lg_EMPLACEMENT_ID` =?3 ";
    private final String excludeStatement = " AND  p.`lg_PREENREGISTREMENT_ID` NOT IN (SELECT v.preenregistrement_id FROM vente_exclu v) ";
    private final String flagStatement = " AND  m.flag_id IS NULL ";

    private final String tvasSql = "SELECT {byDay} SUM(d.int_PRICE) AS montantTTC,SUM(d.int_UG*d.int_PRICE_UNITAIR) AS montantUg,d.valeurTva AS valeurTva FROM t_preenregistrement_detail d,t_preenregistrement p,t_user u ,mvttransaction m WHERE p.lg_PREENREGISTREMENT_ID=d.lg_PREENREGISTREMENT_ID AND p.`lg_PREENREGISTREMENT_ID`=m.pkey AND m.flag_id IS NULL"
            + " AND p.lg_TYPE_VENTE_ID <> ?1 AND p.str_STATUT='is_Closed' AND DATE(p.dt_UPDATED)  BETWEEN ?2 AND ?3 AND p.lg_USER_ID=u.lg_USER_ID AND u.lg_EMPLACEMENT_ID=?4 {excludeStatement} {tvaVnoOnly} GROUP BY d.`valeurTva` {groupByDay}";
    private final String amountToRemoveSql = "SELECT COALESCE(SUM(m.`montantAcc`),0) AS montantAcc, COALESCE(SUM(m.`montant`),0) AS montant FROM mvttransaction m,t_preenregistrement p WHERE m.flag IS TRUE AND m.flag_id IS NULL AND m.`typeTransaction` =0 AND p.`lg_PREENREGISTREMENT_ID`=m.pkey AND DATE(p.`dt_UPDATED`) BETWEEN  ?1 AND ?2 AND p.`str_STATUT`='is_Closed' AND p.`lg_TYPE_VENTE_ID` <> ?3 AND m.`lg_EMPLACEMENT_ID` =?4 ";
    private final String TVA_BALANCE_LAST_UPDATE = "TVA_BALANCE_LAST_UPDATE";
    private final String tvaVnoOnly = " AND m.`typeTransaction` =0 ";
    private final String byDay = " DATE_FORMAT(p.`dt_UPDATED`,'%Y-%m-%d' ) AS mvtDate ,";
    private final String groupByDay = " ,mvtDate ";
    private static final String TVA_DATE_PATERN = "dd/MM/yyyy";

    private final String tableauBoardSql = "SELECT DATE_FORMAT(p.`dt_UPDATED`,'%Y-%m-%d' ) AS mvtDate , SUM(m.montant) AS montantTTC,SUM(m.`montantNet`) AS montantNet,SUM(m.`montantCredit`) AS montantCredit,SUM(m.`montantRemise`) AS montantRemise,"
            + "SUM(m.`montantRegle`) AS montantRegle, SUM(m.`montantPaye`) AS montantPaye,SUM(m.`montantRestant`) AS montantDiffere,SUM(sqlQ.montantTTCDetatil) AS montantTTCDetatil,SUM(sqlQ.montantUg) AS montantUg,SUM(sqlQ.montantRemiseDetail) AS montantRemiseDetail,  SUM(CASE p.`lg_PREENREGISTREMENT_ID`  WHEN p.`int_PRICE` <0 || p.`b_IS_CANCEL`=1 THEN - 1 ELSE 1 END) AS totalVente,SUM(m.`avoidAmount`) AS avoidAmount,SUM(m.`montantAcc`) AS montantAcc  FROM  mvttransaction m,t_preenregistrement p,(SELECT d.`lg_PREENREGISTREMENT_ID` AS idVente, SUM(d.`int_PRICE`) AS montantTTCDetatil,SUM(d.`int_UG`*d.`int_PRICE_UNITAIR`) AS montantUg,"
            + " SUM(d.`int_PRICE_REMISE`) AS montantRemiseDetail FROM t_preenregistrement_detail d GROUP BY d.`lg_PREENREGISTREMENT_ID`  ) AS sqlQ  WHERE  sqlQ.idVente=p.`lg_PREENREGISTREMENT_ID` AND m.pkey=p.lg_PREENREGISTREMENT_ID AND  DATE(p.`dt_UPDATED`) BETWEEN  ?3 AND ?4 AND p.`str_STATUT`='is_Closed' AND p.`lg_TYPE_VENTE_ID` <> ?1 AND m.`lg_EMPLACEMENT_ID` =?2  AND  p.`lg_PREENREGISTREMENT_ID` NOT IN (SELECT v.preenregistrement_id FROM vente_exclu v)   AND  m.flag_id IS NULL  GROUP BY mvtDate";
    private final String tableauBoardSqlAchats = "SELECT m.mvtdate AS mvtDate ,SUM(m.montant) AS montant,gf.libelle FROM  mvttransaction m,t_grossiste g,groupefournisseur gf WHERE DATE(m.mvtdate) BETWEEN ?1 AND ?2 AND m.`typeTransaction` =2 "
            + " AND m.`lg_EMPLACEMENT_ID` =?3 AND m.`grossisteId`=g.`lg_GROSSISTE_ID` AND g.`groupeId`=gf.id GROUP BY gf.id,mvtdate ORDER BY mvtDate";
    private final Comparator<TableauBaordPhDTO> comparator = Comparator.comparing(TableauBaordPhDTO::getMvtDate);
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public List<BalanceDTO> buildBalanceFromPreenregistrement(BalanceParamsDTO balanceParams) {

        List<BalanceDTO> balances = new ArrayList<>();
        List<BalanceVenteItemDTO> dataVentes;
        if (!balanceParams.isToPrint()) {
            dataVentes = fetchPreenregistrements(balanceParams, "", "", "", "")
                    .stream().map(this::buildFromTuple).collect(Collectors.toList());
        } else {
            dataVentes = fetchPreenregistrements(balanceParams, "", "", "m.`typeMvtCaisseId` AS typeMvtCaisse,", ",typeMvtCaisse")
                    .stream().map(this::buildPdfDataFromTuple).collect(Collectors.toList());
        }

        Map<TypeTransaction, List<BalanceVenteItemDTO>> groupByTypeVente = dataVentes.stream().collect(Collectors.groupingBy(BalanceVenteItemDTO::getTypeTransaction));
        if (groupByTypeVente.containsKey(TypeTransaction.VENTE_COMPTANT)) {
            boolean checkUg = checkUg();
            List<BalanceVenteItemDTO> vnoData = groupByTypeVente.remove(TypeTransaction.VENTE_COMPTANT);
            BalanceDTO balanceVno = buildVenteBalance(vnoData, checkUg);
            balanceVno.setTypeVente(DateConverter.VENTE_COMPTANT);
            balanceVno.setBalanceId(balanceVno.getTypeVente());
            balanceVno.setMontantTTC(balanceVno.getMontantTTC() - this.montantToRemove(balanceParams));
            balances.add(balanceVno);
        }
        if (groupByTypeVente.containsKey(TypeTransaction.VENTE_CREDIT)) {
            List<BalanceVenteItemDTO> vnoData = groupByTypeVente.remove(TypeTransaction.VENTE_CREDIT);
            BalanceDTO balanceVo = buildVenteBalance(vnoData, false);
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
    @Override
    public Map<TableauBaordSummary, List<TableauBaordPhDTO>> getTableauBoardData(BalanceParamsDTO balanceParams) {

        List<TableauBaordPhDTO> bons = buildBonAchats(fetchBonLivraisons(balanceParams));
        List<TableauBaordPhDTO> ventes = buildVente(fetchPreenregistrements(balanceParams));
        bons.addAll(ventes);

        if (!balanceParams.isByMonth()) {
            Map<LocalDate, List<TableauBaordPhDTO>> dailyData = bons.stream().collect(Collectors.groupingBy(TableauBaordPhDTO::getMvtDate));
            return buildTableauBoard(dailyData);
        } else {

            Map<YearMonth, List<TableauBaordPhDTO>> monthyData = bons.stream().collect(Collectors.groupingBy(TableauBaordPhDTO::getYearMonth));
            return buildTableauBoard(monthyData);
        }

    }

    @Override
    public JSONObject tableauBoardDatas(BalanceParamsDTO balanceParams) throws JSONException {

        JSONObject json = new JSONObject();
        Map<TableauBaordSummary, List<TableauBaordPhDTO>> map = getTableauBoardData(balanceParams);

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
    @Override
    public List<TvaDTO> statistiqueTva(BalanceParamsDTO balanceParams) {
        if (balanceParams.isByDay()) {
            return this.statistiqueTvaGroupingByDay(balanceParams);
        }
        return this.statistiqueTvaPeriodique(balanceParams);
    }
    private BalanceDTO buildVenteBalance(List<BalanceVenteItemDTO> venteData, boolean checkUg) {
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
        Map<String, List<BalanceVenteItemDTO>> mapByTypeReglemement = venteData.stream().collect(Collectors.groupingBy(BalanceVenteItemDTO::getTypeReglement));
        for (Map.Entry<String, List<BalanceVenteItemDTO>> entry : mapByTypeReglemement.entrySet()) {
            String typeReglemement = entry.getKey();
            List<BalanceVenteItemDTO> values = entry.getValue();
            for (BalanceVenteItemDTO balanceVenteItem : values) {
                montantTTC += balanceVenteItem.getMontantTTCDetatil().longValue();
                if (checkUg) {
                    montantTTC -= balanceVenteItem.getMontantUg().longValue();
                }
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
        if (balanceParams.isExcludeStatement()) {
            sql = sql.replace("{excludeStatement}", excludeStatement);
        } else {
            sql = sql.replace("{excludeStatement}", "");
        }
        if (balanceParams.isFlagedStatement()) {
            sql = sql.replace("{flagStatement}", flagStatement);
        } else {
            sql = sql.replace("{flagStatement}", "");
        }

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

    private List<TvaDTO> findTvaDatas(BalanceParamsDTO balanceParams) {
        if (balanceParams.isByDay()) {
            return findTvas(balanceParams).stream().map(this::buildPdfByDayTva).collect(Collectors.toList());
        }
        return findTvas(balanceParams).stream().map(this::buildTva).collect(Collectors.toList());
    }

    private List<Tuple> findTvas(BalanceParamsDTO balanceParams) {

        String sql = tvasSql;
        try {
            if (balanceParams.isExcludeStatement()) {
                sql = sql.replace("{excludeStatement}", excludeStatement);
            } else {
                sql = sql.replace("{excludeStatement}", "");
            }
            if (balanceParams.isVnoOnly()) {
                sql = sql.replace("{tvaVnoOnly}", tvaVnoOnly);
            } else {
                sql = sql.replace("{tvaVnoOnly}", "");
            }
            if (balanceParams.isByDay()) {
                sql = sql.replace("{byDay}", byDay).replace("{groupByDay}", groupByDay);
            } else {
                sql = sql.replace("{byDay}", "").replace("{groupByDay}", "");
            }
            LOG.log(Level.INFO, "sql--- TVAS {0}", sql);
            Query query = em.createNativeQuery(sql, Tuple.class)
                    .setParameter(1, DateConverter.DEPOT_EXTENSION)
                    .setParameter(2, java.sql.Date.valueOf(balanceParams.getDtStart()))
                    .setParameter(3, java.sql.Date.valueOf(balanceParams.getDtEnd()))
                    .setParameter(4, balanceParams.getEmplacementId());
            return query.getResultList();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return List.of();
        }

    }

    private TvaDTO buildTva(Tuple tuple) {
        TvaDTO o = new TvaDTO();
        o.setMontantTtc(tuple.get("montantTTC", BigDecimal.class).longValue());
        o.setMontantUg(tuple.get("montantUg", BigDecimal.class).longValue());
        o.setTaux(tuple.get("valeurTva", Integer.class));
        return o;
    }

    private TvaDTO buildPdfByDayTva(Tuple tuple) {
        TvaDTO o = new TvaDTO();
        LocalDate mvDate = LocalDate.parse(tuple.get("mvtDate", String.class));
        o.setMontantTtc(tuple.get("montantTTC", BigDecimal.class).longValue());
        o.setMontantUg(tuple.get("montantUg", BigDecimal.class).longValue());
        o.setTaux(tuple.get("valeurTva", Integer.class));
        o.setLocalOperation(mvDate);
        o.setDateOperation(mvDate.format(DateTimeFormatter.ofPattern(TVA_DATE_PATERN)));
        return o;
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
                .totalVente(tuple.get("totalVente", BigDecimal.class).intValue())
                .montantRemiseDetail(tuple.get("montantRemiseDetail", BigDecimal.class))
                .montantTvaUg(tuple.get("montantTvaUg", BigDecimal.class))
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



    private List<TvaDTO> statistiqueTvaPeriodique(BalanceParamsDTO balanceParams) {
        List<TvaDTO> tvas = new ArrayList<>();
        List<TvaDTO> datas = findTvaDatas(balanceParams);
        boolean checkUg = checkUg();
        for (TvaDTO data : datas) {
            TvaDTO o = new TvaDTO();
            Double valeurTva = 1 + (Double.valueOf(data.getTaux()) / 100);
            long montantTTC = data.getMontantTtc();
            if (data.getTaux() == 0) {
                montantTTC -= montantToRemove(balanceParams);
                if (checkUg) {
                    montantTTC -= data.getMontantUg();
                }
            }
            long htAmont = (long) Math.ceil(montantTTC / valeurTva);
            long montantTva = montantTTC - htAmont;
            o.setMontantTva(montantTva);
            o.setMontantTtc(montantTTC);
            o.setTaux(data.getTaux());
            o.setMontantHt(htAmont);
            tvas.add(o);
        }

        return tvas;
    }

    @Override
    public JSONObject statistiqueTvaView(BalanceParamsDTO balanceParams) {
        List<TvaDTO> datas = statistiqueTva(balanceParams);
        return FunctionUtils.returnData(datas);

    }

    @Override
    public List<TvaDTO> statistiqueTvaGroupingByDay(BalanceParamsDTO balanceParams) {
        List<TvaDTO> tvas = new ArrayList<>();
        balanceParams.setByDay(true);
        List<TvaDTO> datas = findTvaDatas(balanceParams);
        boolean checkUg = checkUg();
        for (TvaDTO data : datas) {
            TvaDTO o = new TvaDTO();
            Double valeurTva = 1 + (Double.valueOf(data.getTaux()) / 100);
            long montantTTC = data.getMontantTtc();
            if (data.getTaux() == 0) {
                String mvtDate = data.getLocalOperation().toString();
                balanceParams.setDtStart(mvtDate);
                balanceParams.setDtEnd(mvtDate);

                montantTTC -= montantToRemove(balanceParams);
                if (checkUg) {
                    montantTTC -= data.getMontantUg();
                }
            }
            long htAmont = (long) Math.ceil(montantTTC / valeurTva);
            long montantTva = montantTTC - htAmont;
            o.setMontantTva(montantTva);
            o.setMontantTtc(montantTTC);
            o.setTaux(data.getTaux());
            o.setMontantHt(htAmont);
            o.setLocalOperation(data.getLocalOperation());
            o.setDateOperation(data.getDateOperation());
            tvas.add(o);
        }
        tvas.sort(Comparator.comparing(TvaDTO::getLocalOperation));
        return tvas;
    }

    @Override
    public long montantToRemove(BalanceParamsDTO balanceParams) {
        if (isNormalUse()) {
            return 0;
        }
        try {
            Query query = em.createNativeQuery(amountToRemoveSql, Tuple.class)
                    .setParameter(4, balanceParams.getEmplacementId())
                    .setParameter(3, DateConverter.DEPOT_EXTENSION)
                    .setParameter(1, java.sql.Date.valueOf(balanceParams.getDtStart()))
                    .setParameter(2, java.sql.Date.valueOf(balanceParams.getDtEnd()));
            Tuple tuple = (Tuple) query.getSingleResult();
            return tuple.get("montant", BigDecimal.class).longValue() - tuple.get("montantAcc", BigDecimal.class).longValue();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    private boolean findParam(String key) {
        try {
            TParameters tp = em.find(TParameters.class, key);
            if (tp != null) {
                return Integer.parseInt(tp.getStrVALUE().trim()) == 1;
            }
            return false;
        } catch (NumberFormatException e) {

            return false;
        }
    }

    @Override
    public boolean useLastUpdateStats() {
        return findParam(TVA_BALANCE_LAST_UPDATE);
    }

    private boolean checkUg() {

        return this.findParam(DateConverter.KEY_CHECK_UG);

    }

    private boolean isNormalUse() {
        return !this.findParam(DateConverter.KEY_TAKE_INTO_ACCOUNT);

    }

    private long avoirFournisseur(LocalDate dtStart, LocalDate dtEnd) {
        try {
            Query q = em.createNativeQuery("SELECT COALESCE(SUM(o.dl_AMOUNT),0) FROM  t_retour_fournisseur o where DATE(o.dt_UPDATED)  BETWEEN ?1 AND ?2 AND o.str_REPONSE_FRS <>'' AND o.str_STATUT='enable'")
                    .setParameter(1, java.sql.Date.valueOf(dtStart))
                    .setParameter(2, java.sql.Date.valueOf(dtEnd));
            return ((Number) q.getSingleResult()).longValue();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }

    }

    private List<Tuple> fetchPreenregistrements(BalanceParamsDTO balanceParams) {
        LOG.log(Level.INFO, "sql--- tableau vente {0}", tableauBoardSql);
        try {
            Query query = em.createNativeQuery(tableauBoardSql, Tuple.class)
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

    private List<Tuple> fetchBonLivraisons(BalanceParamsDTO balanceParams) {
        LOG.log(Level.INFO, "sql--- tableau vente {0}", tableauBoardSqlAchats);
        try {
            Query query = em.createNativeQuery(tableauBoardSqlAchats, Tuple.class)
                    .setParameter(3, balanceParams.getEmplacementId())
                    .setParameter(1, java.sql.Date.valueOf(balanceParams.getDtStart()))
                    .setParameter(2, java.sql.Date.valueOf(balanceParams.getDtEnd()));
            return query.getResultList();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new ArrayList<>();
        }
    }

    private List<TableauBaordPhDTO> buildBonAchats(List<Tuple> tuple) {
        List<TableauBaordPhDTO> list = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(tuple)) {
            for (Tuple t : tuple) {
                TableauBaordPhDTO o = new TableauBaordPhDTO();
                java.sql.Date mvtDate = t.get("mvtDate", java.sql.Date.class);
                o.setVente(false);
                o.setMvtDate(mvtDate.toLocalDate());
                int montant = t.get("montant", BigDecimal.class).intValue();
                o.setMontantAchat(montant);
                switch (t.get("libelle", String.class)) {

                    case DateConverter.LABOREXCI:
                        o.setMontantAchatOne(montant);

                        break;
                    case DateConverter.DPCI:
                        o.setMontantAchatTwo(montant);

                        break;
                    case DateConverter.COPHARMED:
                        o.setMontantAchatThree(montant);

                        break;
                    case DateConverter.TEDIS:
                        o.setMontantAchatFour(montant);

                        break;
                    case DateConverter.AUTRES:
                        o.setMontantAchatFive(montant);
                        break;
                    default:
                        break;
                }
                list.add(o);
            }

            return list;
        }
        return list;
    }



    private TableauBaordSummary buildBaordSummary(List<TableauBaordPhDTO> datas) {
        TableauBaordSummary summary = new TableauBaordSummary();
        for (TableauBaordPhDTO o : datas) {
            summary.setMontantTTC(o.getMontantTTC() + summary.getMontantTTC());
            summary.setMontantNet(o.getMontantNet() + summary.getMontantNet());
            summary.setMontantRemise(o.getMontantRemise() + summary.getMontantRemise());
            summary.setMontantEsp(o.getMontantEsp() + summary.getMontantEsp());
            summary.setMontantCredit(o.getMontantCredit() + summary.getMontantCredit());
            summary.setNbreVente(o.getNbreVente() + summary.getNbreVente());
            summary.setMontantAchatOne(summary.getMontantAchatOne() + o.getMontantAchatOne());
            summary.setMontantAchatTwo(summary.getMontantAchatTwo() + o.getMontantAchatTwo());
            summary.setMontantAchatThree(summary.getMontantAchatThree() + o.getMontantAchatThree());
            summary.setMontantAchatFour(summary.getMontantAchatFour() + o.getMontantAchatFour());
            summary.setMontantAchatFive(summary.getMontantAchatFive() + o.getMontantAchatFive());
            summary.setMontantAchat(summary.getMontantAchat() + o.getMontantAchat());
            summary.setMontantAvoir(summary.getMontantAvoir() + o.getMontantAvoir());
            Long _montantNet = summary.getMontantNet();
            Long _montantAchat = summary.getMontantAchat();
            if (_montantAchat.compareTo(0l) > 0) {
                summary.setRatioVA(new BigDecimal(Double.valueOf(_montantNet) / _montantAchat).setScale(2, RoundingMode.FLOOR).doubleValue());
            }
            if (_montantNet.compareTo(0l) > 0) {
                summary.setRationAV(new BigDecimal(Double.valueOf(_montantAchat) / _montantNet).setScale(2, RoundingMode.FLOOR).doubleValue());
            }
        }
        return summary;
    }

    private Map<TableauBaordSummary, List<TableauBaordPhDTO>> buildTableauBoard(Map<? extends TemporalAccessor, List<TableauBaordPhDTO>> data) {
        Map<TableauBaordSummary, List<TableauBaordPhDTO>> map = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MM/yyyy");
        List<TableauBaordPhDTO> tableauBaords = new ArrayList<>();

        for (Map.Entry<? extends TemporalAccessor, List<TableauBaordPhDTO>> entry : data.entrySet()) {
            TableauBaordPhDTO baordPh = new TableauBaordPhDTO();
            int avoir;
            if (entry.getKey() instanceof LocalDate) {
                LocalDate perode = (LocalDate) entry.getKey();
                baordPh.setMvtDate(perode);
                avoir = (int) avoirFournisseur(perode, perode);
            } else {
                YearMonth perode = (YearMonth) entry.getKey();
                baordPh.setMvtDate(perode.atEndOfMonth());
                baordPh.setMvtDateInt(Integer.valueOf(perode.format(formatter)));
                baordPh.setDateOperation(baordPh.getMvtDate().format(formatter2));
                avoir = (int) avoirFournisseur(baordPh.getMvtDate().withDayOfMonth(1), baordPh.getMvtDate());
            }
            List<TableauBaordPhDTO> value = entry.getValue();
            for (TableauBaordPhDTO o : value) {
                baordPh.setMontantTTC(o.getMontantTTC() + baordPh.getMontantTTC());
                baordPh.setMontantNet(o.getMontantNet() + baordPh.getMontantNet());
                baordPh.setMontantRemise(o.getMontantRemise() + baordPh.getMontantRemise());
                baordPh.setMontantEsp(o.getMontantEsp() + baordPh.getMontantEsp());
                baordPh.setMontantCredit(o.getMontantCredit() + baordPh.getMontantCredit());
                baordPh.setNbreVente(o.getNbreVente() + baordPh.getNbreVente());
                baordPh.setMontantAchatOne(baordPh.getMontantAchatOne() + o.getMontantAchatOne());
                baordPh.setMontantAchatTwo(baordPh.getMontantAchatTwo() + o.getMontantAchatTwo());
                baordPh.setMontantAchatThree(baordPh.getMontantAchatThree() + o.getMontantAchatThree());
                baordPh.setMontantAchatFour(baordPh.getMontantAchatFour() + o.getMontantAchatFour());
                baordPh.setMontantAchatFive(baordPh.getMontantAchatFive() + o.getMontantAchatFive());
                baordPh.setMontantAchat(baordPh.getMontantAchat() + o.getMontantAchat());
            }
            Integer _montantNet = baordPh.getMontantNet();
            Integer _montantAchat = baordPh.getMontantAchat() - avoir;
            if (_montantAchat.compareTo(0) > 0) {
                baordPh.setRatioVA(new BigDecimal(Double.valueOf(_montantNet) / _montantAchat).setScale(2, RoundingMode.FLOOR).doubleValue());
            }
            if (_montantNet.compareTo(0) > 0) {
                baordPh.setRationAV(new BigDecimal(Double.valueOf(_montantAchat) / _montantNet).setScale(2, RoundingMode.FLOOR).doubleValue());
            }
            baordPh.setMontantAvoir(avoir);
            tableauBaords.add(baordPh);
        }
        tableauBaords.sort(comparator);
        map.put(buildBaordSummary(tableauBaords), tableauBaords);
        return map;
    }
        private List<TableauBaordPhDTO> buildVente(List<Tuple> tuple) {
        List<TableauBaordPhDTO> list = new ArrayList<>();
        boolean checkUg = checkUg();
        if (CollectionUtils.isNotEmpty(tuple)) {
            for (Tuple t : tuple) {
                TableauBaordPhDTO o = new TableauBaordPhDTO();
                o.setVente(true);
                o.setMvtDate(LocalDate.parse(t.get("mvtDate", String.class)));
                int montantTTC = t.get("montantTTCDetatil", BigDecimal.class).intValue();
                int montantNet = t.get("montantNet", BigDecimal.class).intValue();
                int montantRemise = t.get("montantRemise", BigDecimal.class).intValue();
                int montantCredit = t.get("montantCredit", BigDecimal.class).intValue();
                int montantDiffere = t.get("montantDiffere", BigDecimal.class).intValue();
                int montantEsp = t.get("montantRegle", BigDecimal.class).intValue();
                int totalVente = t.get("totalVente", BigDecimal.class).intValue();
                int montantUg = checkUg ? t.get("montantUg", BigDecimal.class).intValue() : 0;
                o.setMontantTTC(montantTTC - montantUg);
                o.setMontantNet(montantNet - montantUg);
                o.setMontantRemise(montantRemise);
                o.setMontantEsp(montantEsp - montantUg);
                o.setMontantCredit(montantCredit + montantDiffere);
                o.setNbreVente(totalVente);

                list.add(o);
            }

            return list;
        }
        return list;
    }

}
