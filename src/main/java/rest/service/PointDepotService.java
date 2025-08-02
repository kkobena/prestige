
package rest.service;

import java.util.List;
import javax.ejb.Local;
import org.json.JSONObject;
import rest.service.dto.PointDepotDTO;

/**
 *
 * @author airman
 */
public interface PointDepotService {
    /**
     * Récupère le point de caisse par caissière pour un ou plusieurs dépôts sur une période donnée.
     *
     * @param dtStart
     *            Date de début (format YYYY-MM-DD)
     * @param dtEnd
     *            Date de fin (format YYYY-MM-DD)
     * @param emplacementId
     *            ID du dépôt, ou "ALL" pour tous les dépôts.
     *
     * @return Un JSONObject contenant la liste des points de caisse.
     */
    JSONObject getPointDepot(String dtStart, String dtEnd, String emplacementId);
}
