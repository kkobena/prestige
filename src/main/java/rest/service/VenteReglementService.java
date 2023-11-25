package rest.service;

import dal.TPreenregistrement;
import dal.TTypeReglement;
import dal.VenteReglement;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author koben
 */
@Local
public interface VenteReglementService {

    void createNew(TPreenregistrement preenregistrement, TTypeReglement typeReglement, int montant, int montantAttendu);

    List<VenteReglement> getByVenteId(String venteId);
}
