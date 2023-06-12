package rest.service;

import java.util.List;
import javax.ejb.Local;
import org.json.JSONObject;
import rest.service.dto.EtatControlAnnuelWrapperDTO;
import rest.service.dto.EtatControlBon;

/**
 *
 * @author koben
 */
@Local
public interface EtatControlBonService {

    List<EtatControlBon> list(boolean fullAuth, String search, String dtStart, String dtEnd, String grossisteId, int start, int limit, boolean all);

    JSONObject list(boolean fullAuth, String search, String dtStart, String dtEnd, String grossisteId, int start, int limit);

    EtatControlAnnuelWrapperDTO listBonAnnuel(String groupBy, String dtStart, String dtEnd, String grossisteId, Integer groupeId);

    JSONObject listBonAnnuelView(String groupBy, String dtStart, String dtEnd, String grossisteId, Integer groupeId);

}
