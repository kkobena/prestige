package rest.service.impl;

import commonTasks.dto.BalanceDTO;
import dal.TPreenregistrementDetail;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private final String balanceSqlQuery = "SELECT %s p.`lg_TYPE_VENTE_ID` AS typeVente,tr.`lg_TYPE_REGLEMENT_ID` AS typeReglment, SUM(d.`int_PRICE`) AS montantVenteDetail,SUM(d.`int_QUANTITY`*d.`prixAchat`) AS montantAchat, SUM(d.`montantTva`) AS montantTva,SUM(d.`int_UG`*d.`int_PRICE_UNITAIR`) AS montantUG"
            + "  FROM  t_preenregistrement_detail d,t_preenregistrement p,t_user u,t_type_reglement tr,t_reglement r,t_mode_reglement mr WHERE d.`lg_PREENREGISTREMENT_ID`=p.`lg_PREENREGISTREMENT_ID`"
            + " AND p.`lg_USER_ID`=u.`lg_USER_ID` AND u.`lg_EMPLACEMENT_ID`=?1  AND %s =r.`str_REF_RESSOURCE` AND r.`lg_MODE_REGLEMENT_ID`=mr.`lg_MODE_REGLEMENT_ID`"
            + " AND mr.`lg_TYPE_REGLEMENT_ID`=tr.`lg_TYPE_REGLEMENT_ID` AND p.`str_STATUT`='is_Closed' %s  AND p.`lg_TYPE_VENTE_ID` <> ?2 AND DATE(p.`dt_UPDATED`) BETWEEN ?3 AND ?4 GROUP BY typeVente,typeReglment %s";
    private final String remiseSqlQuery = "SELECT %s p.`lg_TYPE_VENTE_ID` AS typeVente,SUM(d.`int_PRICE_REMISE`) AS montantRemise FROM  t_preenregistrement_detail d,t_preenregistrement p,t_user u,t_famille f WHERE   d.`lg_PREENREGISTREMENT_ID`=p.`lg_PREENREGISTREMENT_ID`  AND d.`lg_FAMILLE_ID`=f.`lg_FAMILLE_ID` AND f.`bool_ACCOUNT`=?1 "
            + "  AND p.`lg_USER_ID`=u.`lg_USER_ID` AND u.`lg_EMPLACEMENT_ID`=?2  AND p.`str_STATUT`='is_Closed' AND %s  AND p.`lg_TYPE_VENTE_ID` <> ?3 AND DATE(p.`dt_UPDATED`) BETWEEN ?4 AND ?5 AND d.`int_PRICE_REMISE` <>0 GROUP BY typeVente %s";
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public List<TPreenregistrementDetail> listPreenregistrements(BalanceParamsDTO balanceParams) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<BalanceDTO> buildBalanceFromPreenregistrement(BalanceParamsDTO balanceParams) {
// List<BalanceVenteItemDTO> dataVentes = fetchPreenregistrements(balanceParams, " DATE_FORMAT(p.`dt_UPDATED`,'%Y-%m-%d' ) AS mvtDate , ", " AND p.`int_PRICE`>?0 ", ",mvtDate");
        List<BalanceVenteItemDTO> dataVentes = fetchPreenregistrements(balanceParams, "", " p.`lg_PREENREGISTREMENT_ID` ", " AND p.`int_PRICE`>?0 ", "");
        List<BalanceVenteItemDTO> dataVentesSupprimes = fetchPreenregistrements(balanceParams, "", " p.`lg_PREENGISTREMENT_ANNULE_ID` ", " AND p.`int_PRICE` <?0 ", "");
        Map<String, List<BalanceVenteItemDTO>> groupdataVentesSupprimesByTypeVente = dataVentesSupprimes.stream().collect(Collectors.groupingBy(BalanceVenteItemDTO::getTypeVente));
        List<BalanceVenteItemDTO> remiseVentesNonPara = fetchRemises(balanceParams, "", " AND p.`int_PRICE` >?0 ", "", true);
     //   List<BalanceVenteItemDTO> remiseVentesPara = fetchRemises(balanceParams, "", " AND p.`int_PRICE` >?0 ", "", false);

        List<BalanceVenteItemDTO> remiseVentesNonParaSupp = fetchRemises(balanceParams, "", " AND p.`int_PRICE` <?0 ", "", true);
       // List<BalanceVenteItemDTO> remiseVentesParaSupp = fetchRemises(balanceParams, "", " AND p.`int_PRICE` <?0 ", "", false);

        Map<String, List<BalanceVenteItemDTO>> groupByTypeVente = dataVentes.stream().collect(Collectors.groupingBy(BalanceVenteItemDTO::getTypeVente));
        if (groupByTypeVente.containsKey(DateConverter.TYPE_VENTE_VNO) || groupdataVentesSupprimesByTypeVente.containsKey(DateConverter.TYPE_VENTE_VNO)) {

            BalanceDTO balanceVNO = new BalanceDTO();
            balanceVNO.setTypeVente("VNO");
            long count=count(balanceParams, DateConverter.TYPE_VENTE_VNO);
            balanceVNO.setNbreVente(count);
            List<BalanceVenteItemDTO> vnoData = groupByTypeVente.remove(DateConverter.TYPE_VENTE_VNO);
            List<BalanceVenteItemDTO> vnoDataSupprimees = groupdataVentesSupprimesByTypeVente.remove(DateConverter.TYPE_VENTE_VNO);
        }
        return null;
    }

    private void build(BalanceDTO balanceVNO, List<BalanceVenteItemDTO> vnoData, List<BalanceVenteItemDTO> vnoDataSupprimees,  List<BalanceVenteItemDTO> remiseVentesPara, List<BalanceVenteItemDTO> remiseVentesNonParaSupp) {
        long montantTTC = 0;
        long montantNet = 0;
        long montantRemise = 0;
        long panierMoyen = 0;
        long montantEsp = 0;
        long montantCheque = 0;
        long montantVirement = 0;
        long montantCB = 0;
        long montantDiff = 0;
        long nbreVente = 0;
        long montantMobilePayment = 0;
        long   montantFlag;

    }

    private List<BalanceVenteItemDTO> fetchPreenregistrements(BalanceParamsDTO balanceParams, String subQueryMvtDate, String id, String subQuery, String subQueryGroupBy) { //AND p.`int_PRICE`>?0

        String sql = String.format(balanceSqlQuery, subQueryMvtDate, id, subQuery, subQueryGroupBy);
        LOG.log(Level.INFO, "sql--- balance vente {0}", sql);
        try {
            Query query = em.createNativeQuery(sql, Tuple.class)
                    .setParameter(1, balanceParams.getEmplacementId())
                    .setParameter(2, DateConverter.DEPOT_EXTENSION)
                    .setParameter(3, java.sql.Date.valueOf(balanceParams.getDtStart()))
                    .setParameter(4, java.sql.Date.valueOf(balanceParams.getDtEnd()));
            List<Tuple> list = query.getResultList();
            return list.stream().map(this::buildFromTuple).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new ArrayList<>();
        }
    }

    private long count(BalanceParamsDTO balanceParams,String typeVente){
        return 0;
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

    private BalanceVenteItemDTO buildRemiseFromTuple(Tuple tuple) {
        return BalanceVenteItemDTO.builder()
                .typeVente(tuple.get("typeVente", String.class))
                .montantRemise(tuple.get("montantRemise", BigDecimal.class))
                .build();
    }

    private BalanceVenteItemDTO buildFromTuple(Tuple tuple) {
        return BalanceVenteItemDTO.builder()
                .typeVente(tuple.get("typeVente", String.class))
                .typeReglment(tuple.get("typeReglment", String.class))
                .montantVenteDetail(tuple.get("montantVenteDetail", BigDecimal.class))
                .montantAchat(tuple.get("montantAchat", BigDecimal.class))
                .montantTva(tuple.get("montantTva", BigDecimal.class))
                .montantUG(tuple.get("montantUG", BigDecimal.class))
                .build();
    }

    private BalanceVenteItemDTO buildFromTuple2(Tuple tuple) {
        return BalanceVenteItemDTO.builder()
                .mvtDate(LocalDate.parse(tuple.get("mvtDate", String.class)))
                .typeVente(tuple.get("typeVente", String.class))
                .typeReglment(tuple.get("typeReglment", String.class))
                .montantVenteDetail(tuple.get("montantVenteDetail", BigDecimal.class))
                .montantAchat(tuple.get("montantAchat", BigDecimal.class))
                .montantTva(tuple.get("montantTva", BigDecimal.class))
                .montantUG(tuple.get("montantUG", BigDecimal.class))
                .build();
    }
}
