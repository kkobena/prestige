package rest.service;

import java.util.List;
import javax.ejb.Local;
import org.json.JSONObject;
import rest.service.dto.EtatControlBon;

/**
 *
 * @author koben
 */
@Local
public interface EtatControlBonService {

    List<EtatControlBon> list(String search, String dtStart, String dtEnd, String grossisteId, int start, int limit, boolean all);

    JSONObject list(String search, String dtStart, String dtEnd, String grossisteId, int start, int limit);
    
    void  hasReturnFullBLLAuthority(boolean b);
    
}
