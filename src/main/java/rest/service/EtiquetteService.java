package rest.service;

import java.util.List;
import javax.ejb.Local;
import rest.service.dto.EtiquetteDTO;

/**
 *
 * @author koben
 */
@Local
public interface EtiquetteService {

    List<EtiquetteDTO> buildEtiquettes(String bonId, int startAt, String rasionSociale);
}
