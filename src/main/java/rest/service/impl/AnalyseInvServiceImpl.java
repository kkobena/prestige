
package rest.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import rest.service.AnalyseInvService;
import rest.service.dto.AnalyseInvDTO;

/**
 *
 * @author airman
 */
@Stateless
public class AnalyseInvServiceImpl implements AnalyseInvService {

    private static final Logger LOG = Logger.getLogger(AnalyseInvServiceImpl.class.getName());

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public List<AnalyseInvDTO> analyseInventaire(String inventaireId) {
        if (inventaireId == null || inventaireId.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            String queryStr = "SELECT f.int_CIP AS codeCip, f.str_NAME AS nom, f.int_PAF AS prixAchat, f.int_PRICE AS prixVente, z.str_LIBELLEE AS emplacement, i.lg_INVENTAIRE_ID AS inventaireId, iv.str_NAME AS invName, i.int_NUMBER AS qteSaisie, i.int_NUMBER_INIT AS qteInitiale "
                    + "FROM t_inventaire_famille i, t_inventaire iv, t_famille f, t_zone_geographique z "
                    + "WHERE i.lg_FAMILLE_ID=f.lg_FAMILLE_ID AND f.lg_ZONE_GEO_ID = z.lg_ZONE_GEO_ID AND i.lg_INVENTAIRE_ID=iv.lg_INVENTAIRE_ID "
                    + "AND i.lg_INVENTAIRE_ID = :inventaireId " + "ORDER BY z.str_LIBELLEE, f.str_NAME ASC";

            List<Tuple> results = em.createNativeQuery(queryStr, Tuple.class).setParameter("inventaireId", inventaireId)
                    .getResultList();

            return results.stream().map(this::tupleToDto).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error during inventory analysis for ID: " + inventaireId, e);
            return Collections.emptyList();
        }
    }

    private AnalyseInvDTO tupleToDto(Tuple tuple) {
        return AnalyseInvDTO.builder().codeCip(tuple.get("codeCip", String.class)).nom(tuple.get("nom", String.class))
                .prixAchat(getTupleValue(tuple, "prixAchat", Number.class, 0.0).doubleValue())
                .prixVente(getTupleValue(tuple, "prixVente", Number.class, 0.0).doubleValue())
                .emplacement(tuple.get("emplacement", String.class))
                .inventaireId(tuple.get("inventaireId", String.class)).invName(tuple.get("invName", String.class))
                .qteSaisie(getTupleValue(tuple, "qteSaisie", Number.class, 0).intValue())
                .qteInitiale(getTupleValue(tuple, "qteInitiale", Number.class, 0).intValue()).build();
    }

    private <T> T getTupleValue(Tuple tuple, String alias, Class<T> type, T defaultValue) {
        Object value = tuple.get(alias);
        return value != null ? type.cast(value) : defaultValue;
    }
}
