package rest.service;

import commonTasks.dto.ComboDTO;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author koben
 */
@Local
public interface TypeReglementService {

    List<ComboDTO> findAll();

    List<ComboDTO> findAllWithoutEspece();
}
