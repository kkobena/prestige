package rest.service;

import commonTasks.dto.VenteDTO;
import dal.Caution;
import java.util.List;
import javax.ejb.Local;
import org.json.JSONObject;
import rest.service.dto.AddCautionDTO;
import rest.service.dto.CautionDTO;
import rest.service.dto.CautionHistoriqueDTO;

/**
 *
 * @author koben
 */
@Local
public interface CautionTiersPayantService {

    void addCaution(AddCautionDTO addCaution);

    JSONObject update(AddCautionDTO addCaution);

    JSONObject supprimerCaution(String idCaution);

    void updateCaution(Caution caution, int saleAmount);

    List<CautionDTO> fetch(String tiersPayantId, int start, int limit, boolean all);

    JSONObject fetch(String tiersPayantId, int start, int limit);

    List<VenteDTO> getVentes(String idCaution);

    JSONObject getVentesView(String idCaution);

    List<CautionHistoriqueDTO> getHistoriques(String idCaution);

    JSONObject getHistoriquesView(String idCaution);
}
