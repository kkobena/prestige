package rest.service.impl;

import commonTasks.dto.BalanceDTO;
import dal.TPreenregistrementDetail;
import dal.enumeration.TypeTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import rest.service.BalanceService;
import rest.service.dto.BalanceParamsDTO;
import rest.service.dto.BalanceVenteItemDTO;
import util.DateConverter;

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

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public List<TPreenregistrementDetail> listPreenregistrements(BalanceParamsDTO balanceParams) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<BalanceDTO> buildBalanceTypeMvts(BalanceParamsDTO balanceParams) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

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

        // Map<String, List<BalanceVenteItemDTO>> groupRemiseByTypeVente = remiseVentesNonPara.stream().collect(Collectors.groupingBy(BalanceVenteItemDTO::getTypeVente));
        //  Map<String, List<BalanceVenteItemDTO>> groupRemiseSupprimerByTypeVente = remiseVentesNonParaSupp.stream().collect(Collectors.groupingBy(BalanceVenteItemDTO::getTypeVente));
        if (groupByTypeVente.containsKey(TypeTransaction.VENTE_COMPTANT)) {

            List<BalanceVenteItemDTO> vnoData = groupByTypeVente.remove(TypeTransaction.VENTE_COMPTANT);
            BalanceDTO balanceVno = buildVenteBalance(vnoData);
            balanceVno.setTypeVente(DateConverter.VENTE_COMPTANT);
            balances.add(balanceVno);
        }
        if (groupByTypeVente.containsKey(TypeTransaction.VENTE_CREDIT)) {
            List<BalanceVenteItemDTO> vnoData = groupByTypeVente.remove(TypeTransaction.VENTE_CREDIT);
            BalanceDTO balanceVno = buildVenteBalance(vnoData);
            balanceVno.setTypeVente(DateConverter.VENTE_ASSURANCE);
            balances.add(balanceVno);
        }
        return balances;
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
        // long montantRemise = remiseVentesPara.stream().flatMap(e -> Stream.of(e.getMontantRemise().longValue())).reduce(0l, Long::sum);
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
            //  return list.stream().map(this::buildFromTuple).collect(Collectors.toList());

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
                    .setParameter(3, balanceParams.getEmplacementId())
                    .setParameter(1, java.sql.Date.valueOf(balanceParams.getDtStart()))
                    .setParameter(2, java.sql.Date.valueOf(balanceParams.getDtEnd()));
            List<Tuple> list = query.getResultList();
            return list.stream().map(this::buildFromTupleOtherMvt).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new ArrayList<>();
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
                .totalVente(tuple.get("totalVente", Long.class))
                .build();
    }

    private BalanceVenteItemDTO buildPdfDataFromTuple(Tuple tuple) {
        return buildFromTuple(tuple).builder()
                .typeMvtCaisse(tuple.get("typeMvtCaisse", String.class))
                .build();
    }

    private BalanceVenteItemDTO buildFromTupleOtherMvt(Tuple tuple) {
        return BalanceVenteItemDTO.builder()
                .typeMvtCaisse(tuple.get("typeMvtCaisse", String.class))
                .montantTTC(tuple.get("montantTTC", BigDecimal.class))
                .build();
    }

}
