package rest.service;

import rest.service.dto.InventaireFamilleDTO;
import java.util.List;
import javax.ejb.Local;
import org.json.JSONObject;
import java.util.List;

/**
 *
 * @author airman
 */
@Local
public interface InventaireFamilleService {

    JSONObject getAllInventaireFamilles(String invId, int start, int limit);

    List<InventaireFamilleDTO> getAllInventaireFamilles(String invId, boolean all);

    List<InventaireFamilleDTO> getAllInventaireFamilles(String invId, int start, int limit, boolean all);

}