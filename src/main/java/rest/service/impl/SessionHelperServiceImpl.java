package rest.service.impl;

import dal.TUser;
import javax.ejb.Stateless;
import rest.service.SessionHelperService;
import rest.service.dto.SessionHelperData;

/**
 *
 * @author koben
 */
@Stateless
public class SessionHelperServiceImpl implements SessionHelperService {

    // Un bean @Stateless est mis en pool et partage entre les requetes : un champ
    // d'instance ecrit par l'AuthenticationFilter peut etre lu par une AUTRE
    // instance du pool (valeur null -> NPE aleatoires) voire par une autre
    // requete (risque de melange d'utilisateurs). Le stockage en ThreadLocal
    // garantit que la valeur posee par le filtre est relue par la meme requete
    // HTTP (meme thread), quelle que soit l'instance du pool utilisee.
    private static final ThreadLocal<TUser> CURRENT_USER = new ThreadLocal<>();
    private static final ThreadLocal<SessionHelperData> CURRENT_DATA = new ThreadLocal<>();

    @Override
    public TUser getCurrentUser() {
        return CURRENT_USER.get();
    }

    @Override
    public void setCurrentUser(TUser user) {
        if (user == null) {
            CURRENT_USER.remove();
        } else {
            CURRENT_USER.set(user);
        }
    }

    @Override
    public void setData(SessionHelperData data) {
        if (data == null) {
            CURRENT_DATA.remove();
        } else {
            CURRENT_DATA.set(data);
        }
    }

    @Override
    public SessionHelperData getData() {
        return CURRENT_DATA.get();
    }

}
