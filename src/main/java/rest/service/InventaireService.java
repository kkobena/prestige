package rest.service;

import dal.TUser;
import javax.ejb.Local;
import org.json.JSONObject;

/**
 *
 * @author koben
 */
@Local
public interface InventaireService {

    JSONObject createInventaireFromCanceledList(String dtStart, String dtEnd, String userId, TUser tUser);
}
