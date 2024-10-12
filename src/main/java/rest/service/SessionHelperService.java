package rest.service;

import dal.TUser;
import javax.ejb.Local;

/**
 *
 * @author koben
 */
@Local
public interface SessionHelperService {

    TUser getCurrentUser();

    void setCurrentUser(TUser user);

}
