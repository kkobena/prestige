package rest.service;

import dal.Caution;
import java.util.List;
import javax.ejb.Local;
import org.json.JSONObject;
import rest.service.dto.CautionDTO;

/**
 *
 * @author koben
 */
@Local
public interface CautionTiersPayantService {

    void addCaution(String idTiersPayant, int amount);

    JSONObject update(String idCaution, int amount);

    JSONObject supprimerHistorique(String cautionHistoriqueId);

    JSONObject supprimerCaution(String idCaution);

    void updateCaution(Caution caution, int saleAmount);

    List<CautionDTO> fetch(String tiersPayantId, int start, int limit, boolean all);

    JSONObject fetch(String tiersPayantId, int start, int limit);
}
