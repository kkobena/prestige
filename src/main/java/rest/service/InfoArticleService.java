package rest.service;

import rest.service.dto.InfoArticleDTO;

import java.util.List;

/**
 *
 * @author airman
 */

public interface InfoArticleService {
    List<InfoArticleDTO> getInfoArticles(int start, int limit, String searchTerm);

    public Long countInfoArticles(String searchTerm);
}