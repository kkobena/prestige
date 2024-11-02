package rest.service.impl;

import dal.TParameters;
import java.util.Objects;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rest.service.ParametreService;

/**
 *
 * @author koben
 */
@Stateless
public class ParametreServiceImpl implements ParametreService {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public boolean isEnable(String key) {
        try {
            TParameters tp = em.find(TParameters.class, key);
            return Objects.nonNull(tp) && tp.getStrVALUE().trim().equals("1");
        } catch (Exception e) {
            return true;
        }
    }

}
