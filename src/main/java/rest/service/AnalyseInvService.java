
package rest.service;

import java.util.List;
import rest.service.dto.AnalyseInvDTO;

/**
 *
 * @author airman
 */
public interface AnalyseInvService {
    List<AnalyseInvDTO> analyseInventaire(String inventaireId);
}
