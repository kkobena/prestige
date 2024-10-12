package rest.service.impl;

import dal.TUser;
import javax.ejb.Stateless;
import rest.service.SessionHelperService;

/**
 *
 * @author koben
 */
@Stateless
public class SessionHelperServiceImpl implements SessionHelperService {

    private TUser user;

    @Override
    public TUser getCurrentUser() {
        return this.user;
    }

    @Override
    public void setCurrentUser(TUser user) {
        this.user = user;

    }

}
