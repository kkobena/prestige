package rest.service.impl;

import java.util.List;
import rest.service.InventaireFamilleService;
import rest.service.dto.InventaireFamilleDTO;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.JSONObject;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import org.json.JSONObject;
import util.FunctionUtils;

@Stateless
public class InventaireFamilleServiceImpl implements InventaireFamilleService {

    private static final Logger LOG = Logger.getLogger(InventaireFamilleServiceImpl.class.getName());

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    private static final String QUERY = "SELECT ifa.lg_INVENTAIRE_FAMILLE_ID, ifa.lg_INVENTAIRE_ID, ifa.lg_FAMILLE_ID, f.int_CIP, f.str_NAME, f.str_DESCRIPTION AS designation, z.str_LIBELLEE AS emplacement, \n"
            + "fa.str_LIBELLE AS famille, g.str_LIBELLE AS grossiste, f.int_PRICE, f.int_PRICE AS prixReference, f.int_PAF, \n"
            + "ifa.int_NUMBER, ifa.int_NUMBER_INIT, (ifa.int_NUMBER - ifa.int_NUMBER_INIT) AS Ecart\n"
            + "FROM t_famille f, t_inventaire_famille ifa, t_famillearticle fa, t_zone_geographique z, t_grossiste g \n"
            + "WHERE f.lg_FAMILLE_ID=ifa.lg_FAMILLE_ID AND f.lg_FAMILLEARTICLE_ID=fa.lg_FAMILLEARTICLE_ID AND f.lg_GROSSISTE_ID=g.lg_GROSSISTE_ID\n"
            + "AND f.lg_ZONE_GEO_ID=z.lg_ZONE_GEO_ID AND ifa.lg_INVENTAIRE_ID =?1";

    private static final String MVT_QUERY_COUNT = "SELECT COUNT(*) FROM t_inventaire_famille i WHERE i.lg_INVENTAIRE_ID=?1";

    // appEler dans la ressource
    @Override
    public JSONObject getAllInventaireFamilles(String invId, int start, int limit) {
        // int total = getCount(invId);
        return FunctionUtils.returnData(this.getAllInventaireFamilles(invId, limit, start, true)/* , total */);
    }

    @Override
    public List<InventaireFamilleDTO> getAllInventaireFamilles(String invId, int start, int limit, boolean all) {
        return fetchAllInventaireFamilles(invId, start, limit, all).stream().map(this::buildDTO)
                .collect(Collectors.toList());

    }

    @Override
    public List<InventaireFamilleDTO> getAllInventaireFamilles(String invId, boolean all) {
        return fetchAllInventaireFamilles(invId, all).stream().map(this::buildDTO).collect(Collectors.toList());
    }

    private List<Tuple> fetchAllInventaireFamilles(String invId, int start, int limit, boolean all) {
        return em.createNativeQuery(QUERY, Tuple.class).setParameter(1, invId) // Paramètre pour lg_INVENTAIRE_ID
                .getResultList();
    }

    private List<Tuple> fetchAllInventaireFamilles(String invId, boolean all) {
        return em.createNativeQuery(QUERY, Tuple.class).setParameter(1, invId) // Paramètre pour lg_INVENTAIRE_ID
                .getResultList();
    }

    // Méthode utilitaire pour conversion ID
    private String convertIdToString(Object value) {
        if (value == null) {
            return null;
        }
        // Gère à la fois BigInteger, Long et String
        return value.toString();
    }

    private Integer getIntValue(Object value) {
        if (value == null)
            return null;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            LOG.log(Level.WARNING, "Erreur de conversion en Integer : {0}", value);
            return null;
        }
    }

    private InventaireFamilleDTO buildDTO(Tuple t) {
        return rest.service.dto.InventaireFamilleDTO.builder()
                .lgInventaireFamilleId(t.get("lg_INVENTAIRE_FAMILLE_ID", String.class))
                .lgInventaireId(t.get("lg_INVENTAIRE_ID", String.class))
                .lgFamilleId(t.get("lg_FAMILLE_ID", String.class)).cip(t.get("int_CIP", String.class))
                .name(t.get("str_NAME", String.class)).designation(t.get("designation", String.class))
                .lgZoneGeoId(t.get("emplacement", String.class)).lgFamilleArticleId(t.get("famille", String.class))
                .lgGrossisteId(t.get("grossiste", String.class)).prixVente(t.get("int_PRICE", Integer.class))
                .prixReference(t.get("prixReference", Integer.class)).paf(t.get("int_PAF", Integer.class))
                .stockRayon(t.get("int_NUMBER", Integer.class)).stockMachine(t.get("int_NUMBER_INIT", Integer.class))
                .ecart(getIntValue(t.get("ecart"))).build();
    }

    private int getCount(String invId) {

        LOG.log(Level.INFO, "sql--- getCount {0}", MVT_QUERY_COUNT);
        try {
            Query query = em.createNativeQuery(MVT_QUERY_COUNT).setParameter(1, invId);

            return ((Number) query.getSingleResult()).intValue();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

}