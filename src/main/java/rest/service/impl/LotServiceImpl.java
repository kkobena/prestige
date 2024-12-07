/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.Tuple;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import rest.service.LogService;
import rest.service.LotService;
import rest.service.dto.BonsDTO;
import rest.service.dto.BonsParam;
import rest.service.dto.LotDTO;
import util.DateCommonUtils;
import util.DateConverter;
import util.FunctionUtils;

/**
 *
 * @author Hermann N'ZI
 */
@Stateless
public class LotServiceImpl implements LotService {

    private static final Logger LOG = Logger.getLogger(LotServiceImpl.class.getName());
    private static final String MVT_QUERY = "SELECT f.int_CIP, f.str_NAME, f.int_PAF, f.int_PRICE,g.str_LIBELLE, l.int_NUM_LOT, l.str_REF_LIVRAISON,l.int_NUMBER, l.int_NUMBER_GRATUIT,\n"
            + "l.dt_SORTIE_USINE, l.dt_PEREMPTION\n" + "FROM t_lot l, t_famille f, t_grossiste g\n"
            + "WHERE l.lg_FAMILLE_ID = f.lg_FAMILLE_ID AND f.lg_GROSSISTE_ID = g.lg_GROSSISTE_ID";

    private static final String MVT_QUERY_COUNT = "SELECT COUNT(l.lg_LOT_ID) FROM t_lot l";

    @EJB
    private LogService logService;
    @EJB
    private LotService lotService;
    @EJB
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public JSONObject getAllLots(String dtStart, String dtEnd, int limit, int start) {
        return FunctionUtils.returnData(this.getAllLots(dtStart, dtEnd, limit, start));
    }

    public JSONObject getAllLots() {
        return FunctionUtils.returnData(this.getAllLots());
    }
    
    public List<LotDTO> listLots() {
        return listDesLots().stream().map(this::build).collect(Collectors.toList());
    }
    

    public List<rest.service.dto.LotDTO> getAllLots(String dtStart, String dtEnd, int limit, int start, boolean all) {
        List<rest.service.dto.LotDTO> list = new ArrayList<>();
        fetchAllLots(dtStart, dtEnd, limit, start, all).forEach(e -> list.add(build(e)));
        return list;

    }

    private List<Tuple> fetchAllLots(String dtStart, String dtEnd, int limit, int start, boolean all) {
        String sql = (MVT_QUERY);
        LOG.log(Level.INFO, "sql---  getAllLots {0}", sql);
        try {
            Query query = em.createNativeQuery(sql, Tuple.class).setParameter(1, java.sql.Date.valueOf(dtStart))
                    .setParameter(2, java.sql.Date.valueOf(dtEnd));
            if (!all) {
                query.setFirstResult(start);
                query.setMaxResults(limit);
            }
            return query.getResultList();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new ArrayList<>();
        }
    }

    private List<Tuple> listDesLots() {
        
        String sql = MVT_QUERY;
        LOG.log(Level.INFO, "sql--- listAllLots {0}", sql);
        try {
            Query query = em.createNativeQuery(sql, Tuple.class);
            
            return query.getResultList();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new ArrayList<>();
        }
    }
    
    private rest.service.dto.LotDTO build(Tuple t) {

     //   String LotId = t.get("lg_LOT_ID", String.class);

        return rest.service.dto.LotDTO.builder().lgFamilleId(t.get("lg_FAMILLE_ID", String.class))
                .intCip(t.get("int_CIP", Integer.class)).strName(t.get("str_NAME", String.class))
                .intPrice(t.get("int_PRICE", Integer.class)).intPaf(t.get("int_PAF", Integer.class))
                .intNumLot(t.get("int_NUM_LOT", String.class)).intNumber(t.get("int_NUMBER", Integer.class))
                .dtCreated(t.get("dt_CREATED", String.class)).dtUpdated(t.get("dt_UPDATED", String.class))
                .intPaf(t.get("int_PAF", Integer.class)).lgGrossisteId(t.get("lg_GROSSISTE_ID", String.class))
                .strRefLivraison(t.get("str_REF_LIVRAISON", String.class))
                .dtSortieUsine(t.get("dt_SORTIE_USINE", String.class))
                .intNumberGratuit(t.get("int_NUMBER_GRATUIT", Integer.class))
                .dtPeremption(t.get("dt_PEREMPTION", String.class)).intQtyVendue(t.get("int_QTY_VENDUE", Integer.class))
                .lgUserId(t.get("lg_USER_ID", String.class)).build();
        /*
         * .heureOpreration(t.get("heureOpreration", String.class)) .dateOpreration(t.get("dateOpreration",
         * java.sql.Date.class).toLocalDate() .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).build();
         */
    }

    @Override
    public JSONObject getLots() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
