package rest.service;

import dal.TTiersPayant;
import javax.ejb.Local;

/**
 *
 * @author koben
 */
@Local
public interface CautionTiersPayantService {

    void addCaution(String idTiersPayant, int caution);

    void update(TTiersPayant payant, int caution);
}
