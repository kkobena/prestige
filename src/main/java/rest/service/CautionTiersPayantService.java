package rest.service;

import dal.Caution;
import javax.ejb.Local;
import org.json.JSONObject;

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
}
