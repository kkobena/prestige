package rest.service;

import javax.ejb.Local;
import rest.service.dto.LicenceDTO;

/**
 *
 * @author koben
 */
@Local
public interface LicenceService {

    String getLicence();

    void save(String licence);

    String generateLicence(LicenceDTO licence);

}
