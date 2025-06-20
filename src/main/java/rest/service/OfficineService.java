
package rest.service;

import java.util.List;
import javax.ejb.Local;
import org.json.JSONObject;
import rest.service.dto.OfficineDTO;

/**
 *
 * @author airman
 */

@Local
public interface OfficineService {

    public List<OfficineDTO> getAllOfficines();

}
