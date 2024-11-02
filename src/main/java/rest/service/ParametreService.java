package rest.service;

import javax.ejb.Local;

/**
 *
 * @author koben
 */
@Local
public interface ParametreService {

    boolean isEnable(String key);
}
