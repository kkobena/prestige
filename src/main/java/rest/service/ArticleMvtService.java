package rest.service;

import java.util.List;
import javax.ejb.Local;
import org.json.JSONObject;
import rest.service.dto.ArticleMvtDTO;

@Local
public interface ArticleMvtService {

    JSONObject getAllArticleMvt(String dtStart, String dtEnd, String query, int limit, int start);

    JSONObject getAllArticleMvt(String dtStart, String dtEnd, String query);

    List<ArticleMvtDTO> getAllArticleMvt(String dtStart, String dtEnd, String query, int limit, int start, boolean all);
}
